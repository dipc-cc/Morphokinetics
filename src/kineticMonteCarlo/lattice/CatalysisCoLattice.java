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

import java.util.ArrayList;
import java.util.Iterator;
import kineticMonteCarlo.site.AbstractCatalysisSite;
import kineticMonteCarlo.site.CatalysisCoSite;
import static kineticMonteCarlo.site.AbstractCatalysisSite.BR;
import static kineticMonteCarlo.site.CatalysisCoSite.CO;
import static kineticMonteCarlo.site.AbstractCatalysisSite.CUS;
import static kineticMonteCarlo.site.CatalysisCoSite.O;
import utils.LinearRegression;

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
  private final int MAX;
  
  public CatalysisCoLattice(int hexaSizeI, int hexaSizeJ, String ratesLibrary) {
    super(hexaSizeI, hexaSizeJ);
    coverage = new int[2][2];
    hexaArea = (float) hexaSizeI * hexaSizeJ;
    MAX = (int) Math.sqrt(hexaSizeI * hexaSizeJ) * 20;
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
      }
    }

    double[] x = new double[last1000events.size()];
    Iterator iter = last1000eventsTime.iterator();
    int i = 0;
    while (iter.hasNext() && i < last1000events.size()) {
      x[i++] = (double) iter.next();
    }
    regressions = new ArrayList();
    for (int j = 0; j < covTmp.length; j++) {
      regressions.add(new LinearRegression(x, y[j]));
    }

    boolean stationary = false;
    if (last1000events.size() == MAX) {
      stationary = true;
      for (int j = 0; j < covTmp.length; j++) {
        if (regressions.get(j).R2() > 0.1) {
          stationary = false;
        }
      }
      if (stationary)
        for (int j = 0; j < covTmp.length; j++)
          System.out.println(regressions.get(j).R2()+" ");
        
    }
    return stationary;
  }
  
  /**
   * Check whether two CO^CUS atoms are together. Only for Farkas.
   * 
   * @param atom
   */
  @Override
  void updateCoCus(AbstractCatalysisSite atom) {
  }

  @Override
  AbstractCatalysisSite newAtom(int i, int j) {
    return new CatalysisCoSite(createId(i, j), (short) i, (short) j);
  }
}
