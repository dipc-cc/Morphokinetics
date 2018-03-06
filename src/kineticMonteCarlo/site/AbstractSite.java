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
package kineticMonteCarlo.site;

import javafx.geometry.Point3D;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public abstract class AbstractSite implements ISite {

  // Atoms types
  public static final byte TERRACE = 0;
  public static final byte CORNER = 1;
  public static final byte EDGE = 2;
  public static final byte ZIGZAG_EDGE = 2;
  public static final byte ARMCHAIR_EDGE = 3;
  public static final byte ZIGZAG_WITH_EXTRA = 4;
  public static final byte SICK = 5;
  public static final byte KINK = 6;
  public static final byte BULK = 7;
  
  // Attributes
  private Boolean list = false;
  private double[] probabilities;
  private int numberOfNeighbours;
  private boolean removed = false;
  private boolean occupied;
  
  @Override
  public void setProbabilities(double[] probabilities) {
    this.probabilities = probabilities;
  }

  @Override
  public double[] getProbabilities() {
    return probabilities;
  }

  @Override
  public void setList(Boolean list) {
    this.list = list;
  }

  @Override
  public boolean isOnList() {
    if (list != null) 
      return list;
    else
      return false;
  }

  @Override
  public boolean isRemoved() {
    return removed;
  }

  @Override
  public void unRemove() {
    removed = false;
    occupied = !removed;
  }
  
  @Override
  public void setRemoved() {
    removed = true;
    occupied = !removed;
  }

  @Override
  public final boolean isOccupied() {
    return occupied;
  }
  
  public final void setOccupied(boolean occupied) {
    this.occupied = occupied;
    removed = !occupied;
  }

  /**
   * Number of neighbours that current atoms has, independently whether are occupied or not.
   * 
   * @return fixed number of neighbours.
   */
  @Override
  public final int getNumberOfNeighbours() {
    return numberOfNeighbours;
  }

  @Override
  public final void setNumberOfNeighbours(int numberOfNeighbours) {
    this.numberOfNeighbours = numberOfNeighbours;
  }

  @Override
  public void setNeighbour(AbstractSite lattice, int i) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  /**
   * If not overridden, returns directly the type of current atom.
   * @return atom type of current atom. 
   */
  @Override
  public byte getRealType() {
    return getType();
  }
  
  /**
   * Returns the position within the unit cell, (0,0) in this case.
   * @return coordinates in unit cell
   */
  @Override
  public Point3D getPos() {
    return new Point3D(0, 0, 0);
  }
}
