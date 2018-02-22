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
public class BasicSimulationTest {
  
  private float[][] simulatedSurface;
  private int simulatedIslands;
  private double simulatedTime;

  public BasicSimulationTest() {
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
  public void testBasic120() {
    AbstractSimulation.printHeader("Basic 120 test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/Basic120Parameters");
    parser.print();

    doBasicGrowthTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {parser.getCartSizeX(), parser.getCartSizeY()};
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceText2D(2, sizes, "BasicMulti120Surface000.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.001);
    }
    assertEquals(simulatedIslands, 9);
    assertEquals(28.800499838209642, simulatedTime, 0.0);
  }
  
  @Test
  public void testBasic180() {
    AbstractSimulation.printHeader("Basic 180 test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/Basic180Parameters");
    parser.print();

    doBasicGrowthTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {parser.getCartSizeX(), parser.getCartSizeY()};
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceText2D(2, sizes, "BasicMulti180Surface000.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.001);
    }
    assertEquals(simulatedIslands, 1);
    assertEquals(29.7818787698688, simulatedTime, 0.0);
  }
  
  private void doBasicGrowthTest(Parser parser) {
    AbstractSimulation simulation = new BasicGrowthSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();

    simulatedSurface = simulation.getKmc().getSampledSurface((int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()));
    simulatedIslands = simulation.getKmc().getLattice().getIslandCount();
    simulatedTime = simulation.getSimulatedTime();
  }

}
