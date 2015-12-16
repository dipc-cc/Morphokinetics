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
  private SiAtom neighbour0;
  private SiAtom neighbour1;
  private SiAtom neighbour2;
  private SiAtom neighbour3;

  private byte n1;
  private byte n2;
  private float x;
  private float y;
  private float z;

  public SiAtom(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.numberOfNeighbours = 4;
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

  @Override
  public void setNeighbour(AbstractAtom a, int pos) {
    switch (pos) {
      case 0:
        neighbour0 = (SiAtom) a;
        break;
      case 1:
        neighbour1 = (SiAtom) a;
        break;
      case 2:
        neighbour2 = (SiAtom) a;
        break;
      default:
        neighbour3 = (SiAtom) a;
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

  @Override
  public void updateN1FromScratch() {
    n1 = 0;
    for (int i = 0; i < 4; i++) {
      if (getNeighbour(i) != null && !getNeighbour(i).isRemoved()) {
        n1++;
      }
    }
  }

  @Override
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

  @Override
  public void setAsBulk() {
    n1 = 4;
    n2 = 12;
  }

  @Override
  public void remove() {
    if (!isRemoved()) {
      if (n1 < 4 && list != null) {
        list.addTotalProbability(-probabilities[n1 * 16 + n2]);
      }
      remove();
      for (int i = 0; i < numberOfNeighbours; i++) {
        SiAtom atom1st = getNeighbour(i);
        if (atom1st != null) {
          atom1st.remove1st();
          for (int j = 0; j < numberOfNeighbours; j++) {
            SiAtom atom2nd = atom1st.getNeighbour(j);
            if (atom2nd != null && atom2nd != this && !atom2nd.isRemoved()) {
              atom2nd.remove2nd();
            }
          }
        }
      }
    }
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
