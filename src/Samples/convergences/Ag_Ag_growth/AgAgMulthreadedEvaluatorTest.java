/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Samples.convergences.Ag_Ag_growth;

import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.PSD_Evaluator.Ag_ag_growth.Ag_ag_growth_Threaded_PSD_Evaluation;
import Genetic_Algorithm.Individual;
import Genetic_Algorithm.Population;
import Kinetic_Monte_Carlo.KMC_core.diffusion.Ag_Ag_Growth.Ag_Ag_KMC_config;
import Kinetic_Monte_Carlo.list.List_configuration;
import Rates_library.diffusion.Ag_Ag_Growth.Ag_Ag_growth_rates_factory;

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
        double deposition_rate = new Ag_Ag_growth_rates_factory().getDepositionRate("COX_PRB", experitental_temp);
        double island_density = new Ag_Ag_growth_rates_factory().getIslandDensity("COX_PRB", experitental_temp);

        Ag_Ag_KMC_config config = new Ag_Ag_KMC_config(256, (int) (256 / constant_Y), listConfig, deposition_rate, island_density);

        Ag_ag_growth_Threaded_PSD_Evaluation evaluation = new Ag_ag_growth_Threaded_PSD_Evaluation(config, 20, Integer.MAX_VALUE, 4);

        Individual individual = new Individual(new Ag_Ag_growth_rates_factory().getRates("COX_PRB", experitental_temp));
        float[][] experimentalPSD = evaluation.calculate_PSD_from_individual(individual);

        evaluation.setPSD(experimentalPSD);

        Individual newIndividual = new Individual(new Ag_Ag_growth_rates_factory().getRates("COX_PRB", 125));
        Population population = new Population(1);
        population.setIndividual(newIndividual, 0);
        double[] populationErrors = evaluation.evaluate(population);

        System.out.println(populationErrors[0]);

    }

}
