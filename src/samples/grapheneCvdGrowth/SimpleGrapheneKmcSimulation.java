/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.grapheneCvdGrowth;

import graphicInterfaces.growth.GrowthKmcFrame;
import graphicInterfaces.growth.KmcCanvas;
import kineticMonteCarlo.kmcCore.growth.GrapheneKmc;
import kineticMonteCarlo.kmcCore.growth.RoundPerimeter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import utils.list.ListConfiguration;
import ratesLibrary.GrapheneRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class SimpleGrapheneKmcSimulation {

  private static final double COS30 = Math.cos(30 * Math.PI / 180);

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Graphene KMC");

    GrapheneRatesFactory ratesFactory = new GrapheneRatesFactory();
    GrapheneKmc kmc = initialize_kmc();
    GrowthKmcFrame frame = create_graphics_frame(kmc);

    frame.setVisible(true);
    for (int i = 0; i < 10; i++) {
      initializeRates(ratesFactory, kmc);
      kmc.simulate();
    }

    float[][] surface = kmc.getSampledSurface(256, 256);

  }

  private static GrowthKmcFrame create_graphics_frame(GrapheneKmc kmc) {
    GrowthKmcFrame frame = new GrowthKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()));
    return frame;
  }

  private static GrapheneKmc initialize_kmc() {

    new StaticRandom();
    ListConfiguration config = new ListConfiguration()
            .setListType(ListConfiguration.LINEAR_LIST);

    int sizeX = 256;
    int sizeY = (int) (sizeX * (2 * COS30));
    if ((sizeY & 1) != 0) {
      sizeY++;
    }
    GrapheneKmc kmc = new GrapheneKmc(config, sizeX, sizeY, true, false, 0.3f, false, RoundPerimeter.CIRCLE, false);
    return kmc;
  }

  private static void initializeRates(GrapheneRatesFactory ratesFactory, GrapheneKmc kmc) {

    double depositionRatePerSite = ratesFactory.getDepositionRatePerSite();
    double islandDensity = ratesFactory.getIslandDensity(0);
    kmc.setDepositionRate(depositionRatePerSite, islandDensity);
    kmc.reset();
    kmc.initialiseRates(ratesFactory.getRates(0));
    kmc.depositSeed(); //might not be needed, it is a multiflake simulation

  }
}
