/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
package geneticAlgorithm.populationInitialisation;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractInitialisator implements IInitialisator {

  /**
   * Initialises the population with random numbers, with linear or logarithmic distribution.
   *
   * @param populationSize
   * @param dimensions number of genes (variables)
   * @param min minimum possible value
   * @param max maximum possible value
   * @param log are the variables order of magnitude different? If yes use exponential distribution
   * @return new Population with given parameters
   */
  @Override
  public Population createRandomPopulation(int populationSize, int dimensions, double min, double max, boolean log) {
    Population p = new Population(dimensions, populationSize);
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
