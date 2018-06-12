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
package kineticMonteCarlo.unitCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point3D;
import static kineticMonteCarlo.process.BdaProcess.ADSORPTION;
import static kineticMonteCarlo.process.BdaProcess.ROTATION;
import kineticMonteCarlo.site.AbstractSite;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.site.BdaAgSurfaceSite;
import kineticMonteCarlo.site.ISite;

/**
 * This unit cell is for the Ag below the molecules.
 * 
 * @author J. Alberdi-Rodriguez
 */
public class BdaSurfaceUc extends AbstractGrowthUc implements IUc, ISite {
    
  private final BdaSurfaceUc[] neighbours;
  /** Whether an atom can deposit on top (at any position) of this unit cell. */
  private final boolean[] available;
  
  List<ISite> spiralSitesAll;
  private final int[] spiralSitesPos; // 1, 9, 25, 49, 81, 121
  private final Map<Integer,List<ISite>> spiralSites;
  
  public BdaSurfaceUc(int posI, int posJ, AbstractSurfaceSite agSite) {
    super(posI, posJ, agSite);
    neighbours = new BdaSurfaceUc[4];
    available = new boolean[6];
    for (int i = 0; i < 6; i++) {
      available[i] = true;
    }
    spiralSitesAll = new ArrayList<>();
    spiralSitesPos = new int[6];
    spiralSitesPos[0] = 1;
    for (int i = 1; i < spiralSitesPos.length; i++) {
      spiralSitesPos[i] = (int) 8 * i + spiralSitesPos[i - 1];
    }
    spiralSites = new HashMap<>();
  }
  
  @Override
  public Point3D getPos() {
    Point3D pos = super.getPos();//.add(getPos("bridge"));
    return pos;
  }
  
  private Point3D getPos(String location) {
    switch (location){
      case "top":
        return new Point3D(0, 0, 0);
      case "bridge":
        return new Point3D(0, 0.5, 0);
      case "hollow":
        return new Point3D(0.5, 0.5, 0);
      default:
        throw new IllegalArgumentException("argument has to be top, bridge or hollow");
    }
  } 

  public void setNeighbour(BdaSurfaceUc uc, int pos) {
    neighbours[pos] = uc;
  }

  @Override
  public BdaSurfaceUc getNeighbour(int pos) {
    return neighbours[pos];
  }

  public boolean isAvailable(int process) {
    return available[process];
  }

  public void setAvailable(int process, boolean available) {
    this.available[process] = available;
    if (available && (process == ADSORPTION || process == ROTATION)) { // do not make available if the current point is fixed to several BDA molecules
      // if (available || process == DIFFUSION)
      if (((BdaAgSurfaceSite) getSite(0)).getBdaSize() > 0){
        this.available[process] = false;
      }
    }
  }
  
  @Override
  public String toString() {
    String returnString = "Unit cell " + getPosI() + " " + getPosJ();
    return returnString;
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double[] getProbabilities() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setList(Boolean list) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isOnList() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double getProbability() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isEligible() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isRemoved() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isOccupied() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void unRemove() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setRemoved() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public double remove() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public byte getType() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public byte getRealType() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getNumberOfNeighbours() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setNumberOfNeighbours(int numberOfNeighbours) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setNeighbour(AbstractSite atom, int i) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  public void reset() {
    for (int i = 0; i < 6; i++) {
      available[i] = true;
    }
  }
  
  @Override
  public List<ISite> getSpiralSites(int size) {
    return spiralSites.get(size);
  }
  
  @Override
  public void setSpiralSites(List<ISite> sites, int size) {
    this.spiralSitesAll = sites;
    for (int i = 1; i < 6 ; i++) {
      spiralSites.put(i, copySublist(sites, 0, spiralSitesPos[i]));
    }
  }
  
  private List<ISite> copySublist(List inputList, int init, int end) {
    List resultList = new ArrayList<>();
    for (int i = init; i < end; i++) {
      resultList.add(inputList.get(i));
    }
    return resultList;
  }
}
