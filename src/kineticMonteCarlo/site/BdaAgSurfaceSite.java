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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kineticMonteCarlo.process.BdaProcess;
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

  public BdaAgSurfaceSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa, 4, 5);
    neighbours = new BdaAgSurfaceSite[4];
    processes = new BdaProcess[6];
    for (int i = 0; i < 6; i++) {
      processes[i] = new BdaProcess();
    }
    setProcceses(processes);
    bdaUcSet = new HashSet<>();
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
  
  public boolean hasTheSameBdaUc(BdaMoleculeUc other) {
    Iterator iter = bdaUcSet.iterator();
    boolean sameBdaUc = false;
    while (iter.hasNext()) {
      BdaMoleculeUc currentBdaUc = (BdaMoleculeUc) iter.next();
      if (currentBdaUc.equals(other)) {
        sameBdaUc = true;
        break;
      }
    }
    return sameBdaUc;
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
  
}
