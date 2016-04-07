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

  private int hexaSizeI;
  private int hexaSizeJ;
  private int hexaSizeK;
  private int unitCellSize;

  public int size() {
    return hexaSizeI * hexaSizeJ * hexaSizeK;
  }
  
  public final int getHexaSizeI() {
    return hexaSizeI;
  }

  public final int getHexaSizeJ() {
    return hexaSizeJ;
  }

  public final int getHexaSizeK() {
    return hexaSizeK;
  }
  
  public final void setHexaSizeI(int hexaSizeI) {
    this.hexaSizeI = hexaSizeI;
  }

  public final void setHexaSizeJ(int hexaSizeJ) {
    this.hexaSizeJ = hexaSizeJ;
  }

  public final void setHexaSizeK(int hexaSizeK) {
    this.hexaSizeK = hexaSizeK;
  }
 
  /**
   * @return the unitCellSize
   */
  public final int getUnitCellSize() {
    return unitCellSize;
  }

  /**
   * @param unitCellSize the unitCellSize to set
   */
  public final void setUnitCellSize(int unitCellSize) {
    this.unitCellSize = unitCellSize;
  }


  public abstract AbstractAtom getAtom(int i, int j, int k, int unitCellPos);

  public abstract void reset();

  public void setProbabilities(double[] rates) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }


}
