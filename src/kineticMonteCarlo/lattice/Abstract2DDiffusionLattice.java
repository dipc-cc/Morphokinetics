/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.Abstract2DDiffusionAtom;
import java.awt.geom.Point2D;
import static java.lang.Math.abs;
import static java.lang.Math.round;
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

  public Abstract2DDiffusionLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified) {
    this.hexaSizeI = hexaSizeI;
    this.hexaSizeJ = hexaSizeJ;
    hexaSizeK = 1;
    unitCellSize = 4;
    this.modified = modified;
  }

  public abstract Abstract2DDiffusionAtom getNeighbour(int iHexa, int jHexa, int neighbour);

  public void configure(double[][] probabilities) {
    for (int iHexa = 0; iHexa < atoms[0].length; iHexa++) {
      for (int jHexa = 0; jHexa < atoms.length; jHexa++) {
        atoms[jHexa][iHexa].initialize(this, probabilities, this.getModified());
      }
    }
  }

  /**
   * We ignore the unitCellPos by now, we get directly the atom of i,j hexagonal location
   *
   * @param iHexa
   * @param jHexa
   * @return
   */
  public Abstract2DDiffusionAtom getAtom(int iHexa, int jHexa) {
    return atoms[iHexa][jHexa];
  }

  @Override
  public AbstractAtom getAtom(int iHexa, int jHexa, int kHexa, int unitCellPos) {
    if (kHexa != 0 || unitCellPos != 0) {
      throw new UnsupportedOperationException("Z position or position inside unit cell cannot be different than 0, not supported"); //To change body of generated methods, choose Tools | Templates.
    }
    return getAtom(iHexa, jHexa);
  }

  /**
   * Obtains the spatial location of certain atom, the distance between atoms is considered as 1
   * Returns the cartesian position, given the hexagonal (lattice) location
   *
   * @param iHexa i index in the hexagonal mesh
   * @param jHexa j index in the hexagonal mesh
   * @return
   */
  public abstract Point2D getCartesianLocation(int iHexa, int jHexa);

  public abstract Point2D getCentralCartesianLocation();

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
    Point2D middle = getCentralCartesianLocation();
    for (int jHexa = 0; jHexa < hexaSizeJ; jHexa++) {
      for (int iHexa = 0; iHexa < hexaSizeI; iHexa++) {
        Point2D cartPosition = getCartesianLocation(iHexa, jHexa);
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
        atoms[iHexa][jHexa].setAngle((float) angle);
      }
    }
  }

  public double getDistanceToCenter(int iHexa, int jHexa) {

    Point2D middle = getCentralCartesianLocation();
    Point2D position = getCartesianLocation(iHexa, jHexa);

    return position.distance(middle);
  }

  /**
   * Defines which atoms are inside from the current position.
   *
   * Define como átomos inside a los átomos dentro de dicho rádio Devuelve un array de átomos que es
   * el perimetro de dicha circunferencia.
   *
   * @param radius
   * @return An array with the atoms that are in the circumference (only the perimeter)
   */
  public Abstract2DDiffusionAtom[] setInsideCircle(int radius) {

    ArrayList<Abstract2DDiffusionAtom> perimeterList = new ArrayList();

    for (int jHexa = 0; jHexa < hexaSizeJ; jHexa++) {
      for (int iHexa = 0; iHexa < hexaSizeI; iHexa++) {
          double distance = getDistanceToCenter(iHexa, jHexa);
          if (radius <= distance) {
            atoms[iHexa][jHexa].setOutside(true);
          } else {
            atoms[iHexa][jHexa].setOutside(false);
            if (distance > (radius - 1)) {
              perimeterList.add(atoms[iHexa][jHexa]);
            }
          }
      }
    }
    
    Abstract2DDiffusionAtom[] perimeter = perimeterList.toArray(new Abstract2DDiffusionAtom[perimeterList.size()]);

    QuickSort.orderByAngle(perimeter, perimeter.length - 1);

    return perimeter;
  }

  /**
   * Defines which atoms are inside from the current position.
   *
   *
   * @param radius is the half of the square side
   * @return An array with the atoms that are in the perimeter
   */
  public Abstract2DDiffusionAtom[] setInsideSquare(int radius) {
    ArrayList<Abstract2DDiffusionAtom> perimeterList = new ArrayList();

    int yRadius = round(radius / (float) AgAgLattice.YRatio);
    Point2D centreCart = getCentralCartesianLocation();
    double left = centreCart.getX() - radius;
    double right = centreCart.getX() + radius;
    double bottom = centreCart.getY() - yRadius * AgAgLattice.YRatio;
    double top = centreCart.getY() + radius;
    Point2D position;
    for (int jHexa = 0; jHexa < hexaSizeJ; jHexa++) {
      for (int iHexa = 0; iHexa < hexaSizeI; iHexa++) {
        position = getCartesianLocation(iHexa, jHexa); // TODO deklarazioa hemendik kentzeko!!!!!!!!!!!!!!!
        if (left <= position.getX() && position.getX() <= right
                && bottom <= position.getY() + AgAgLattice.YRatio
                && position.getY() - AgAgLattice.YRatio <= top) {
          atoms[iHexa][jHexa].setOutside(false);
          if (abs(left - position.getX()) < 0.49
                  || abs(right - position.getX()) < 0.49
                  || abs(top - position.getY()) <  AgAgLattice.YRatio - 0.5
                  || abs(bottom - position.getY()) <  AgAgLattice.YRatio - 0.5) {
            perimeterList.add(atoms[iHexa][jHexa]);
          }
        } else {
          atoms[iHexa][jHexa].setOutside(true);
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
