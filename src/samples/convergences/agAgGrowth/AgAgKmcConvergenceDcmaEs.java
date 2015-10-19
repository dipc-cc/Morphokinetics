/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.convergences.agAgGrowth;

import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractPsdEvaluation;
import geneticAlgorithm.GeneticAlgorithmConfiguration;
import geneticAlgorithm.GeneticAlgorithmDcmaEs;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import geneticAlgorithm.geneticAlgorithmDatabase.GeneticAlgorithmConfigFactory;
import graphicInterfaces.gaConvergence.GaProgressFrame;
import ratesLibrary.AgAgRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgAgKmcConvergenceDcmaEs {

  public static void main(String[] args) {

    new StaticRandom();
    float experitentalTemp = 135;
    double depositionRate = new AgAgRatesFactory().getDepositionRate(experitentalTemp);
    double islandDensity = new AgAgRatesFactory().getIslandDensity(experitentalTemp);
    double diffusionRate = new AgAgRatesFactory().getRates(experitentalTemp)[0];

    GeneticAlgorithmConfiguration geneticConfiguration = new GeneticAlgorithmConfigFactory()
            .createAgAgDcmaEsConvergenceConfiguration(diffusionRate, islandDensity, depositionRate);

    GeneticAlgorithmDcmaEs ga = new GeneticAlgorithmDcmaEs(geneticConfiguration);

    new GaProgressFrame(ga).setVisible(true);
    AbstractPsdEvaluation evaluator = geneticConfiguration.getMainEvaluator();

    //--------------------------------
    evaluator.setRepeats(evaluator.getRepeats() * 5);
    Individual individual = new Individual(new AgAgRatesFactory().getRates(experitentalTemp));
    float[][] experimentalPsd = evaluator.calculatePsdFromIndividual(individual);
    double simulationTime = individual.getSimulationTime();
    evaluator.setRepeats(evaluator.getRepeats() / 5);
         //--------------------------------

    geneticConfiguration.setExperimentalPsd(experimentalPsd);
    geneticConfiguration.setExpectedSimulationTime(simulationTime);

    ga.initialize();
    ga.iterate(100);
    printResult(ga);

  }

  private static void printResult(IGeneticAlgorithm ga) {
    Individual individual = ga.getIndividual(0);
    System.out.print(individual.getTotalError() + " ");
    for (int gene = 0; gene < individual.getGeneSize(); gene++) {
      System.out.print(individual.getGene(gene) + " ");
    }
    System.out.println();
  }

}
