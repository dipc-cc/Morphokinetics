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
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
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
public class AgSimulationTest {
  
  private float[][] currentSurface;
  private float[][] currentPsd;

  public AgSimulationTest() {
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
  public void testParameterFile() {
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgParameters");
    parser.print();
    assertEquals("Ag", parser.getCalculationMode());
    assertEquals(135, parser.getTemperature());
    assertEquals(1, parser.getNumberOfSimulations());
    assertEquals(256, parser.getCartSizeX());
    assertEquals(256, parser.getCartSizeY());
    assertEquals(true, parser.justCentralFlake());
    assertEquals("linear", parser.getListType());
    assertEquals(true, parser.doPsd());
    assertEquals(100, parser.getBinsLevels());
    assertEquals(0, parser.getExtraLevels());
    assertEquals(true, parser.outputData());
    assertEquals(false, parser.randomSeed());
    assertEquals(false, parser.useMaxPerimeter());
    assertEquals(RoundPerimeter.CIRCLE, parser.getPerimeterType());
  }
  
  /**
   * Test of the Ag simulation
   */
  @Test
  public void testAg() {
    AbstractSimulation.printHeader("Ag test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgParameters");
    parser.print();

    doAgTest(parser);

    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {parser.getCartSizeX() / 2, parser.getCartSizeY() / 2};
    float[][] ref = null;
    try {
      ref = restart.readSurfaceText2D(2, sizes, "AgSurfaceRef");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    for (int i = 0; i < ref.length; i++) {
      assertArrayEquals(ref[i], currentSurface[i], (float) 0.001);
    }
  }

  /**
   * Really simple and quick test to ensure that runs correctly. No further checks.
   */
  @Test
  public void testAgSimple() {
    AbstractSimulation.printHeader("Ag simple test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgSmallParameters");
    parser.print();

    doAgTest(parser);
  }
  
  @Test
  public void testAgPsd() {
    AbstractSimulation.printHeader("Ag PSD test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgPsdParameters");
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
    System.out.println("Frobenius error is " + FrobeniusError);
    List<Double> results = new ArrayList();
    results.add(FrobeniusError);
    results.add(0.04); // the error must be lower than 0.032
    results.sort((a, b) -> b.compareTo(a));
    assertEquals(0.04, results.get(0), 0.0); // ensure that the first value is 0.04, and therefore, the current error is lower
 
  }
  
  @Test
  public void testAgMulti() {
    AbstractSimulation.printHeader("Ag test multi-flake");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgMultiParameters");
    parser.print();

    doAgTest(parser);

    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {parser.getCartSizeX() / 2, parser.getCartSizeY() / 2};
    float[][] ref0 = null;
    float[][] ref1 = null;
    float[][] ref2 = null;
    try {
      ref0 = restart.readSurfaceText2D(2, sizes, "AgMultiSurface000.txt");
      ref1 = restart.readSurfaceText2D(2, sizes, "AgMultiSurface001.txt");
      ref2 = restart.readSurfaceText2D(2, sizes, "AgMultiSurface002.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    // For the moment only comparing the last surface
    //assertArrayEquals(ref2, currentSurface, (float) 0.001);
    for (int i = 0; i < ref2.length; i++) {
      assertArrayEquals(ref2[i], currentSurface[i], (float) 0.001);
    }
    // TODO compare the number of islands and surface 0 and 1
  }

  private void doAgTest(Parser parser) {
    AbstractSimulation simulation = new AgSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();

    currentSurface = simulation.getKmc().getSampledSurface(parser.getCartSizeX() / 2, parser.getCartSizeY() / 2);
    currentPsd = simulation.getPsd().getPsd();
  }
   
}
