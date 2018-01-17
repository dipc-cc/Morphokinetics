/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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
package utils;

import graphicInterfaces.growth.GrowthKmcFrame;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.lattice.AgUcLattice;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class printByHandHelper {

  private final GrowthKmcFrame frame;
  private final paintLoop p;

  public printByHandHelper() {

    ModifiedBuffer modified = new ModifiedBuffer();
    AgUcLattice lattice = new AgUcLattice(30, 13, modified, null, 1);
    lattice.init();
    frame = new GrowthKmcFrame(lattice, null, 1);
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
        frame.repaintKmc();
        try {
          paintLoop.sleep(100);
        } catch (Exception e) {
        }
      }
    }
  }
}
