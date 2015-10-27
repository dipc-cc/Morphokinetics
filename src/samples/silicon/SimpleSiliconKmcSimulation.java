package samples.silicon;

import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import kineticMonteCarlo.kmcCore.etching.SiKmcConfig;
import utils.list.ListConfiguration;
import ratesLibrary.SiRatesFactory;
import utils.StaticRandom;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nestor
 */
public class SimpleSiliconKmcSimulation {

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Silicon etching KMC");

    SiKmcConfig config = configKmc();

    SiKmc kmc = new SiKmc(config);

    long start = System.nanoTime();
    kmc.reset();
    kmc.initialiseRates(new SiRatesFactory().getRates(350));
    kmc.depositSeed();
    kmc.simulate();

    System.out.println((System.nanoTime() - start) / 1000000);

    new SiFrame().drawKmc(kmc);
  }

  private static SiKmcConfig configKmc() {
    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(20)
            .setExtraLevels(1);

    SiKmcConfig config = new SiKmcConfig()
            .setMillerX(0)
            .setMillerY(1)
            .setMillerZ(1)
            .setSizeX_UC(96)
            .setSizeY_UC(96)
            .setSizeZ_UC(16)
            .setListConfig(listConfig);
    return config;
  }

}
