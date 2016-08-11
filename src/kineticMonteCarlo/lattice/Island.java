/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Island {

  private int islandNumber;
  private int numberOfAtoms;
  private double maxDistance;
  private double avgDistance;
  private double sumDistance;
  private Point2D centreOfMass;

  public Island(int islandNumber) {
    this.islandNumber = islandNumber;
    sumDistance = 0.0d;
    numberOfAtoms = 0;
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
    return maxDistance;
  }

  public void setMaxDistance(double maxDistance) {
    this.maxDistance = maxDistance;
  }

  public double getAvgDistance() {
    return sumDistance / (double) numberOfAtoms;
  }

  public void setAvgDistance(int avgDistance) {
    this.avgDistance = avgDistance;
  }

  public Point2D getCentreOfMass() {
    return centreOfMass;
  }

  public void setCentreOfMass(Point2D centreOfMass) {
    this.centreOfMass = centreOfMass;
  }
  
  /**
   * Updates the average and max distances.
   * @param distanceX Cartesian distance in X coordinate.
   * @param distanceY Cartesian distance in Y coordinate.
   */
  public void update(double distanceX, double distanceY) {
    double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
    numberOfAtoms++;
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
}
