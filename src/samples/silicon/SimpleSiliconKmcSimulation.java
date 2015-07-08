package samples.silicon;

import graphicInterfaces.siliconEtching.SiliconFrame;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC_config;
import Kinetic_Monte_Carlo.list.List_configuration;
import ratesLibrary.siEtching.SiEtchRatesFactory;

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

        Si_etching_KMC_config config = configKMC();

        Si_etching_KMC KMC = new Si_etching_KMC(config);

        long start = System.nanoTime();

        KMC.initializeRates(new SiEtchRatesFactory()
                .getRates("Gosalvez_PRE", 350));

        KMC.simulate();

        System.out.println((System.nanoTime() - start) / 1000000);

        new SiliconFrame().drawKMC(KMC);
    }

    private static Si_etching_KMC_config configKMC() {
        List_configuration listConfig = new List_configuration()
                .setList_type(List_configuration.BINNED_LIST)
                .setBins_per_level(20)
                .set_extra_levels(1);
        
        Si_etching_KMC_config config = new Si_etching_KMC_config()
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
