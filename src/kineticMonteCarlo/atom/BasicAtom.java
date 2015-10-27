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
    this.numberOfNeighbours = 4;
    this.neighbours = new BasicAtom[numberOfNeighbours];
    this.removed = false;
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

  public void updateTypeFromScratch() {
    type = 0;
    for (int i = 0; i < numberOfNeighbours; i++) {
      if (neighbours[i] != null && !neighbours[i].isRemoved()) {
        type++;
      }
    }
  }

  public void remove1st() {
    type--;
    if (type < 3 && !removed && list != null) {
      list.addTotalProbability(probabilities[type] - probabilities[type + 1]);
    }
  }

  @Override
  public void remove() {
    if (!removed) {
      if (list != null) {
        list.addTotalProbability(-probabilities[type]);
      }
      removed = true;
      for (int i = 0; i < numberOfNeighbours; i++) {
        if (neighbours[i] != null) {
          neighbours[i].remove1st();
        }
      }
    }
  }

  @Override
  public double getProbability() {
    return probabilities[type];
  }

  @Override
  public boolean isEligible() {
    return (type >= 0 && type < 4);
  }
}
