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
package geneticAlgorithm.selection;

import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez, E. Sanchez
 */
public class RankingSelection implements ISelection {

  /** selective pressure, choose in interval [1, 2] */
  private float selectivePressure; // 

  public RankingSelection() {
    selectivePressure = 2f;
  }
  
  @Override
  public IndividualGroup[] Select(Population p, int groupCount) {

    float[] Fitness = new float[p.size()];
    for (int i = 0; i < p.size(); i++) {
      Fitness[p.size() - i - 1] = 2.0f - selectivePressure + 2.0f * (selectivePressure - 1.0f) * (i)
              / (p.size() - 1.0f);
    }

    IndividualGroup[] groups = new IndividualGroup[groupCount];

    for (int i = 0; i < groupCount; i++) {
      groups[i] = new IndividualGroup(2);
      groups[i].set(0, p.getIndividual(linearSearch(Fitness)));

      do {
        groups[i].set(1, p.getIndividual(linearSearch(Fitness)));
      } while (groups[i].get(0) == groups[i].get(1));
    }
    return groups;
  }

  private int linearSearch(float[] probs) {

    float total = 0;
    for (int i = 0; i < probs.length; i++) {
      total += probs[i];
    }

    float selected = ((float) utils.StaticRandom.raw()) * total;

    float acc = 0;
    int i;
    for (i = 0; i < probs.length; i++) {
      acc += probs[i];
      if (acc > selected) {
        return i;
      }
    }
    return i;
  }

}
