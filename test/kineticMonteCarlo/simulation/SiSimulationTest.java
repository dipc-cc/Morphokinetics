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
package kineticMonteCarlo.simulation;

import basic.Parser;
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/Si.json");
    parser.print();

    assertEquals("Si", parser.getCalculationMode());
    assertEquals(135.0, parser.getTemperature(),1e-10);
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
    parser.readFile(TestHelper.getBaseDir() + "/test/input/Si.json");
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
    assertEquals(5.147736907144184E13, simulatedTime, 0.0);
  }
  
}
