/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import java.io.PrintWriter;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.floorDiv;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.CatalysisAtom;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.unitCell.IUc;
import kineticMonteCarlo.unitCell.SimpleUc;

/**
 * In this case we assume that the unit cell is one and it only contains one element. Thus, we can
 * maintain the previous logic, where there is no any unit cell at all.
 
/**
 *
 * @author karmele
 */
public abstract class CatalysisLattice extends AbstractGrowthLattice {

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
  private int monomerCount;
  private int[] atomTypesCounter;
  private int atomTypesAmount;
  private ArrayList<Island> islands;
  private int innerPerimeter;
  private int outerPerimeter;
  private double diffusivityDistance;
  private int mobileAtoms;
  private int hops;

  public CatalysisLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified) {
    super(hexaSizeI, hexaSizeJ, modified);
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
  }

  public abstract CatalysisAtom getNeighbour(int iHexa, int jHexa, int neighbour);

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
  public CatalysisAtom getAtom(int index) {
    return (CatalysisAtom) getUc(index).getAtom(0);
  }
  
  /**
   * We ignore the unitCellPos by now, we get directly the atom of i,j hexagonal location.
   *
   * @param iHexa
   * @param jHexa
   * @return required atom.
   */
  @Deprecated
  public CatalysisAtom getAtom(int iHexa, int jHexa) {
    return (CatalysisAtom) ucArray[iHexa][jHexa].getAtom(0);
  }
  
  public CatalysisAtom getAtom(int iHexa, int jHexa, int unitCellPos) {
    int index = jHexa * getHexaSizeI() + iHexa;
    return (CatalysisAtom) getUc(index).getAtom(0);
  }
  
  /**
   * Returns the atom that it is in the middle of single flake simulation.
   * 
   * @return central atom.
   */
  public abstract CatalysisAtom getCentralAtom();

  @Override
  public SimpleUc getUc(int pos) {
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
  
  public final void setAtoms(CatalysisAtom[][] atoms) {
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        CatalysisAtom atom = atoms[i][j];
        ucArray[i][j] = new SimpleUc(i, j, atom);

        ucArray[i][j].setPosX(getCartX(i, j));
        ucArray[i][j].setPosY(getCartY(j));
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
  
  public SimpleUc getUc(int iLattice, int jLattice) {
    return ucArray[iLattice][jLattice];
  }
  
  public Island getIsland(int i) {
    if (islands != null) {
      return islands.get(i);
    }
    return null;
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
    
  /**
   * Calculates arithmetic average of gyradius, iterating over all islands. Only valid for basic
   * growth simulation mode.
   *
   * @return average gyradius
   */
  public double getCentreOfMassAndAverageGyradius() {
    double averageGyradius = 0.0;
    int i;
    for (i = 0; i < islands.size(); i++) {
      averageGyradius += islands.get(i).calculateCentreOfMassAndGyradius();
    }
    return averageGyradius / (double) i;
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
  
  public abstract void deposit(CatalysisAtom atom, boolean forceNucleation);

  /**
   * Extract the given atom from the lattice.
   * 
   * @param atom the atom to be extracted.
   * @return probability change (positive value).
   */
  public abstract double extract(CatalysisAtom atom);
  
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
        CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
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

  public void getCentreOfMassTry() {
    int islandAmount = getIslandCount();
    int minX[] = new int[islandAmount];
    int maxX[] = new int[islandAmount];
    int minY[] = new int[islandAmount];
    int maxY[] = new int[islandAmount];
    //islands = new Island[islandAmount];
    //Arrays.setAll(islands, i -> new Island(i));
    for (int i = 0; i < size(); i++) {
      IUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
        int islandNumber = atom.getIslandNumber();
      }
    }
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
        CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
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
  
  public String getAtomTypesCounter() {
    String sentence = "";
    for (int i = 0; i < atomTypesAmount; i++) {
      sentence += "\t"+String.valueOf(atomTypesCounter[i]);
    }
    return sentence;
  }

  /**
   * How far in average is an atom from where it was deposited. It is calculated in {@link  #countIslands(java.io.PrintWriter)
   * } method.
   *
   * @return distance^2
   */
  public double getDiffusivityDistance() {
    return diffusivityDistance;
  }
  
  /**
   * How many steps has been moved all the atoms. In practice, it should be the same number as the
   * total rate - depositions.
   *
   * @return
   */
  public int getTotalHops() {
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
    for (int i = 0; i < size(); i++) {
      SimpleUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
        atom.initialiseRates(probabilities);
      }
    }
  }

  @Override
  public void reset() {
    for (int i = 0; i < size(); i++) {
      SimpleUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
        atom.clear();
      }
    }
  }

  void addAtom(CatalysisAtom atom) {
    modified.addOwnAtom(atom);
  }
  
  void addBondAtom(CatalysisAtom atom) {
    modified.addBondAtom(atom);
  }

  /**
   * Counts the number of islands that the simulation has.
   *
   * @param print to where should print the output: null (nowhere), standard output or a file.
   * @return number of islands.
   */
  public int countIslands(PrintWriter print) {
    diffusivityDistance = 0.0;
    hops = 0;
    double distanceX;
    double distanceY;
    mobileAtoms = 0;
    // reset all the atoms
    for (int i = 0; i < size(); i++) {
      SimpleUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
        atom.setVisited(false);
        atom.setIslandNumber(0);
        if (atom.isOccupied()) {
          mobileAtoms++;
          distanceX = abs(atom.getPos().getX() + uc.getPos().getX() - atom.getDepositionPosition().getX());
          if (distanceX > getCartSizeX() / 2) {
            distanceX = getCartSizeX() - distanceX;
          }
          distanceY = abs(atom.getPos().getY() + uc.getPos().getY() - atom.getDepositionPosition().getY());
          if (distanceY > getCartSizeY() / 2) {
            distanceY = getCartSizeY() - distanceY;
          }
          diffusivityDistance += Math.pow(distanceX, 2) + Math.pow(distanceY, 2);
          hops += atom.getHops();
        }
      }
    }
    islands = new ArrayList<>(); // reset all islands to null
    
    // do the count
    islandCount = 0;
    monomerCount = 0;
    for (int i = 0; i < atomTypesAmount; i++) {
      atomTypesCounter[i] = 0;
      
    }
    for (int i = 0; i < size(); i++) {
      // visit all the atoms within the unit cell
      SimpleUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        identifyIsland((CatalysisAtom) uc.getAtom(j), false, 0, 0);
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
        SimpleUc uc = getUc(i);
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
      SimpleUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
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

  /**
   * Counts the number of islands that the simulation has. It iterates trough all neighbours, to set 
   * all them the same island number.
   *
   * @param atom atom to be classified.
   * @param fromNeighbour whether is called from outside or recursively.
   */
  private void identifyIsland(CatalysisAtom atom, boolean fromNeighbour, int xDiference, int yDiference) {
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
      atom.setRelativeX(xDiference);
      atom.setRelativeY(yDiference);
      islands.get(islandCount-1).addAtom(atom);
      for (int pos = 0; pos < atom.getNumberOfNeighbours(); pos++) {
        CatalysisAtom neighbour = atom.getNeighbour(pos);
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
    }
  }
}

