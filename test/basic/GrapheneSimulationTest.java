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
  
  private float[][] simulatedSurface;
  private int simulatedIslands;
  private double simulatedTime;
  private float[][][] simulatedSurfaces;
  
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/Graphene.json");
    parser.print();
    assertEquals("graphene", parser.getCalculationMode());
    assertEquals(1273.0, parser.getTemperature(),1e-10);
    assertEquals(1, parser.getNumberOfSimulations());
    assertEquals(200, parser.getCartSizeX());
    assertEquals(200, parser.getCartSizeY());
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/Graphene.json");
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

    for (int i = 0; i < ref.length; i++) {
      assertArrayEquals(ref[i], simulatedSurface[i], (float) 0.001);
    }
    assertEquals(2206.977229875009, simulatedTime, 0.0);
  }

  @Test
  public void testGrapheneMany() {
    AbstractSimulation.printHeader("Graphene test many");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/GrapheneMany.json");
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
    for (int i = 0; i < ref2.length; i++) {
      assertArrayEquals(ref4[i], simulatedSurface[i], (float) 0.001);
    }
    assertEquals(123.68364303727847, simulatedTime, 0.0);
    // TODO compare the rest of surfaces
  }
    
  @Test
  public void testGrapheneMulti() {
    AbstractSimulation.printHeader("Graphene test multi-flake");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/GrapheneMulti.json");
    parser.print();

    doGrapheneTest(parser);

    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {parser.getCartSizeX() / 2, parser.getCartSizeY() / 2};
    float[][] ref0 = null;
    float[][] ref1 = null;
    float[][] ref2 = null;
    try {
      ref0 = restart.readSurfaceText2D(2, sizes, "GrapheneMultiSurface000.txt");
      ref1 = restart.readSurfaceText2D(2, sizes, "GrapheneMultiSurface001.txt");
      ref2 = restart.readSurfaceText2D(2, sizes, "GrapheneMultiSurface002.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(GrapheneSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    // For the moment only comparing the last surface
    for (int i = 0; i < ref2.length; i++) {
      assertArrayEquals(ref2[i], simulatedSurface[i], (float) 0.001);
    }
    assertEquals(simulatedIslands, 1);
    assertEquals(9660.486940127535, simulatedTime, 0.0);
    // TODO compare the rest of surfaces
    /*try {
      // read reference surfaces and compare them with simulated ones
      for (int i = 0; i < parser.getNumberOfSimulations(); i++) {
        String fileName = format("%s/GrapheneMultiSurface%03d.txt", TestHelper.getBaseDir() + "/test/references/", i);

        float[][] tmpSurface = restart.readSurfaceText2D(2, sizes, fileName);
        for (int j = 0; j < sizes[0]; j++) {
          for (int k = 0; k < sizes[1]; k++) {
            System.out.println("i j "+j+" "+k+" "+simulatedSurfaces[i][j][k]+" "+tmpSurface[j][k]);
            assertEquals(simulatedSurfaces[i][j][k], tmpSurface[j][k], 1.0f);
          }
        }
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }*/
  }
    
    
    
  private void doGrapheneTest(Parser parser) {
    AbstractSimulation simulation = new GrapheneSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();
    
    simulatedSurface = simulation.getKmc().getSampledSurface(parser.getCartSizeX() / 2, parser.getCartSizeY() / 2);
    simulatedIslands = simulation.getKmc().getLattice().getIslandCount();
    simulatedTime = simulation.getSimulatedTime();
    /*Restart readResults = new Restart(simulation.getRestartFolderName());
    int[] sizes = {parser.getCartSizeX() / 2, parser.getCartSizeY() / 2};
    simulatedSurfaces = new float[parser.getNumberOfSimulations()][sizes[0]][sizes[1]];
    for (int i = 0; i < parser.getNumberOfSimulations(); i++) {
      String fileName = format("%s/surface%03d.mko", simulation.getRestartFolderName(), i);
      try {
        float[][] tmpSurface = readResults.readSurfaceBinary2D(fileName, 2);
        for (int j = 0; j < sizes[0]; j++) {
          for (int k = 0; k < sizes[1]; k++) {
            simulatedSurfaces[i][j][k] = tmpSurface[j][k];
          }
        }
      } catch (FileNotFoundException ex) {
        Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
      }
    }//*/
  }
}
