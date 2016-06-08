/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.convergences.agAgGrowth;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.GeneticAlgorithm;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import graphicInterfaces.gaConvergence.GaProgressFrame;
import ratesLibrary.AgRatesFromPrbCox;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgAgKmcConvergence {

  public static void main(String[] args) {

    new StaticRandom();
    float experitentalTemp = 135;
    
    Parser parser = new Parser();
    parser.setPopulation(100);
    
    GeneticAlgorithm ga = new GeneticAlgorithm(parser);

    new GaProgressFrame(ga).setVisible(true);
    AbstractPsdEvaluator evaluator = ga.getMainEvaluator();

    //--------------------------------
    evaluator.setRepeats(evaluator.getRepeats() * 5);
    Individual individual = new Individual(new AgRatesFromPrbCox().getRates(experitentalTemp));
    float[][] experimentalPsd = evaluator.calculatePsdFromIndividual(individual);
    double simulationTime = individual.getSimulationTime();
    evaluator.setRepeats(evaluator.getRepeats() / 5);
    //--------------------------------

    ga.setExperimentalPsd(experimentalPsd);
    ga.setExpectedSimulationTime(simulationTime);

    ga.initialise();
    ga.iterate();
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
