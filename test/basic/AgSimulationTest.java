/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.Restart;
import geneticAlgorithm.evaluationFunctions.AgBasicPsdEvaluator;
import java.io.FileNotFoundException;
import static java.lang.String.format;
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
  
  private int simulatedIslands;
  private double simulatedTime;
  private float[][] simulatedSurface;
  private float[][] simulatedPsd;
  private float[][][] simulatedSurfaces;

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
    int[] sizes = {parser.getCartSizeX(), parser.getCartSizeY()};
    float[][] ref = null;
    try {
      ref = restart.readSurfaceText2D(2, sizes, "AgSurfaceRef");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    for (int i = 0; i < ref.length; i++) {
      assertArrayEquals(ref[i], simulatedSurface[i], (float) 0.001);
    }
    assertEquals(198.99123314622588, simulatedTime, 0.0);
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
    double FrobeniusError = evaluator.calculateFrobeniusNormErrorMatrix(simulatedPsd);
    System.out.println("Frobenius error is " + FrobeniusError);
    List<Double> results = new ArrayList();
    results.add(FrobeniusError);
    results.add(0.04); // the error must be lower than 0.032
    results.sort((a, b) -> b.compareTo(a));
    assertEquals(0.04, results.get(0), 0.0); // ensure that the first value is 0.04, and therefore, the current error is lower
    assertEquals(350939.25839387067, simulatedTime, 1000.0);
  }
  
  @Test
  public void testAgMulti() {
    AbstractSimulation.printHeader("Ag test multi-flake");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgMultiParameters");
    parser.print();

    doAgTest(parser);

    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {parser.getCartSizeX(), parser.getCartSizeY()};
   
    try {
      // read reference surfaces and compare them with simulated ones
      for (int i = 0; i < parser.getNumberOfSimulations(); i++) {
        String fileName = format("%s/AgMultiSurface%03d.txt", TestHelper.getBaseDir() + "/test/references/", i);

        float[][] tmpSurface = restart.readSurfaceText2D(2, sizes, fileName);
        for (int j = 0; j < sizes[0]; j++) {
          for (int k = 0; k < sizes[1]; k++) {
            assertEquals(simulatedSurfaces[i][j][k], tmpSurface[j][k], 0.001f);
          }
        }
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    assertEquals(simulatedIslands, 3);
    assertEquals(14.381566300521941, simulatedTime, 0.0);
  }

  private void doAgTest(Parser parser) {
    AbstractSimulation simulation = new AgSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();

    simulatedSurface = simulation.getKmc().getSampledSurface(parser.getCartSizeX(), parser.getCartSizeY());
    simulatedPsd = simulation.getPsd().getPsd();
    simulatedIslands = simulation.getKmc().getLattice().getIslandCount();
    simulatedTime = simulation.getSimulatedTime();
    Restart readResults = new Restart(simulation.getRestartFolderName());
    int[] sizes = {parser.getCartSizeX(), parser.getCartSizeY()};
    simulatedSurfaces = new float[parser.getNumberOfSimulations()][sizes[0]][sizes[1]];
    for (int i = 0; i < parser.getNumberOfSimulations(); i++) {
      String fileName = format("%s/surface%03d.mko", simulation.getRestartFolderName(), i);
      try {
        float[][] tmpSurface = readResults.readSurfaceBinary2D(fileName);
        for (int j = 0; j < sizes[0]; j++) {
          for (int k = 0; k < sizes[1]; k++) {
            simulatedSurfaces[i][j][k] = tmpSurface[j][k];
          }
        }
      } catch (FileNotFoundException ex) {
        Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
