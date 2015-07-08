/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Samples.AgAg_growth;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth.AgAgBasicPsdEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth.AgAgGrowthThreadedPsdEvaluation;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth.Ag_Ag_KMC_config;
import Kinetic_Monte_Carlo.list.List_configuration;
import ratesLibrary.diffusion.agAgGrowth.AgAgGrowthRatesFactory;

;

/**
 *
 * @author Nestor
 */
public class AgAgMulthreadedEvaluatorTest {

    public static float constant_Y = (float) Math.sqrt(3) / 2.0f;

    public static void main(String[] args) {

        List_configuration listConfig = new List_configuration()
                .setList_type(List_configuration.LINEAR_LIST);

        float experitental_temp = 135;
        double deposition_rate = new AgAgGrowthRatesFactory().getDepositionRate("COX_PRB", experitental_temp);
        double island_density = new AgAgGrowthRatesFactory().getIslandDensity("COX_PRB", experitental_temp);

        Ag_Ag_KMC_config config = new Ag_Ag_KMC_config(256, (int) (256 / constant_Y), listConfig, deposition_rate, island_density);

        //Ag_ag_growth_Threaded_PSD_Evaluation evaluation = new AgAgGrowthThreadedPsdEvaluation(config, 20, Integer.MAX_VALUE, 4);
        AgAgBasicPsdEvaluation evaluation= new AgAgBasicPsdEvaluation(config, 20, Integer.MAX_VALUE);
        
        Individual individual = new Individual(new AgAgGrowthRatesFactory().getRates("COX_PRB", experitental_temp));
        float[][] experimentalPSD = evaluation.calculate_PSD_from_individual(individual);

        evaluation.setPSD(experimentalPSD);

        Individual newIndividual = new Individual(new AgAgGrowthRatesFactory().getRates("COX_PRB", 125));
        Population population = new Population(1);
        population.setIndividual(newIndividual, 0);
        double[] populationErrors = evaluation.evaluate(population);

        System.out.println(populationErrors[0]);

    }

}
