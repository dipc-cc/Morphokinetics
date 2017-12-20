/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
  private Set<Integer> multiAtomNumber;
  
  public AbstractGrowthAtomAttributes() {
    multiAtomNumber = new HashSet();
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
    return multiAtomNumber;
  }

  public void addMultiAtomNumber(int multiAtomNumber) {
    this.multiAtomNumber.add(multiAtomNumber);
  }
  
  
  public void clear() {
    depositionPosition = null;
    depositionTime = 0;
    hops = 0;
    islandNumber = 0;
    multiAtomNumber = new HashSet<>();
  }
}
