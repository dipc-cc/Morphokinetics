/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.silicon;

import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractEvaluator;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.geneticOperators.evaluationFunctions.SiThreadedPsdEvaluator;
import geneticAlgorithm.Individual;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
import utils.list.ListConfiguration;
import ratesLibrary.SiRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class SiliconMultithreadedPsdCalculation {

  public static void main(String[] args) {

    System.out.println("Multithreaded PSD calculation from a KMC configuration");

    SiEtchingKmcConfig config = configKmc();

    AbstractEvaluator evaluation = new SiThreadedPsdEvaluator(config, 20, 10000, 4);
    evaluation.setWheight(1.0f);
    evaluation.setShowGraphics(false);

    float[][] psd = ((AbstractPsdEvaluator) evaluation).calculatePsdFromIndividual(new Individual(
            new SiRatesFactory().getRates(350)));

    evaluation.dispose();

    new Frame2D("Multi-threaded calculated PSD")
            .setLogScale(true)
            .setShift(true)
            .setMesh(psd);
  }

  private static SiEtchingKmcConfig configKmc() {
    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(12)
            .setExtraLevels(1);

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
