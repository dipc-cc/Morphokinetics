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
  
  private float[][] currentSurface;
  private float[][] currentPsd;
  private int currentIslandCount;

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
      assertArrayEquals(ref0[i], currentSurface[i], (float) 0.001);
    }
    assertEquals(currentIslandCount, 14);
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
      assertArrayEquals(ref0[i], currentSurface[i], (float) 0.001);
    }
    assertEquals(currentIslandCount, 1);
    
  }
  
  private void doAgTest(Parser parser) {
    AbstractSimulation simulation = new BasicGrowthSimulation(parser);

    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();

    currentSurface = simulation.getKmc().getSampledSurface((int) (parser.getCartSizeX() * parser.getPsdScale()), (int) (parser.getCartSizeY() * parser.getPsdScale()));
    if (parser.doPsd()) {
      currentPsd = simulation.getPsd().getPsd();
    }
    currentIslandCount = simulation.getKmc().getLattice().getIslandCount();
  }

}
