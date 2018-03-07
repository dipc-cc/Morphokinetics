/* 
 * Copyright (C) 2018 K. Valencia, J. Alberdi-Rodriguez
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
package kineticMonteCarlo.site;

import java.util.List;
import kineticMonteCarlo.process.CatalysisProcess;
import static kineticMonteCarlo.process.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DESORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DIFFUSION;
import static kineticMonteCarlo.process.CatalysisProcess.REACTION;
import utils.StaticRandom;

/**
 *
 * @author K. Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisSite extends AbstractGrowthSite {

  public static final byte CO = 0;
  public static final byte O = 1;
  public static final byte C = 0;
  public static final byte O2 = 1;
  public static final byte BR = 0;
  public static final byte CUS = 1;

  private final CatalysisSite[] neighbours = new CatalysisSite[4];
  /**
   * Bridge or CUS.
   */
  private final byte latticeSite;
  private final CatalysisProcess[] processes;
  
  private CatalysisSiteAttributes attributes;
  
  /** Current atom is CO^CUS and it has as many CO^CUS neighbours. */
  private int coCusWithCoCus;
  
  public CatalysisSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa, 4, 4);
    if (iHexa % 2 == 0) {
      latticeSite = BR;
    } else {
      latticeSite = CUS;
    }
    attributes = new CatalysisSiteAttributes();
    processes = new CatalysisProcess[4];
    processes[ADSORPTION] = new CatalysisProcess();
    processes[DESORPTION] = new CatalysisProcess();
    processes[REACTION] = new CatalysisProcess();
    processes[DIFFUSION] = new CatalysisProcess();
    setProcceses(processes);
    coCusWithCoCus = 0;
  }
  
  @Override
  public AbstractGrowthAtomAttributes getAttributes() {
    return attributes;
  }
  
  @Override
  public void setAttributes(AbstractGrowthAtomAttributes attributes) {
    this.attributes = (CatalysisSiteAttributes) attributes;
  }
  
  public byte getLatticeSite() {
    return latticeSite;
  }
  
  /**
   * Tells when current atom is completely surrounded.
   * 
   * @return true if it has 4 occupied neighbours.
   */
  @Override
  public boolean isIsolated() {
    return getOccupiedNeighbours() == 4;
  }
    
  /**
   * Dummy method. Just to run.
   * @param originType
   * @param targetType
   * @return 
   */
  @Override
  public double getProbability(int originType, int targetType) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  /**
   * For the orientation they are only available two position. Orientation is either | or _. It is
   * assumed that current atom is of type EDGE.
   *
   * @return horizontal (0) or vertical (1).
   */
  @Override
  public int getOrientation() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setNeighbour(AbstractGrowthSite a, int pos) {
    neighbours[pos] = (CatalysisSite) a;
  }
  
  @Override
  public List getAllNeighbours() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public CatalysisSite getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  public CatalysisSite getRandomNeighbour(byte process) {
    CatalysisSite neighbour;
    double randomNumber = StaticRandom.raw() * getRate(process);
    double sum = 0.0;
    for (int j = 0; j < getNumberOfNeighbours(); j++) {
      sum += processes[process].getEdgeRate(j);
      if (sum > randomNumber) {
        neighbour = getNeighbour(j);
        return neighbour;
      }
    }
    // raise an error
    return null;
  }

  public void addCoCusNeighbours(int value) {
    coCusWithCoCus += value;
  }

  public void cleanCoCusNeighbours() {
    coCusWithCoCus = 0;
  }

  public int getCoCusNeighbours() {
    return coCusWithCoCus;
  }

  @Override
  public double updateOneBound(int pos) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isEligible() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  @Override
  public boolean isPartOfImmobilSubstrate() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  @Override
  public byte getTypeWithoutNeighbour(int position) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean areTwoTerracesTogether() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public AbstractGrowthSite chooseRandomHop() {    
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void obtainRateFromNeighbours() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  @Override
  public double probJumpToNeighbour(int ignored, int position) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  @Override
  public void clear() {
    super.clear();
    setType(TERRACE);
    
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(0, i);
    }
    for (int i = 0; i < 4; i++) {
      processes[i].clear();
    }
  }
  
  @Override
  public void swapAttributes(AbstractGrowthSite a) {
    CatalysisSite atom = (CatalysisSite) a;
    CatalysisSiteAttributes tmpAttributes = this.attributes;
    this.attributes = (CatalysisSiteAttributes) atom.getAttributes();
    this.attributes.addOneHop();
    atom.setAttributes(tmpAttributes);
  }
  
  @Override
  public String toString() {
    String returnString = "Atom Id " + getId() + " desorptionRate " + processes[DESORPTION].getRate() + " " + processes[DESORPTION].getSumRate();
    return returnString;
  }
}
