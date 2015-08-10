/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;

/**
 *
 * @author Nestor
 */
public abstract class Abstract2DDiffusionAtom extends AbstractAtom {
  /** TODO document the types and change them to constants
   * 
   */
  protected byte type;
  protected double[][] probabilities;
  protected double totalProbability;
  protected double[] bondsProbability;
  protected float angle;
  protected boolean occupied = false;
  protected boolean outside = true;
  protected short iHexa;
  protected short jHexa;
  protected int multiplier = 1;
  protected ModifiedBuffer modified;
  protected HopsPerStep distancePerStep;

  public Abstract2DDiffusionAtom(short X, short Y, HopsPerStep distancePerStep) {

    this.iHexa = X;
    this.jHexa = Y;
    
    this.distancePerStep = distancePerStep;
    this.bondsProbability = null;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + iHexa;
    result = prime * result + jHexa;
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    Abstract2DDiffusionAtom other = (Abstract2DDiffusionAtom) obj;
    if (iHexa != other.iHexa) {
      return false;
    }
    if (jHexa != other.jHexa) {
      return false;
    }
    return true;
  }


  public abstract byte getTypeWithoutNeighbour(int neighPos);

  public abstract void deposit(boolean forceNucleation);

  public abstract void extract();

  public abstract boolean areTwoTerracesTogether();

  public abstract Abstract2DDiffusionAtom chooseRandomHop();

  public abstract int getOrientation();

  public abstract void updateAllRates();

  public abstract void updateOneBound(int bond);

  public abstract void clear();

  public abstract void initialize(Abstract2DDiffusionLattice lattice, double[][] probabilities, ModifiedBuffer modified);

  @Override
  public boolean isRemoved() {
    return !occupied;
  }

  public short getX() {
    return iHexa;
  }

  public short getY() {
    return jHexa;
  }

  public float getAngle() {
    return angle;
  }

  public void setAngle(float angle) {
    this.angle = angle;
  }

  public boolean isOutside() {
    return outside;
  }

  public void setOutside(boolean outside) {
    this.outside = outside;
  }

  @Override
  public double getProbability() {
    return totalProbability;
  }

  public boolean isOccupied() {
    return occupied;
  }

  public double getProbability(int pos) {
    if (bondsProbability != null) {
      return bondsProbability[pos];
    } else {
      return totalProbability / getNeighbourCount();
    }
  }

  @Override
  public byte getType() {
    return type;
  }

  public void setMultiplier(int multiplier) {

    this.multiplier = multiplier;
  }

  public float multiplier() {
    return multiplier;
  }

  public abstract int getNeighbourCount();

  protected void initialize(double[][] probabilities, ModifiedBuffer modified) {
    this.probabilities = probabilities;
    this.modified = modified;
  }

}
