/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractAtom;

/**
 *
 * @author Nestor
 */
public abstract class AbstractLattice {

  protected int axonSizeI;
  protected int axonSizeJ;
  protected int axonSizeK;

  protected int unitCellSize;

  public int getAxonSizeI() {
    return axonSizeI;
  }

  public int getAxonSizeJ() {
    return axonSizeJ;
  }

  public int getAxonSizeK() {
    return axonSizeK;
  }

  public int getSizeUC() {
    return unitCellSize;
  }

  public abstract AbstractAtom getAtom(int i, int j, int k, int unitCellPos);

  public abstract void reset();

}
