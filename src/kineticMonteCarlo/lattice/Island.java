/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import kineticMonteCarlo.atom.AbstractGrowthAtom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Island {

  private int islandNumber;
  private int numberOfAtoms;
  private double maxDistance;
  private double sumDistance;
  private Point2D centreOfMass;
  private ArrayList<AbstractGrowthAtom> atoms;

  public Island(int islandNumber) {
    this.islandNumber = islandNumber;
    sumDistance = 0.0d;
    numberOfAtoms = 0;
    atoms = new ArrayList<>();
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
    atoms.add(atom);
  }
  
  /**
   * Updates the average and max distances.
   * @param distanceX Cartesian distance in X coordinate.
   * @param distanceY Cartesian distance in Y coordinate.
   */
  public void update(double distanceX, double distanceY) {
    double distance = distanceX * distanceX + distanceY * distanceY;
    numberOfAtoms++;
    updateAvg(distance);
    updateMax(distance);
  }

  /**
   * Calculates centre of mass and gyradius in an infinite surface. Gyradius is calculated with
   * equation (19) of Kinsner, A unified approach to fractal dimensions.
   *
   * @return gyradius
   */
  public double calculateCentreOfMassAndGyradius() {
    int sumX = 0;
    int sumY = 0;
    int sumSquareX = 0;
    int sumSquareY = 0;
    for (int i = 0; i < atoms.size(); i++) {
      int currentX = atoms.get(i).getRelativeX();
      int currentY = atoms.get(i).getRelativeY();
      sumX += currentX;
      sumY += currentY;
      // gyradius
      sumSquareX += currentX * currentX;
      sumSquareY += currentY * currentY;
    }
    System.out.println(atoms.size());
    System.out.println(islandNumber + " Centre of mass " + sumX / atoms.size() + " " + sumY / atoms.size());
    // gyradius
    double sigmaX = sumSquareX - Math.pow(sumX, 2.0) / atoms.size();
    double sigmaY = sumSquareY - Math.pow(sumY, 2.0) / atoms.size();
    double gyradius = Math.sqrt((sigmaX + sigmaY) / atoms.size());
    System.out.println(islandNumber + " Gyradius " + gyradius);
    return gyradius;
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
