/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.lattice.AbstractGrowthLattice;

/**
 *
 * @author Nestor
 */
public abstract class AbstractGrowthAtom extends AbstractAtom {
  /** TODO document the types and change them to constants
   * 
   */
  private byte type;
  private double[][] probabilities;
  private double totalProbability;
  private double[] bondsProbability;
  private float angle;
  private boolean occupied;
  private boolean outside;
  private final short iHexa;
  private final short jHexa;
  private int multiplier;
  private ModifiedBuffer modified;

  public AbstractGrowthAtom(short iHexa, short jHexa, int numberOfNeighbours) {

    this.occupied = false;
    this.outside = true;
    this.iHexa = iHexa;
    this.jHexa = jHexa;
    
    this.setNumberOfNeighbours(numberOfNeighbours);
    this.bondsProbability = new double[numberOfNeighbours];
    multiplier = 1;
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

    AbstractGrowthAtom other = (AbstractGrowthAtom) obj;
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

  public abstract AbstractGrowthAtom chooseRandomHop();

  public abstract int getOrientation();

  public void updateAllRates() {
    double tmp = -getTotalProbability();
    resetTotalProbability();

    if (this.isEligible()) {
      obtainRatesFromNeighbours(areAllRatesTheSame());
      tmp += getTotalProbability();
    }
    if (this.isOnList()) {
      addTotalProbability(tmp);
    }
  }
  
  abstract void obtainRatesFromNeighbours(boolean equalRates);
  abstract boolean areAllRatesTheSame();
  
  public abstract void updateOneBound(int bond);

  public abstract void clear();
  
  public void initialise(double[][] probabilities, ModifiedBuffer modified) {
    this.probabilities = probabilities;
    this.modified = modified;
  }
  
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

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

  public short getiHexa() {
    return iHexa;
  }

  public short getjHexa() {
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

  public boolean isOccupied() {
    return occupied;
  }
  
  public void setOccupied(boolean occupied) {
    this.occupied = occupied;
  }

  @Override
  public byte getType() {
    return type;
  }

  public void setType(byte type) {
    this.type = type;
  }
  
  public void setMultiplier(int multiplier) {
    this.multiplier = multiplier;
  }

  public int getMultiplier() {
    return multiplier;
  }

  void addOwnAtom() {
    modified.addOwnAtom(this);
  }
  
  void addBondAtom() {
    modified.addBondAtom(this);
  }

  /**
   * @return the bondsProbability
   */
  public double[] getBondsProbability() {
    return bondsProbability;
  }

  /**
   * @param bondsProbability the bondsProbability to set
   */
  public void setBondsProbability(double[] bondsProbability) {
    this.bondsProbability = bondsProbability;
  }

  @Override
  public double getProbability() {
    return totalProbability;
  }
  
  public double getTotalProbability() {
    return totalProbability;
  }
  
  public void resetTotalProbability() {
    totalProbability = 0;
  }
  
  public void addToTotalProbability(double probability) {
    totalProbability += probability;
  }

  /**
   * @return the probabilities
   */
  public double getProbability(int x, int y) {
    return probabilities[x][y];
  }
  
  public double getProbability(int pos) {
    if (getBondsProbability() != null) {
      return getBondsProbability()[pos];
    } else {
      return totalProbability / getNumberOfNeighbours();
    }
  }
}
