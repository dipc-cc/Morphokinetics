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
public class SiliconPSDDifferencesBetweenTemperatures {

    public static void main(String args[]) {

          System.out.println("Showing PSD differences between two temperatures ");
        
            Si_etching_KMC_config config = configKMC();
        
        Si_etching_KMC KMC = new Si_etching_KMC(config);

        float[][] PSD300_1 = 
                getPSDfromSimulation(KMC, 300);
        float[][] PSD300_2 = 
                getPSDfromSimulation(KMC, 300);
        float[][] PSD400_1 = 
                getPSDfromSimulation(KMC, 400);

        float[][] Relative_error_1 = 
                new float[PSD300_1.length][PSD300_1[0].length];
        float[][] Relative_error_2 = 
                new float[PSD300_1.length][PSD300_1[0].length];

        for (int i = 0; i < PSD300_1.length; i++) {
            for (int j = 0; j < PSD300_1[0].length; j++) {
                Relative_error_1[i][j] = ((PSD400_1[i][j] - PSD300_1[i][j]) / PSD300_1[i][j]);
                Relative_error_2[i][j] = ((PSD300_2[i][j] - PSD300_1[i][j]) / PSD300_1[i][j]);
            }
        }

        Frame2D frame = new Frame2D("Relative difference between PSDs 400K vs 300K")
            .setLogScale(false)
            .setShift(true)
            .setMesh(MathUtils.avg_Filter(Relative_error_1, 1));
        
        frame.setLocation(frame.getWidth(), 0);

        if (Math.abs(frame.getMax()) > Math.abs(frame.getMin())) {
            frame.setMin(-frame.getMax());
        } else {
            frame.setMax(-frame.getMin());
        }

        new Frame2D("Relative difference between PSDs 300K vs 300K")
            .setLogScale(false)
            .setShift(true)
            .setMax(frame.getMax())
            .setMin(frame.getMin())
            .setMesh(MathUtils.avg_Filter(Relative_error_2, 1));
    }

    private static float[][] getPSDfromSimulation(Si_etching_KMC KMC, int temperature) {

        PSD_signature_2D PSD = new PSD_signature_2D(KMC.getLattice().getSizeY()*2 , KMC.getLattice().getSizeX()*2 );
        float[][] surface = new float[KMC.getLattice().getSizeY()*2][KMC.getLattice().getSizeX()*2 ];

        for (int a = 0; a < 30; a++) {

            KMC.initializeRates(
                    new Si_etch_rates_factory()
                    .getRates("Gosalvez_PRE", temperature));

            KMC.simulate(5000);
            for (int i = 0; i < 10; i++) {
                KMC.simulate(10000);
                KMC.getSampledSurface(surface);
                PSD.addSurfaceSample(surface);
            }
        }

        PSD.apply_simmetry_fold(PSD_signature_2D.HORIZONTAL_SIMMETRY);
        PSD.apply_simmetry_fold(PSD_signature_2D.VERTICAL_SIMMETRY);

        return PSD.getPSD();
    }

    private static Si_etching_KMC_config configKMC() {
        List_configuration listConfig=  new List_configuration()
           .setList_type(List_configuration.BINNED_LIST)
           .setBins_per_level(100)
           .set_extra_levels(0);
        Si_etching_KMC_config config = new Si_etching_KMC_config()
                                    .setMillerX(1)
                                    .setMillerY(0)
                                    .setMillerZ(0)
                                    .setSizeX_UC(48)
                                    .setSizeY_UC(48)
                                    .setSizeZ_UC(16)
                                    .setListConfig(listConfig);
        return config;
    }
}
