/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.Restart;
import geneticAlgorithm.evaluationFunctions.AgBasicPsdEvaluator;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgUcSimulationTest {
  
  private float[][] simulatedSurface;
  private float[][] simulatedPsd;
  private double simulatedTime;

  public AgUcSimulationTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testAgUc180() {
    AbstractSimulation.printHeader("AgUc 180 test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgUc180Parameters");
    parser.print();

    doAgTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("AgUc180Surface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(7.703879893264137, simulatedTime, 0.0);
  }
  
  @Test
  public void testAgUcPsd() {
    AbstractSimulation.printHeader("AgUc PSD test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgUcPsdParameters");
    parser.print();

    doAgTest(parser);
    //TODO check that PSDs are equivalent
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {(int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale())};
    float[][] ref = null;
    try {
      ref = restart.readPsdText2D(2, sizes, "AgPsdAvgRaw.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    AgBasicPsdEvaluator evaluator = new AgBasicPsdEvaluator(null,
            0, 0,
            (int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()),
            null, "Frobenius", null, parser.getTemperature());
    evaluator.setPsd(ref);
    double FrobeniusError = evaluator.calculateFrobeniusNormErrorMatrix(simulatedPsd);
    System.out.println("Frobenius error is " + FrobeniusError);
    List<Double> results = new ArrayList();
    results.add(FrobeniusError);
    results.add(0.04); // the error must be lower than 0.04
    results.sort((a, b) -> b.compareTo(a));
    assertEquals(0.04, results.get(0), 0.0); // ensure that the first value is 0.04, and therefore, the current error is lower
    assertEquals(350939.25839387067, simulatedTime, 52649); // tolerance is 15%. It is too big but the simulation time varies a lot.

  }

  private void doAgTest(Parser parser) {
    AbstractSimulation simulation = new AgUcSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();

    simulatedSurface = simulation.getKmc().getSampledSurface((int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()));
    simulatedPsd = simulation.getPsd().getPsd();
    simulatedTime = simulation.getSimulatedTime();
  }

}
