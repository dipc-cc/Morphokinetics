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
public class CatalysisSite extends AbstractSurfaceSite {

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
    CatalysisProcess[]processes = new CatalysisProcess[4];
    processes[ADSORPTION] = new CatalysisProcess();
    processes[DESORPTION] = new CatalysisProcess();
    processes[REACTION] = new CatalysisProcess();
    processes[DIFFUSION] = new CatalysisProcess();
    setProcceses(processes);
    coCusWithCoCus = 0;
  }
  
  @Override
  public GrowthAtomAttributes getAttributes() {
    return attributes;
  }
  
  @Override
  public void setAttributes(GrowthAtomAttributes attributes) {
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

  @Override
  public void setNeighbour(AbstractSurfaceSite a, int pos) {
    neighbours[pos] = (CatalysisSite) a;
  }

  @Override
  public CatalysisSite getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  @Override
  public CatalysisSite getRandomNeighbour(byte process) {
    return (CatalysisSite) super.getRandomNeighbour(process);
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
  public boolean isEligible() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  @Override
  public void clear() {
    super.clear();
    setType(TERRACE);
  }
  
  @Override
  public void swapAttributes(AbstractSurfaceSite a) {
    CatalysisSite atom = (CatalysisSite) a;
    CatalysisSiteAttributes tmpAttributes = this.attributes;
    this.attributes = (CatalysisSiteAttributes) atom.getAttributes();
    this.attributes.addOneHop();
    atom.setAttributes(tmpAttributes);
  }
  
  @Override
  public String toString() {
    String returnString = "Atom Id " + getId();
    return returnString;
  }

  @Override
  public double getProbability() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
