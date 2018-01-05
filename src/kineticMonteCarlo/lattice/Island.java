/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Island  implements Comparable {

  private int islandNumber;
  private int numberOfAtoms;
  private double maxDistance;
  private double sumDistance;
  private Point2D centreOfMass;
  private ArrayList<AbstractGrowthAtom> atoms;
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
  
  public void addAtom(AbstractGrowthAtom atom) {
    numberOfAtoms++;
    atoms.add(atom);
  }
  
  public void removeAtom(AbstractGrowthAtom atom) {
    numberOfAtoms--;
    atoms.remove(atom);
  }
  
  public AbstractGrowthAtom getAtomAt(int i) {
    return atoms.get(i);
  }

  public double getTotalRate() {
    return totalRate;
  }

  public void setTotalRate(double totalRate) {
    this.totalRate = totalRate;
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
    if (o instanceof AbstractGrowthAtom) {
      AbstractGrowthAtom a = (AbstractGrowthAtom) o;
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
