/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import graphicInterfaces.growth.KmcCanvas;
import graphicInterfaces.growth.DiffusionKmcFrame;
import kineticMonteCarlo.kmcCore.diffusion.AgKmc;
import kineticMonteCarlo.kmcCore.diffusion.RoundPerimeter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import kineticMonteCarlo.lattice.AgLattice;
import utils.list.ListConfiguration;
import ratesLibrary.AgAgRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class SimpleAgAgGrowthKmcSimulation {

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Ag/Ag growth KMC");

    AgAgRatesFactory ratesFactory = new AgAgRatesFactory();

    AgKmc kmc = initialize_kmc();

    DiffusionKmcFrame frame = create_graphics_frame(kmc);
    frame.setVisible(true);

    for (int simulations = 0; simulations < 10; simulations++) {
      initializeRates(ratesFactory, kmc);
      kmc.simulate();
    }
  }

  private static DiffusionKmcFrame create_graphics_frame(AgKmc kmc) {
    DiffusionKmcFrame frame = new DiffusionKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()));
    return frame;
  }

  private static AgKmc initialize_kmc() {
    new StaticRandom();
    ListConfiguration config = new ListConfiguration()
            .setListType(ListConfiguration.LINEAR_LIST);

    int sizeX = 256;
    int sizeY = (int) (sizeX / AgLattice.YRatio);

    AgKmc kmc = new AgKmc(config, (int) (sizeX * 1.71), (int) (sizeY * 1.71), true, (float) -1, false, RoundPerimeter.CIRCLE);

    return kmc;
  }

  private static void initializeRates(AgAgRatesFactory reatesFactory, AgKmc kmc) {

    double deposition_rate = reatesFactory.getDepositionRate(135);
    double island_density = reatesFactory.getIslandDensity(135);
    kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
    kmc.reset();
    kmc.initialiseRates(reatesFactory.getRates(135));
    kmc.depositSeed();
  }

}
