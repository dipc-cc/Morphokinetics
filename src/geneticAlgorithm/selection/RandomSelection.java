/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez, E. Sanchez
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
package geneticAlgorithm.selection;

import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;
import utils.StaticRandom;

public class RandomSelection implements ISelection {

  @Override
  public IndividualGroup[] Select(Population p, int groupCount) {
    IndividualGroup[] groups = new IndividualGroup[groupCount];

    for (int k = 0; k < groupCount; k++) {
      int k1 = k; while (k1 == k) k1 = (int) Math.ceil(groupCount * StaticRandom.raw()) - 1;
      int k2 = k; while (k2 == k) k2 = (int) Math.ceil(groupCount * StaticRandom.raw()) - 1;
      int k3 = k; while (k3 == k) k3 = (int) Math.ceil(groupCount * StaticRandom.raw()) - 1;

      IndividualGroup group = new IndividualGroup(3);
      group.set(0, p.getIndividual(k1));
      group.set(1, p.getIndividual(k2));
      group.set(2, p.getIndividual(k3));

      groups[k] = group;
    }

    return groups;
  }

}
