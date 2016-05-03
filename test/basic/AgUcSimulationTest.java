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
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgUcSimulationTest {
  
  private float[][] currentSurface;
  private float[][] currentPsd;

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
    double FrobeniusError = evaluator.calculateFrobeniusNormErrorMatrix(currentPsd);
    System.out.println("Writing difference");
    float[][] differencePsd = PsdSignature2D.doOnePsd(evaluator.getCurrentDifference());
    
    restart.writeSurfaceText2D(2, sizes, evaluator.getCurrentDifference(), "difference");
    restart.writeSurfaceText2D(2, sizes, differencePsd, "differencePsd");
    int count = 0;
    int countLog = 0;
    List<Float> list = new ArrayList<>();
    for (int i = 0; i < differencePsd[0].length; i++) {
      for (int j = 0; j < differencePsd.length; j++) {
        if (differencePsd[i][j] > 0) {
          count++;
        }
        if (Math.log(differencePsd[i][j]) > 0) {
          countLog++;
          list.add(differencePsd[i][j]);
          if (i == 0 && j == 0) {
            System.out.println("0 0  is different, we are not going to count it");
            countLog--;
          }
        }
      }
    }
    if (countLog > 0) {
      System.out.println("------------> Two simulations are NOT equal!!");
    } else {
      System.out.println("------------> Two simulations are EQUAL!!");
    }
    System.out.println("Count "+count);
    System.out.println("CountLog "+countLog);
    for (int i = 0; i < list.size(); i++) {
      System.out.print(" "+list.get(i)+" "+Math.log(list.get(i))+" |");
    }
    System.out.println("");
    System.out.println("Frobenius error is " + FrobeniusError);
    
    List<Double> results = new ArrayList();
    results.add(FrobeniusError);
    results.add(0.04); // the error must be lower than 0.04
    results.sort((a, b) -> b.compareTo(a));
    assertEquals(0.04, results.get(0), 0.0); // ensure that the first value is 0.04, and therefore, the current error is lower

  }

  private void doAgTest(Parser parser) {
    AbstractSimulation simulation = new AgUcSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();

    currentSurface = simulation.getKmc().getSampledSurface((int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()));
    currentPsd = simulation.getPsd().getPsd();
  }

}
