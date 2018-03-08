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
 * Really simple unit cell, which will contain only one atom.
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractSurfaceUc implements IUc {

  private final AbstractSurfaceSite atom;
  private final int size; // how many atoms
  private final int posI; // index in X axis
  private final int posJ; // index in Y axis
  
  private double posX;
  private double posY;
  
  private float sizX;
  private float sizY;

  public AbstractSurfaceUc(int posI, int posJ, AbstractSurfaceSite atom) {
    this.size = 1;
    this.posI = posI;
    this.posJ = posJ;
    this.atom = atom;
    sizX = 1.0f;
    sizY = 1.0f;
  }

  /**
   * Always returns the current atom.
   *
   * @param pos ignored.
   * @return current atom.
   */
  @Override
  public AbstractSurfaceSite getSite(int pos) {
    return atom;
  }

  @Override
  public Point3D getPos() {
    return new Point3D(getSizeX() * posX, getSizeY() * posY, 0);
  }

  @Override
  public int getPosI() {
    return posI;
  }

  @Override
  public int getPosJ() {
    return posJ;
  }
  
  public void setPosX(double x) {
    posX = x;
  }
  
  public void setPosY(double y) {
    posY = y;
  }
  
  /**
   * Number of elements.
   *
   * @return quantity of unit cells.
   */
  @Override
  public int size() {
    return size;
  }
  
  /**
   * Cartesian size of the unit cell in X axis
   *
   * @return size in X
   */
  @Override
  public float getSizeX() {
    return sizX;
  }

  /**
   * Cartesian size of the unit cell in Y axis
   *
   * @return size in Y
   */
  @Override
  public float getSizeY() {
    return sizY;
  }

  @Override
  public float getSizeZ() {
    return 0.0f;
  }
  
  public final void setSizeX(float x) {
    sizX = x;
  }
  
  public final void setSizeY(float y) {
    sizY = y;
  }
}
