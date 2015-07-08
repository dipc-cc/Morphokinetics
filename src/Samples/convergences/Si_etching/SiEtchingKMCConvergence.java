/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Samples.convergences.Si_etching;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPSDEvaluation;
import geneticAlgorithm.GeneticAlgorithm;
import geneticAlgorithm.GeneticAlgorithmConfiguration;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import geneticAlgorithm.geneticAlgorithmDatabase.genetic_algorithm_config_factory;
import Graphic_interfaces.GA_convergence.GA_progress_frame;
import Rates_library.Si_etching.Si_etch_rates_factory;

/**
 *
 * @author Nestor
 */
public class SiEtchingKMCConvergence {

    private final int totalConvergences = 15;

    public static void main(String[] args) {

        System.out.println("Recovering Si etching KMC rates by using the KMC");
        new SiEtchingKMCConvergence().performConvergence();

    }

    public void performConvergence() {


        GeneticAlgorithmConfiguration geneticConfiguration = new genetic_algorithm_config_factory()
                .create_silicon_convergence_configuration();
        GeneticAlgorithm GA = new GeneticAlgorithm(geneticConfiguration);
        new GA_progress_frame(GA).setVisible(true);
        AbstractPSDEvaluation evaluator=geneticConfiguration.mainEvaluator;
        
        for (int i = 0; i < totalConvergences; i++) {

          evaluator.setRepeats(evaluator.getRepeats()*20);
          Individual individual=new Individual(new Si_etch_rates_factory().getRates("Gosalvez_PRE", 340));
          float[][] experimentalPSD=evaluator.calculate_PSD_from_individual(individual);
          double simulationTime=individual.getSimulationTime();
          evaluator.setRepeats(evaluator.getRepeats()/20);
           
          geneticConfiguration.setExperimentalPSD(experimentalPSD);
          geneticConfiguration.expected_simulation_time=simulationTime;
          
            GA.initialize();
            GA.iterate(100);
            printResult(GA);
        }
    }

    private void printResult(IGeneticAlgorithm GA) {
        Individual individual = GA.getIndividual(0);
        System.out.print(individual.getTotalError() + " ");
        for (int gene = 0; gene < individual.getGeneSize(); gene++) {
            System.out.print(individual.getGene(gene) + " ");
        }
        System.out.println();
    }
}
