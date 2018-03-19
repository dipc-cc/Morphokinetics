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

import kineticMonteCarlo.simulation.AgUcSimulation;
import kineticMonteCarlo.simulation.AbstractSimulation;
import basic.io.Restart;
import geneticAlgorithm.evaluationFunctions.AgBasicPsdEvaluator;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
public class AgUcSimulationTest {
  
  private float[][] simulatedSurface;
  private float[][] simulatedPsd;
  private double simulatedTime;

  public AgUcSimulationTest() {
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
  public void testAgUc180() {
    AbstractSimulation.printHeader("AgUc 180 test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgUc180.json");
    parser.print();

    doAgTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("AgUc180Surface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(57.23857330592217, simulatedTime, 0.0);
  }
  
  @Test
  public void testAgUcPsd() {
    AbstractSimulation.printHeader("AgUc PSD test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgUcPsd.json");
    parser.print();

    doAgTest(parser);
    //TODO check that PSDs are equivalent
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    int[] sizes = {(int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale())};
    float[][] ref = null;
    try {
      ref = restart.readPsdText2D(2, sizes, "AgPsdAvgRaw.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }

    AgBasicPsdEvaluator evaluator = new AgBasicPsdEvaluator(null,
            0, 0,
            (int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()),
            null, "Frobenius", null, parser.getTemperature());
    evaluator.setPsd(ref);
    double FrobeniusError = evaluator.calculateFrobeniusNormErrorMatrix(simulatedPsd);
    System.out.println("Frobenius error is " + FrobeniusError);
    List<Double> results = new ArrayList();
    results.add(FrobeniusError);
    results.add(0.04); // the error must be lower than 0.04
    results.sort((a, b) -> b.compareTo(a));
    assertEquals(0.04, results.get(0), 0.0); // ensure that the first value is 0.04, and therefore, the current error is lower
    assertEquals(350939.25839387067, simulatedTime, 52649); // tolerance is 15%. It is too big but the simulation time varies a lot.

  }
  
  @Test
  public void testAgUcExtraOutput() {
    AbstractSimulation.printHeader("AgUc extra output test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/AgUcExtra.json");
    parser.print();

    doAgTest(parser);
    
    Restart restart = new Restart(TestHelper.getBaseDir() + "/test/references/");
    float[][] ref0 = null;
    try {
      ref0 = restart.readSurfaceBinary2D("AgUcExtraSurface000.mko");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(AgSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }    
    for (int i = 0; i < ref0.length; i++) {
      assertArrayEquals(ref0[i], simulatedSurface[i], (float) 0.0001);
    }
    assertEquals(2.1246955660861665E-6, simulatedTime, 0.0);
    ArrayList<ArrayList> data = null;
    Restart restartRun = new Restart("results/");
    try {
      data = restartRun.readDataTextFile("dataEvery1percentAndNucleation.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(CatalysisSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    ArrayList readDataLastLine = data.get(data.size() - 7);
    Double[] ref = new Double[]{0.098370, 2.11089e-06, 0.0, 7.0, 82950000.000000, 2247972.682327, 0.0, 
      24780.0, 33590328769005.703000, 3.038708, 181.0, 324.0, 27950.000722, 1914757.064401, 18.0, 
      24599.0, 0.0, 25.0, 69.0, 59.0, 24.0, 4.0, 0.0, 1335.0, 147.0, 90.0, 56.0, 21.0, 9.0, 1.0};
    Object[] read = readDataLastLine.toArray();
    assertArrayEquals(ref, read);
    
    ref = new Double[]{0.09945652, 2.122443278862133E-6, 2.5713448306030566E-10, 5.674102886400366E-8, 
      1.3872178293216556E-8, 8.405044982769268E-7, 2248299.6039003315, 7.0};
    try {
      data = restartRun.readDataTextFile("deltaTimeBetweenTwoAttachments.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(CatalysisSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    readDataLastLine = data.get(data.size() - 1);
    read = readDataLastLine.toArray();
    assertArrayEquals(ref, read);
    
    ref = new Double[]{0.09945652, 2.122443278862133E-6, 8.981666458090267E-12, 5.0618686787943304E-8,
      5.513717703413084E-9, 8.435988086222019E-7, 2248299.6039003315};
    try {
      data = restartRun.readDataTextFile("deltaTimePerAtom.txt");
    } catch (FileNotFoundException ex) {
      Logger.getLogger(CatalysisSimulationTest.class.getName()).log(Level.SEVERE, null, ex);
    }
    readDataLastLine = data.get(data.size() - 1);
    read = readDataLastLine.toArray();
    assertArrayEquals(ref, read);
  }
    
    

  private void doAgTest(Parser parser) {
    AbstractSimulation simulation = new AgUcSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();

    simulatedSurface = simulation.getKmc().getSampledSurface((int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()));
    try {
      simulatedPsd = simulation.getPsd().getPsd();
    } catch (NullPointerException e) {
    }
    simulatedTime = simulation.getSimulatedTime();
  }

}
