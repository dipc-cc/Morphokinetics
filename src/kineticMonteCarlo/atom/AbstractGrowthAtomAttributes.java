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
package kineticMonteCarlo.atom;

import java.util.HashSet;
import java.util.Set;
import javafx.geometry.Point3D;

/**
 * Atom attributes which move with diffusion. 
 * 
 * @author J. Alberdi-Rodriguez
 */
public class AbstractGrowthAtomAttributes {
  
  /**
   * Stores when the atom has been deposited. It has to be moved with the
   * corresponding diffusion.
   */
  private double depositionTime;
  /**
   * The position were atom is deposited. Useful to get the diffusivity.
   */
  private Point3D depositionPosition;
  /**
   * Number of hops that atom has done. How many steps the atom has moved.
   */
  private int hops;
  /**
   * If current atom belong to an island, its number is stored, otherwise is 0.
   */
  private int islandNumber;
  /**
   * If current atom can move along with another atom inside an island, otherwise is 0.
   */
  private Set<Integer> multiAtomNumberSet;
  
  public AbstractGrowthAtomAttributes() {
    multiAtomNumberSet = new HashSet();
  }
  /**
   * Stores when the atom has been deposited. It is defined first when an atom is deposited and it
   * has to be moved with the corresponding diffusion.
   *
   * @param time deposition time or former time.
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
  
  public void setDepositionPosition(Point3D position) {
    depositionPosition = position;
  }
    
  public Point3D getDepositionPosition() {
    return depositionPosition;
  }
  
  public void setHops(int hops) {
    this.hops = hops;
  }
  
  public int getHops() {
    return hops;
  }
  
  public void addOneHop() {
    hops++;
  }

  public int getIslandNumber() {
    return islandNumber;
  }

  public void setIslandNumber(int islandNumber) {
    this.islandNumber = islandNumber;
  }

  public Set getMultiAtomNumber() {
    return multiAtomNumberSet;
  }

  public void addMultiAtomNumber(int multiAtomNumber) {
    this.multiAtomNumberSet.add(multiAtomNumber);
  }
  
  public void removeMultiAtomNumber(int multiAtomNumber) {
    this.multiAtomNumberSet.remove(multiAtomNumber);
  }
  
  /**
   * Removes all multi atoms that current atoms belonged to.
   */
  public void removeMultiAtoms() {
    multiAtomNumberSet = new HashSet<>();
  }    
  
  public void clear() {
    depositionPosition = null;
    depositionTime = 0;
    hops = 0;
    islandNumber = 0;
    multiAtomNumberSet = new HashSet<>();
  }
}
