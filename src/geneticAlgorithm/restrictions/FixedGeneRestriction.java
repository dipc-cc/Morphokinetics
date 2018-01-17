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

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class FixedGeneRestriction extends GeneRestriction {

  private final double fixedValue;

  public FixedGeneRestriction(double fixedValue, int genePosition) {
    super(genePosition);
    this.fixedValue = fixedValue;
  }

  @Override
  public void restrictGene(Individual i) {
    i.setGene(getGenePosition(), fixedValue);
  }

  @Override
  public int getRestrictionType() {
    return GeneRestriction.FIXED_VALUE;
  }

}
