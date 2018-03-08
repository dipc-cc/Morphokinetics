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

import java.awt.geom.Point2D;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.unitCell.AbstractSurfaceUc;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractSurfaceLattice extends AbstractLattice {
  
  private int occupied;
  private final int hexaArea;
  
  public AbstractSurfaceLattice(int hexaSizeI, int hexaSizeJ) {
    setHexaSizeI(hexaSizeI);
    setHexaSizeJ(hexaSizeJ);
    setHexaSizeK(1);
    setUnitCellSize(1);
    occupied = 0;
    hexaArea = hexaSizeI * hexaSizeJ;
  }
  
  /**
   * Adds an occupied location to the counter.
   */
  public void addOccupied() {
    occupied++;
  }
  
  /**
   * Subtracts an occupied location from the counter.
   */
  public void subtractOccupied() {
    occupied--;
  }
  
  /**
   * Resets to zero the number of occupied locations.
   */
  public void resetOccupied() {
    occupied = 0;
  }
  
  /**
   * 
   * @return the coverage of the lattice.
   */
  public float getCoverage() {
    return (float) occupied / (float) hexaArea;
  }
  
  /**
   * 
   * @return  number of occupied positions.
   */
  public int getOccupied() {
    return occupied;
  }
  
  @Override
  public void reset() {
    for (int i = 0; i < size(); i++) {
      AbstractSurfaceUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractSurfaceSite atom = uc.getSite(j);
        atom.clear();
      }
    }
  }
  
  /**
   * Obtains the spatial location of certain atom, the distance between atoms is considered as 1
   * Returns the Cartesian position, given the hexagonal (lattice) location.
   *
   * @param iHexa i index in the hexagonal mesh.
   * @param jHexa j index in the hexagonal mesh.
   * @return spatial location in Cartesian.
   */
  public abstract Point2D getCartesianLocation(int iHexa, int jHexa);

  public abstract Point2D getCentralCartesianLocation();
  
  public abstract double getCartX(int iHexa, int jHexa);
  
  public abstract double getCartY(int jHexa);
  
  public abstract float getCartSizeX();

  public abstract float getCartSizeY();
  
  @Override
  public abstract AbstractSurfaceUc getUc(int pos);
  
  public abstract void changeOccupationByHand(double xMouse, double yMouse, int scale);
  
  public abstract void deposit(AbstractSurfaceSite atom, boolean forceNucleation);

  /**
   * Extract the given atom from the lattice.
   * 
   * @param atom the atom to be extracted.
   * @return probability change (positive value).
   */
  public abstract double extract(AbstractSurfaceSite atom);
}
