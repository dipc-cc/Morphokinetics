/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import graphicInterfaces.growth.KmcCanvas;
import graphicInterfaces.growth.GrowthKmcFrame;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import utils.list.ListConfiguration;
import ratesLibrary.AgRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class SimpleAgAgGrowthKmcSimulation {

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Ag/Ag growth KMC");

    AgRatesFactory ratesFactory = new AgRatesFactory();

    AgKmc kmc = initialiseKmc();

    GrowthKmcFrame frame = createGraphicsFrame(kmc);
    frame.setVisible(true);

    for (int simulations = 0; simulations < 10; simulations++) {
      initialiseRates(ratesFactory, kmc);
      kmc.simulate();
    }
  }

  private static GrowthKmcFrame createGraphicsFrame(AgKmc kmc) {
    GrowthKmcFrame frame = new GrowthKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()));
    return frame;
  }

  private static AgKmc initialiseKmc() {
    new StaticRandom();
    ListConfiguration config = new ListConfiguration()
            .setListType(ListConfiguration.LINEAR_LIST);

    int sizeX = 256;
    int sizeY = (int) (sizeX / AbstractGrowthLattice.Y_RATIO);

    AgKmc kmc = new AgKmc(config, (int) (sizeX * 1.71), (int) (sizeY * 1.71), true, (float) -1, false, RoundPerimeter.CIRCLE, false);

    return kmc;
  }

  private static void initialiseRates(AgRatesFactory reatesFactory, AgKmc kmc) {

    double depositionRatePerSite = reatesFactory.getDepositionRatePerSite();
    double islandDensity = reatesFactory.getIslandDensity(135);
    kmc.setDepositionRate(depositionRatePerSite, islandDensity);
    kmc.reset();
    kmc.initialiseRates(reatesFactory.getRates(135));
    kmc.depositSeed();
  }

}
