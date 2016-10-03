/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import graphicInterfaces.growth.GrowthKmcFrame;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.lattice.AgUcLattice;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class printByHandHelper {

  private final paintLoop p;

  public printByHandHelper() {

    ModifiedBuffer modified = new ModifiedBuffer();
    AgUcLattice lattice = new AgUcLattice(30, 13, modified, null);
    lattice.init();
    frame = new GrowthKmcFrame(lattice, 1);
    frame.setVisible(true);
    p = new paintLoop();
  }

  public void init() {
    p.start();
  }
  /**
   * Private class responsible to repaint every 100 ms the KMC frame.
   */
  final class paintLoop extends Thread {

    @Override
    public void run() {
      while (true) {
        try {
          paintLoop.sleep(100);
        } catch (Exception e) {
        }
      }
    }
  }
}
