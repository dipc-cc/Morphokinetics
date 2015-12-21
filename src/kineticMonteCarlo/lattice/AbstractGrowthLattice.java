/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import java.awt.geom.Point2D;
import static java.lang.Math.abs;
import java.util.ArrayList;
import kineticMonteCarlo.atom.ModifiedBuffer;
import utils.QuickSort;

/**
 *
 * @author Nestor
 */
public abstract class AbstractGrowthLattice extends AbstractLattice implements IDevitaLattice {

  public static final float Y_RATIO = (float) Math.sqrt(3) / 2.0F; // it is the same as: sin 60º

  private AbstractGrowthAtom[][] atoms;

  private final ModifiedBuffer modified;
  private static Point2D middle;
  private final ArrayList<Integer> includePerimeterList; 
  private final int hexaArea;
  private int occupied;

  public AbstractGrowthLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified) {
    setHexaSizeI(hexaSizeI);
    setHexaSizeJ(hexaSizeJ);
    setHexaSizeK(1);
    setUnitCellSize(4);
    this.modified = modified;
    hexaArea = hexaSizeI * hexaSizeJ;
    occupied = 0;
    
    // Initialise the square perimeter include points. This is required because the number of points in the horizontal and vertical perimeters should be as equal as possible.
    this.includePerimeterList = new ArrayList<>();
    for (int i = 0; i < 256; i++) {
      includePerimeterList.add(Math.round(2*AgLattice.Y_RATIO + 2*i*AgLattice.Y_RATIO));
    }
  }

  public abstract AbstractGrowthAtom getNeighbour(int iHexa, int jHexa, int neighbour);

  public void configure(double[][] probabilities) {
    for (int iHexa = 0; iHexa < atoms[0].length; iHexa++) {
      for (int jHexa = 0; jHexa < atoms.length; jHexa++) {
        atoms[jHexa][iHexa].initialise(probabilities, modified);
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
  public AbstractGrowthAtom getAtom(int iHexa, int jHexa) {
    return atoms[iHexa][jHexa];
  }

  @Override
  public AbstractAtom getAtom(int iHexa, int jHexa, int kHexa, int unitCellPos) {
    if (kHexa != 0 || unitCellPos != 0) {
      throw new UnsupportedOperationException("Z position or position inside unit cell cannot be different than 0, not supported"); //To change body of generated methods, choose Tools | Templates.
    }
    return getAtom(iHexa, jHexa);
  }

  public final void setAtoms(AbstractGrowthAtom[][] atoms) {
    this.atoms = atoms;
  }
  
  /**
   * Obtains the spatial location of certain atom, the distance between atoms is considered as 1
   * Returns the Cartesian position, given the hexagonal (lattice) location
   *
   * @param iHexa i index in the hexagonal mesh
   * @param jHexa j index in the hexagonal mesh
   * @return
   */
  public abstract Point2D getCartesianLocation(int iHexa, int jHexa);

  public abstract Point2D getCentralCartesianLocation();

  public abstract float getCartSizeX();

  public abstract float getCartSizeY();
  
  public abstract double getCartX(int iHexa, int jHexa);
  
  public abstract double getCartY(int jHexa);

  @Override
  public void reset() {
    for (int i = 0; i < atoms[0].length; i++) { //X
      for (int j = 0; j < atoms.length; j++) { //Y   
        atoms[j][i].clear();
      }
    }
  }

  protected final void setAngles() {
    middle = getCentralCartesianLocation();
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
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
    return middle.distance(getCartesianLocation(iHexa, jHexa));
  }

  /**
   * Defines which atoms are inside from the current position (centre) and given radius.
   *
   * Define como átomos inside a los átomos dentro de dicho rádio Devuelve un array de átomos que es
   * el perimetro de dicha circunferencia.
   *
   * @param radius
   * @return An array with the atoms that are in the circumference (only the perimeter)
   */
  public AbstractGrowthAtom[] setInsideCircle(int radius) {

    ArrayList<AbstractGrowthAtom> perimeterList = new ArrayList();

    middle = getCentralCartesianLocation();
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        double distance = getDistanceToCenter(iHexa, jHexa);
        if (radius <= distance) {
          atoms[iHexa][jHexa].setOutside(true);
        } else {
          atoms[iHexa][jHexa].setOutside(false);
          if (distance > radius - 1) {
            perimeterList.add(atoms[iHexa][jHexa]);
          }
        }
      }
    }

    AbstractGrowthAtom[] perimeter = perimeterList.toArray(new AbstractGrowthAtom[perimeterList.size()]);
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
  public AbstractGrowthAtom[] setInsideSquare(int radius) {
    ArrayList<AbstractGrowthAtom> perimeterList = new ArrayList();

    Point2D centreCart = getCentralCartesianLocation();
    double left = centreCart.getX() - radius;
    double right = centreCart.getX() + radius;
    double bottom = centreCart.getY() - radius;
    double top = centreCart.getY() + radius;
    Point2D position;
    int countTop = 1;
    int countBottom = 1;
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        position = getCartesianLocation(iHexa, jHexa);
        if (left <= position.getX() && position.getX() <= right
                && bottom <= position.getY() + AgLattice.Y_RATIO
                && position.getY() - AgLattice.Y_RATIO <= top) {
          atoms[iHexa][jHexa].setOutside(false);
          if (abs(left - position.getX()) < 0.49
                  || abs(right - position.getX()) < 0.49
                  || abs(top - position.getY()) < AgLattice.Y_RATIO/2
                  || abs(bottom - position.getY()) < AgLattice.Y_RATIO/2) { 
            if (abs(top - position.getY()) < AgLattice.Y_RATIO/2){
              countTop++;
              if (!includePerimeterList.contains(countTop)) continue;
            }
            if (abs(bottom - position.getY())< AgLattice.Y_RATIO/2) {
              countBottom++;
              if (!includePerimeterList.contains(countBottom)) continue;
            }
            perimeterList.add(atoms[iHexa][jHexa]);
          }
        } else {
          atoms[iHexa][jHexa].setOutside(true);
        }

      }
    }

    AbstractGrowthAtom[] perimeter = perimeterList.toArray(new AbstractGrowthAtom[perimeterList.size()]);

    QuickSort.orderByAngle(perimeter, perimeter.length - 1);

    return perimeter;
  }

  /**
   * Adds an occupied location to the counter.
   */
  public void addOccupied() {
    occupied++;
  }
  
  /**
   * Subtracts an occupied location from the counter.
   */
  public void subtractOccupied() {
    occupied--;
  }
  
  /**
   * Resets to zero the number of occupied locations.
   */
  public void resetOccupied() {
    occupied = 0;
  }
  
  /**
   * 
   * @return the coverage of the lattice
   */
  public float getCoverage() {
    return (float) occupied / (float) hexaArea;
  }
  
  /**
   * 
   * @return  number of occupied positions
   */
  public int getOccupied() {
    return occupied;
  }
}
