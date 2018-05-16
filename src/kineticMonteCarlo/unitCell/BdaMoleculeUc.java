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
 * BDA molecules on top of Ag (BdaSurfaceUc). There are 12 possible neighbours. It can have 8 neighbours at the
 * same time: north-west, north-east, east-north, east-south, south-east,
 * south-west, east-south, east-north. North, east, south and west also exists.
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaMoleculeUc extends AbstractGrowthUc implements IUc {
  
  /** Central position is in this AgUc. */
  //private BdaSurfaceUc agUc;
  private final BdaSurfaceUc[] neighbours;
  private BdaMoleculeSite bdaMolecule;
  
  public BdaMoleculeUc() {
    super(-1, -1, null);
    bdaMolecule = new BdaMoleculeSite(-1, false);
   
    neighbours = new BdaSurfaceUc[12];
  }
   
  public void setNeighbour(BdaSurfaceUc uc, int pos) {
    neighbours[pos] = uc;
  }

  public BdaSurfaceUc getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  public int getOccupiedNeighbours() {
    int occupied = 0;
    for (int i = 0; i < neighbours.length; i++) {
      if (neighbours[i] != null) {
        occupied++;
      }
    }
    return occupied < 4 ? occupied : 3;
  }

  /**
   * Always returns the current atom.
   *
   * @param pos ignored.
   * @return current atom.
   */
  @Override
  public AbstractGrowthSite getSite(int pos) {
    return bdaMolecule;
  }
  
  /**
   * Number of elements.
   *
   * @return quantity of unit cells.
   */
  @Override
  public int size() {
    return 1;
  }
  
  @Override
  public Point3D getPos(){
    Point3D pos;
    // bridge
    if (bdaMolecule.isRotated()) {
      pos = new Point3D(-0.5, 0, 0);
    } else {
      pos = new Point3D(0, 0.5, 0);
    }
    return pos;
  }
  
  public boolean isRotated() {
    return bdaMolecule.isRotated();
  }

  public void setRotated(boolean rotated) {
    bdaMolecule.setRotated(rotated);
  }
}
