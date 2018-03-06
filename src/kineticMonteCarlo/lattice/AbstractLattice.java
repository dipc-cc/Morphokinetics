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
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.site.AbstractSite;
import kineticMonteCarlo.unitCell.IUc;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public abstract class AbstractLattice {

  private int hexaSizeI;
  private int hexaSizeJ;
  private int hexaSizeK;
  private int unitCellSize;
  private boolean paused;
  
  public final int getHexaSizeI() {
    return hexaSizeI;
  }

  public final int getHexaSizeJ() {
    return hexaSizeJ;
  }

  public final int getHexaSizeK() {
    return hexaSizeK;
  }
  
  public final void setHexaSizeI(int hexaSizeI) {
    this.hexaSizeI = hexaSizeI;
  }

  public final void setHexaSizeJ(int hexaSizeJ) {
    this.hexaSizeJ = hexaSizeJ;
  }

  public final void setHexaSizeK(int hexaSizeK) {
    this.hexaSizeK = hexaSizeK;
  }
 
  /**
   * @return the unitCellSize
   */
  public final int getUnitCellSize() {
    return unitCellSize;
  }

  /**
   * @param unitCellSize the unitCellSize to set
   */
  public final void setUnitCellSize(int unitCellSize) {
    this.unitCellSize = unitCellSize;
  }

  @Deprecated
  public abstract AbstractSite getSite(int i, int j, int k, int unitCellPos);
  
  public abstract IUc getUc(int pos);

  public void setPaused(boolean pause) {
    this.paused = pause;
  }
  
  public boolean isPaused() {
    return paused;
  }
  
  public int size() {
    return hexaSizeI * hexaSizeJ * hexaSizeK;
  }

  /**
   * Only defined in growth lattice.
   *
   * @return number of island of simulation (or -1)
   */
  public abstract int getIslandCount();

  /**
   * Only defined in growth lattice.
   *
   * @return average gyradius of all islands (or -1)
   */
  public abstract float getAverageGyradius();
  
  public abstract void reset();

  public void setProbabilities(double[] rates) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
 
}
