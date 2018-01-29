/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
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
import kineticMonteCarlo.unitCell.IUc;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.round;
import static java.lang.Math.abs;
import static java.lang.Math.floorDiv;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import static kineticMonteCarlo.process.ConcertedProcess.MULTI;

/**
 * In this case we assume that the unit cell is one and it only contains one element. Thus, we can
 * maintain the previous logic, where there is no any unit cell at all.
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthLattice extends AbstractLattice implements IDevitaLattice {

  public static final float Y_RATIO = (float) sqrt(3) / 2.0F; // it is the same as: sin 60º

  /**
   * Unit cell array, where all the atoms are located.
   */
  private final SimpleUc[][] ucArray;

  private final ModifiedBuffer modified;
  private final ArrayList<Integer> includePerimeterList; 
  private final int hexaArea;
  private int occupied;
  private int islandCount;
  private int multiAtomsIndex;
  private int monomerCount;
  private int[] atomTypesCounter;
  private int[] emptyTypesCounter;
  private int atomTypesAmount;
  private ArrayList<Island> islands;
  private final Map<Integer, MultiAtom> multiAtomsMap;
  private int innerPerimeter;
  private int outerPerimeter;
  private double tracerDistance;
  private double centreMassDistance;
  private int mobileAtoms;
  private long hops;

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
      includePerimeterList.add(round(2 * Y_RATIO + 2 * i * Y_RATIO));
    }
    ucArray = new SimpleUc[hexaSizeI][hexaSizeJ];
    innerPerimeter = 0;
    outerPerimeter = 0;
    islands = new ArrayList<>();
    multiAtomsMap = new HashMap<>();
    multiAtomsIndex = 1;
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
  
  public AbstractGrowthAtom getAtom(int iHexa, int jHexa, int unitCellPos) {
    int index = jHexa * getHexaSizeI() + iHexa;
    return getUc(index).getAtom(0);
  }
  
  /**
   * Returns the atom that it is in the middle of single flake simulation.
   * 
   * @return central atom.
   */
  public abstract AbstractGrowthAtom getCentralAtom();

  @Override
  public AbstractGrowthUc getUc(int pos) {
    int j = floorDiv(pos, getHexaSizeI());
    int i = pos - (j * getHexaSizeI());

    return ucArray[i][j];
  }
  
  @Override
  public AbstractAtom getAtom(int iHexa, int jHexa, int kHexa, int unitCellPos) {
    if (kHexa != 0 || unitCellPos != 0) {
      throw new UnsupportedOperationException("Z position or position inside unit cell cannot be different than 0, not supported"); //To change body of generated methods, choose Tools | Templates.
    }
    return getAtom(iHexa, jHexa, unitCellPos);
  }
  
  public final void setAtoms(AbstractGrowthAtom[][] atoms) {
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        AbstractGrowthAtom atom = atoms[i][j];
        ucArray[i][j] = new SimpleUc(i, j, atom);

        ucArray[i][j].setPosX(getCartX(i, j));
        ucArray[i][j].setPosY(getCartY(j));
        atoms[i][j].setCartesianPosition(ucArray[i][j].getPos());
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
        double angle = atan(yDif / xDif);
        if (xDif < 0) {
          angle = PI + angle;
        }
        if (xDif >= 0 && yDif < 0) {
          angle = 2 * PI + angle;
        }
        atom.setAngle(angle);
      }
    }
  }

  /**
   * Inner perimeter is the sum of atoms that are occupied and have some neighbour not occupied.
   * Thus, are in the limit of the island.
   *
   * @return number of atoms that are in the inner perimeter.
   */
  public int getInnerPerimeterLenght() {
    return innerPerimeter;
  }

  /**
   * Outer perimeter is the sum of atoms that are not occupied and are touching an occupied one.
   *
   * @return number of atoms that are in the outer perimeter.
   */
  public int getOuterPerimeterLenght() {
    return outerPerimeter;
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
   * @return the coverage of the lattice.
   */
  public float getCoverage() {
    return (float) occupied / (float) hexaArea;
  }
  
  /**
   * 
   * @return  number of occupied positions.
   */
  public int getOccupied() {
    return occupied;
  }
  
  public AbstractGrowthUc getUc(int iLattice, int jLattice) {
    return ucArray[iLattice][jLattice];
  }
  
  public Island getIsland(int i) {
    if (islands != null) {
      return islands.get(i);
    }
    return null;
  }
  
  public void swapIsland(Island origin, Island destination) {
    destination.setRate((byte) 0, origin.getRate((byte) 0));
    destination.setIslandNumber(origin.getIslandNumber());
    int number = destination.getIslandNumber();
    islands.remove(number);
    islands.add(number, destination);
  }
  
  public MultiAtom getMultiAtom(int i) {
    if (multiAtomsMap != null) {
      return multiAtomsMap.get(i);
    }
    return null;
  }
  
  public Iterator getMultiAtomsIterator() {
    return multiAtomsMap.values().iterator();
  }
  
  /**
   * Computes the average distances to the centre of mass for all islands.
   *
   * @return average gyradius.
   */
  @Override
  public float getAverageGyradius() {
    float sumAvg = 0.0f;
    for (int i = 0; i < islandCount; i++) {
      sumAvg += islands.get(i).getAvgDistance();
    }
    return (float) (sumAvg / islandCount);
  }
  
  @Override
  public int getIslandCount() {
    return islandCount;
  }

  public Iterator<Island> getIslandIterator() {
    return islands.iterator();
  }
  
  public Iterator<MultiAtom> getMultiAtomIterator() {
    return multiAtomsMap.values().iterator();
  }

  public int getMultiAtomCount() {
    return multiAtomsMap.size();
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
          double distanceX = abs(posX - islands.get(islandNumber - 1).getCentreOfMass().getX());
          if (distanceX > getCartSizeX() / 2) {
            distanceX = getCartSizeX() - distanceX;
          }
          
          double posY = atom.getPos().getY() + uc.getPos().getY();
          double distanceY = abs(posY - islands.get(islandNumber - 1).getCentreOfMass().getY());
          if (distanceY > getCartSizeY() / 2) {
            distanceY = getCartSizeY() - distanceY;
          }
          
          islands.get(islandNumber - 1).update(distanceX, distanceY);
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
    double theta;
    double xiX[] = new double[islandAmount];
    double zetaX[] = new double[islandAmount];
    double xiY[] = new double[islandAmount];
    double zetaY[] = new double[islandAmount];
    // count the island with their coordinates and translate them
    for (int i = 0; i < size(); i++) {
      IUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = (AbstractGrowthAtom) uc.getAtom(j);
        int islandNumber = atom.getIslandNumber();
        // atom belongs to an island
        if (islandNumber > 0) {
          double posX = atom.getPos().getX() + uc.getPos().getX();
          theta = (posX / getCartSizeX()) * 2.0 * PI;
          xiX[islandNumber - 1] += cos(theta);
          zetaX[islandNumber - 1] += sin(theta);

          double posY = atom.getPos().getY() + uc.getPos().getY();
          theta = (posY / getCartSizeY()) * 2.0 * PI;
          xiY[islandNumber - 1] += cos(theta);
          zetaY[islandNumber - 1] += sin(theta);

          counter[islandNumber - 1]++;
        }
      }
    }

    // get centres
    for (int i = 0; i < islandAmount; i++) {
      // values lower than 1e-10 are considered -0
      double centreX = (getCartSizeX() * (atan2(-toZeroIfTooClose(zetaX[i] / counter[i]), -toZeroIfTooClose(xiX[i] / counter[i])) + PI)) / (2 * PI);
      double centreY = (getCartSizeY() * (atan2(-toZeroIfTooClose(zetaY[i] / counter[i]), -toZeroIfTooClose(xiY[i] / counter[i])) + PI)) / (2 * PI);
      islands.get(i).setCentreOfMass(new Point2D.Double(centreX, centreY));
    }
  }

  public void setAtomsTypesCounter(int length) {
    atomTypesCounter = new int[length];
    emptyTypesCounter = new int[length];
  }
  
  public String getAtomTypesCounter() {
    String sentence = "";
    for (int i = 0; i < atomTypesAmount; i++) {
      sentence += "\t"+String.valueOf(atomTypesCounter[i]);
    }
    return sentence;
  }
  
  public String getEmptyTypesCounter() {
    String sentence = "";
    for (int i = 0; i < atomTypesAmount; i++) {
      sentence += "\t"+String.valueOf(emptyTypesCounter[i]);
    }
    return sentence;
  }

  /**
   * How far in average is an atom from where it was deposited. It is calculated in {@link  #countIslands(java.io.PrintWriter)
   * } method.
   *
   * @return distance^2
   */
  public double getTracerDistance() {
    return tracerDistance;
  }
  
  public double getCmDistance() {
    return centreMassDistance;
  }
  
  /**
   * How many steps has been moved all the atoms. In practice, it should be the same number as the
   * total rate - depositions.
   *
   * @return
   */
  public long getTotalHops() {
    return hops;
  }

  /**
   * How many mobile atoms are. It is calculated in {@link  #countIslands(java.io.PrintWriter) } method.
   * 
   * @return atoms that are not bulk (island).
   */
  public int getMobileAtoms() {
    return mobileAtoms;
  }
  
  /**
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   *
   * @param probabilities Default rates.
   */
  public void initialiseRates(double[][] probabilities) {
    atomTypesAmount = probabilities.length;
    atomTypesCounter = new int[atomTypesAmount];
    emptyTypesCounter = new int[atomTypesAmount];
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
    islands = new ArrayList<>(); // empty islands
    islandCount = 0;
    multiAtomsMap.clear();
    multiAtomsIndex = 1;
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
    computeDiffusivity();
    islands = new ArrayList<>(); // reset all islands to null
    
    // do the count
    islandCount = 0;
    monomerCount = 0;
    for (int i = 0; i < atomTypesAmount; i++) {
      atomTypesCounter[i] = 0;
      emptyTypesCounter[i] = 0;
    }
    for (int i = 0; i < size(); i++) {
      // visit all the atoms within the unit cell
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        identifyIsland(uc.getAtom(j), false, 0, 0);
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
  
  /**
   * Counts the sum of all islands perimeters. Results are saved in private attributes; call to
   * {@link #getInnerPerimeterLenght()} and {@link #getOuterPerimeterLenght()} to get them.
   *
   * @param print to where should print the output: null (nowhere), standard output or a file.
   */
  public void countPerimeter(PrintWriter print) {
    int occupiedNeighbours;
    innerPerimeter = 0;
    outerPerimeter = 0;
    for (int i = 0; i < size(); i++) {
      // visit all the atoms within the unit cell
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        occupiedNeighbours = 0;
        atom.resetPerimeter();
        for (int k = 0; k < atom.getNumberOfNeighbours(); k++) {
          if (atom.getNeighbour(k).isOccupied()) {
            occupiedNeighbours++;
          } 
        }
        if (atom.isOccupied() && occupiedNeighbours < atom.getNumberOfNeighbours()) {
          atom.setInnerPerimeter();
          innerPerimeter++;
        }
        if (!atom.isOccupied() && occupiedNeighbours > 0) {
          atom.setOuterPerimeter();
          outerPerimeter++; // it can also be +=occupiedNeighbours;
        }
      }
    }

    if (print != null) {
      print.println("Inner perimeter atoms " + innerPerimeter + " outer perimeter atoms " + outerPerimeter);
    }
  }
  
  public void printDistances() {
    double sumAvg = 0.0d;
    double sumMax = 0.0d;
    for (int i = 0; i < getIslandCount(); i++) {
      System.out.println("For island " + i + " centre is " + islands.get(i).getCentreOfMass() + " max distance " + islands.get(i).getMaxDistance() + " avg " + islands.get(i).getAvgDistance());
      sumAvg += islands.get(i).getAvgDistance();
      sumMax += islands.get(i).getMaxDistance();
    }
    System.out.println("--\n Average of average distances " + sumAvg / getIslandCount() + " Average of maximum distances " + sumMax / getIslandCount());
    System.out.println(" Fractal dimension " + 1 / (sumAvg / getIslandCount()) + "\n--");
  }
  
  private double toZeroIfTooClose(double value) {
    return abs(value) < 1e-10 ? -0.0d : value;
  }

  private double computeDiffusivity() {
    tracerDistance = 0;
    centreMassDistance = 0;
    hops = 0;
    double distanceX;
    double distanceY;
    double distanceXTotal;
    double distanceYTotal;
    distanceXTotal = 0.0;
    distanceYTotal = 0.0;
    mobileAtoms = 0;
    // reset all the atoms
    for (int i = 0; i < size(); i++) {
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        atom.setVisited(false);
        atom.setIslandNumber(0);
        if (atom.isOccupied()) {
          mobileAtoms++;
          distanceX = abs(atom.getCartesianSuperCell().getX() * getCartSizeX() + atom.getPos().getX() + uc.getPos().getX() - atom.getDepositionPosition().getX());
          distanceY = abs(atom.getCartesianSuperCell().getY() * getCartSizeY() + atom.getPos().getY() + uc.getPos().getY() - atom.getDepositionPosition().getY());
          distanceXTotal += distanceX;
          distanceYTotal += distanceY;
          tracerDistance += Math.pow(distanceX, 2) + Math.pow(distanceY, 2);
          hops += atom.getHops();
        }
      }
    }
    centreMassDistance = Math.pow(distanceXTotal, 2) + Math.pow(distanceYTotal, 2);
    return tracerDistance;
  }
  
  /**
   * Counts the number of islands that the simulation has. It iterates trough all neighbours, to set 
   * all them the same island number.
   *
   * @param atom atom to be classified.
   * @param fromNeighbour whether is called from outside or recursively.
   */
  private void identifyIsland(AbstractGrowthAtom atom, boolean fromNeighbour, int xDiference, int yDiference) {
    int xRef = xDiference;
    int yRef = yDiference;
    if (!atom.isVisited() && atom.isOccupied() && !fromNeighbour) {
      if (atom.isIsolated()) {
        monomerCount--;
        atom.setIslandNumber(monomerCount);
        atom.setVisited(true);
        atomTypesCounter[atom.getType()]++;
      } else {
        islands.add(new Island(islandCount));
        islandCount++;
      }
    }
    if (atom.isVisited())
      return;
    atom.setVisited(true);
    if (atom.isOccupied()) {
      // Get atom type
      atomTypesCounter[atom.getType()]++;
      atom.setIslandNumber(islandCount);
      islands.get(islandCount-1).addAtom(atom);
      for (int pos = 0; pos < atom.getNumberOfNeighbours(); pos++) {
        AbstractGrowthAtom neighbour = atom.getNeighbour(pos);
        if (!neighbour.isVisited()) {
          if (pos == 0) {
            xDiference = xRef;
            yDiference = yRef - 1;
          }
          if (pos == 1) {
            xDiference = xRef + 1;
            yDiference = yRef;
          }
          if (pos == 2) {
            xDiference = xRef;
            yDiference = yRef + 1;
          }
          if (pos == 3) {
            xDiference = xRef - 1;
            yDiference = yRef;
          }
          identifyIsland(neighbour, true, xDiference, yDiference);
        }
      }
    } else {
      emptyTypesCounter[atom.getType()]++;
    }
  }
  
  /**
   * Counts the number of islands from current atom. It iterates trough all neighbours, to set 
   * all them the same island number.
   *
   * @param atom atom to be classified.
   * @param first previously has detected that a neighbour atom is in an island.
   * @param fromNeighbour whether is called from outside or recursively.
   */
  public void identifyIsland(AbstractGrowthAtom atom, boolean first, boolean fromNeighbour) {
    if (atom.isOccupied() && first) { // Check all neighbours if they're already in an island
      for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
        AbstractGrowthAtom neighbour = atom.getNeighbour(i);
        if (neighbour.getIslandNumber() > 0) {
          atom.setIslandNumber(neighbour.getIslandNumber());
          islands.get(neighbour.getIslandNumber() - 1).addAtom(atom);
          return;
        }
      }
    }
    if (!atom.isVisited() && atom.isOccupied() && !fromNeighbour) {
      if (atom.isIsolated()) {
        atom.setIslandNumber(monomerCount);
        atom.setVisited(true);
      } else {
        islands.add(new Island(islandCount));
        islandCount++;
      }
    }
    if (atom.isVisited())
      return;
    atom.setVisited(true);
    if (atom.isOccupied()) {
      // Get atom type
      atom.setIslandNumber(islandCount);
      islands.get(islandCount-1).addAtom(atom);
      for (int pos = 0; pos < atom.getNumberOfNeighbours(); pos++) {
        AbstractGrowthAtom neighbour = atom.getNeighbour(pos);
        if (!neighbour.isVisited()) {
          identifyIsland(neighbour, false, true);
        }
      }
    }
  }
  
  public void swapAtomsInMultiAtom(AbstractGrowthAtom origin, AbstractGrowthAtom destination) {
    if (!destination.getMultiAtomNumber().isEmpty()) {
      Iterator iter = destination.getMultiAtomNumber().iterator();
      while (iter.hasNext()) {
        Integer multiAtomNumber = ((Integer) iter.next());
        MultiAtom multiAtom = multiAtomsMap.get(multiAtomNumber);
        multiAtom.removeAtom(origin);
        multiAtom.addAtom(destination);
      }
    }
  }
  
  /**
   * Counts the number of islands from current atom. It iterates trough all neighbours, to set 
   * all them the same island number.
   *
   * @param atom Atom to be classified.
   * @return Created Island, null otherwise.
   */
  public ArrayList<MultiAtom> identifyAddMultiAtom(AbstractGrowthAtom atom) {
    ArrayList<MultiAtom> foundMultiAtoms = new ArrayList<>();
    if (atom.isOccupied()) {
      if (atom.getRealType() == 5) {// 3 consecutive occupied neighbours.
        for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
          AbstractGrowthAtom neighbour = atom.getNeighbour(i);
          if (neighbour.isOccupied() && neighbour.getRealType() == 5) {
            MultiAtom found = addMultiAtomIsland(atom, neighbour, i);
            if (found != null)
              foundMultiAtoms.add(found);
          }
        }
      }
    }
    return foundMultiAtoms;
  }
 
  private MultiAtom addMultiAtomIsland(AbstractGrowthAtom atom, AbstractGrowthAtom neighbour, int i) {
    if (onlyOneNeighbourInCommon(atom, neighbour)) {
      if (atom.getMultiAtomNumber().isEmpty() || neighbour.getMultiAtomNumber().isEmpty() ||
              allMultiAtomsDifferent(atom, neighbour)) { // an atom can belong to many multi-atom
        MultiAtom multiAtomIsland = new MultiAtom(multiAtomsIndex);
        multiAtomsMap.put(multiAtomsIndex,multiAtomIsland);
        atom.setMultiAtomNumber(multiAtomsIndex);
        neighbour.setMultiAtomNumber(multiAtomsIndex);
        multiAtomsIndex++;
        multiAtomIsland.addAtom(atom);
        multiAtomIsland.addAtom(neighbour);
        multiAtomIsland.setDirection(i);
        return multiAtomIsland;
      }
    }
    return null;
  }
  
  private boolean allMultiAtomsDifferent(AbstractGrowthAtom atom, AbstractGrowthAtom neighbour) {
    boolean overlap = neighbour.getMultiAtomNumber().stream().noneMatch(s -> atom.getMultiAtomNumber().contains(s));
    return overlap;
  }
  
  /**
   * Edge diffusion can only happen when two atoms are in an edge. This can be identified by
   * counting common neighbours: if only one neighbour is in common, true edge diffusion; if there
   * are two, is not possible edge diffusion.
   *
   * @param atom
   * @param neighbour
   * @return
   */
  private boolean onlyOneNeighbourInCommon(AbstractGrowthAtom atom, AbstractGrowthAtom neighbour) {
    ArrayList<AbstractGrowthAtom> allNeighbours = new ArrayList(neighbour.getAllNeighbours());
    allNeighbours.addAll(atom.getAllNeighbours());
    List sameNeighbours = (List) allNeighbours.stream().filter(i -> Collections.frequency(allNeighbours, i) > 1).collect(Collectors.toList()); // always two common neighbours.
    return !((AbstractGrowthAtom)sameNeighbours.get(0)).isOccupied() ||
            !((AbstractGrowthAtom)sameNeighbours.get(1)).isOccupied();
  }
  
  public double identifyRemoveMultiAtomIsland(AbstractGrowthAtom atom) {
    double removedCount = 0;
    boolean separated = false;
    if (!atom.getMultiAtomNumber().isEmpty()) {
      
      if (!atom.isOccupied()) {
        separated = true;
      }
      if (atom.getRealType() != 5) {
        separated = true;
      } 
      
      if (atom.isOccupied() && atom.getRealType() == 5) { // atom is occupied and it is of type 5
        separated = true;
        for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
          AbstractGrowthAtom neighbour = atom.getNeighbour(i);
          if (neighbour.isOccupied() && neighbour.getRealType() == 5) {
            separated = false;
          }
        }
      }
      if (separated) {
        removedCount += removeMultiAtom(atom);
      }
    }
    return removedCount;
  }
  
  private double removeMultiAtom(AbstractGrowthAtom atom) {
    double removedRate = 0;
    Iterator iter = new HashSet(atom.getMultiAtomNumber()).iterator(); // iterate over a copy
    while (iter.hasNext()) {
      int multiAtomIndex = (int) iter.next();
      MultiAtom multiAtom = multiAtomsMap.get(multiAtomIndex);
      removedRate += multiAtom.getRate(MULTI);
      while (multiAtom.getNumberOfAtoms() > 0) {
        AbstractGrowthAtom neighbour = multiAtom.getAtomAt(0);
        multiAtom.removeAtom(neighbour);
        atom.removeMultiAtomNumber(multiAtomIndex);
        neighbour.removeMultiAtomNumber(multiAtomIndex);
      }
      multiAtomsMap.remove(multiAtomIndex);
    }

    return removedRate;
  }
}
