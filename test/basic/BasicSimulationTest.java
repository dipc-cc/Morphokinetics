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

    doAgTest(parser);
    
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
    assertEquals(simulatedIslands, 12);
    assertEquals(28.7753268671013, simulatedTime, 0.0);
  }
  
  @Test
  public void testBasic180() {
    AbstractSimulation.printHeader("Basic 180 test");
    Parser parser = new Parser();
    parser.readFile(TestHelper.getBaseDir() + "/test/input/Basic180Parameters");
    parser.print();

    doAgTest(parser);
    
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
    assertEquals(30.39123056743884, simulatedTime, 0.0);
  }
  
  private void doAgTest(Parser parser) {
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
