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
 * This class only considers 6 different genes for Ag/Ag growth.
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgReduced6Initialisator extends AbstractInitialisator implements IInitialisator {

  /**
   * Initialises all the rates between 1 and 1e12
   *
   * @param populationSize
   * @return new Population with 6 genes in the interval of 1 and 1e12
   */
  @Override
  public Population createRandomPopulation(int populationSize) {
    return createRandomPopulation(populationSize, 6, 1, 1e12, true);
  }
}
