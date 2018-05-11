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

import javafx.geometry.Point3D;
import kineticMonteCarlo.site.AbstractSurfaceSite;

/**
 * This unit cell is for the Ag below the molecules.
 * 
 * @author J. Alberdi-Rodriguez
 */
public class BdaSurfaceUc extends AbstractGrowthUc implements IUc {
    
  private final BdaSurfaceUc[] neighbours;
  /** Whether an atom can deposit on top (at any position) of this unit cell. */
  private boolean available;
  /** This object is set as occupied if it is a central atom of a BDA molecule. */
  private boolean occupied;
  
  private BdaMoleculeUc bdaUc;
  
  public BdaSurfaceUc(int posI, int posJ, AbstractSurfaceSite atom) {
    super(posI, posJ, atom);
    neighbours = new BdaSurfaceUc[4];
    available = true;
    bdaUc = null;
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

  public BdaSurfaceUc getNeighbour(int pos) {
    return neighbours[pos];
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }
 
  /**
   * This object is set as occupied if it is a central atom of a BDA molecule.
   *
   * @return
   */
  public boolean isOccupied() {
    return occupied;
  }

  public void setOccupied(boolean occupied) {
    this.occupied = occupied;
  }

  public BdaMoleculeUc getBdaUc() {
    return bdaUc;
  }

  public void setBdaUc(BdaMoleculeUc bdaUc) {
    this.bdaUc = bdaUc;
    occupied = true;
  }
  
  @Override
  public String toString() {
    String returnString = "Unit cell "+getPosI()+" "+getPosJ();
    return returnString;
  }
}
