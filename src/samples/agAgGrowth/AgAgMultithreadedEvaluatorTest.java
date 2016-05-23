/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.AgBasicPsdEvaluator;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import ratesLibrary.AgRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgAgMultithreadedEvaluatorTest {

  public static void main(String[] args) {

    float experimentalTemp = 135;

    //AgAgGrowthThreadedPsdEvaluation evaluation = new AgAgGrowthThreadedPsdEvaluation(config, 20, Integer.MAX_VALUE, 4);
    AgBasicPsdEvaluator evaluation = new AgBasicPsdEvaluator(localAgAgKmc(experimentalTemp), 20, Integer.MAX_VALUE, 128, 128, null, null, null, 135);

    Individual individual = new Individual(new AgRatesFactory().getRates(experimentalTemp));
    float[][] experimentalPsd = evaluation.calculatePsdFromIndividual(individual);

    evaluation.setPsd(experimentalPsd);

    Individual newIndividual = new Individual(new AgRatesFactory().getRates(125));
    Population population = new Population(1);
    population.setIndividual(newIndividual, 0);
    double[] populationErrors = evaluation.evaluate(population);

    System.out.println(populationErrors[0]);

  }
  
  private static AgKmc localAgAgKmc(float experimentalTemp) {
    new StaticRandom();
    double depositionRatePerSite = new AgRatesFactory().getDepositionRatePerSite();
    double islandDensity = new AgRatesFactory().getIslandDensity(experimentalTemp);
    
    Parser parser = new Parser();
    parser.setCartSizeX(256);
    parser.setCartSizeY((int) (256 / AbstractGrowthLattice.Y_RATIO));

    AgKmc kmc = new AgKmc(parser);
    kmc.setDepositionRate(depositionRatePerSite, islandDensity);
    return kmc;
  }

}
