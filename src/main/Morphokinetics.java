/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import basic.AbstractSimulation;
import basic.AgSimulation;
import basic.GrapheneSimulation;
import basic.Parser;
import basic.SiSimulation;
import geneticAlgorithm.GeneticAlgorithm;
import geneticAlgorithm.GeneticAlgorithmConfiguration;
import geneticAlgorithm.GeneticAlgorithmDcmaEs;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import geneticAlgorithm.geneticAlgorithmDatabase.GeneticAlgorithmConfigFactory;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractPsdEvaluator;
import graphicInterfaces.gaConvergence.GaProgressFrame;
import graphicInterfaces.surfaceViewer2D.Frame2D;
import ratesLibrary.AgAgRatesFactory;
import utils.MathUtils;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Morphokinetics {

  public static void main(String[] args) {
    AbstractSimulation.printHeader();

    Parser parser = new Parser();
    parser.readFile("parameters");
    parser.print();

    switch (parser.getCalculationType()) {
      case "batch":
        batchSimulation(parser);
        break;
      case "evolutionary":
        evoluationarySimulation(parser);
        break;
      default:
        System.err.println("Error: Default case calculation type. This simulation mode is not implemented!");
        System.err.println("Current value: " + parser.getCalculationType() + ". Possible values are batch or evolutionary");
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    System.out.println("Execution has finished");
    if (!parser.withGui() || !parser.visualize()) {
      System.exit(0);
    }
  }

  private static void batchSimulation(Parser parser) {
    AbstractSimulation simulation = null;
    switch (parser.getCalculationMode()) {
      case "Ag":
        simulation = new AgSimulation(parser);
        break;
      case "graphene":
        simulation = new GrapheneSimulation(parser);
        break;
      case "Si":
        simulation = new SiSimulation(parser);
        break;
      default:
        System.err.println("Error: Default case calculation mode. This simulation mode is not implemented!");
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }
    simulation.initialiseKmc();
    simulation.createFrame();
    simulation.doSimulation();
    simulation.finishSimulation();
  }

  private static void evoluationarySimulation(Parser parser) {
    GeneticAlgorithmConfiguration geneticConfiguration;
    IGeneticAlgorithm ga;
    switch (parser.getEvolutionaryAlgorithm()) {
      case "original":
        geneticConfiguration = new GeneticAlgorithmConfigFactory(parser)
                .createAgAgConvergenceConfiguration();
        ga = new GeneticAlgorithm(geneticConfiguration);
        break;
      case "dcma":
        geneticConfiguration = new GeneticAlgorithmConfigFactory(parser)
                .createAgAgDcmaEsConvergenceConfiguration();
        ga = new GeneticAlgorithmDcmaEs(geneticConfiguration);
        break;
      default:
        System.err.println("Error: Default evolutionary algorithm. This evolutionary algorithm is not implemented!");
        System.err.println("Current value: "+parser.getEvolutionaryAlgorithm());
        throw new IllegalArgumentException("This simulation mode is not implemented");
    }

    new GaProgressFrame(ga).setVisible(true);
    AbstractPsdEvaluator evaluator = geneticConfiguration.getMainEvaluator();

    //--------------------------------
    evaluator.setRepeats(evaluator.getRepeats() * 5);
    Individual individual = new Individual(new AgAgRatesFactory().getRates(parser.getTemperature()));
    float[][] experimentalPsd = evaluator.calculatePsdFromIndividual(individual);
    new Frame2D("Expected PSD analysis").setMesh(MathUtils.avgFilter(experimentalPsd, 1))
            .setLogScale(true)
            .setShift(true)
            .performDrawToImage(1);
    double simulationTime = individual.getSimulationTime();
    evaluator.setRepeats(evaluator.getRepeats() / 5);
    //--------------------------------

    geneticConfiguration.setExperimentalPsd(experimentalPsd);
    geneticConfiguration.setExpectedSimulationTime(simulationTime);
    ga.initialize();
    System.exit(-9);
    ga.iterate(parser.getTotalIterations());
    printResult(ga);
    experimentalPsd = evaluator.calculatePsdFromIndividual(individual);
    new Frame2D("Calculated PSD analysis").setMesh(MathUtils.avgFilter(experimentalPsd, 1))
            .setLogScale(true)
            .setShift(true)
            .performDrawToImage(1);
  }
  
  private static void printResult(IGeneticAlgorithm ga) {
    Individual individual = ga.getIndividual(0);
    
    System.out.println("These are the results: ");
    System.out.println("Total error is:" + individual.getTotalError());
    for (int gene = 0; gene < individual.getGeneSize(); gene++) {
      System.out.print(individual.getGene(gene) + " ");
    }
    System.out.println();
  }
}
