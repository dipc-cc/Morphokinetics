/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.unitCell;

import java.awt.geom.Point2D;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;

/**
 * Really simple unit cell, which will contain only one atom
 *
 * @author J. Alberdi-Rodriguez
 */
public class SimpleUc extends AbstractUc {

  private final AbstractGrowthAtom atom;
  private final int size; // how many atoms
  private final int posI; // index in X axis
  private final int posJ; // index in Y axis
  private static final float SIZE_X = 1; // Cartesian size X per unit
  private static final float SIZE_Y = 1; // Cartesian size Y per unit
  
  private int sizeI;
  private double posX;
  private double posY;

  public SimpleUc(int size, int posI, int posJ, AbstractGrowthAtom atom) {
    this.size = 1;
    this.posI = posI;
    this.posJ = posJ;
    this.atom = atom;
  }
  
  public final void setSizeI(int sizeI) {
    this.sizeI = sizeI;
  }

  /**
   * Always returns the current atom
   *
   * @param pos ignored
   * @return current atom
   */
  @Override
  public AbstractGrowthAtom getAtom(int pos) {
    return atom;
  }

  @Override
  public Point2D getPos() {
    return new Point2D.Double(SIZE_X * posX, SIZE_Y * posY);
  }

  /**
   * Number of elements.
   *
   * @return quantity of unit cells
   */
  @Override
  public int size() {
    return size;
  }

  /**
   * Cartesian size of the unit cell in X axis
   *
   * @return size in X
   */
  public static float getSizeX() {
    return SIZE_X;
  }

  /**
   * Cartesian size of the unit cell in Y axis
   *
   * @return size in Y
   */
  public static float getSizeY() {
    return SIZE_Y;
  }

  @Override
  public int getPosI() {
    return posI;
  }

  @Override
  public int getPosJ() {
    return posJ;
  }
  
  public void setPosX(double x) {
    posX = x;
  }
  
  public void setPosY(double y) {
    posY = y;
  }
}
