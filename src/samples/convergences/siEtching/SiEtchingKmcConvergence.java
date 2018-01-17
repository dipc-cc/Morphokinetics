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
package samples.convergences.siEtching;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.AbstractPsdEvaluator;
import geneticAlgorithm.GeneticAlgorithm;
import geneticAlgorithm.IGeneticAlgorithm;
import geneticAlgorithm.Individual;
import graphicInterfaces.gaConvergence.GaProgressFrame;
import ratesLibrary.SiRatesFromPreGosalvez;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando
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
      Individual individual = new Individual(new SiRatesFromPreGosalvez().getRates(340));
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
