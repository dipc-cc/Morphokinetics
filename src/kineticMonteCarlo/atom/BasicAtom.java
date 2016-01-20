/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

/**
 *
 * @author Nestor
 */
public class BasicAtom extends AbstractAtom {

  private BasicAtom[] neighbours;
  private byte type;
  private short x;
  private short y;

  public BasicAtom(short x, short y) {
    this.x = x;
    this.y = y;
    setNumberOfNeighbours(4);
    neighbours = new BasicAtom[getNumberOfNeighbours()];
  }
  
  public short getX() {
    return x;
  }

  public short getY() {
    return y;
  }

  @Override
  public void setNeighbour(AbstractAtom a, int pos) {
    neighbours[pos] = (BasicAtom) a;
  }

  public BasicAtom getNeighbour(int pos) {
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

  @Override
  public double getProbability() {
    return getProbabilities()[type];
  }

  @Override
  public boolean isEligible() {
    return (type >= 0 && type < 4);
  }
}
