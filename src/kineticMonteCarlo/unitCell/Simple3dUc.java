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
import kineticMonteCarlo.site.AbstractSite;
import static kineticMonteCarlo.unitCell.IUc.SIZE_X;
import static kineticMonteCarlo.unitCell.IUc.SIZE_Y;
import static kineticMonteCarlo.unitCell.IUc.SIZE_Z;

/**
 * Really simple unit cell, which will contain only one atom.
 *
 * @author J. Alberdi-Rodriguez
 */
public class Simple3dUc implements IUc{

  private final AbstractSite atom;
  private final int size; // how many atoms
  private final int posI; // index in X axis
  private final int posJ; // index in Y axis
  private final int posK; // index in Z axis
  
  private double posX;
  private double posY;
  private double posZ;

  public Simple3dUc(int posI, int posJ, AbstractSite atom) {
    this.size = 1;
    this.posI = posI;
    this.posJ = posJ;
    this.posK = 0;
    this.atom = atom;
  }
  
  public Simple3dUc(int posI, int posJ, int posK, AbstractSite atom) {
    this.size = 1;
    this.posI = posI;
    this.posJ = posJ;
    this.posK = posK;
    this.atom = atom;
  }

  /**
   * Always returns the current atom.
   *
   * @param pos ignored.
   * @return current atom.
   */
  @Override
  public AbstractSite getSite(int pos) {
    return atom;
  }

  @Override
  public Point3D getPos() {
    return new Point3D(SIZE_X * posX, SIZE_Y * posY, SIZE_Z * posZ);
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
  
  public void setPosZ(double z) {
    posZ = z;
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
}
