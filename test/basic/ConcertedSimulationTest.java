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
public class ConcertedSimulationTest {
  
  private float[][] simulatedSurface;
  private double simulatedTime;
  private String restartFolder;
  
  public ConcertedSimulationTest() {
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
  public void testConcertedAdsorptionExtra() {
    AbstractSimulation.printHeader("Concerted only adsorption with extra output test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/ConcertedAdsorptionExtra.json");
    parser.print();

    doConcertedTest(parser);
    assertEquals(2.9725425258993533E-20, simulatedTime, 0.0);
    
    ArrayList<ArrayList> data = null;
    try {
      Restart restartRun = new Restart(restartFolder);
      data = restartRun.readDataTextFile("dataAePossibleDiscrete000.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ConcertedSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    ArrayList readDataLastLine = data.get(data.size() - 1);
    Double[] ref = new Double[]{2.9725425258993533E-20, 58.0, 120.0, 29.0, 17.0, 0.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 19.0, 12.0, 10.0, 36.0, 18.0, 9.0, 14.0, 0.0, 2.0, 0.0, 0.0, 0.0, 40.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 14.0, 1.0, 4.0, 17.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 1.0, 32.0, 6.0, 9.0, 54.0, 6.0, 12.0, 16.0, 5.0, 0.0, 8.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 9.0, 5.0, 5.0, 31.0, 0.0, 18.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 15.0, 0.0, 20.0, 38.0, 0.0, 26.0, 0.0, 0.0, 0.0, 6.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 8.0, 0.0, 20.0, 1.0, 14.0, 17.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 22.0, 0.0, 22.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 16.0, 0.0, 10.0, 13.0, 9.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 9.0, 0.0, 14.0, 1.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 7.0, 0.0, 37.0, 28.0, 22.0, 0.0, 0.0, 0.0, 0.0};
    Object[] read = readDataLastLine.toArray();

    assertArrayEquals(ref, read);
  }
  
  private void doConcertedTest(Parser parser) {
    AbstractSimulation simulation = new ConcertedSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();
    restartFolder = simulation.getRestartFolderName();

    simulatedSurface = simulation.getKmc().getSampledSurface((int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()));
    simulatedTime = simulation.getSimulatedTime();
  }
}
