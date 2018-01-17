/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez, E. Sanchez
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
package geneticAlgorithm.recombination;

import geneticAlgorithm.Individual;
import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez, E. Sanchez
 */
public class RealRecombination implements IRecombination {

  private final float outBounds;

  /**
   * Each gene of the child will be between the father and mother gene value +-15%.
   */
  public RealRecombination() {
    outBounds = 0.1f;
  }

  /**
   * Creates a new offspring population. The new population is usually smaller than whole population
   *
   * @param population It is ignored
   * @param groups new individuals to be included in the offspring population
   * @return new Population
   */
  @Override
  public Population recombinate(Population population, IndividualGroup[] groups) {
    Population offspring = new Population(groups.length);

    for (int i = 0; i < (offspring.size()); i++) {
      Individual child = new Individual(groups[0].get(0).getGeneSize(),
              groups[0].get(0).getErrorsSize());

      for (int a = 0; a < child.getGeneSize(); a++) {

        double e = StaticRandom.raw() * (1 + 2 * outBounds) - outBounds;
        child.setGene(a, Math.max(0.0, groups[i].get(0).getGene(a) * e + groups[i].get(1).getGene(a) * (1 - e)));

      }
      offspring.setIndividual(child, i);
    }

    return offspring;
  }

  /**
   * Does nothing
   * @param population 
   */
  @Override
  public void initialise(Population population) {
    // Do nothing 
  }

  /**
   * Does nothing
   * @return false always
   */
  @Override
  public boolean isDtooLarge() {
    return false; // Do nothing
  }
}
