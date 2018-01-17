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

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import java.util.List;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class BgaBasedMutator implements IMutation {

  /** mutation range */
  private final float r;
  /** mutation precision */
  private final float k;
  /** mutation rate as percentage */
  private final float mutRate;
  
  public BgaBasedMutator() {
    r = /*0.1f*/ 1f;
    k = 7;
    mutRate = 0.5f;
  }

  @Override
  public void mutate(Population p, List nonFixedGenes) {
    for (int ind = 0; ind < p.size(); ind++) {
      Individual child = p.getIndividual(ind);
      int mutations = (int) Math.round(utils.StaticRandom.raw() * nonFixedGenes.size() * mutRate);
      for (int m = 0; m < mutations; m++) {
        int posList = (int) (utils.StaticRandom.raw() * nonFixedGenes.size());
        int pos = (Integer) nonFixedGenes.get(posList);

        double a = (Math.pow(2, utils.StaticRandom.raw() * k) / Math.pow(2, k));
        double s = utils.StaticRandom.raw() * 2 - 1;
        double oldValue = child.getGene(pos);
        double newValue = oldValue + s * r * a * oldValue;

        child.setGene(pos, newValue);
      }
    }
  }
}
