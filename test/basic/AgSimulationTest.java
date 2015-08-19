/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.Restart;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.diffusion.RoundPerimeter;
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
    try {
      parser.readFile("test/input/AgParameters");
    } catch (IOException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
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
    Parser parser = new Parser();
    try {
      parser.readFile("test/input/AgParameters");
    } catch (IOException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    AbstractSimulation simulation = new AgSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();
    
    Restart restart = new Restart("test/references/");
    int[] sizes = {parser.getCartSizeX()/2,parser.getCartSizeY()/2};
    float[][] ref = null;
    try {
      ref = restart.readSurfaceText2D(2, sizes, "AgSurfaceRef");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    float[][] surface = simulation.getKmc().getSampledSurface(parser.getCartSizeX()/2, parser.getCartSizeY()/2);
    assertArrayEquals(ref, surface);
  }
   
}
