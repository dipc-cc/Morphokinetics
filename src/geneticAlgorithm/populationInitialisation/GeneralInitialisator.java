/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.populationInitialisation;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class GeneralInitialisator implements IInitialisator {

  /**
   * Initialises the population with random numbers, with linear or logarithmic distribution.
   *
   * @param populationSize
   * @param dimensions number of genes (variables)
   * @param min minimum possible value
   * @param max maximum possible value
   * @param log are the variables order of magnitude different? If yes use exponential distribution
   * @return
   */
  public Population createRandomPopulation(int populationSize, int dimensions, double min, double max, boolean log) {
    Population p = new Population(populationSize);
    for (int ind = 0; ind < p.size(); ind++) {
      Individual i = new Individual(dimensions, 4);
      for (int j = 0; j < dimensions; j++) {
        if (log) { // Calculate a random number with exponential distribution
          i.setGene(j, min * Math.pow(max / min, StaticRandom.raw()));
        } else { // Calculate a random number with linear distribution
          i.setGene(j, min + (max - min) * StaticRandom.raw());
        }
      }

      p.setIndividual(i, ind);
    }
    return p;
  }
}
