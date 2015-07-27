/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.agAgGrowth;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth.AgAgBasicPsdEvaluation;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmcConfig;
import kineticMonteCarlo.lattice.AgAgLattice;
import utils.list.ListConfiguration;
import ratesLibrary.AgAgRatesFactory;

;

/**
 *
 * @author Nestor
 */
public class AgAgMultithreadedEvaluatorTest {

    public static void main(String[] args) {

        ListConfiguration listConfig = new ListConfiguration().setListType(ListConfiguration.LINEAR_LIST);

        float experitental_temp = 135;
        double deposition_rate = new AgAgRatesFactory().getDepositionRate(experitental_temp);
        double island_density = new AgAgRatesFactory().getIslandDensity(experitental_temp);

        AgAgKmcConfig config = new AgAgKmcConfig(256, (int) (256 / AgAgLattice.YRatio), listConfig, deposition_rate, island_density);

        //AgAgGrowthThreadedPsdEvaluation evaluation = new AgAgGrowthThreadedPsdEvaluation(config, 20, Integer.MAX_VALUE, 4);
        AgAgBasicPsdEvaluation evaluation= new AgAgBasicPsdEvaluation(config, 20, Integer.MAX_VALUE);
        
        Individual individual = new Individual(new AgAgRatesFactory().getRates(experitental_temp));
        float[][] experimentalPSD = evaluation.calculatePsdFromIndividual(individual);

        evaluation.setPsd(experimentalPSD);

        Individual newIndividual = new Individual(new AgAgRatesFactory().getRates(125));
        Population population = new Population(1);
        population.setIndividual(newIndividual, 0);
        double[] populationErrors = evaluation.evaluate(population);

        System.out.println(populationErrors[0]);

    }

}
