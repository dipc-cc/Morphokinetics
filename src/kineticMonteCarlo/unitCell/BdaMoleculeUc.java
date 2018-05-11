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
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.BdaMoleculeSite;

/**
 * BDA molecules on top of Ag (BdaSurfaceUc).
 * 
 * @author J. Alberdi-Rodriguez
 */
public class BdaMoleculeUc extends AbstractGrowthUc implements IUc {
  
  private final BdaMoleculeSite[] atoms;
  /** Central position is in this AgUc. */
  //private BdaSurfaceUc agUc;
  private final BdaSurfaceUc[] neighbours;
  
  public BdaMoleculeUc() {
    super(-1, -1, null);
    
    atoms = new BdaMoleculeSite[19];
    for (int i = 0; i < atoms.length; i++) {
      atoms[i] = new BdaMoleculeSite(i, i);
      atoms[i].setOccupied(true);
    }
    neighbours = new BdaSurfaceUc[4];
  }
   
  public void setNeighbour(BdaSurfaceUc uc, int pos) {
    neighbours[pos] = uc;
  }

  public BdaSurfaceUc getNeighbour(int pos) {
    return neighbours[pos];
  }

  /**
   * Always returns the current atom.
   *
   * @param pos ignored.
   * @return current atom.
   */
  @Override
  public AbstractGrowthSite getSite(int pos) {
    return atoms[pos];
  }
  
  /**
   * Number of elements.
   *
   * @return quantity of unit cells.
   */
  @Override
  public int size() {
    return atoms.length;
  }
  
  /*public void setAgUc(BdaSurfaceUc agUc) {
    this.agUc = agUc;
    agUc.setOccupied(true);
  }
  
  public BdaSurfaceUc getAgUc() {
    return agUc;
  }//*/
  
  @Override
  public Point3D getPos(){
    Point3D pos;
    // bridge
    if (isRotated())
      pos = new Point3D(-0.5, 0, 0);
    else
      pos = new Point3D(0, 0.5, 0);
    return pos;
  }//*/
  public boolean isRotated() {
    return atoms[0].isRotated();
  }

  public void setRotated(boolean rotated) {
    for (int i = 0; i < atoms.length; i++) {
      atoms[i].setRotated(rotated);
    }
  }
}
