/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.unitCell;

import javafx.geometry.Point3D;
import kineticMonteCarlo.atom.AbstractGrowthAtom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public interface IUc {
  
  static final float SIZE_X = 1; // Cartesian size X per unit
  static final float SIZE_Y = 1; // Cartesian size Y per unit
  
  public AbstractGrowthAtom getAtom(int pos);
  
  public Point3D getPos();

  /**
   * Number of elements.
   * @return quantity of unit cells
   */
  public int size();

  /**
   * Cartesian size of the unit cell in X axis
   * @return size in X
   */
  public static float getSizeX() {
    return SIZE_X;
  }

  /**
   * Cartesian size of the unit cell in Y axis
   * @return size in Y
   */
  public static float getSizeY() {
    return SIZE_Y;
  }

  public int getPosI();

  public int getPosJ();
}
