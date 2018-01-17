/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-RodriguezRodriguez
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
package geneticAlgorithm.evaluationFunctions;

import geneticAlgorithm.Population;
import java.util.List;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public interface IEvaluator {

  /**
   * Evaluate all the elements of the population with the list of given evaluators. After, it orders
   * them from min to max error.
   *
   * @param p
   * @param functionWithSimulation
   * @param functions
   */
  public void evaluateAndOrder(Population p, AbstractPsdEvaluator functionWithSimulation, List<IEvaluation> functions);
}
