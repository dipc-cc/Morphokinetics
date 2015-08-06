/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import graphicInterfaces.diffusion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmc;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import kineticMonteCarlo.lattice.AgAgLattice;
import utils.list.ListConfiguration;
import ratesLibrary.AgAgRatesFactory;

/**
 *
 * @author Nestor
 */
public class SimpleAgAgGrowthKmcSimulation {

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Ag/Ag growth KMC");

    AgAgRatesFactory ratesFactory = new AgAgRatesFactory();

    AgAgKmc kmc = initialize_kmc();

    DiffusionKmcFrame frame = create_graphics_frame(kmc);
    frame.setVisible(true);

    for (int simulations = 0; simulations < 10; simulations++) {
      initializeRates(ratesFactory, kmc);
      kmc.simulate();
    }
  }

  private static DiffusionKmcFrame create_graphics_frame(AgAgKmc kmc) {
    DiffusionKmcFrame frame = new DiffusionKmcFrame(new AgAgKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
    return frame;
  }

  private static AgAgKmc initialize_kmc() {
    ListConfiguration config = new ListConfiguration()
            .setListType(ListConfiguration.LINEAR_LIST);

    int sizeX = 256;
    int sizeY = (int) (sizeX / AgAgLattice.YRatio);

    AgAgKmc kmc = new AgAgKmc(config, (int) (sizeX * 1.71), (int) (sizeY * 1.71), true, true, false);

    return kmc;
  }

  private static void initializeRates(AgAgRatesFactory reatesFactory, AgAgKmc kmc) {

    double deposition_rate = reatesFactory.getDepositionRate(135);
    double island_density = reatesFactory.getIslandDensity(135);
    kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
    kmc.initializeRates(reatesFactory.getRates(135));

  }

}
