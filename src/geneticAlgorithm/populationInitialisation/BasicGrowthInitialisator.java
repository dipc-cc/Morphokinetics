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

import geneticAlgorithm.Population;

/**
 * This class only considers 5 different genes for basic growth.
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthInitialisator extends AbstractInitialisator implements IInitialisator {

  /**
   * Initialises all the rates between 1e-6 and 1e9.
   *
   * @param populationSize
   * @return new Population with 5 genes in the interval of 1e-6 and 1e9
   */
  @Override
  public Population createRandomPopulation(int populationSize) {
    return createRandomPopulation(populationSize, 5, 1e-6, 1e9, true);
  }
}
