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
public class SiSite extends AbstractSite {

  //we reduce the amount of memory use by not using an array neighbour[4] and directly adding the neighbours as part of the object
  private SiSite neighbour0;
  private SiSite neighbour1;
  private SiSite neighbour2;
  private SiSite neighbour3;

  /**
   * Number of 1st neighbours.
   */
  private byte n1;
  /**
   * Number of 2nd neighbours.
   */
  private byte n2;
  private final double x;
  private final double y;
  private final double z;
  private float limitX;
  private float limitY;
  private float limitZ;
  private short id;

  public SiSite(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    setNumberOfNeighbours(4);
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }
  
  @Override
  public Point3D getPos() {
    return new Point3D(x, y, z);
  }    

  @Override
  public SiSite getNeighbour(int pos) {
    switch (pos) {
      case 0:
        return neighbour0;
      case 1:
        return neighbour1;
      case 2:
        return neighbour2;
      default:
        return neighbour3;
    }
  }

  @Override
  public void setNeighbour(AbstractSite atom, int pos) {
    switch (pos) {
      case 0:
        neighbour0 = (SiSite) atom;
        break;
      case 1:
        neighbour1 = (SiSite) atom;
        break;
      case 2:
        neighbour2 = (SiSite) atom;
        break;
      default:
        neighbour3 = (SiSite) atom;
        break;
    }
  }

  @Override
  public byte getType() {
    return (byte) ((n1 << 4) + n2);
  }

  /**
   * Number of 1st neighbours.
   * @return number of 1st neighbours.
   */
  public byte getN1() {
    return n1;
  }

  /**
   * Number of 2nd neighbours.
   * @return number of 2nd neighbours.
   */
  public byte getN2() {
    return n2;
  }

  private double remove1st() {
    n1--;
    if (n1 < 3 && isOnList()) {
      return getProbabilities()[n1 * 16 + n2] - getProbabilities()[(n1 + 1) * 16 + n2];
    }
    return 0;
  }

  private double remove2nd() {
    n2--;
    if (n1 < 4 && isOnList()) {
      return getProbabilities()[n1 * 16 + n2] - getProbabilities()[n1 * 16 + n2 + 1];
    }
    return 0;
  }

  public void updateN1FromScratch() {
    n1 = 0;
    for (int i = 0; i < 4; i++) {
      if (getNeighbour(i) != null && !getNeighbour(i).isRemoved()) {
        n1++;
      }
    }
  }

  public void updateN2FromScratch() {
    n2 = 0;
    for (int i = 0; i < 4; i++) {
      if (getNeighbour(i) != null) {
        n2 += getNeighbour(i).getN1();
        if (!isRemoved()) {
          n2--;
        }
      }
    }
  }

  public void setAsBulk() {
    n1 = 4;
    n2 = 12;
  }

  @Override
  public double remove() {
    double probabilityChange = 0;
    if (!isRemoved()) {
      if (n1 < 4 && isOnList()) {
        probabilityChange += -getProbabilities()[n1 * 16 + n2];
      }
      setRemoved();
      for (int i = 0; i < getNumberOfNeighbours(); i++) {
        SiSite atom1st = getNeighbour(i);
        if (atom1st != null) {
          probabilityChange += atom1st.remove1st();
          for (int j = 0; j < getNumberOfNeighbours(); j++) {
            SiSite atom2nd = atom1st.getNeighbour(j);
            if (atom2nd != null && atom2nd != this && !atom2nd.isRemoved()) {
              probabilityChange += atom2nd.remove2nd();
            }
          }
        }
      }
    }
    return probabilityChange;
  }
  
  @Override
  public double getProbability() {
    return getProbabilities()[n1 * 16 + n2];
  }

  @Override
  public boolean isEligible() {
    return getProbabilities()[n1 * 16 + n2] > 0 && getProbabilities()[n1 * 16 + n2] < 4;
  }
  
  /**
   * Initialises limits in X, Y and Z.
   * 
   * @param limitX_a
   * @param limitY_a
   * @param limitZ_a
   */
  public void initialiseLimits(double limitX_a, double limitY_a, double limitZ_a) {
    limitX = (float) limitX_a;
    limitY = (float) limitY_a;
    limitZ = (float) limitZ_a;
  }
  
  public float getLimitX() {
    return limitX;
  }

  public float getLimitY() {
    return limitY;
  }

  public float getLimitZ() {
    return limitZ;
  }
  
  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }
}
