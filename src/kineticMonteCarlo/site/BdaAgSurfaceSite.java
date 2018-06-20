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
package kineticMonteCarlo.site;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kineticMonteCarlo.process.BdaProcess;
import static kineticMonteCarlo.process.BdaProcess.ADSORPTION;
import static kineticMonteCarlo.process.BdaProcess.ROTATION;
import kineticMonteCarlo.unitCell.BdaMoleculeUc;

/**
 *This object is set as occupied if it is a central atom of a BDA molecule.
 * 
 * @author J. Alberdi-Rodriguez
 */
public class BdaAgSurfaceSite extends AbstractGrowthSite {
  
  private final BdaAgSurfaceSite[] neighbours;
  private final BdaProcess[] processes;
  private final Set<BdaMoleculeUc> bdaUcSet;
  /** Only if it is the central position for BDA molecule. */
  private BdaMoleculeUc bdaUc;

  /** Whether an atom can deposit on top (at any position) of this Ag site. */
  private final boolean[] available;
  
  public BdaAgSurfaceSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa, 4, 5);
    neighbours = new BdaAgSurfaceSite[4];
    processes = new BdaProcess[6];
    for (int i = 0; i < 6; i++) {
      processes[i] = new BdaProcess();
    }
    setProcceses(processes);
    bdaUcSet = new HashSet<>();
    available = new boolean[6];
  }

  public BdaMoleculeUc getBdaUc() {
    return bdaUc;
  }

  public void setBdaUc(BdaMoleculeUc bdaUc) {
    if (isOccupied()) { // redundant!!
      this.bdaUc = bdaUc;
    }
  }
  
  public void setBelongingBdaUc(BdaMoleculeUc bdaUc) {
    this.bdaUcSet.add(bdaUc);
  }
  
  public void removeBdaUc(BdaMoleculeUc bdaUc) {
    bdaUcSet.remove(bdaUc);
    if (bdaUc.equals(this.bdaUc)) {
      this.bdaUc = null;
    }
  }
  
  public int getBdaSize() {
    return bdaUcSet.size();
  }
  
  public boolean isAvailable(int process) {
    return available[process];
  }

  public void setAvailable(int process, boolean available) {
    this.available[process] = available;
    if (available && (process == ADSORPTION || process == ROTATION)) { // do not make available if the current point is fixed to several BDA molecules
      // if (available || process == DIFFUSION)
      if (getBdaSize() > 0){
        this.available[process] = false;
      }
    }
  }
  
  @Override
  public byte getTypeWithoutNeighbour(int posNeighbour) {
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
  public int getOrientation() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void obtainRateFromNeighbours() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double probJumpToNeighbour(int originType, int position) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List getAllNeighbours() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double updateOneBound(int bond) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isPartOfImmobilSubstrate() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public AbstractGrowthSite getNeighbour(int pos) {
    return neighbours[pos];
  }

  @Override
  public void setNeighbour(AbstractSurfaceSite a, int pos) {
    neighbours[pos] = (BdaAgSurfaceSite) a;
  }

  @Override
  public boolean isEligible() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  /**
   * Resets current atom; frees its occupancy.
   */
  @Override
  public void clear(){
    super.clear();
    bdaUcSet.clear();
    bdaUc = null;
    for (int i = 0; i < 6; i++) {
      available[i] = true;
    }
  }
  
}
