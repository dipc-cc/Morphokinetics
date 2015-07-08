/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.silicon;

import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPSDEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.siEtching.SiEtchingThreadedPsdEvaluation;
import geneticAlgorithm.Individual;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmcConfig;
import kineticMonteCarlo.list.ListConfiguration;
import ratesLibrary.siEtching.SiEtchRatesFactory;

/**
 *
 * @author Nestor
 */
public class SiliconMultithreadedPsdCalculation {

    public static void main(String[] args) {
        
        System.out.println("Multithreaded PSD calculation from a KMC configuration");
        
        SiEtchingKmcConfig config = configKMC();
        
        AbstractEvaluation evaluation = new SiEtchingThreadedPsdEvaluation(config, 20, 10000, 4);
                evaluation.setWheight(1.0f);
                evaluation.setShowGraphics(false);
        
        float[][] PSD = ((AbstractPSDEvaluation)evaluation).calculate_PSD_from_individual(new Individual(
                new SiEtchRatesFactory().getRates("Gosalvez_PRE", 350)));
        
        evaluation.dispose();
     

        
        new Frame2D("Multi-threaded calculated PSD")
                .setLogScale(true)
                .setShift(true)
                .setMesh(PSD);
    }

    private static SiEtchingKmcConfig configKMC() {
        ListConfiguration listConfig=  new ListConfiguration()
              .setList_type(ListConfiguration.BINNED_LIST)
              .setBins_per_level(12)
              .set_extra_levels(1);
        
        SiEtchingKmcConfig config = new SiEtchingKmcConfig()
                .setMillerX(1)
                .setMillerY(0)
                .setMillerZ(0)
                .setSizeX_UC(32)
                .setSizeY_UC(32)
                .setSizeZ_UC(64)
                .setListConfig(listConfig);
        return config;
    }
}
