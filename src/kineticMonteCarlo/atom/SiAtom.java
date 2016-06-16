/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import javafx.geometry.Point3D;

/**
 *
 * @author U010531
 */
public class SiAtom extends AbstractAtom {

  //we reduce the amount of memory use by not using an array neighbour[4] and directly adding the neighbours as part of the object
  private SiAtom neighbour0;
  private SiAtom neighbour1;
  private SiAtom neighbour2;
  private SiAtom neighbour3;

  /**
   * Number of 1st neighbours.
   */
  private byte n1;
  /**
   * Number of 2nd neighbours.
   */
  private byte n2;
  private final float x;
  private final float y;
  private final float z;

  public SiAtom(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
    setNumberOfNeighbours(4);
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public float getZ() {
    return z;
  }
  
  @Override
  public Point3D getPos() {
    return new Point3D(x, y, z);
  }    

  public SiAtom getNeighbour(int pos) {
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
  public void setNeighbour(AbstractAtom atom, int pos) {
    switch (pos) {
      case 0:
        neighbour0 = (SiAtom) atom;
        break;
      case 1:
        neighbour1 = (SiAtom) atom;
        break;
      case 2:
        neighbour2 = (SiAtom) atom;
        break;
      default:
        neighbour3 = (SiAtom) atom;
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
        SiAtom atom1st = getNeighbour(i);
        if (atom1st != null) {
          probabilityChange += atom1st.remove1st();
          for (int j = 0; j < getNumberOfNeighbours(); j++) {
            SiAtom atom2nd = atom1st.getNeighbour(j);
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
}
