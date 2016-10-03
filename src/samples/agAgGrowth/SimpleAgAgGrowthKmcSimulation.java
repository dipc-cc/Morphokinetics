/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Nestor
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
    frame = new GrowthKmcFrame(kmc.getLattice(), 1);
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
