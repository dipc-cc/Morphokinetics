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
package geneticAlgorithm.reinsertion;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;

public class ElitistAllReinsertion implements IReinsertion {

  public ElitistAllReinsertion() {
  }

  /**
   * The offspring individual is accepted if its error is lower than the corresponding original
   * error
   *
   * @param origin Original population
   * @param offpring Offspring population
   * @param substitutions Completely ignored
   * @return new Population
   */
  @Override
  public Population Reinsert(Population origin, Population offpring, int substitutions) {
    for (int k = 0; k < origin.size(); k++) {

      Individual original = origin.getIndividual(k);
      Individual candidate = offpring.getIndividual(k);

      if (candidate.getTotalError() <= original.getTotalError()) {
        origin.setIndividual(candidate, k);
        origin.getOffFitness().set(k, candidate.getTotalError());
      }
    }
    origin.newOffspringGenes();
    return origin;
  }

}
