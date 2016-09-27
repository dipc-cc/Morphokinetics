/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.unitCell;

import basic.Point2D;
import java.util.List;
import kineticMonteCarlo.atom.AbstractGrowthAtom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public interface IUc {
  
  static final float SIZE_X = 1; // Cartesian size X per unit
  static final float SIZE_Y = 1; // Cartesian size Y per unit
  static final float SIZE_Z = 1; // Cartesian size Z per unit
  
  public AbstractAtom getAtom(int pos);
  
  /**
   * Cartesian size of the unit cell in X axis
   *
   * @return size in X
   */
  public float getSizeX();

  /**
   * Cartesian size of the unit cell in Y axis
   *
   * @return size in Y
   */
  public float getSizeY();

  /**
   * Cartesian size of the unit cell in Y axis
   *
   * @return size in Y
   */
  public static float getSizeZ() {
    return SIZE_Z;
  }

  public int getPosI();

  public int getPosJ();
  
  /**
   * Cartesian position of the origin of the unit cell.
   *
   * @return a point with 3 coordinates.
   */
  public Point3D getPos();

  /**
   * Number of elements.
   *
   * @return quantity of unit cells
   */
  public int size();
}
