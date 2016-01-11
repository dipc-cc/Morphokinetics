/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

/**
 *
 * @author Nestor
 */
public abstract class AbstractGrowthAtom extends AbstractAtom {
  /** TODO document the types and change them to constants
   * 
   */
  private byte type;
  /**
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   */
  private double[][] probabilities;
  private double probability;
  private double[] bondsProbability;
  private float angle;
  private boolean occupied;
  private boolean outside;
  private final short iHexa;
  private final short jHexa;
  private int multiplier;

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

  public abstract boolean areTwoTerracesTogether();

  public abstract AbstractGrowthAtom chooseRandomHop();

  public abstract int getOrientation();
  
  /**
   * If current atom is not eligible (thus, is immobile) return its probability in negative. Its
   * probability is then set to zero.
   *
   * If the current atom is eligible, update its probability with its neighbours.
   *
   * @return a probability change
   */
  public double updateRate() {
    double tmp = -getProbability();
    resetProbability();

    if (this.isEligible()) {
      obtainRateFromNeighbours();
      tmp += getProbability();
    }
    return tmp;
  }
  public abstract void obtainRateFromNeighbours();
  
  public abstract void setNeighbour(AbstractGrowthAtom a, int pos);

  public abstract AbstractGrowthAtom getNeighbour(int pos);
  
  public abstract double updateOneBound(int bond);

  public abstract void clear();
  
  public void initialise(double[][] probabilities) {
    this.probabilities = probabilities;
  }
  
  @Override
  public double remove() {
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
    return probability;
  }
  
  public void resetProbability() {
    probability = 0;
  }
  
  public void addProbability(double probability) {
    this.probability += probability;
  }
  
  /**
   * Returns the predefined probability (from rates) to jump from origin type to target type.
   *
   * @param originType origin type, from where the atom is going to jump.
   * @param targetType target type, to where the atom is going to jump.
   * @return the probabilities
   */
  public double getProbability(int originType, int targetType) {
    return probabilities[originType][targetType];
  }
  
  public double getProbability(int pos) {
    if (getBondsProbability() != null) {
      return getBondsProbability()[pos];
    } else {
      return probability / getNumberOfNeighbours();
    }
  }
}
