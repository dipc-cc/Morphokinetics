/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.unitCell;

import basic.Point2D;
import java.util.List;
import javafx.geometry.Point3D;
import kineticMonteCarlo.atom.AgAtom;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;

/**
 * Unit cell of Ag (Silver) lattice
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgUc extends AbstractGrowthUc implements IUc {

  private final List<AgAtom> atoms;
  private final int posI; // index in X axis
  private final int posJ; // index in Y axis
  private static final float SIZE_X = 1; // Cartesian size X
  private static final float SIZE_Y = 2 * AbstractGrowthLattice.Y_RATIO; // Cartesian size Y

  public AgUc(int posI, int posJ, List<AgAtom> atoms) {
    this.posI = posI;
    this.posJ = posJ;
    this.atoms = atoms;
  }

  /**
   * Cartesian size of the unit cell in X axis
   *
   * @return size in X
   */
  public float getSizeX() {
    return SIZE_X;
  }

  /**
   * Cartesian size of the unit cell in Y axis
   *
   * @return size in Y
   */
  public float getSizeY() {
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
  
  @Override
  public AgAtom getAtom(int pos) {
    return atoms.get(pos);
  }

  @Override
  public Point3D getPos() {
    return new Point3D(SIZE_X * posI, SIZE_Y * posJ, 0);
  }

  /**
   * Number of elements per unit cell.
   *
   * @return quantity of unit cells
   */
  @Override
  public int size() {
    return atoms.size();
  }

}
