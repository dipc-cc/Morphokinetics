/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom.diffusion;

import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;

/**
 *
 * @author Nestor
 */
public abstract class Abstract2DDiffusionAtom extends AbstractAtom {

  public Abstract2DDiffusionAtom(short X, short Y, HopsPerStep distancePerStep) {

    this.X = X;
    this.Y = Y;
    
    bondsProbability = new double[6];
    this.distancePerStep = distancePerStep;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + X;
    result = prime * result + Y;
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    Abstract2DDiffusionAtom other = (Abstract2DDiffusionAtom) obj;
    if (X != other.X) {
      return false;
    }
    if (Y != other.Y) {
      return false;
    }
    return true;
  }

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
  protected short X, Y;
  protected int multiplier = 1;
  protected ModifiedBuffer modified;
  protected HopsPerStep distancePerStep;

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
    return X;
  }

  public short getY() {
    return Y;
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
