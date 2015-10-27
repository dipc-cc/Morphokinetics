/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import geneticAlgorithm.geneticOperators.evaluationFunctions.AgBasicPsdEvaluator;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import kineticMonteCarlo.kmcCore.diffusion.AgKmc;
import kineticMonteCarlo.lattice.AgAgLattice;
import utils.list.ListConfiguration;
import ratesLibrary.AgAgRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgAgMultithreadedEvaluatorTest {

  public static void main(String[] args) {

    float experimentalTemp = 135;

    //AgAgGrowthThreadedPsdEvaluation evaluation = new AgAgGrowthThreadedPsdEvaluation(config, 20, Integer.MAX_VALUE, 4);
    AgBasicPsdEvaluator evaluation = new AgBasicPsdEvaluator(localAgAgKmc(experimentalTemp), 20, Integer.MAX_VALUE, 128, 128);

    Individual individual = new Individual(new AgAgRatesFactory().getRates(experimentalTemp));
    float[][] experimentalPsd = evaluation.calculatePsdFromIndividual(individual);

    evaluation.setPsd(experimentalPsd);

    Individual newIndividual = new Individual(new AgAgRatesFactory().getRates(125));
    Population population = new Population(1);
    population.setIndividual(newIndividual, 0);
    double[] populationErrors = evaluation.evaluate(population);

    System.out.println(populationErrors[0]);

  }
  
  private static AgKmc localAgAgKmc(float experimentalTemp) {

    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration().setListType(ListConfiguration.LINEAR_LIST);
    double depositionRate = new AgAgRatesFactory().getDepositionRate(experimentalTemp);
    double islandDensity = new AgAgRatesFactory().getIslandDensity(experimentalTemp);

    return new AgKmc(listConfig, 256, (int) (256 / AgAgLattice.YRatio), depositionRate, islandDensity);

  }

}
