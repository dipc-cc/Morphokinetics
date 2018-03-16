/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/Catalysis.json");
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisDiffusion.json");
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisDesorption.json");
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisAdd.json");
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisReaction.json");
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisDesorptionReaction.json");
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/CatalysisTof.json");
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
    assertEquals(4.52115466652838E-5, simulatedTime, 0.0);
    ArrayList<ArrayList> data = null;
    try {
      Restart restartRun = new Restart(restartFolder);
      data = restartRun.readDataTextFile("dataCatalysis000.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(CatalysisSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    ArrayList readDataLastLine = data.get(data.size() - 1);
    Double[] ref = new Double[]{4.52115e-05, 0.005, 0.05, 0.995, 0.95, 5014.0, 4938.0,
      47.0, 1.0, 0.0, 5.0, 1.0, 18.0, 0.0, 400.0, 33.0, 0.0};
    Object[] read = readDataLastLine.toArray();
    
    assertArrayEquals(ref, read);
  }
  
  private void doCatalysisTest(Parser parser) {
    AbstractSimulation simulation = new CatalysisCoSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();
    restartFolder = simulation.getRestartFolderName();

    simulatedSurface = simulation.getKmc().getSampledSurface((int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()));
    simulatedTime = simulation.getSimulatedTime();
  }

}
