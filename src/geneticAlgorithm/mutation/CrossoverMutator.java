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
package geneticAlgorithm.mutation;

import java.util.List;
import java.util.Random;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import utils.StaticRandom;

public class CrossoverMutator implements IMutation {

  /** Crossover standard deviation. */
  private final double crs; 

  public CrossoverMutator() {
    crs = 0.1;
  }

  @Override
  public void mutate(Population p, List nonFixedGenes) {
    for (int k = 0; k < p.size(); k++) {
      Individual child = p.getIndividual(k);

      // Crossover.
      Random random = new Random();
      double jr = Math.ceil(p.getIndividual(0).getGeneSize() * StaticRandom.raw());

      for (int j = 0; j < p.getIndividual(0).getGeneSize(); j++) {
        // Normal distribution with mean Crm and standard deviation Crs.
        double cr = p.getCrm() + crs * random.nextGaussian();

        if (StaticRandom.raw() > cr && j != jr) {
          child.setGene(j, p.getOffspringGenes().get(k).get(j));
        }
      }
    }
  }

}
