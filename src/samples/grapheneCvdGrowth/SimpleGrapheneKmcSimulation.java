/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.grapheneCvdGrowth;

import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import graphicInterfaces.diffusion2DGrowth.KmcCanvas;
import kineticMonteCarlo.kmcCore.diffusion.GrapheneKmc;
import kineticMonteCarlo.kmcCore.diffusion.RoundPerimeter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import utils.list.ListConfiguration;
import ratesLibrary.GrapheneRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class SimpleGrapheneKmcSimulation {

  private static final double cos30 = Math.cos(30 * Math.PI / 180);

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Graphene KMC");

    GrapheneRatesFactory ratesFactory = new GrapheneRatesFactory();
    GrapheneKmc kmc = initialize_kmc();
    DiffusionKmcFrame frame = create_graphics_frame(kmc);

    frame.setVisible(true);
    for (int i = 0; i < 10; i++) {
      initializeRates(ratesFactory, kmc);
      kmc.simulate();
    }

    float[][] surface = kmc.getSampledSurface(256, 256);

  }

  private static DiffusionKmcFrame create_graphics_frame(GrapheneKmc kmc) {
    DiffusionKmcFrame frame = new DiffusionKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()));
    return frame;
  }

  private static GrapheneKmc initialize_kmc() {

    new StaticRandom();
    ListConfiguration config = new ListConfiguration()
            .setListType(ListConfiguration.LINEAR_LIST);

    int sizeX = 256;
    int sizeY = (int) (sizeX * (2 * cos30));
    if ((sizeY & 1) != 0) {
      sizeY++;
    }
    GrapheneKmc kmc = new GrapheneKmc(config, sizeX, sizeY, false, 0.3f, false, RoundPerimeter.CIRCLE);
    return kmc;
  }

  private static void initializeRates(GrapheneRatesFactory reatesFactory, GrapheneKmc kmc) {

    double deposition_rate = reatesFactory.getDepositionRate(0);
    double island_density = reatesFactory.getIslandDensity(0);
    kmc.setIslandDensityAndDepositionRate(deposition_rate, island_density);
    kmc.reset();
    kmc.initialiseRates(reatesFactory.getRates(0));
    kmc.depositSeed(); //might not be needed, it is a multiflake simulation

  }
}
