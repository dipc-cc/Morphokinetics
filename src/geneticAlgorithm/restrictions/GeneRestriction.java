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
public abstract class GeneRestriction {

  public static final int NO_RESTRICTION = 0;
  public static final int BOUNDED_VALUES = 1;
  public static final int REPLICATE_GENE = 2;
  public static final int FIXED_VALUE = 3;

  private int genePosition;

  public GeneRestriction(int genePosition) {
    this.genePosition = genePosition;
  }

  public abstract void restrictGene(Individual i);

  public abstract int getRestrictionType();

  public int getGenePosition() {
    return genePosition;
  }

}
