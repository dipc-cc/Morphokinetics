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
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
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
  
  private float[][] currentSurface;
  
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/GrapheneParameters");
    parser.print();
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/GrapheneParameters");
    parser.print();

    doGrapheneTest(parser);

    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {parser.getCartSizeX() / 2, parser.getCartSizeY() / 2};
    float[][] ref = null;
    try {
      ref = restart.readSurfaceText2D(2, sizes, "GrapheneSurfaceRef");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(GrapheneSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    assertArrayEquals(ref, currentSurface);
  }

  @Test
  public void testGrapheneMany() {
    AbstractSimulation.printHeader("Graphene test many");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/GrapheneManyParameters");
    parser.print();

    doGrapheneTest(parser);

    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {parser.getCartSizeX() / 2, parser.getCartSizeY() / 2};
    float[][] ref0 = null;
    float[][] ref1 = null;
    float[][] ref2 = null;
    float[][] ref3 = null;
    float[][] ref4 = null;
    try {
      ref0 = restart.readSurfaceText2D(2, sizes, "GrapheneManySurface000.txt");
      ref1 = restart.readSurfaceText2D(2, sizes, "GrapheneManySurface001.txt");
      ref2 = restart.readSurfaceText2D(2, sizes, "GrapheneManySurface002.txt");
      ref3 = restart.readSurfaceText2D(2, sizes, "GrapheneManySurface003.txt");
      ref4 = restart.readSurfaceText2D(2, sizes, "GrapheneManySurface004.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(GrapheneSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    // For the moment only comparing the last surface
    assertArrayEquals(ref4, currentSurface);
    // TODO compare the rest of surfaces
  }
    
    
  private void doGrapheneTest(Parser parser) {
    AbstractSimulation simulation = new GrapheneSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();
    
    currentSurface = simulation.getKmc().getSampledSurface(parser.getCartSizeX() / 2, parser.getCartSizeY() / 2);
  }
}
