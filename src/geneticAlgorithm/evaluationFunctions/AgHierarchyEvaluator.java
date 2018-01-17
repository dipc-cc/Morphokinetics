/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class AgHierarchyEvaluator extends AbstractEvaluator {

  @Override
  public void dispose() {
    //nothing to dispose
  }

  @Override
  public double[] evaluate(Population p) {

    double[] errors = new double[p.size()];

    for (int i = 0; i < p.size(); i++) {

      Individual individual = p.getIndividual(i);

      double stepA = Math.log10(individual.getGene(2 * 7 + 2));
      double stepB = Math.log10(individual.getGene(5 * 7 + 5));

      errors[i] += Math.max(0, 0.75 - (stepA - Math.log10(individual.getGene(2 * 7 + 3))));
      errors[i] += Math.max(0, 0.75 - (stepA - Math.log10(individual.getGene(2 * 7 + 4))));
      errors[i] += Math.max(0, 0.75 - (stepA - Math.log10(individual.getGene(2 * 7 + 5))));
      errors[i] += Math.max(0, 0.75 - (stepA - Math.log10(individual.getGene(2 * 7 + 6))));

      errors[i] += Math.max(0, 0.75 - (stepB - Math.log10(individual.getGene(5 * 7 + 2))));
      errors[i] += Math.max(0, 0.75 - (stepB - Math.log10(individual.getGene(5 * 7 + 3))));
      errors[i] += Math.max(0, 0.75 - (stepB - Math.log10(individual.getGene(5 * 7 + 4))));
      errors[i] += Math.max(0, 0.75 - (stepB - Math.log10(individual.getGene(5 * 7 + 6))));

      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 1)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 2)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 3)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 4)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 5)) - Math.max(stepA, stepB)));
      errors[i] += Math.max(0, 0.75 - (Math.log10(individual.getGene(1 * 7 + 6)) - Math.max(stepA, stepB)));

    }
    return errors;
  }

}
