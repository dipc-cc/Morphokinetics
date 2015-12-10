/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.convergences.siEtching;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.GeneticAlgorithm;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
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
    Parser parser = new Parser();
    parser.setCalculationMode("Si");
    
    GeneticAlgorithm ga = new GeneticAlgorithm(parser);
    new GaProgressFrame(ga).setVisible(true);
    AbstractPsdEvaluator evaluator = ga.getMainEvaluator();

    for (int i = 0; i < totalConvergences; i++) {

      evaluator.setRepeats(evaluator.getRepeats() * 20);
      Individual individual = new Individual(new SiRatesFactory().getRates(340));
      float[][] experimentalPSD = evaluator.calculatePsdFromIndividual(individual);
      double simulationTime = individual.getSimulationTime();
      evaluator.setRepeats(evaluator.getRepeats() / 20);

      ga.setExperimentalPsd(experimentalPSD);
      ga.setExpectedSimulationTime(simulationTime);

      ga.initialise();
      ga.iterate();
      printResult(ga);
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
