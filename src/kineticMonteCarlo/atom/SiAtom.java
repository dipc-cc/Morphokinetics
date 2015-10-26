/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

/**
 *
 * @author U010531
 */
public class SiAtom extends AbstractAtom {

  //we reduce the amount of memory use by not using an array neighbour[4] and directly adding the neighbours as part of the object
  protected SiAtom neighbour0;
  protected SiAtom neighbour1;
  protected SiAtom neighbour2;
  protected SiAtom neighbour3;

  protected byte n1;
  protected byte n2;
  protected boolean removed = false;
  private float x, y, z;

  public SiAtom(float x, float y, float z) {

    this.x = x;
    this.y = y;
    this.z = z;
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

  public void setNeighbour(SiAtom a, int pos) {
    switch (pos) {
      case 0:
        neighbour0 = a;
        break;
      case 1:
        neighbour1 = a;
        break;
      case 2:
        neighbour2 = a;
        break;
      default:
        neighbour3 = a;
        break;
    }
  }

  @Override
  public byte getType() {
    return (byte) ((n1 << 4) + n2);
  }

  public byte getN1() {
    return n1;
  }

  public byte getN2() {
    return n2;
  }

  @Override
  public boolean isRemoved() {
    return removed;
  }

  private void remove1st() {
    n1--;
    if (n1 < 3 && list != null) {
      list.addTotalProbability(probabilities[n1 * 16 + n2] - probabilities[(n1 + 1) * 16 + n2]);
    }
  }

  private void remove2nd() {
    n2--;
    if (n1 < 4 && list != null) {
      list.addTotalProbability(probabilities[n1 * 16 + n2] - probabilities[n1 * 16 + n2 + 1]);
    }
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
        if (!removed) {
          n2--;
        }
      }
    }
  }

  public void setAsBulk() {
    n1 = 4;
    n2 = 12;
  }

  public void remove() {
    if (!removed) {
      if (n1 < 4 && list != null) {
        list.addTotalProbability(-probabilities[n1 * 16 + n2]);
      }

      removed = true;

      for (int i = 0; i < 4; i++) {
        SiAtom atom1st = getNeighbour(i);
        if (atom1st != null) {
          atom1st.remove1st();
          for (int j = 0; j < 4; j++) {
            SiAtom atom2nd = atom1st.getNeighbour(j);
            if (atom2nd != null && atom2nd != this && !atom2nd.isRemoved()) {
              atom2nd.remove2nd();
            }
          }
        }
      }
    }
  }

  public void unRemove() {
    removed = false;
  }

  @Override
  public double getProbability() {
    return probabilities[n1 * 16 + n2];
  }

  @Override
  public boolean isEligible() {
    return probabilities[n1 * 16 + n2] > 0 && probabilities[n1 * 16 + n2] < 4;
  }
}
