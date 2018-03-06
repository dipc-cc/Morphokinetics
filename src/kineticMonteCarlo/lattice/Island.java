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
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.process.IElement;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Island implements Comparable, IElement {

  private int islandNumber;
  private int numberOfAtoms;
  private double maxDistance;
  private double sumDistance;
  private Point2D centreOfMass;
  private ArrayList<AbstractGrowthSite> atoms;
  /** In concerted mode, all island can diffuse with certain rate. */
  private double totalRate;

  public Island(int islandNumber) {
    this.islandNumber = islandNumber;
    sumDistance = 0.0d;
    numberOfAtoms = 0;
    atoms = new ArrayList<>();
  }
  
  /**
   * To clone.
   * 
   * @param another 
   */
  public Island(Island another) {
    this.islandNumber = another.islandNumber;
    this.numberOfAtoms = another.numberOfAtoms;
    this.maxDistance = another.maxDistance;
    this.sumDistance = another.sumDistance;
    this.centreOfMass = another.centreOfMass;
    this.atoms = new ArrayList<>(another.atoms);
    this.totalRate = another.totalRate;            
  }
  
  public int getIslandNumber() {
    return islandNumber;
  }

  public void setIslandNumber(int islandNumber) {
    this.islandNumber = islandNumber;
  }

  public int getNumberOfAtoms() {
    return numberOfAtoms;
  }

  public void setNumberOfAtoms(int numberOfAtoms) {
    this.numberOfAtoms = numberOfAtoms;
  }

  public double getMaxDistance() {
    return Math.sqrt(maxDistance);
  }
  
  /**
   * Equation (15) of Kinsner, A unified approach to fractal dimensions.
   * @return 
   */
  public double getAvgDistance() {
    return Math.sqrt(sumDistance / (double) numberOfAtoms);
  }

  public Point2D getCentreOfMass() {
    return centreOfMass;
  }
 
  public void setCentreOfMass(Point2D centreOfMass) {
    this.centreOfMass = centreOfMass;
  }
  
  public void addAtom(AbstractGrowthSite atom) {
    numberOfAtoms++;
    atoms.add(atom);
  }
  
  public void removeAtom(AbstractGrowthSite atom) {
    if (atoms.remove(atom)) {
      numberOfAtoms--;
    }
  }
  
  public AbstractGrowthSite getAtomAt(int i) {
    return atoms.get(i);
  }
  
  @Override
  public double getRate(byte process) {
    return totalRate;
  }

  @Override
  public void setRate(byte process, double rate) {
    totalRate = rate;
  }
  
  
  @Override
  public void addRate(byte process, double rate, int pos) {
    // do nothing, not used
  }
  
  @Override
  public void setOnList(byte process, boolean onList) {
    // to be done
  }
  
  @Override
  public double getSumRate(byte process) {
    // to be done
    return 0.0;
  }
  
  @Override
  public void setSumRate(byte process, double rate) {
    // to be done
  }

  @Override
  public void addToSumRate(byte process, double rate) {
    // to be done
  }
  
  @Override
  public void equalRate(byte process) {
    // to be done
  }
  
  @Override
  public void clear() {
    // do nothing
  }
  
  /**
   * Updates the average and max distances.
   * 
   * @param distanceX Cartesian distance in X coordinate.
   * @param distanceY Cartesian distance in Y coordinate.
   */
  public void update(double distanceX, double distanceY) {
    double distance = distanceX * distanceX + distanceY * distanceY;
    updateAvg(distance);
    updateMax(distance);
  }
  
  private void updateAvg(double distance) {
    sumDistance += distance;
  }

  private void updateMax(double distance) {
    if (distance > maxDistance) {
      maxDistance = distance;
    }
  }
  
  /**
   * Selects a random direction for the island to move.
   * 
   * @return random direction out of 6 possible directions.
   */
  public int getRandomDirection() {
    return StaticRandom.rawInteger(6);
  }

  /**
   * Compares island numbers of two islands.
   * 
   * @param o other atom.
   * @return 
   */
  @Override
  public int compareTo(Object o) {
    if (o instanceof AbstractGrowthSite) {
      AbstractGrowthSite a = (AbstractGrowthSite) o;
      double otherId = a.getId();
      if (getIslandNumber() < otherId) {
        return -1;
      } else if (getIslandNumber()> otherId) {
        return 1;
      } else {
        return 0;
      }
    } else {
      throw new IllegalArgumentException("obj must be an "
              + " instance of a Island object.");
    }
  }
  
  @Override
  public String toString() {
    String returnString = "Island " + islandNumber + " atoms " + numberOfAtoms;
    return returnString;
  }
}
