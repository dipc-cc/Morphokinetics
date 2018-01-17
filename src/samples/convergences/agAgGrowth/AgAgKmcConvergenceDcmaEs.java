/* 
 * Copyright (C) 2018 N. Ferrando
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package samples.convergences.agAgGrowth;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.GeneticAlgorithmDcmaEs;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import graphicInterfaces.gaConvergence.GaProgressFrame;
import ratesLibrary.AgRatesFromPrbCox;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando
 */
public class AgAgKmcConvergenceDcmaEs {

  public static void main(String[] args) {

    new StaticRandom();
    float experitentalTemp = 135;

    Parser parser = new Parser();
    parser.setEvolutionaryAlgorithm("dcma");
    
    GeneticAlgorithmDcmaEs ga = new GeneticAlgorithmDcmaEs(parser);

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
