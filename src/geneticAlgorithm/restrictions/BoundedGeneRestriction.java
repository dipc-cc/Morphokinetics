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
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class BoundedGeneRestriction extends GeneRestriction {

  private final double minValue;
  private final double maxValue;

  public BoundedGeneRestriction(double minValue, double maxValue, int genePosition) {
    super(genePosition);
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  @Override
  public void restrictGene(Individual i) {
    if (i.getGene(getGenePosition()) < minValue) {
      i.setGene(getGenePosition(), minValue);
    }
    if (i.getGene(getGenePosition()) > maxValue) {
      i.setGene(getGenePosition(), maxValue);
    }
    // If gene is not defined initialise again with random number. In any case, the algorithm seems to be lost by now
    if (Double.isNaN(i.getGene(getGenePosition()))) {
      double newGene = minValue * Math.pow(maxValue / minValue, StaticRandom.raw());
      System.err.println("Setting new random gene to position " + getGenePosition() + " because it turned to be NaN. Value: " + newGene);
      System.out.println("Setting new random gene to position " + getGenePosition() + " because it turned to be NaN. Value: " + newGene);
      i.setGene(getGenePosition(), newGene);
    }
  }

  @Override
  public int getRestrictionType() {
    return GeneRestriction.BOUNDED_VALUES;
  }

}
