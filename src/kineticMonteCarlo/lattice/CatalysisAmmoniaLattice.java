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

import java.util.Iterator;
import kineticMonteCarlo.site.CatalysisAmmoniaSite;
import kineticMonteCarlo.site.AbstractCatalysisSite;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisAmmoniaLattice extends AbstractCatalysisLattice {
  
  /**
   * Current species coverages, for sites CUS.
   */
  private final int[] coverage;
  private final float hexaArea;
  private final int MAX;
  
  public CatalysisAmmoniaLattice(int hexaSizeI, int hexaSizeJ, String ratesLibrary) {
    super(hexaSizeI, hexaSizeJ);
    coverage = new int[11];
    hexaArea = (float) hexaSizeI * hexaSizeJ;
    MAX = (int) Math.sqrt(hexaSizeI * hexaSizeJ) * 10;
  }

  @Override
  public float getCoverage(byte type) {
    float cov = (float) coverage[type];
    return cov / hexaArea;
  }

  /**
   * Computes a partial coverage for NH3, NH2, NH, O, OH, N, NO
   * in CUS sites (BR sites are always empty).
   * 
   * @return coverage for particles in CUS sites.
   */
  @Override
  public float[] getCoverages() {
    float[] cov = new float[7];
    for (int i = 0; i < cov.length; i++) {
      cov[i] = coverage[i] / (hexaArea / 2.0f);
    }
    return cov;
  }

  @Override
  void setCoverage(int type, int site, int change) {
    coverage[type] += change;
  }

  @Override
  double getCoverage(int type, int site) {
    return (double) coverage[type] / (hexaArea / 2.0f);
  }
  
  @Override
  public float getCoverage() {
    float cov = 0.f;
    for (int i = 0; i < coverage.length; i++) {
      cov += coverage[i];
    }
    return cov / (hexaArea / 2.0f);
  }

  /**
   * Check whether two CO^CUS atoms are together. Only for CO for Farkas.
   * 
   * @param atom
   */
  @Override
  void updateCoCus(AbstractCatalysisSite atom) {
  }
  
  @Override
  CatalysisAmmoniaSite[][] instantiateAtoms() {
    return new CatalysisAmmoniaSite[getHexaSizeI()][getHexaSizeJ()];
  }
  
  @Override
  CatalysisAmmoniaSite newAtom(int i, int j) {
    return new CatalysisAmmoniaSite(createId(i, j), (short) i, (short) j);
  }
  
  public void transformTo(AbstractCatalysisSite specie, byte type) {
    coverage[specie.getType()]--;
    coverage[type]++;
    specie.setType(type);
  }
  
  @Override
  public void reset() {
    super.reset();
    for (int i = 0; i < coverage.length; i++) {
      coverage[i] = 0;
    }
  }
  
  /**
   * Identifies stationary situation, when sufficient number of previous steps are saved and their
   * R² is lower than 0.1 for all the species.
   *
   * @param time
   * @return
   */
  @Override
  public boolean isStationary(double time) {
    float[] covTmp = getCoverages();
    double[] covMin = new double[covTmp.length];
    double[] covMax = new double[covTmp.length];
    for (int i = 0; i < covMin.length; i++) {
      covMin[i] = 2.f; // max coverage will be always below 1      
    }
    last1000events.add(covTmp);
    last1000eventsTime.add(time);
    if (last1000events.size() > MAX) {
      last1000events.remove(0);
      last1000eventsTime.remove(0);
    }

    double[][] y = new double[covTmp.length][last1000events.size()];
    for (int i = 0; i < last1000events.size(); i++) {
      for (int j = 0; j < covTmp.length; j++) {
        y[j][i] = last1000events.get(i)[j];
        if (y[j][i] > covMax[j]) {
          covMax[j] = y[j][i];
        }
        if (y[j][i] < covMin[j]) {
          covMin[j] = y[j][i];
        }
      }
    }

    double[] x = new double[last1000events.size()];
    Iterator iter = last1000eventsTime.iterator();
    int i = 0;
    while (iter.hasNext() && i < last1000events.size()) {
      x[i++] = (double) iter.next();
    }
    
    boolean stationary = false;
    if (last1000events.size() == MAX) {
      stationary = true;
      for (int j = 0; j < covTmp.length; j++) {
        if (covMax[j] - covMin[j] > 0.05) {
          stationary = false;
        }
      }        
    }
    return stationary;
  }  
}
