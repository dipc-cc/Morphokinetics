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

  protected IProbabilityHolder list;
  protected double[] probabilities;
  protected int numberOfNeighbours;

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

  public void initialize(double[] probabilities) {
    this.probabilities = probabilities;
  }

  public void setOnList(IProbabilityHolder list) {
    this.list = list;
  }

  public boolean isOnList() {
    return list != null;
  }

  public abstract double getProbability();

  public abstract boolean isEligible();

  public abstract boolean isRemoved();

  public abstract byte getType();

  public int getNumberOfNeighbours() {
    return numberOfNeighbours;
  }

}
