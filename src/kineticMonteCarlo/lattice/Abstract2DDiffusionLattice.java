/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.Abstract2DDiffusionAtom;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import kineticMonteCarlo.atom.ModifiedBuffer;
import utils.QuickSort;

/**
 *
 * @author Nestor
 */
public abstract class Abstract2DDiffusionLattice extends AbstractLattice implements IDevitaLattice {

  protected Abstract2DDiffusionAtom[][] atoms;

  private final ModifiedBuffer modified;

  public Abstract2DDiffusionLattice(int axonSizeI, int axonSizeJ, ModifiedBuffer modified) {
    this.axonSizeI = axonSizeI;
    this.axonSizeJ = axonSizeJ;
    axonSizeK = 1;
    unitCellSize = 4;
    this.modified = modified;
  }

  public abstract Abstract2DDiffusionAtom getNeighbour(int iAxon, int jAxon, int neighbour);

  public void configure(double[][] probabilities) {
    for (int iAxon = 0; iAxon < atoms[0].length; iAxon++) {
      for (int jAxon = 0; jAxon < atoms.length; jAxon++) {
        atoms[jAxon][iAxon].initialize(this, probabilities, this.getModified());
      }
    }
  }

  /**
   * We ignore the unitCellPos by now, we get directly the atom of i,j axonometric location
   *
   * @param iAxon
   * @param jAxon
   * @return
   */
  public Abstract2DDiffusionAtom getAtom(int iAxon, int jAxon) {
    return atoms[iAxon][jAxon];
  }

  @Override
  public AbstractAtom getAtom(int iAxon, int jAxon, int kAxon, int unitCellPos) {
    if (kAxon != 0 || unitCellPos != 0) {
      throw new UnsupportedOperationException("Z position or position inside unit cell cannot be different than 0, not supported"); //To change body of generated methods, choose Tools | Templates.
    }
    return getAtom(iAxon, jAxon);
  }

  /**
   * Obtains the spatial location of certain atom, the distance between atoms is considered as 1
   * Returns the cartesian position, given the axonometric (lattice) location
   *
   * @param iAxon i index in the axonometric mesh
   * @param jAxon j index in the axonometric mesh
   * @return
   */
  public abstract Point2D getCartesianLocation(int iAxon, int jAxon);

  public abstract Point2D getCentralAxonometricLocation();

  public abstract float getCartSizeX();

  public abstract float getCartSizeY();

  @Override
  public void reset() {
    for (int i = 0; i < atoms[0].length; i++) { //X
      for (int j = 0; j < atoms.length; j++) { //Y   
        atoms[j][i].clear();
      }
    }
  }

  protected void setAngles() {
    Point2D middle = getCentralAxonometricLocation();
    for (int jAxon = 0; jAxon < axonSizeJ; jAxon++) {
      for (int iAxon = 0; iAxon < axonSizeI; iAxon++) {
        Point2D cartPosition = getCartesianLocation(iAxon, jAxon);
        double xDif = cartPosition.getX() - middle.getX();
        double yDif = cartPosition.getY() - middle.getY();
        if (xDif == 0) {
          xDif = 1e-8;
        }
        double angle = Math.atan(yDif / xDif);
        if (xDif < 0) {
          angle = Math.PI + angle;
        }
        if (xDif >= 0 && yDif < 0) {
          angle = 2 * Math.PI + angle;
        }
        atoms[iAxon][jAxon].setAngle((float) angle);
      }
    }
  }

  public double getDistanceToCenter(int iAxon, int jAxon) {

    Point2D middle = getCentralAxonometricLocation();
    Point2D position = getCartesianLocation(iAxon, jAxon);

    return position.distance(middle);
  }

  /**
   * Defines which atoms are inside from the current position.
   *
   * Define como átomos inside a los átomos dentro de dicho rádio Devuelve un array de átomos que es
   * el perimetro de dicha circunferencia.
   *
   * @param radius
   * @return An array with the atoms that are inside this circumference
   */
  public Abstract2DDiffusionAtom[] setInside(int radius) {

    ArrayList<Abstract2DDiffusionAtom> perimeterList = new ArrayList();

    for (int jAxon = 0; jAxon < axonSizeJ; jAxon++) {
      for (int iAxon = 0; iAxon < axonSizeI; iAxon++) {
        double distance = getDistanceToCenter(iAxon, jAxon);
        if (radius <= distance) {
          atoms[iAxon][jAxon].setOutside(true);
        } else {
          atoms[iAxon][jAxon].setOutside(false);
          if (distance > (radius - 1)) {
            perimeterList.add(atoms[iAxon][jAxon]);
          }
        }
      }
    }

    Abstract2DDiffusionAtom[] perimeter = perimeterList.toArray(new Abstract2DDiffusionAtom[perimeterList.size()]);

    QuickSort.orderByAngle(perimeter, perimeter.length - 1);

    return perimeter;
  }

  public ModifiedBuffer getModified() {
    return modified;
  }
}
