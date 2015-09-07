package samples.silicon;

import graphicInterfaces.siliconEtching.SiliconFrame;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
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

    SiEtchingKmcConfig config = configKmc();

    SiEtchingKmc kmc = new SiEtchingKmc(config);

    long start = System.nanoTime();

    kmc.initializeRates(new SiRatesFactory().getRates(350));

    kmc.simulate();

    System.out.println((System.nanoTime() - start) / 1000000);

    new SiliconFrame().drawKmc(kmc);
  }

  private static SiEtchingKmcConfig configKmc() {
    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(20)
            .setExtraLevels(1);

    SiEtchingKmcConfig config = new SiEtchingKmcConfig()
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
