/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic;

import basic.io.Restart;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
  private String restartFolder;

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
    assertEquals(1.2502576379402801E-8, simulatedTime, 1e-14);
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
    assertEquals(1.0270168073118504E-8, simulatedTime, 0.0);
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
    assertEquals(1.6483405890072265E20, simulatedTime, 0.0);
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
    assertEquals(0.003029557984908654, simulatedTime, 0.0);
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
    assertEquals(0.4953070112706185, simulatedTime, 0.0);
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
    assertEquals(1.5146394613233045E20, simulatedTime, 0.0);
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
    assertEquals(4.559062277553586E-5, simulatedTime, 0.0);
    ArrayList<ArrayList> data = null;
    try {
      Restart restartRun = new Restart(restartFolder);
      data = restartRun.readDataTextFile("dataCatalysis000.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(CatalysisSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    ArrayList readDataLastLine = data.get(data.size() - 1);
    ArrayList refData = new ArrayList();
    refData.add(4.55906E-5);
    refData.add(0.005);
    refData.add(0.045);
    refData.add(0.995);
    refData.add(0.95);
    refData.add(5013.0);
    refData.add(4938.0);
    refData.add(47.0);
    refData.add(1.0);
    refData.add(0.0);
    refData.add(5.0);
    refData.add(1.0);
    refData.add(41.0);
    refData.add(1.0);
    refData.add(399.0);
    refData.add(31.0);
    refData.add(4.0);
    System.out.println(refData);
    assertArrayEquals(refData.toArray(), readDataLastLine.toArray());
  }
  
  private void doCatalysisTest(Parser parser) {
    AbstractSimulation simulation = new CatalysisSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();
    restartFolder = simulation.getRestartFolderName();

    simulatedSurface = simulation.getKmc().getSampledSurface((int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()));
    simulatedTime = simulation.getSimulatedTime();
  }

}
