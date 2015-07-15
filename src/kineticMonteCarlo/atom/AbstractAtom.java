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

}
