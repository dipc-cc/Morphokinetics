/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import javafx.geometry.Point3D;

/**
 *
 * @author Nestor
 */
public abstract class AbstractGrowthAtom extends AbstractAtom {
  /** TODO document the types and change them to constants
   * 
   */
  /**
   * Type of the atom. Some examples are; TERRACE, EDGE, KINK, ISLAND... Precise types are defined
   * in child classes.
   */
  private byte type;
  /**
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   */
  private double[][] probabilities;
  /**
   * Should be the sum of neighbour probabilities.
   */
  private double probability;
  /**
   * Current probabilities to jump to neighbour position.
   */
  private double[] bondsProbability;
  private double angle;
  private boolean outside;
  /**
   * Hexagonal i coordinate.
   */
  private final short iHexa;
  /**
   * Hexagonal j coordinate.
   */
  private final short jHexa;
  private int multiplier;
  /**
   * If current atom belong to an island, its number is stored, otherwise is 0.
   */
  private int islandNumber;
  /**
   * Helper attribute used when counting islands, to check if current atom has
   * been already visited.
   */
  private boolean visited;
  /**
   * Unique identifier.
   */
  private final int id;
  
  private boolean innerPerimeter;
  private boolean outerPerimeter;
  /**
   * Number of hops that atom has done. How many steps the atom has moved.
   */
  private int hops;
  private Point3D cartesianPosition;
  private Point3D cartesianSuperCell;
  private AbstractGrowthAtomAttributes attributes;
  
  public AbstractGrowthAtom(int id, short iHexa, short jHexa, int numberOfNeighbours) {
    this.id = id;
    setOccupied(false);
    outside = false;
    this.iHexa = iHexa;
    this.jHexa = jHexa;
    
    setNumberOfNeighbours(numberOfNeighbours);
    bondsProbability = new double[numberOfNeighbours];
    multiplier = 1;
    islandNumber = 0;
    visited = false;
    innerPerimeter = false;
    outerPerimeter = false;
    hops = 0;
    cartesianSuperCell = new Point3D(0, 0, 0);
    attributes = new AbstractGrowthAtomAttributes();
  }
  
  /**
   * Dummy constructor to be able to have a proper AgAtom(pos) constructor.
   * 
   * @param id atom identifier.
   * @param numberOfNeighbours number of neighbours that each atom has.
   */
  public AbstractGrowthAtom(int id, int numberOfNeighbours) {
    this.id = id;
    iHexa = 0;
    jHexa = 0;
    bondsProbability = new double[numberOfNeighbours];
    setNumberOfNeighbours(numberOfNeighbours);
    hops = 0;
    cartesianSuperCell = new Point3D(0, 0, 0);
    attributes = new AbstractGrowthAtomAttributes();
    
  }
  
  public AbstractGrowthAtomAttributes getAttributes() {
    return attributes;
  }
  
  public void setAttributes(AbstractGrowthAtomAttributes attributes) {
    this.attributes = attributes;
  }
  
  public int getId() {
    return id;
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

  public Point3D getCartesianPosition() {
    return cartesianPosition;
  }

  public void setCartesianPosition(Point3D cartesianPosition) {
    this.cartesianPosition = cartesianPosition;
  }

  public Point3D getCartesianSuperCell() {
    return cartesianSuperCell;
  }

  public void setCartesianSuperCell(Point3D cartesianSuperCell) {
    this.cartesianSuperCell = cartesianSuperCell;
  }
  
  public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }

  public boolean isOutside() {
    return outside;
  }

  public void setOutside(boolean outside) {
    this.outside = outside;
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
   * @return the bondsProbability.
   */
  public double[] getBondsProbability() {
    return bondsProbability;
  }
  
  /**
   * Get probability in the given neighbour position.
   *
   * @param i neighbour position.
   * @return probability (rate).
   */
  public double getBondsProbability(int i) {
    return bondsProbability[i];
  }

  /**
   * @param bondsProbability the bondsProbability to set.
   */
  public void setBondsProbability(double[] bondsProbability) {
    this.bondsProbability = bondsProbability;
  }
  
  /**
   * Set the given probability in the given neighbour position.
   *
   * @param value probability (rate).
   * @param i neighbour position.
   */
  public void setBondsProbability(double value, int i) {
    bondsProbability[i] = value;
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
   * @return the probabilities.
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
   *
   * @param time deposition time or former time.
   */
  public void setDepositionTime(double time) {
    attributes.setDepositionTime(time);
  }
  
  /**
   * 
   * @return when the atom has been deposited.
   */
  public double getDepositionTime() {
    return attributes.getDepositionTime();
  }
  
  public void setDepositionPosition(Point3D position) {
    attributes.setDepositionPosition(position);
  }
    
  public Point3D getDepositionPosition() {
    return attributes.getDepositionPosition();
  }
  
  public void setHops(int hops) {
    this.hops = hops;
  }
  
  public int getHops() {
    return hops;
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
   * @return whether current atoms has been previously visited.
   */
  public boolean isVisited() {
    return visited;
  }

  /**
   * When counting islands, we have to keep track of the visited atoms.
   *
   * @param visited whether current atoms is visited.
   */
  public void setVisited(boolean visited) {
    this.visited = visited;
  }
  
  /**
   * Checks if the current atom has occupied neighbours.
   *
   * @return true if the current atoms has no any occupied neighbour, false otherwise.
   */
  public boolean isIsolated() {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      if (getNeighbour(i).isOccupied()) {
        return false;
      }
    }
    return true;
  }

  public void setInnerPerimeter() {
    innerPerimeter = true;
  }

  public void setOuterPerimeter() {
    outerPerimeter = true;
  }
  
  public boolean isInnerPerimeter() {
    return innerPerimeter;
  }
  
  public boolean isOuterPerimeter() {
    return outerPerimeter;
  }
  
  public void resetPerimeter() {
    innerPerimeter = false;
    outerPerimeter = false;
  }
  
  /**
   * Returns the type of the neighbour atom if current one would not exist.
   *
   * @param posNeighbour current atom.
   * @return the type.
   */
  public abstract byte getTypeWithoutNeighbour(int posNeighbour);

  public abstract boolean areTwoTerracesTogether();

  public abstract AbstractGrowthAtom chooseRandomHop();

  public abstract int getOrientation();

  public abstract void obtainRateFromNeighbours();

  public abstract double probJumpToNeighbour(int originType, int position);

  public abstract void setNeighbour(AbstractGrowthAtom a, int pos);

  public abstract AbstractGrowthAtom getNeighbour(int pos);

  public abstract double updateOneBound(int bond);
  
  abstract public boolean isPartOfImmobilSubstrate();
  
  /**
   * Every atom will have an unique Id. So, we can use it as hash.
   *
   * @return hash value, based on Id.
   */
  @Override
  public int hashCode() {
    return id;
  }

  /**
   * In general two atoms are equal if the have the same Id.
   *
   * @param obj the other object. Should be atom, but can be any object.
   * @return true if equals, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof AbstractGrowthAtom)) {
      return false;
    }
    AbstractGrowthAtom other = (AbstractGrowthAtom) obj;
    if (getId() != other.getId()) {
      return false;
    }
    if (iHexa != other.iHexa) {
      return false;
    }
    if (jHexa != other.jHexa) {
      return false;
    }
    return true;
  }
  
  public void swapAttributes(AbstractGrowthAtom atom) {
    AbstractGrowthAtomAttributes tmpAttributes = this.attributes;
    this.attributes = atom.getAttributes();
    atom.setAttributes(tmpAttributes);
  }
  
  /**
   * If current atom is not eligible (thus, is immobile) return its probability in negative. Its
   * probability is then set to zero.
   *
   * If the current atom is eligible, update its probability with its neighbours.
   *
   * @return probability change.
   */
  public double updateRate() {
    double tmp = -getProbability();
    resetProbability();

    if (isEligible()) {
      obtainRateFromNeighbours();
      tmp += getProbability();
    }
    return tmp;
  }
  
  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  public void clear(){
    visited = false;
    setOccupied(false);
    outside = false;
    probability = 0;
    attributes.setDepositionTime(0);
    innerPerimeter = false;
    outerPerimeter = false;
    setList(false);
    hops = 0;
    cartesianSuperCell = new Point3D(0, 0, 0);
  }
  
  /**
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   *
   * @param probabilities Default rates.
   */
  public void initialiseRates(double[][] probabilities) {
    this.probabilities = probabilities;
  }
  
  @Override
  public double remove() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * It has one neighbour. Check if one neighbour of the current atom is a terrace. This is useful
   * to catch dimer formation when an atom is doing perimeter reentrance;
   *
   * @param originAtom has one neighbour and it is a terrace.
   * @return true if a dimer is going to be created, false otherwise.
   */
  public boolean areTwoTerracesTogetherInPerimeter(AbstractGrowthAtom originAtom) {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      AbstractGrowthAtom neighbour = getNeighbour(i);
      if (neighbour.isOccupied() && !neighbour.equals(originAtom) && neighbour.getType() == TERRACE) {
        return true;
      }
    }
    return false;
  }
}
