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
import kineticMonteCarlo.atom.AbstractSite;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public interface IUc {
  
  static final float SIZE_X = 1; // Cartesian size X per unit
  static final float SIZE_Y = 1; // Cartesian size Y per unit
  static final float SIZE_Z = 1; // Cartesian size Z per unit
  
  /**
   * Returns the site of the given position; an atom or an empty site.
   * 
   * @param pos position inside the unit cell.
   * @return Site object.
   */
  public AbstractSite getSite(int pos);
  
  /**
   * Cartesian size of the unit cell in X axis
   *
   * @return size in X
   */
  public static float getSizeX() {
    return SIZE_X;
  }

  /**
   * Cartesian size of the unit cell in Y axis
   *
   * @return size in Y
   */
  public static float getSizeY() {
    return SIZE_Y;
  }

  /**
   * Cartesian size of the unit cell in Z axis
   *
   * @return size in Z
   */
  public static float getSizeZ() {
    return SIZE_Z;
  }

  public int getPosI();

  public int getPosJ();
  
  /**
   * Cartesian position of the origin of the unit cell.
   *
   * @return a point with 3 coordinates.
   */
  public Point3D getPos();

  /**
   * Number of elements.
   *
   * @return quantity of unit cells
   */
  public int size();
}
