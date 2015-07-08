package Samples.silicon;

import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC;
import Rates_library.Si_etching.Si_etch_rates_factory;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import Kinetic_Monte_Carlo.KMC_core.etching.Si_etching.Si_etching_KMC_config;
import Kinetic_Monte_Carlo.list.List_configuration;
import utils.MathUtils;
import utils.PSD_analysis.PSD_signature_2D;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nestor
 */
public class SimpleSiliconKMCFFTAnalisys {

    public static void main(String args[]) {

        System.out.println("Simple 2D FFT analisys of an etched silicon surface");
        
            Si_etching_KMC_config config = configKMC();
        
        Si_etching_KMC KMC = new Si_etching_KMC(config);

        KMC.initializeRates(
                new Si_etch_rates_factory()
                .getRates("Gosalvez_PRE", 350));

        float[][] surface = new float[128][128];
        PSD_signature_2D PSD = new PSD_signature_2D(128, 128);

        KMC.simulate(5000);
        for (int i = 0; i < 100; i++) {
            KMC.simulate(5000);
            KMC.getSampledSurface(surface);
            PSD.addSurfaceSample(surface);
        }

        PSD.apply_simmetry_fold(PSD_signature_2D.HORIZONTAL_SIMMETRY);
        PSD.apply_simmetry_fold(PSD_signature_2D.VERTICAL_SIMMETRY);

         new Frame2D("PSD analysis")
                 .setMesh(MathUtils.avg_Filter(PSD.getPSD(),1));
    }

    private static Si_etching_KMC_config configKMC() {
        List_configuration listConfig=  new List_configuration()
          .setList_type(List_configuration.BINNED_LIST)
          .setBins_per_level(16)
          .set_extra_levels(0);
        Si_etching_KMC_config config = new Si_etching_KMC_config()
                                    .setMillerX(1)
                                    .setMillerY(1)
                                    .setMillerZ(0)
                                    .setSizeX_UC(64)
                                    .setSizeY_UC(64)
                                    .setSizeZ_UC(256)
                                    .setListConfig(listConfig);
        return config;
    }
}
