/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.unitCell.SimpleUc;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import kineticMonteCarlo.atom.ModifiedBuffer;
import utils.QuickSort;
import java.util.List;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import static java.lang.Math.abs;
import kineticMonteCarlo.unitCell.IUc;
import java.util.Arrays;

/**
 * In this case we assume that the unit cell is one and it only contains one element. Thus, we can
 * maintain the previous logic, where there is no any unit cell at all.
 *
 * @author Nestor, J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthLattice extends AbstractLattice implements IDevitaLattice {

  public static final float Y_RATIO = (float) Math.sqrt(3) / 2.0F; // it is the same as: sin 60º

  /**
   * Unit cell array, where all the atoms are located.
   */
  private final SimpleUc[][] ucArray;

  private final ModifiedBuffer modified;
  private final ArrayList<Integer> includePerimeterList; 
  private final int hexaArea;
  private int occupied;
  private int islandCount;
  private int monomerCount;
  private Island islands[];

  public AbstractGrowthLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified) {
    setHexaSizeI(hexaSizeI);
    setHexaSizeJ(hexaSizeJ);
    setHexaSizeK(1);
    setUnitCellSize(1);
    this.modified = modified;
    hexaArea = hexaSizeI * hexaSizeJ;
    occupied = 0;
    
    // Initialise the square perimeter include points. This is required because the number of points in the horizontal and vertical perimeters should be as equal as possible.
    includePerimeterList = new ArrayList<>();
    for (int i = 0; i < 256; i++) {
      includePerimeterList.add(Math.round(2 * Y_RATIO + 2 * i * Y_RATIO));
    }
    ucArray = new SimpleUc[hexaSizeI][hexaSizeJ];
  }

  public abstract AbstractGrowthAtom getNeighbour(int iHexa, int jHexa, int neighbour);

  /**
   * Obtains the spatial location of certain atom, the distance between atoms is considered as 1
   * Returns the Cartesian position, given the hexagonal (lattice) location.
   *
   * @param iHexa i index in the hexagonal mesh.
   * @param jHexa j index in the hexagonal mesh.
   * @return spatial location in Cartesian.
   */
  public abstract Point2D getCartesianLocation(int iHexa, int jHexa);

  public abstract Point2D getCentralCartesianLocation();

  public abstract float getCartSizeX();

  public abstract float getCartSizeY();
  
  public abstract double getCartX(int iHexa, int jHexa);
  
  public abstract double getCartY(int jHexa);  
  
  /**
   * Knowing the X and Y Cartesian location, returns closest atom hexagonal coordinate.
   * 
   * @param xCart Cartesian X coordinate.
   * @param yCart Cartesian Y coordinate.
   * @return i hexagonal position.
   */
  public abstract int getiHexa(double xCart, double yCart);

  /**
   * Knowing the X and Y Cartesian location, returns closest atom hexagonal coordinate.
   * 
   * @param yCart Cartesian Y coordinate.
   * @return j hexagonal position.
   */
  public abstract int getjHexa(double yCart);

  /**
   * Return the selected atom.
   *
   * @param index
   * @return selected atom.
   */
  @Deprecated
  public AbstractGrowthAtom getAtom(int index) {
    return getUc(index).getAtom(0);
  }
  
  /**
   * We ignore the unitCellPos by now, we get directly the atom of i,j hexagonal location.
   *
   * @param iHexa
   * @param jHexa
   * @return required atom.
   */
  @Deprecated
  public AbstractGrowthAtom getAtom(int iHexa, int jHexa) {
    return ucArray[iHexa][jHexa].getAtom(0);
  }

  @Override
  public AbstractGrowthUc getUc(int pos) {
    int j = Math.floorDiv(pos, getHexaSizeI());
    int i = pos - (j * getHexaSizeI());

    return ucArray[i][j];
  }
  
  @Override
  public AbstractAtom getAtom(int iHexa, int jHexa, int kHexa, int unitCellPos) {
    if (kHexa != 0 || unitCellPos != 0) {
      throw new UnsupportedOperationException("Z position or position inside unit cell cannot be different than 0, not supported"); //To change body of generated methods, choose Tools | Templates.
    }
    return getAtom(iHexa, jHexa);
  }

  public final void setAtoms(AbstractGrowthAtom[][] atoms) {
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        AbstractGrowthAtom atom = atoms[i][j];
        ucArray[i][j] = new SimpleUc(i, j, atom);
        
        ucArray[i][j].setPosX(getCartX(i, j));
        ucArray[i][j].setPosY(getCartY(j));
      }
    }
  }
  
  final void setAngles() {   
    for (int i = 0; i < size(); i++) {
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        double posY = atom.getPos().getY() + uc.getPos().getY();
        double posX = atom.getPos().getX() + uc.getPos().getX();

        double xDif = posX - getCentralCartesianLocation().getX();
        double yDif = posY - getCentralCartesianLocation().getY();
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
        atom.setAngle(angle);
      }
    }
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
  
  public AbstractGrowthUc getUc(int iLattice, int jLattice) {
    return ucArray[iLattice][jLattice];
  }
  
  public Island getIsland(int i) {
    if (islands != null) {
      return islands[i];
    }
    return null;
  }
  
  /**
   * Computes the fractal dimension from the average distances to the centre of mass.
   *
   * @return fractal dimension.
   */
  @Override
  public float getFractalDimension() {
    float sumAvg = 0.0f;
    for (int i = 0; i < islandCount; i++) {
      sumAvg += islands[i].getAvgDistance();
    }
    return (float) (1.0 / (sumAvg / islandCount));
  }
  
  @Override
  public int getIslandCount() {
    return islandCount;
  }

  @Override
  public int size() {
    return getHexaSizeI() * getHexaSizeJ();
  }
  
  public abstract void changeOccupationByHand(double xMouse, double yMouse, int scale);
  
  public abstract void deposit(AbstractGrowthAtom atom, boolean forceNucleation);

  /**
   * Extract the given atom from the lattice.
   * 
   * @param atom the atom to be extracted.
   * @return probability change (positive value).
   */
  public abstract double extract(AbstractGrowthAtom atom);
  
  public double getDistanceToCenter(int iHexa, int jHexa) {
    return getCentralCartesianLocation().distance(getCartesianLocation(iHexa, jHexa));
  }
  
  /**
   * Computes each atoms distance to the centre of mass.
   */
  public void getDistancesToCentre() {
    for (int i = 0; i < size(); i++) {
      IUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = (AbstractGrowthAtom) uc.getAtom(j);
        int islandNumber = atom.getIslandNumber();
        // atom belongs to an island
        if (islandNumber > 0) {
          double posX = atom.getPos().getX() + uc.getPos().getX();
          double distanceX = Math.abs(posX - islands[islandNumber - 1].getCentreOfMass().getX());
          if (distanceX > getCartSizeX() / 2) {
            distanceX = getCartSizeX()-distanceX;
          }
          
          double posY = atom.getPos().getY() + uc.getPos().getY();
          double distanceY = Math.abs(posY - islands[islandNumber - 1].getCentreOfMass().getY());
          if (distanceY > getCartSizeY() / 2) {
            distanceY = getCartSizeY()-distanceY;
          }
          
          islands[islandNumber - 1].update(distanceX, distanceY);
        }
      }
    }
    
  }

  /**
   * Returns the distance of the given x,y point to the centre.
   * 
   * @param x Cartesian coordinate.
   * @param y Cartesian coordinate.
   * @return distance.
   */
  public double getDistanceToCenter(double x, double y) {
    return getCentralCartesianLocation().distance(new Point2D.Double(x,y));
  }
  
  /**
   * Monomers are atoms without neighbours. After have counted them with
   * {@link #countIslands(java.io.PrintWriter)}, the number of monomers is available.
   *
   * @return number of monomers.
   */
  public int getMonomerCount() {
    return -monomerCount;
  }

  /**
   * Calculates the centre of mass of each island. The result is stored in "centres" vector. The way
   * to compute the distances is considering periodic boundary conditions and the algorithm is taken
   * from Wikipedia
   * (https://en.wikipedia.org/wiki/Center_of_mass#Systems_with_periodic_boundary_conditions).
   */
  public void getCentreOfMass() {
    int islandAmount = getIslandCount();
    int counter[] = new int[islandAmount];
    double valueTheta;
    double xiX[] = new double[islandAmount];
    double zetaX[] = new double[islandAmount];
    double xiY[] = new double[islandAmount];
    double zetaY[] = new double[islandAmount];
    islands = new Island[islandAmount];
    Arrays.setAll(islands, i -> new Island(i));
    // count the island with their coordinates and translate them
    for (int i = 0; i < size(); i++) {
      IUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = (AbstractGrowthAtom) uc.getAtom(j);
        int islandNumber = atom.getIslandNumber();
        // atom belongs to an island
        if (islandNumber > 0) {
          double posX = atom.getPos().getX() + uc.getPos().getX();
          valueTheta = (posX / getCartSizeX()) * 2.0 * Math.PI;
          xiX[islandNumber - 1] += Math.cos(valueTheta);
          zetaX[islandNumber - 1] += Math.sin(valueTheta);

          double posY = atom.getPos().getY() + uc.getPos().getY();
          valueTheta = (posY / getCartSizeY()) * 2.0 * Math.PI;
          xiY[islandNumber - 1] += Math.cos(valueTheta);
          zetaY[islandNumber - 1] += Math.sin(valueTheta);

          counter[islandNumber - 1]++;
        }
      }
    }

    // get centres
    for (int i = 0; i < islandAmount; i++) {
      // values lower than 1e-10 are considered -0
      double centreX = (getCartSizeX() * (Math.atan2(-toZeroIfTooClose(zetaX[i] / counter[i]), -toZeroIfTooClose(xiX[i] / counter[i])) + Math.PI)) / (2 * Math.PI);
      double centreY = (getCartSizeY() * (Math.atan2(-toZeroIfTooClose(zetaY[i] / counter[i]), -toZeroIfTooClose(xiY[i] / counter[i])) + Math.PI)) / (2 * Math.PI);
      islands[i].setCentreOfMass(new Point2D.Double(centreX, centreY));
    }
  }
  
  /**
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   *
   * @param probabilities Default rates.
   */
  public void initialiseRates(double[][] probabilities) {
    for (int i = 0; i < size(); i++) {
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        atom.initialiseRates(probabilities);
      }
    }
  }

  @Override
  public void reset() {
    for (int i = 0; i < size(); i++) {
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        atom.clear();
      }
    }
  }

  /**
   * Defines which atoms are inside from the current position (centre) and given radius.
   *
   * Define como átomos inside a los átomos dentro de dicho rádio Devuelve un array de átomos que es
   * el perimetro de dicha circunferencia.
   *
   * @param radius
   * @return An array with the atoms that are in the circumference (only the perimeter).
   */
  public List<AbstractGrowthAtom> setInsideCircle(int radius, boolean periodicSingleFlake) {
    ArrayList<AbstractGrowthAtom> perimeterList = new ArrayList();

    for (int i = 0; i < size(); i++) {
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        double x = atom.getPos().getX() + uc.getPos().getX();
        double y = atom.getPos().getY() + uc.getPos().getY();
        double distance = getDistanceToCenter(x, y);
      
        if (radius < distance && !periodicSingleFlake) {
          atom.setOutside(true);
        } else {
          atom.setOutside(false);
          if (distance > radius - 1) {
            perimeterList.add(atom);
          }
        }
      }
    }
    
    QuickSort.orderByAngle(perimeterList, perimeterList.size() - 1);

    return perimeterList;
  }
  
  /**
   * Defines which atoms are inside from the current position.
   *
   *
   * @param radius is the half of the square side.
   * @return An array with the atoms that are in the perimeter.
   */
  public List<AbstractGrowthAtom> setInsideSquare(int radius) {
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
                && bottom <= position.getY() + Y_RATIO
                && position.getY() - Y_RATIO <= top) {
          ucArray[iHexa][jHexa].getAtom(0).setOutside(false);
          if (abs(left - position.getX()) < 0.49
                  || abs(right - position.getX()) < 0.49
                  || abs(top - position.getY()) < Y_RATIO / 2
                  || abs(bottom - position.getY()) < Y_RATIO / 2) {
            if (abs(top - position.getY()) < Y_RATIO / 2) {
              countTop++;
              if (!includePerimeterList.contains(countTop)) {
                continue;
              }
            }
            if (abs(bottom - position.getY()) < Y_RATIO / 2) {
              countBottom++;
              if (!includePerimeterList.contains(countBottom)) {
                continue;
              }
            }
            perimeterList.add(ucArray[iHexa][jHexa].getAtom(0));
          }
        } else {
          ucArray[iHexa][jHexa].getAtom(0).setOutside(true);
        }
      }
    }

    QuickSort.orderByAngle(perimeterList, perimeterList.size() - 1);

    return perimeterList;
  }
  
  void addAtom(AbstractGrowthAtom atom) {
    modified.addOwnAtom(atom);
  }
  
  void addBondAtom(AbstractGrowthAtom atom) {
    modified.addBondAtom(atom);
  }

  /**
   * Counts the number of islands that the simulation has.
   *
   * @param print to where should print the output: null (nowhere), standard output or a file.
   * @return number of islands.
   */
  public int countIslands(PrintWriter print) {
    // reset all the atoms
    for (int i = 0; i < size(); i++) {
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        uc.getAtom(j).setVisited(false);
        uc.getAtom(j).setIslandNumber(0);
      }
    }
    
    // do the count
    islandCount = 0;
    monomerCount = 0;
    for (int i = 0; i < size(); i++) {
      // visit all the atoms within the unit cell
      AbstractGrowthUc uc = getUc(i);
      for (int j=0; j< uc.size(); j++) {
        identifyIsland(uc.getAtom(j), false);
      }
    }
    
    if (print != null) {
      // create a histogram with the number of atoms per island
      List<Integer> histogram = new ArrayList(islandCount + 1); // count also non occupied area
      for (int i = 0; i < islandCount + 1; i++) {
        histogram.add(0);
      }
      // iterate all atoms and add to the corresponding island
      for (int i = 0; i < size(); i++) {
        AbstractGrowthUc uc = getUc(i);
        for (int j = 0; j < uc.size(); j++) {
          int island = uc.getAtom(j).getIslandNumber();
          if (island >= 0) {
            histogram.set(island, histogram.get(island) + 1);
          }
        }
      }
      print.println("histogram " + histogram.toString());
    }
    return islandCount;
  }
  
  public void printDistances() {
    double sumAvg = 0.0d;
    double sumMax = 0.0d;
    for (int i = 0; i < getIslandCount(); i++) {
      System.out.println("For island " + i + " centre is " + islands[i].getCentreOfMass() + " max distance " + islands[i].getMaxDistance() + " avg " + islands[i].getAvgDistance());
      sumAvg += islands[i].getAvgDistance();
      sumMax += islands[i].getMaxDistance();
    }
    System.out.println("--\n Average of average distances " + sumAvg / getIslandCount() + " Average of maximum distances " + sumMax / getIslandCount());
    System.out.println(" Fractal dimension " + 1 / (sumAvg / getIslandCount()) + "\n--");
  }
  
  private double toZeroIfTooClose(double value) {
    return Math.abs(value) < 1e-10 ? -0.0d : value;
  }

  /**
   * Counts the number of islands that the simulation has. It iterates trough all neighbours, to set
   * all them the same island number.
   *
   * @param atom atom to be classified.
   * @param fromNeighbour whether is called from outside or recursively.
   */
  private void identifyIsland(AbstractGrowthAtom atom, boolean fromNeighbour) {
    if (!atom.isVisited() && atom.isOccupied() && !fromNeighbour) {
      if (atom.isIsolated()) {
        monomerCount--;
        atom.setIslandNumber(monomerCount);
        atom.setVisited(true);
      } else {
        islandCount++;
      }
    }
    if (atom.isVisited())
      return;
    atom.setVisited(true);
    if (atom.isOccupied()) {
      atom.setIslandNumber(islandCount);
      for (int pos = 0; pos < atom.getNumberOfNeighbours(); pos++) {
        AbstractGrowthAtom neighbour = atom.getNeighbour(pos);
        if (!neighbour.isVisited()) {
          identifyIsland(neighbour, true);
        }
      }
    }
  }
}
