/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.Restart;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class SiSimulationTest {
  
  public SiSimulationTest() {
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/SiParameters");
    parser.print();

    assertEquals("Si", parser.getCalculationMode());
    assertEquals(135, parser.getTemperature());
    assertEquals(1, parser.getNumberOfSimulations());
    assertEquals(96, parser.getCartSizeX());
    assertEquals(96, parser.getCartSizeY());
    assertEquals(16, parser.getCartSizeZ());
    assertEquals(true, parser.justCentralFlake());
    assertEquals("binned", parser.getListType());
    assertEquals(true, parser.doPsd());
    assertEquals(20, parser.getBinsLevels());
    assertEquals(1, parser.getExtraLevels());
    assertEquals(true, parser.outputData());
    assertEquals(false, parser.randomSeed());
    assertEquals(false, parser.useMaxPerimeter());
  }

  /**
   * Test of the Si simulation
   */
  @Test
  public void testSi() {
    AbstractSimulation.printHeader("Si test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/SiParameters");
    parser.print();

    AbstractSimulation simulation = new SiSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();

    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {parser.getCartSizeX() / 2, parser.getCartSizeY() / 2};
    float[][] ref = null;
    try {
      ref = restart.readSurfaceText2D(2, sizes, "SiSurfaceRef");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    float[][] surface = simulation.getKmc().getSampledSurface(parser.getCartSizeX() / 2, parser.getCartSizeY() / 2);
    for (int i = 0; i < parser.getCartSizeY() / 2; i++) {
      assertArrayEquals(ref[i], surface[i], 0.001f);
    }
    double simulatedTime = simulation.getSimulatedTime();
    assertEquals(1.0779838516773791E13, simulatedTime, 0.0);
  }
  
}
