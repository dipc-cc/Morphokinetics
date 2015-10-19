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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class GrapheneSimulationTest {
  
  public GrapheneSimulationTest() {
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
    parser.readFile("test/input/GrapheneParameters");
    assertEquals("graphene", parser.getCalculationMode());
    assertEquals(135, parser.getTemperature());
    assertEquals(1, parser.getNumberOfSimulations());
    assertEquals(400, parser.getCartSizeX());
    assertEquals(400, parser.getCartSizeY());
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
   * Test of the graphene simulation
   */
  @Test
  public void testGraphene() {
    AbstractSimulation.printHeader("Graphene test");
    Parser parser = new Parser();
    parser.readFile("test/input/GrapheneParameters");
    
    AbstractSimulation simulation = new GrapheneSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();
    
    Restart restart = new Restart("test/references/");
    int[] sizes = {parser.getCartSizeX()/2,parser.getCartSizeY()/2};
    float[][] ref = null;
    try {
      ref = restart.readSurfaceText2D(2, sizes, "GrapheneSurfaceRef");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(GrapheneSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    float[][] surface = simulation.getKmc().getSampledSurface(parser.getCartSizeX()/2, parser.getCartSizeY()/2);
    assertArrayEquals(ref, surface);
  }
  
}
