/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.unitCell;

import javafx.geometry.Point3D;
import kineticMonteCarlo.atom.CatalysisAtom;
import static kineticMonteCarlo.unitCell.IUc.SIZE_X;
import static kineticMonteCarlo.unitCell.IUc.SIZE_Y;

/**
 * Really simple unit cell, which will contain only one atom.
 *
 * @author Karmele Valencia
 */
public class CatalysisUc extends AbstractGrowthUc implements IUc{

  private final CatalysisAtom atom;
  private final int size; // how many atoms
  private final int posI; // index in X axis
  private final int posJ; // index in Y axis
  
  private double posX;
  private double posY;

  public CatalysisUc(int posI, int posJ, CatalysisAtom atom) {
    this.size = 1;
    this.posI = posI;
    this.posJ = posJ;
    this.atom = atom;
  }

  /**
   * Always returns the current atom
   *
   * @param pos ignored
   * @return current atom
   */
  @Override
  public CatalysisAtom getAtom(int pos) {
    return atom;
  }

  @Override
  public Point3D getPos() {
    return new Point3D(SIZE_X * posX, SIZE_Y * posY, 0);
  }

  /**
   * Cartesian size of the unit cell in X axis.
   *
   * @return size in X.
   */
  public static float getSizeX() {
    return SIZE_X;
  }

  /**
   * Cartesian size of the unit cell in Y axis.
   *
   * @return size in Y.
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
  /**
   * Number of elements.
   *
   * @return quantity of unit cells.
   */
  @Override
  public int size() {
    return size;
  }
}
