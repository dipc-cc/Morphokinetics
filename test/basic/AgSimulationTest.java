/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.Restart;
import geneticAlgorithm.evaluationFunctions.AgBasicPsdEvaluator;
import java.io.FileNotFoundException;
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
    parser.readFile("test/input/AgParameters");
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
    parser.readFile("test/input/AgParameters");

    doAgTest(parser);

    Restart restart = new Restart("test/references/");
    int[] sizes = {parser.getCartSizeX() / 2, parser.getCartSizeY() / 2};
    float[][] ref = null;
    try {
      ref = restart.readSurfaceText2D(2, sizes, "AgSurfaceRef");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    assertArrayEquals(ref, currentSurface);
  }

  /**
   * Really simple and quick test to ensure that runs correctly
   */
  @Test
  public void testAgSimple() {
    AbstractSimulation.printHeader("Ag simple test");
    Parser parser = new Parser();
    parser.readFile("test/input/AgSmallParameters");

    doAgTest(parser);
  }
  
  @Test
  public void testAgPsd() {
    AbstractSimulation.printHeader("Ag PSD test");
    Parser parser = new Parser();
    parser.readFile("test/input/AgPsdParameters");
    
    doAgTest(parser);
    //TODO check that PSDs are equivalent
    Restart restart = new Restart("test/references/");
    int[] sizes = {parser.getCartSizeX() / 2, parser.getCartSizeY() / 2};
    float[][] ref = null;
    try {
      ref = restart.readPsdText2D(2, sizes, "AgPsdAvgRaw.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    for (int i = 0; i < ref.length; i++) {
      for (int j = 0; j < ref[0].length; j++) {
        currentPsd[i][j] = (float) Math.log(currentPsd[i][j]);
        ref[i][j] = (float) Math.log(ref[i][j]);
      }
    }
    
    AgBasicPsdEvaluator evaluator = new AgBasicPsdEvaluator(null,
            0, 0,
            parser.getCartSizeX() / 2, parser.getCartSizeY() / 2,
            null, "Frobenius", null, parser.getTemperature());
    evaluator.setPsd(ref);
    double FrobeniusError = evaluator.calculateFrobeniusNormErrorMatrix(currentPsd);
    System.out.println("Frobenius error is "+FrobeniusError);
    assertEquals(0.016, FrobeniusError, 0.0015);
    
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
