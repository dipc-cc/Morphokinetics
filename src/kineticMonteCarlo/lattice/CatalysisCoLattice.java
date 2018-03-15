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

import static kineticMonteCarlo.site.CatalysisSite.BR;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.CUS;
import static kineticMonteCarlo.site.CatalysisSite.O;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisCoLattice extends CatalysisLattice {
  
  /**
   * Current CO and O coverages, for sites BR, CUS.
   */
  private final int[][] coverage;
  private final float hexaArea;
  
  public CatalysisCoLattice(int hexaSizeI, int hexaSizeJ, String ratesLibrary) {
    super(hexaSizeI, hexaSizeJ, ratesLibrary);
    coverage = new int[2][2];
    hexaArea = (float) hexaSizeI * hexaSizeJ;
  }
  
  @Override
  public float getCoverage(byte type) {
    float cov = (float) coverage[type][BR] + (float) coverage[type][CUS];
    return cov / hexaArea;
  }
  
  /**
   * Computes a partial coverage for CO and O in BR and CUS sites.
   * 
   * @return coverage CO^BR, CO^CUS, O^BR, CO^CUS
   */
  @Override
  public float[] getCoverages() {
    float[] cov = new float[4];
    for (int i = 0; i < cov.length; i++) {
      cov[i] = coverage[i / 2][i % 2] / (hexaArea / 2.0f);
    }
    return cov;
  }
  
  @Override
  public float getCoverage() {
    float cov = (float) coverage[CO][BR] + (float) coverage[CO][CUS] + (float) coverage[O][BR] + (float) coverage[O][CUS];
    return cov / hexaArea;
  }
  
  @Override
  double getCoverage(int type, int site) {
    return (double) coverage[type][site] / (hexaArea / 2.0f);
  }    
  
  /**
   * Package private method to set the coverage for a type (CO or O) in a site (BR or CUS).
   */
  @Override
  void setCoverage(int type, int site, int change) {
    coverage[type][site] += change;
  }
  
  @Override
  public void reset() {
    coverage[CO][BR] = 0;
    coverage[CO][CUS] = 0;
    coverage[O][BR] = 0;
    coverage[O][CUS] = 0;
    super.reset();
  }    
}
