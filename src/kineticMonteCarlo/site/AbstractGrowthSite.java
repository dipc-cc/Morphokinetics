/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package kineticMonteCarlo.site;

import java.util.List;
import java.util.Set;
import javafx.geometry.Point3D;
import kineticMonteCarlo.process.IElement;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthSite extends AbstractSurfaceSite implements Comparable, IElement {
  /** TODO document the types and change them to constants
   * 
   */
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
  
  private boolean innerPerimeter;
  private boolean outerPerimeter;
  private Point3D cartesianSuperCell;
  private GrowthAtomAttributes attributes;
  private Point3D cartesianPosition;
  
  public AbstractGrowthSite(int id, short iHexa, short jHexa, int numberOfNeighbours, int numberOfProcesses) {
    super(id, iHexa, jHexa, numberOfNeighbours, numberOfProcesses);
    setOccupied(false);
    outside = false;
    this.iHexa = iHexa;
    this.jHexa = jHexa;
    
    setNumberOfNeighbours(numberOfNeighbours);
    bondsProbability = new double[numberOfNeighbours];
    multiplier = 1;
    innerPerimeter = false;
    outerPerimeter = false;
    cartesianSuperCell = new Point3D(0, 0, 0);
    attributes = new GrowthAtomAttributes();
  }
  
  
  public Point3D getCartesianPosition() {
    return cartesianPosition;
  }

  public void setCartesianPosition(Point3D cartesianPosition) {
    this.cartesianPosition = cartesianPosition;
  }
  /**
   * Dummy constructor to be able to have a proper AgAtom(pos) constructor.
   * 
   * @param id atom identifier.
   * @param numberOfNeighbours number of neighbours that each atom has.
   */
  public AbstractGrowthSite(int id, int numberOfNeighbours) {
    super(id, (short) 0, (short) 0, numberOfNeighbours, numberOfNeighbours);
    iHexa = 0;
    jHexa = 0;
    bondsProbability = new double[numberOfNeighbours];
    setNumberOfNeighbours(numberOfNeighbours);
    cartesianSuperCell = new Point3D(0, 0, 0);
    attributes = new GrowthAtomAttributes();
  }
  
  @Override
  public GrowthAtomAttributes getAttributes() {
    return attributes;
  }
  
  @Override
  public void setAttributes(GrowthAtomAttributes attributes) {
    this.attributes = attributes;
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
    attributes.setHops(hops);
  }
  
  public int getHops() {
    return attributes.getHops();
  }
  
  public void setIslandNumber(int islandNumber) {
    attributes.setIslandNumber(islandNumber);
  }
  
  public int getIslandNumber() {
    return attributes.getIslandNumber();
  }
  
  public void setMultiAtomNumber(int multiAtomNumber) {
    attributes.addMultiAtomNumber(multiAtomNumber);
  }
  
  public void removeMultiAtomNumber(int multiAtomNumber) {
    attributes.removeMultiAtomNumber(multiAtomNumber);
  }
  
  /**
   * Removes all multi atoms that current atoms belonged to.
   */
  public void removeMultiAtoms() {
    attributes.removeMultiAtoms();
  }
  
  public Set getMultiAtomNumber() {
    return attributes.getMultiAtomNumber();
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

  public abstract AbstractGrowthSite chooseRandomHop();

  public abstract int getOrientation();

  public abstract void obtainRateFromNeighbours();

  public abstract double probJumpToNeighbour(int originType, int position);
  
  public abstract List getAllNeighbours();

  public abstract double updateOneBound(int bond);
  
  abstract public boolean isPartOfImmobilSubstrate();

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
    if (!(obj instanceof AbstractGrowthSite)) {
      return false;
    }
    AbstractGrowthSite other = (AbstractGrowthSite) obj;
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
  
  @Override
  public void swapAttributes(AbstractSurfaceSite atom) {
    GrowthAtomAttributes tmpAttributes = this.attributes;
    this.attributes = atom.getAttributes();
    this.attributes.addOneHop();
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
  @Override
  public void clear(){
    super.clear();
    outside = false;
    probability = 0;
    attributes.clear();
    innerPerimeter = false;
    outerPerimeter = false;
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
  public boolean areTwoTerracesTogetherInPerimeter(AbstractGrowthSite originAtom) {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      AbstractSurfaceSite neighbour = getNeighbour(i);
      if (neighbour.isOccupied() && !neighbour.equals(originAtom) && neighbour.getType() == TERRACE) {
        return true;
      }
    }
    return false;
  }
  
  public abstract AbstractGrowthSite getNeighbour(int pos);
}
