/* 
 * Copyright (C) 2018 N. Ferrando
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
package samples.agAgGrowth;

import basic.Parser;
import graphicInterfaces.growth.GrowthKmcFrame;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import ratesLibrary.AgRatesFromPrbCox;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando
 */
public class SimpleAgAgGrowthKmcSimulation {
  private static GrowthKmcFrame  frame;
  private static paintLoop p;

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Ag/Ag growth KMC");

    AgRatesFromPrbCox ratesFactory = new AgRatesFromPrbCox();

    AgKmc kmc = initialiseKmc();

    createGraphicsFrame(kmc);

    for (int simulations = 0; simulations < 10; simulations++) {
      initialiseRates(ratesFactory, kmc);
      kmc.simulate();
    }
  }

  private static void createGraphicsFrame(AgKmc kmc) {
    frame = new GrowthKmcFrame(kmc.getLattice(), kmc.getPerimeter(), 1);
    frame.setVisible(true);
    p = new paintLoop();
    p.start();
  }

  private static AgKmc initialiseKmc() {
    new StaticRandom();

    int sizeX = 256;
    int sizeY = (int) (sizeX / AbstractGrowthLattice.Y_RATIO);
    Parser parser = new Parser();
    parser.setCartSizeX((int) (sizeX * 1.71));
    parser.setCartSizeY((int) (sizeY * 1.71));
    parser.setListType("linear");

    AgKmc kmc = new AgKmc(parser);

    return kmc;
  }

  private static void initialiseRates(AgRatesFromPrbCox rates, AgKmc kmc) {

    double depositionRatePerSite = rates.getDepositionRatePerSite();
    double islandDensity = rates.getIslandDensity(135);
    kmc.setDepositionRate(depositionRatePerSite, islandDensity);
    kmc.reset();
    kmc.initialiseRates(rates.getRates(135));
    kmc.depositSeed();
  }
  
   /**
   * Private class responsible to repaint every 100 ms the KMC frame.
   */
  static final class paintLoop extends Thread {

    @Override
    public void run() {
      while (true) {
        frame.repaintKmc();
        try {
          paintLoop.sleep(250);
        } catch (Exception e) {
        }
      }
    }
  }

}
