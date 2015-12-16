/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import utils.list.IProbabilityHolder;

/**
 *
 * @author Nestor
 */
public abstract class AbstractAtom {

  private IProbabilityHolder list;
  private double[] probabilities;
  private int numberOfNeighbours;
  private boolean removed = false;

  // Atoms types
  public static final int TERRACE = 0;
  public static final int CORNER = 1;
  public static final int EDGE = 2;
  public static final int ZIGZAG_EDGE = 2;
  public static final int ARMCHAIR_EDGE = 3;
  public static final int ZIGZAG_WITH_EXTRA = 4;
  public static final int SICK = 5;
  public static final int KINK = 6;
  public static final int BULK = 7;
  
  public void setProbabilities(double[] probabilities) {
    this.probabilities = probabilities;
  }

  public double[] getProbabilities() {
    return probabilities;
  }

  public void setOnList(IProbabilityHolder list) {
    this.list = list;
  }

  public boolean isOnList() {
    return list != null;
  }

  public abstract double getProbability();

  public abstract boolean isEligible();

  public boolean isRemoved() {
    return removed;
  }

  public void unRemove() {
    removed = false;
  }
  
  public abstract byte getType();

  public final int getNumberOfNeighbours() {
    return numberOfNeighbours;
  }

  public final void setNumberOfNeighbours(int numberOfNeighbours) {
    this.numberOfNeighbours = numberOfNeighbours;
  }

  public void setAsBulk() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void updateN1FromScratch() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void updateN2FromScratch() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void remove() {
    removed = true;
  }

  public void setNeighbour(AbstractAtom lattice, int i) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public IProbabilityHolder getList() {
    return list;
  }

  public void setList(IProbabilityHolder list) {
    this.list = list;
  }
  
  public boolean isListNull() {
    return list == null;
  } 
}
