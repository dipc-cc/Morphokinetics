/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.unitCell;

import javafx.geometry.Point3D;
import kineticMonteCarlo.atom.AbstractAtom;
import static kineticMonteCarlo.unitCell.IUc.SIZE_X;
import static kineticMonteCarlo.unitCell.IUc.SIZE_Y;
import static kineticMonteCarlo.unitCell.IUc.SIZE_Z;

/**
 * Really simple unit cell, which will contain only one atom.
 *
 * @author J. Alberdi-Rodriguez
 */
public class Simple3dUc implements IUc{

  private final AbstractAtom atom;
  private final int size; // how many atoms
  private final int posI; // index in X axis
  private final int posJ; // index in Y axis
  private final int posK; // index in Z axis
  
  private double posX;
  private double posY;
  private double posZ;

  public Simple3dUc(int posI, int posJ, AbstractAtom atom) {
    this.size = 1;
    this.posI = posI;
    this.posJ = posJ;
    this.posK = 0;
    this.atom = atom;
  }
  
  public Simple3dUc(int posI, int posJ, int posK, AbstractAtom atom) {
    this.size = 1;
    this.posI = posI;
    this.posJ = posJ;
    this.posK = posK;
    this.atom = atom;
  }

  /**
   * Always returns the current atom.
   *
   * @param pos ignored.
   * @return current atom.
   */
  @Override
  public AbstractAtom getAtom(int pos) {
    return atom;
  }

  @Override
  public Point3D getPos() {
    return new Point3D(SIZE_X * posX, SIZE_Y * posY, SIZE_Z * posZ);
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
  
  public void setPosZ(double z) {
    posZ = z;
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
