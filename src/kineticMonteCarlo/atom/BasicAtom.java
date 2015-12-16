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
    this.neighbours = new BasicAtom[getNumberOfNeighbours()];
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

  @Override
  public void setAsBulk() {
    type = 3;
  }

  /**
   * This was updateTypeFromScratch().
   */
  @Override
  public void updateN1FromScratch() {
    type = 0;
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      if (neighbours[i] != null && !neighbours[i].isRemoved()) {
        type++;
      }
    }
  }

  public void remove1st() {
    type--;
    if (type < 3 && !isRemoved() && list != null) {
      list.addTotalProbability(getProbabilities()[type] - getProbabilities()[type + 1]);
    }
  }

  @Override
  public void remove() {
    if (!isRemoved()) {
      if (list != null) {
        list.addTotalProbability(-getProbabilities()[type]);
      }
      remove();
      for (int i = 0; i < getNumberOfNeighbours(); i++) {
        if (neighbours[i] != null) {
          neighbours[i].remove1st();
        }
      }
    }
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
