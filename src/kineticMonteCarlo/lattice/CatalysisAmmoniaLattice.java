/*
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.site.CatalysisAmmoniaSite;
import kineticMonteCarlo.site.CatalysisSite;
import static kineticMonteCarlo.site.CatalysisSite.BR;
import static kineticMonteCarlo.site.CatalysisSite.CUS;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisAmmoniaLattice extends CatalysisLattice {
  
  /**
   * Current species coverages, for sites BR, CUS.
   */
  private final int[][] coverage;
  private final float hexaArea;
  
  public CatalysisAmmoniaLattice(int hexaSizeI, int hexaSizeJ, String ratesLibrary) {
    super(hexaSizeI, hexaSizeJ);
    coverage = new int[11][2];
    hexaArea = (float) hexaSizeI * hexaSizeJ;
  }

  @Override
  public float getCoverage(byte type) {
    float cov = (float) coverage[type][BR] + (float) coverage[type][CUS];
    return cov / hexaArea;
  }

  @Override
  public float[] getCoverages() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  void setCoverage(int type, int site, int change) {
    coverage[type][site] += change;
  }

  @Override
  double getCoverage(int type, int site) {
    return (double) coverage[type][site] / (hexaArea / 2.0f);
  }

  /**
   * Check whether two CO^CUS atoms are together. Only for CO for Farkas.
   * 
   * @param atom
   */
  @Override
  void updateCoCus(CatalysisSite atom) {
  }
  
  @Override
  CatalysisAmmoniaSite[][] instantiateAtoms() {
    return new CatalysisAmmoniaSite[getHexaSizeI()][getHexaSizeJ()];
  }
  
  @Override
  CatalysisAmmoniaSite newAtom(int i, int j) {
    return new CatalysisAmmoniaSite(createId(i, j), (short) i, (short) j);
  }
  
}
