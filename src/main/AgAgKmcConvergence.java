/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import basic.AbstractSimulation;
import basic.Parser;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractPsdEvaluation;
import geneticAlgorithm.GeneticAlgorithm;
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
public class AgAgKmcConvergence {

  public static void main(String[] args) {

    AbstractSimulation.printHeader();
    Parser parser = new Parser();
    parser.readFile("parameters");
    parser.print();
    
    new StaticRandom();
    
    GeneticAlgorithmConfiguration geneticConfiguration;
    IGeneticAlgorithm ga;
    switch (parser.getEvolutionaryAlgorithm()) {
      case "original":
        geneticConfiguration = new GeneticAlgorithmConfigFactory()
                .createAgAgConvergenceConfiguration(parser);
        ga = new GeneticAlgorithm(geneticConfiguration);
        break;
      case "dcma":
        geneticConfiguration = new GeneticAlgorithmConfigFactory()
                .createAgAgDcmaEsConvergenceConfiguration(parser);
        ga = new GeneticAlgorithmDcmaEs(geneticConfiguration);
        break;
      default:
        System.err.println("Error: Default evolutionary algorithm. This evolutionary algorithm is not implemented!");
        System.err.println("Current value: "+parser.getEvolutionaryAlgorithm());
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }

    new GaProgressFrame(ga).setVisible(true);
    AbstractPsdEvaluation evaluator = geneticConfiguration.getMainEvaluator();

    //--------------------------------
    evaluator.setRepeats(evaluator.getRepeats() * 5);
    Individual individual = new Individual(new AgAgRatesFactory().getRates(parser.getTemperature()));
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
    System.out.println("These are the results: ");
    System.out.print(individual.getTotalError() + " ");
    for (int gene = 0; gene < individual.getGeneSize(); gene++) {
      System.out.print(individual.getGene(gene) + " ");
    }
    System.out.println();
  }

}
