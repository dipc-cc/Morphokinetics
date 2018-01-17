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
package geneticAlgorithm.restrictions;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public abstract class RestrictionOperator {

  protected List<GeneRestriction> genesRestriction = new ArrayList();

  public abstract void initialise();

  public void apply(Population p) {
    for (int ind = 0; ind < p.size(); ind++) {

      Individual individual = p.getIndividual(ind);

      for (int i = 0; i < genesRestriction.size(); i++) {
        genesRestriction.get(i).restrictGene(individual);
      }
    }
  }

  public List getNonFixedGenes(int geneSize) {
    List<Integer> result = new ArrayList();

    for (int i = 0; i < geneSize; i++) {
      boolean restricted = false;
      for (int j = 0; j < genesRestriction.size(); j++) {

        if (genesRestriction.get(j).getGenePosition() == i && genesRestriction.get(j).getRestrictionType() > GeneRestriction.BOUNDED_VALUES) {
          restricted = true;
          break;
        }
      }
      if (!restricted) {
        result.add(i);
      }
    }
    return result;
  }

}
