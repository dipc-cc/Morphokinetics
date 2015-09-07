/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.convergences.siEtching;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.GeneticAlgorithm;
import geneticAlgorithm.GeneticAlgorithmConfiguration;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import geneticAlgorithm.geneticAlgorithmDatabase.GeneticAlgorithmConfigFactory;
import graphicInterfaces.gaConvergence.GaProgressFrame;
import ratesLibrary.SiRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class SiEtchingKmcConvergence {

  private final int totalConvergences = 15;

  public static void main(String[] args) {

    System.out.println("Recovering Si etching KMC rates by using the KMC");
    new SiEtchingKmcConvergence().performConvergence();

  }

  public void performConvergence() {

    new StaticRandom();
    GeneticAlgorithmConfiguration geneticConfiguration = new GeneticAlgorithmConfigFactory()
            .create_silicon_convergence_configuration();
    GeneticAlgorithm GA = new GeneticAlgorithm(geneticConfiguration);
    new GaProgressFrame(GA).setVisible(true);
    AbstractPsdEvaluation evaluator = geneticConfiguration.mainEvaluator;

    for (int i = 0; i < totalConvergences; i++) {

      evaluator.setRepeats(evaluator.getRepeats() * 20);
      Individual individual = new Individual(new SiRatesFactory().getRates(340));
      float[][] experimentalPSD = evaluator.calculatePsdFromIndividual(individual);
      double simulationTime = individual.getSimulationTime();
      evaluator.setRepeats(evaluator.getRepeats() / 20);

      geneticConfiguration.setExperimentalPSD(experimentalPSD);
      geneticConfiguration.expected_simulation_time = simulationTime;

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
