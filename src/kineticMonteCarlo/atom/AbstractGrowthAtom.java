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
  /**
   * Stores when the atom has been deposited. It has to be moved with the corresponding diffusion.
   */
  private double depositionTime;
  private int islandNumber;
  private boolean visited;
  
  public AbstractGrowthAtom(short iHexa, short jHexa, int numberOfNeighbours) {

    this.occupied = false;
    this.outside = true;
    this.iHexa = iHexa;
    this.jHexa = jHexa;
    
    this.setNumberOfNeighbours(numberOfNeighbours);
    this.bondsProbability = new double[numberOfNeighbours];
    multiplier = 1;
    islandNumber = 0;
    visited = false;
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
  
  /**
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   *
   * @param probabilities Default rates
   */
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

  /**
   * 
   * @return hexagonal i coordinate
   */
  public short getiHexa() {
    return iHexa;
  }

  /**
   * 
   * @return hexagonal j coordinate
   */
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

  /**
   * Stores when the atom has been deposited. It is defined first when an atom is deposited and it
   * has to be moved with the corresponding diffusion.
   * @param time deposition time or former time
   */
  public void setDepositionTime(double time) {
    depositionTime = time;
  }
  
  /**
   * 
   * @return when the atom has been deposited.
   */
  public double getDepositionTime() {
    return depositionTime;
  }
  
  public void setIslandNumber(int islandNumber) {
    this.islandNumber = islandNumber;
  }
  
  public int getIslandNumber() {
    return islandNumber;
  }
  
  /**
   * When counting islands, we have to keep track of the visited atoms.
   * 
   * @return whether current atoms has been previously visited
   */
  public boolean isVisited() {
    return visited;
  }

  /**
   * When counting islands, we have to keep track of the visited atoms.
   *
   * @param visited whether current atoms is visited
   */
  public void setVisited(boolean visited) {
    this.visited = visited;
  }
  
  /**
   * Checks if the current atom has occupied neighbours.
   *
   * @return true if the current atoms has no any occupied neighbour, else otherwise.
   */
  public boolean isIsolated() {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      if (getNeighbour(i).isOccupied()) {
        return false;
      }
    }
    return true;
  }
}
