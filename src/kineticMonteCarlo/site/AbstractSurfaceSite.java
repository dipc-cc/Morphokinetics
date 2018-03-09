/*
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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

import kineticMonteCarlo.process.AbstractProcess;
import kineticMonteCarlo.process.IElement;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractSurfaceSite extends AbstractSite implements Comparable, IElement {
  
  /**
   * Type of the atom. Some examples are; TERRACE, EDGE, KINK, ISLAND... Precise types are defined
   * in child classes.
   */
  private byte type;
  /** Type of the atom in the previous step. */
  private byte oldType;
  /**
   * Helper attribute used when counting islands, to check if current atom has
   * been already visited.
   */
  private boolean visited;
  /**
   * Unique identifier.
   */
  private final int id;
  
  private int occupiedNeighbours;
  /** Different processes that atom can do. In catalysis: adsorption, desorption, reaction and diffusion. */
  private AbstractProcess[] processes;
  
  public AbstractSurfaceSite(int id, short iHexa, short jHexa, int numberOfNeighbours, int numberOfProcesses) {
    this.id = id;
    setOccupied(false);
    
    setNumberOfNeighbours(numberOfNeighbours);
    visited = false;
  }
  
  public int getId() {
    return id;
  }

  @Override
  public byte getType() {
    return type;
  }

  public void setType(byte type) {
    this.type = type;
  }
  
  public byte getOldType() {
    return oldType;
  }

  public void setOldType(byte type) {
    oldType = type;
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
  
  /**
   * How many neighbours are occupied.
   *
   * @return occupied neighbours.
   */
  public int getOccupiedNeighbours(){
    return occupiedNeighbours;
  }
  
  /**
   * An occupied atom is added to current neighbourhood (for the addition, can
   * be a negative number).
   *
   * @param value to be added or removed.
   */
  public void addOccupiedNeighbour(int value) {
    occupiedNeighbours += value;
  }
  
  public final void setProcceses(AbstractProcess[] processes) {
    this.processes = processes;
  }
  
  public boolean isOnList(byte process) {
    return processes[process].isActive();
  }
  
  @Override
  public void setOnList(byte process, boolean onList) {
    processes[process].setActive(onList);
  }
  
  @Override
  public double getRate(byte process) {
    return processes[process].getRate();
  }
  
  public double getEdgeRate(byte process, int neighbourPos) {
    return processes[process].getEdgeRate(neighbourPos);
  }
  
  @Override
  public void setRate(byte process, double rate) {
    processes[process].setRate(rate);
  }

  @Override
  public void addRate(byte process, double rate, int neighbourPos) {
    processes[process].addRate(rate, neighbourPos);
  }
  
  @Override
  public double getSumRate(byte process) {
    return processes[process].getSumRate();
  }
  
  @Override
  public void setSumRate(byte process, double rate) {
    processes[process].setSumRate(rate);
  }

  @Override
  public void addToSumRate(byte process, double rate) {
    processes[process].addSumRate(rate);
  }
  
  /**
   * Stores the types of the neighbours. Used for activation energy study.
   * 
   * @param process process type.
   * @param type type of the neighbour.
   * @param neighbourPos position of the neighbour.
   */
  public void setEdgeType(byte process, byte type, int neighbourPos) {
    processes[process].setEdgeType(type, neighbourPos);
  }
  
  public byte getEdgeType(byte process, int neighbourPos) {
    return processes[process].getEdgeType(neighbourPos);
  }
  
  @Override
  public void equalRate(byte process) {
    processes[process].equalRate();
  }

  public abstract void setNeighbour(AbstractSurfaceSite a, int pos);

  @Override
  public abstract AbstractSurfaceSite getNeighbour(int pos);
  
  public abstract GrowthAtomAttributes getAttributes();
  
  public abstract void setAttributes(GrowthAtomAttributes a);
  
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
    if (!(obj instanceof AbstractSurfaceSite)) {
      return false;
    }
    AbstractSurfaceSite other = (AbstractSurfaceSite) obj;
    return getId() == other.getId();
  }
  
  public abstract void swapAttributes(AbstractSurfaceSite atom);
  
  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  @Override
  public void clear(){
    visited = false;
    setOccupied(false);
    setList(false);
    occupiedNeighbours = 0; // current atom has no neighbour
  }
  
  @Override
  public double remove() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  /**
   * Compares IDs of two atoms.
   * 
   * @param o other atom.
   * @return 
   */
  @Override
  public int compareTo(Object o) {
    if (o instanceof AbstractSurfaceSite) {
      AbstractSurfaceSite a = (AbstractSurfaceSite) o;
      double otherId = a.getId();
      if (getId() < otherId) {
        return -1;
      } else if (getId() > otherId) {
        return 1;
      } else {
        return 0;
      }
    } else {
      throw new IllegalArgumentException("obj must be an "
              + " instance of a AbstractSurfaceSite object.");
    }
  }
  
  @Override
  public String toString() {
    String returnString = "Atom Id " + getId();
    return returnString;
  }
}
