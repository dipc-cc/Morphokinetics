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

  protected int hexaSizeI;
  protected int hexaSizeJ;
  protected int hexaSizeK;

  protected int unitCellSize;

  public int getHexaSizeI() {
    return hexaSizeI;
  }

  public int getHexaSizeJ() {
    return hexaSizeJ;
  }

  public int getHexaSizeK() {
    return hexaSizeK;
  }

  public int getSizeUC() {
    return unitCellSize;
  }

  public abstract AbstractAtom getAtom(int i, int j, int k, int unitCellPos);

  public abstract void reset();

  public void setProbabilities(double[] rates) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
