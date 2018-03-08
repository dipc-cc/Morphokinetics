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
public class BasicSite extends AbstractSite {

  private BasicSite[] neighbours;
  private byte type;
  private final short x;
  private final short y;

  public BasicSite(short x, short y) {
    this.x = x;
    this.y = y;
    setNumberOfNeighbours(4);
    neighbours = new BasicSite[getNumberOfNeighbours()];
  }
  
  public short getX() {
    return x;
  }

  public short getY() {
    return y;
  }
  
  @Override
  public Point3D getPos() {
    return new Point3D(x, y, 0);
  }

  @Override
  public void setNeighbour(AbstractSite a, int pos) {
    neighbours[pos] = (BasicSite) a;
  }

  public BasicSite getNeighbour(int pos) {
    return neighbours[pos];
  }

  @Override
  public byte getType() {
    return type;
  }

  public void setAsBulk() {
    type = 3;
  }

  /**
   * This was updateTypeFromScratch().
   */
  public void updateN1FromScratch() {
    type = 0;
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      if (neighbours[i] != null && !neighbours[i].isRemoved()) {
        type++;
      }
    }
  }

  public double remove1st() {
    type--;
    if (type < 3 && !isRemoved() && isOnList()) {
      return getProbabilities()[type] - getProbabilities()[type + 1];
    }
    return 0;
  }

  @Override
  public double remove() {
    double probabilityChange = 0;
    if (!isRemoved()) {
      if (isOnList()) {
       probabilityChange += -getProbabilities()[type];
      }
      setRemoved();
      for (int i = 0; i < getNumberOfNeighbours(); i++) {
        if (neighbours[i] != null) {
          probabilityChange += neighbours[i].remove1st();
        }
      }
    }
    return probabilityChange;
  }

  public double getProbability() {
    return getProbabilities()[type];
  }

  @Override
  public boolean isEligible() {
    return (type >= 0 && type < 4);
  }
}
