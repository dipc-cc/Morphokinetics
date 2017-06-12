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
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisSimulationTest {
  
  private float[][] simulatedSurface;
  private double simulatedTime;

  public CatalysisSimulationTest() {
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
  public void testCatalysis() {
    AbstractSimulation.printHeader("Catalysis test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisParameters");
    parser.print();

    doCatalysisTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("CatalysisSurface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(1.2867702701782317E-8, simulatedTime, 1e-14);
  }
  
  @Test
  public void testCatalysisDiffusion() {
    AbstractSimulation.printHeader("Catalysis diffusion test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisDiffusionParameters");
    parser.print();

    doCatalysisTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("CatalysisDiffusionSurface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(9.738112691612535E-9, simulatedTime, 0.0);
  }
  
  @Test
  public void testCatalysisDesorption() {
    AbstractSimulation.printHeader("Catalysis desorption test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisDesorptionParameters");
    parser.print();

    doCatalysisTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("CatalysisDesorptionSurface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(2.5858390465838724E20, simulatedTime, 0.0);
  }
  
  @Test
  public void testCatalysisAdsorptionDesorptionDiffusion() {
    AbstractSimulation.printHeader("Catalysis adsorption desorption diffusion test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisAddParameters");
    parser.print();

    doCatalysisTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("CatalysisAddSurface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(0.002072004454985052, simulatedTime, 0.0);
  }
  
  @Test
  public void testCatalysisReaction() {
    AbstractSimulation.printHeader("Catalysis reaction test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisReactionParameters");
    parser.print();

    doCatalysisTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("CatalysisReactionSurface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(0.6816087139566696, simulatedTime, 0.0);
  }
  
  @Test
  public void testCatalysisDesorptionReaction() {
    AbstractSimulation.printHeader("Catalysis desorption reaction test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisDesorptionReactionParameters");
    parser.print();

    doCatalysisTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("CatalysisDesorptionReactionSurface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(5.324564915046757E20, simulatedTime, 0.0);
  }
  
  @Test
  public void testCatalysisTof() {
    AbstractSimulation.printHeader("Catalysis TOF reaction test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisTofParameters");
    parser.print();

    doCatalysisTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("CatalysisTofSurface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(4.2804054346776806E-5, simulatedTime, 0.0);
    String ref = "4.28041e-05\t0.00000\t0.0250000\t1.00000\t0.970000\t502\t491\t6\t0\t0\t0\t0\t6";
    String extraFile = restart.readFile("results/dataCatalysis.txt");
    String read = extraFile.substring(6781, 6847);
    assertEquals(ref.trim(), read.trim());
  }  
  
  private void doCatalysisTest(Parser parser) {
    AbstractSimulation simulation = new CatalysisSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();

    simulatedSurface = simulation.getKmc().getSampledSurface((int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()));
    simulatedTime = simulation.getSimulatedTime();
  }

}
