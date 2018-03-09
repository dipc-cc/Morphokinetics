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
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import basic.io.Restart;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.site.ModifiedBuffer;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import static java.lang.Math.abs;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;
import static kineticMonteCarlo.site.AbstractSite.TERRACE;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import utils.StaticRandom;
import utils.list.LinearList;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthKmc extends AbstractSurfaceKmc {

  private AbstractGrowthLattice lattice;
  private final ModifiedBuffer modifiedBuffer;
  private final boolean justCentralFlake;
  private final boolean periodicSingleFlake;
  private RoundPerimeter perimeter;
  private final boolean useMaxPerimeter;
  private final short perimeterType;
  private DevitaAccelerator accelerator;
  
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage;
  /**
   * Total area of a single flake simulation.
   */
  private int area;
  /**
   * Attribute to store temporally the current area of the simulation.
   */
  private int currentArea;
  private double depositionRatePerSite;
  private int freeArea;
  private int nucleations;
  /**
   * Attribute to control the output of data every 1% and nucleation.
   */
  private final boolean extraOutput;
  /**
   * Attribute to control the output of extra data of delta time between two attachments and between
   * an atom is deposited and attached to an island.
   */
  private final boolean extraOutput2;
  /**
   * Activation energy output at the end of execution
   */
  private final boolean aeOutput;
  private final ActivationEnergy activationEnergy;
  /**
   * If two terraces are together freeze them, in multi-flake simulation mode.
   */
  private final boolean forceNucleation;
  
  private double terraceToTerraceProbability;
  
  private long simulatedSteps;
  private double sumProbabilities;
  private Restart restart;

  public AbstractGrowthKmc(Parser parser) {
    super(parser);
    justCentralFlake = parser.justCentralFlake();
    periodicSingleFlake = parser.isPeriodicSingleFlake();
    float coverage = (float) parser.getCoverage() / 100;
    if ((!justCentralFlake) && ((0f > coverage) || (1f < coverage))) {
      System.err.println("Chosen coverage is not permitted. Selecting the default one: 30%");
      maxCoverage = 0.3f;
    } else {
      maxCoverage = coverage;
    }
    useMaxPerimeter = parser.useMaxPerimeter();
    forceNucleation = parser.forceNucleation();
    modifiedBuffer = new ModifiedBuffer();
    getList().autoCleanup(true);
    perimeterType = parser.getPerimeterType();
    
    extraOutput2 = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA2);
    if (extraOutput2) {
      extraOutput = extraOutput2;
    } else {
      extraOutput = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    }
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    restart = new Restart(extraOutput, extraOutput2);
    nucleations = 0;
    activationEnergy = new ActivationEnergy(parser);
  }
    
  /**
   * 
   * @param depositionRateML deposition rate per site (synonyms: deposition flux and diffusion mono layer).
   * @param islandDensitySite only used for single flake simulations to properly calculate deposition rate.
   */
  public final void setDepositionRate(double depositionRateML, double islandDensitySite) {
    area = calculateAreaAsInLattice();
    depositionRatePerSite = depositionRateML;
    
    if (justCentralFlake) {
      getList().setDepositionProbability(depositionRateML / islandDensitySite);
    } else {
      freeArea = calculateAreaAsInKmcCanvas();
      getList().setDepositionProbability(depositionRatePerSite * freeArea);
    }
  }

  public void setTerraceToTerraceProbability(double terraceToTerraceProbability) {
    this.terraceToTerraceProbability = terraceToTerraceProbability;
  }
  
  /**
   * Takes the current radius from the perimeter attribute.
   * 
   * @return current radius of the single flake simulation.
   */
  public int getCurrentRadius() {
    return perimeter.getCurrentRadius();
  }
  
  /**
   * Returns the coverage of the simulation. 
   * Thus, the number of occupied locations divided by the total number of locations.
   * @return A value between 0 and 1.
   */
  @Override
  public float getCoverage() {
    if (justCentralFlake && !periodicSingleFlake) { // count only simulated places in the circle
      float occupied = (float) lattice.getOccupied();
      return occupied / area;
    } else {
      return lattice.getCoverage();
    }
  }

  public DevitaAccelerator getAccelerator() {
    return accelerator;
  }

  public void setAccelerator(DevitaAccelerator accelerator) {
    this.accelerator = accelerator;
  }

  /**
   * @param lattice the lattice to set
   */
  public final void setLattice(AbstractGrowthLattice lattice) {
    super.setLattice(lattice);
    this.lattice = lattice;
  }

  /**
   * @return the modifiedBuffer
   */
  public final ModifiedBuffer getModifiedBuffer() {
    return modifiedBuffer;
  }
  
  /**
   * @return the justCentralFlake
   */
  public boolean isJustCentralFlake() {
    return justCentralFlake;
  }

  public RoundPerimeter getPerimeter() {
    return perimeter;
  }

  /**
   * @param perimeter the perimeter to set.
   */
  public final void setPerimeter(RoundPerimeter perimeter) {
    this.perimeter = perimeter;
  }

  /**
   * @param area the area to set.
   */
  public void setArea(int area) {
    this.area = area;
  }
 
  @Override
  public void initialiseRates(double[] rates) {
    //we modify the 1D array into a 2D array;
    int length = (int) Math.sqrt(rates.length);
    double[][] processProbs2D = new double[length][length];

    for (int i = 0; i < length; i++) {
      for (int j = 0; j < length; j++) {
        processProbs2D[i][j] = rates[i * length + j];
      }
    }
    lattice.initialiseRates(processProbs2D);
    activationEnergy.setRates(processProbs2D);
    restart.writeExtraOutput(lattice, 0, 0, 0, depositionRatePerSite * freeArea, getList().getDiffusionProbabilityFromList(), 0, 0);
  }

  @Override
  public void reset() {
    super.reset();
    freeArea = calculateAreaAsInKmcCanvas();
    activationEnergy.reset();
    nucleations = 0;
  }
  
  @Override
  public AbstractGrowthLattice getLattice() {
    return lattice;
  }
  
  @Override
  public int simulate() {
    int coverageThreshold = 1;
    int limit = 100000;
    int returnValue = 0;
    boolean computeTime = false;
    simulatedSteps = 0;
    sumProbabilities = 0.0d;
    terraceToTerraceProbability = lattice.getUc(0).getSite(0).getProbability(0, 0);
    if (justCentralFlake) {
      returnValue = super.simulate();
    } else {
      while (lattice.getCoverage() < maxCoverage) {
        if (lattice.isPaused()) {
          try {
            Thread.sleep(250);
          } catch (InterruptedException ex) {
            Logger.getLogger(AbstractGrowthKmc.class.getName()).log(Level.SEVERE, null, ex);
          }
        } else {
          activationEnergy.updatePossibles(getList().getIterator(), getList().getGlobalProbability(), getList().getDeltaTime(computeTime));
          computeTime = true;
          if (extraOutput && getEstimatedCoverage() * limit >= coverageThreshold) { // print extra data every 1% of coverage, previously every 1/1000 and 1/10000
            if (coverageThreshold == 10 && limit > 100) { // change the interval of printing
              limit = limit / 10;
              coverageThreshold = 1;
            }
            printData(null);
            coverageThreshold++;
          }
          if (performSimulationStep()) {
            break;
          }
          simulatedSteps++;
          sumProbabilities += getList().getDiffusionProbabilityFromList();
        }
      }
    }
    if (aeOutput) {
      double ri = ((LinearList) getList()).getRi_DeltaI();
      double time = getList().getTime();
      System.out.println("Needed steps " + simulatedSteps + " time " + time + " Ri_DeltaI " + ri + " R " + ri / time + " R " + simulatedSteps / time);
      PrintWriter standardOutputWriter = new PrintWriter(System.out);
      activationEnergy.printAe(standardOutputWriter, -1);
    } 
    
    // Dirty mode to have only one interface of countIslands
    PrintWriter standardOutputWriter = new PrintWriter(System.out);
    lattice.countIslands(standardOutputWriter);
    lattice.countPerimeter(standardOutputWriter);
    lattice.getCentreOfMass();
    lattice.getAverageGyradius();
    lattice.getDistancesToCentre();
    standardOutputWriter.flush();
    restart.flushExtra();
    return returnValue;
  }

  @Override
  public void depositSeed() {
    getLattice().resetOccupied();
    if (justCentralFlake) {
      setAtomPerimeter();
      setCurrentOccupiedArea(8); // Seed will have 8 atoms
      int depositedAtoms = 1;
      AbstractGrowthSite centralAtom = lattice.getCentralAtom();
      centralAtom.setDepositionPosition(new Point3D(0, 0, 0));
      deposition:
      while (true) {
        depositAtom(centralAtom);
        for (int i = 0; i < centralAtom.getNumberOfNeighbours(); i++) {
          AbstractGrowthSite atom = centralAtom.getNeighbour(i);
          if (depositAtom(atom)) {
            depositedAtoms++;
            atom.setDepositionPosition(new Point3D(0, 0, 0));
          }
          if (depositedAtoms > 7) {
            break deposition;
          }
        }
        centralAtom = centralAtom.getNeighbour(1);
      }
    } else {
      // Do not deposit anything, just reset deposition probability
      getList().setDepositionProbability(depositionRatePerSite * freeArea);
    }
  }
  
  /**
   * Performs a simulation step.
   * 
   * @return true if a stop condition happened (all atom etched, all surface covered).
   */
  @Override
  protected boolean performSimulationStep() {
    AbstractGrowthSite originAtom = (AbstractGrowthSite) getList().nextEvent();
    AbstractGrowthSite destinationAtom;

    if (originAtom == null) {
      destinationAtom = depositNewAtom();

    } else {
      do {
        destinationAtom = chooseRandomHop(originAtom, 0);
        if (destinationAtom.equals(originAtom)) {
          destinationAtom.equals(originAtom);
          break;
        }
      } while (!diffuseAtom(originAtom, destinationAtom));
    }

    if (justCentralFlake && perimeterMustBeEnlarged(destinationAtom)) {
      int nextRadius = perimeter.goToNextRadius();
      if (nextRadius > 0
              && nextRadius < lattice.getCartSizeX() / 2
              && nextRadius < lattice.getCartSizeY() / 2) {
        printData(null);
        if (perimeterType == RoundPerimeter.CIRCLE) {
          perimeter.setCurrentPerimeter(lattice.setInsideCircle(nextRadius, periodicSingleFlake));
          int newArea;
          newArea = calculateAreaAsInKmcCanvas();
          freeArea += newArea - currentArea;
          currentArea = newArea;
        } else {
          perimeter.setCurrentPerimeter(lattice.setInsideSquare(nextRadius));
        }
      } else {
        return true;
      }
    }
    return false;
  }
  
  /**
   * This has to be called only once from AgKmc or GrapheneKmc.
   * 
   * @param occupiedSize Size of the seed in atoms. Number to calculate free space. 
   */
  void setCurrentOccupiedArea(int occupiedSize) {
    currentArea = calculateAreaAsInKmcCanvas();
    freeArea = currentArea - occupiedSize;
  }
  
  /**
   * Internal method to select the perimeter size and type. Must be used in depositSeed() method
   * just before depositing the seed.
   */
  void setAtomPerimeter() {
    if (useMaxPerimeter) {
      perimeter.setMaxPerimeter(lattice.getCartSizeX(), lattice.getCartSizeY());
    } else {
      perimeter.setMinRadius();
    }
    if (perimeterType == RoundPerimeter.CIRCLE) {
      perimeter.setCurrentPerimeter(lattice.setInsideCircle(perimeter.getCurrentRadius(), periodicSingleFlake));
    } else {
      perimeter.setCurrentPerimeter(lattice.setInsideSquare(perimeter.getCurrentRadius()));
    }
  }

  private boolean depositAtom(AbstractGrowthSite atom) {
    if (atom.isOccupied()) {
      return false;
    }

    boolean force = (forceNucleation && !justCentralFlake && atom.areTwoTerracesTogether()); //indica si 2 terraces se van a chocar
    lattice.deposit(atom, force);
    lattice.addOccupied();
    modifiedBuffer.updateAtoms(getList());
    
    return true;
  }

  /**
   * Selects the next step randomly. If there is not accelerator, an neighbour atom of originAtom is
   * chosen. With Devita accelerator many steps far away atom can be chosen.
   *
   * @param originAtom atom that has to be moved.
   * @param times how many times it has been called recursively
   * @return destinationAtom.
   */
  private AbstractGrowthSite chooseRandomHop(AbstractGrowthSite originAtom, int times) {
    AbstractGrowthSite destinationAtom;
    if (accelerator != null) {
      destinationAtom = accelerator.chooseRandomHop(originAtom);
    } else {
      destinationAtom =  originAtom.chooseRandomHop();
    }

    if (justCentralFlake && destinationAtom.areTwoTerracesTogether()) {
      if (times > originAtom.getNumberOfNeighbours()*100) { // it is not possible to diffuse without forming a dimer. So returning originAtom itself to make possible to another originAtom to be choosen.
        return originAtom;
      }
        
      return chooseRandomHop(originAtom, times+1);
    }
    if (destinationAtom.isOutside()) {
      do {
        destinationAtom = perimeter.getPerimeterReentrance(originAtom);
      } while(destinationAtom.areTwoTerracesTogether() || destinationAtom.areTwoTerracesTogetherInPerimeter(originAtom));
      // Add to the time the inverse of the probability to go from terrace to terrace, multiplied by steps done outside the perimeter (from statistics).
      getList().addTime(perimeter.getNeededSteps() / terraceToTerraceProbability);
    }
    return destinationAtom;
  }
  
  /**
   * Print current information to extra file.
   *
   * @param coverage used to have exactly the coverage and to be easily greppable.
   */
  private void printData(Integer coverage) {
    float printCoverage;
    if (coverage != null) {
      printCoverage = (float) (coverage) / 100;
    } else {
      printCoverage = getCoverage();
    }
    restart.writeExtraOutput(lattice, printCoverage, nucleations, getTime(), 
            (double) (depositionRatePerSite * freeArea), getList().getDiffusionProbability(), simulatedSteps, sumProbabilities);
    
    sumProbabilities = 0.0d;
    if (aeOutput) {
      activationEnergy.printAe(restart.getExtraWriter(), printCoverage);
    }
    restart.flushExtra();
  }
  
  /**
   * Calculates current area or, i.e. the number of current places that simulation has. This total
   * area changes with the current radius. It is calculated as is done in KmcCanvas class.
   * 
   * @return simulated area.
   */
  private int calculateAreaAsInKmcCanvas() {
    int totalArea = 0;
    for (int i = 0; i < lattice.size(); i++) {
      AbstractGrowthUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        if (uc.getSite(j).isOccupied() || !(uc.getSite(j).isOutside())) {
          totalArea++;
        }
      }
    }
    return totalArea;
  }

  /**
   * Calculates the total area of a single flake simulation. 
   * 
   * @return total area.
   */
  private int calculateAreaAsInLattice() {
    int totalArea = 0;
    // Get the minimum radius of both coordinates
    float minRadius = Math.min(lattice.getCartSizeX()/2,lattice.getCartSizeY()/2);
    // Get the maximum radius multiple of 5
    float radius = ((float) Math.floor(minRadius/5f)*5f);
    
    for (int i = 0; i < lattice.size(); i++) {
      AbstractGrowthUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthSite atom = uc.getSite(j);
        double x = atom.getPos().getX() + uc.getPos().getX();
        double y = atom.getPos().getY() + uc.getPos().getY();
        double distance = lattice.getDistanceToCenter(x, y);
        if (radius > distance) {
          totalArea++;
        } 
      }
    }

    return totalArea;
  }
  
  /**
   * Moves an atom from origin to destination.
   * 
   * @param originAtom origin atom.
   * @param destinationAtom destination atom.
   * @return true if atom has moved, false otherwise.
   */
  private boolean diffuseAtom(AbstractGrowthSite originAtom, AbstractGrowthSite destinationAtom) {
    //If is not eligible, it can not be diffused
    if (!originAtom.isEligible()) {
      return false;
    }

    // if the destination atom is occupied do not diffuse (even if it is itself)
    if (destinationAtom.isOccupied()) {
      return false;
    }
    
    boolean force = (forceNucleation && !justCentralFlake && destinationAtom.areTwoTerracesTogether()); //indica si 2 terraces se van a chocar
    if (force) {
      printData(null);
      nucleations++;
    }
    int oldType = originAtom.getRealType();

    Point3D origSuperCell = originAtom.getCartesianSuperCell();
    // Going out from the right ->|->
    if (abs(originAtom.getCartesianPosition().getX() - getLattice().getCartSizeX()) <= 1
            && destinationAtom.getCartesianPosition().getX() <= 1) {
      origSuperCell = new Point3D(origSuperCell.getX() + 1, origSuperCell.getY(), 0.0);
    }
    // Going out from the left <-|<-
    if (originAtom.getCartesianPosition().getX() <= 1
            && abs(destinationAtom.getCartesianPosition().getX() - getLattice().getCartSizeX()) <= 1) {
      origSuperCell = new Point3D(origSuperCell.getX() - 1, origSuperCell.getY(), 0.0);
    }
    // Going down
    if (abs(originAtom.getCartesianPosition().getY() - getLattice().getCartSizeY()) <= 1
            && destinationAtom.getCartesianPosition().getY() <= 1) {
      origSuperCell = new Point3D(origSuperCell.getX(), origSuperCell.getY() + 1, 0.0);
    }
    // Going up
    if (originAtom.getCartesianPosition().getY() <= 1
            && abs(destinationAtom.getCartesianPosition().getY() - getLattice().getCartSizeY()) <= 1) {
      origSuperCell = new Point3D(origSuperCell.getX(), origSuperCell.getY() - 1, 0.0);
    }
    destinationAtom.setCartesianSuperCell(origSuperCell);
    double probabilityChange = lattice.extract(originAtom);
    getList().addDiffusionProbability(-probabilityChange); // remove the probability of the extracted atom
    originAtom.setCartesianSuperCell(new Point3D(0,0,0));
    lattice.deposit(destinationAtom, force);
    destinationAtom.swapAttributes(originAtom);
    if (extraOutput2) {
      if (oldType == TERRACE && destinationAtom.getType() != TERRACE) { // atom gets attached to the island
        atomAttachedToIsland(destinationAtom);
      }
    }
    if (aeOutput) {
      activationEnergy.updateSuccess(oldType, destinationAtom.getRealType());
    }
    modifiedBuffer.updateAtoms(getList());

    return true;
  }
  
  /**
   * An atom has been attached to an island an so printing this to output files.
   *
   * @param destination destination atom is required to compute time difference.
   *
   */
  private void atomAttachedToIsland(AbstractGrowthSite destination) {
    restart.writeExtra2Output(lattice, destination, getCoverage(), getTime(), getList().getDiffusionProbabilityFromList());
  }

  private AbstractGrowthSite depositNewAtom() {
    AbstractGrowthSite destinationAtom;
    int ucIndex = 0;
    if (justCentralFlake) {
      do {
        // Deposit in the perimeter
        destinationAtom = perimeter.getRandomPerimeterAtom();
        destinationAtom.setDepositionPosition(new Point3D(0, 0, 0));
      } while (destinationAtom.areTwoTerracesTogetherInPerimeter(destinationAtom) || !depositAtom(destinationAtom));
    } else {
      do {
        int random = StaticRandom.rawInteger(lattice.size() * lattice.getUnitCellSize());
        ucIndex = Math.floorDiv(random, lattice.getUnitCellSize());
        int atomIndex = random % lattice.getUnitCellSize();
        destinationAtom = lattice.getUc(ucIndex).getSite(atomIndex);
      } while (!depositAtom(destinationAtom));
      // update the free area and the deposition rate counting just deposited atom
      freeArea--;
      getList().setDepositionProbability(depositionRatePerSite * freeArea);
    }
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(lattice.getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    
    return destinationAtom;
  }

  private boolean perimeterMustBeEnlarged(AbstractGrowthSite destinationAtom) {
    if (perimeterType == RoundPerimeter.SQUARE) {
      Point2D centreCart = lattice.getCentralCartesianLocation();
      double left = centreCart.getX() - perimeter.getCurrentRadius();
      double right = centreCart.getX() + perimeter.getCurrentRadius();
      double bottom = centreCart.getY() - perimeter.getCurrentRadius();
      double top = centreCart.getY() + perimeter.getCurrentRadius();
      Point2D position = lattice.getCartesianLocation(destinationAtom.getiHexa(), destinationAtom.getjHexa());

      return (destinationAtom.getType() > 0) && (Math.abs(left - position.getX()) < 2
              || Math.abs(right - position.getX()) < 2
              || Math.abs(top - position.getY()) < 2
              || Math.abs(bottom - position.getY()) < 2);
    } else {
      boolean atomType = destinationAtom.getType() > 0;
      boolean distance = perimeter.contains(destinationAtom);
      return (atomType && distance);
    }
  }
  
  /**
   * Method to obtain theoretical coverage from deposition flux and current time. This is beneficial
   * to obtain better statistics for atom count (atoms with 0 neighbour, 1 neighbour and so on).
   *
   * @return estimated coverage.
   */
  private double getEstimatedCoverage() {
    return 1 - Math.exp(-depositionRatePerSite * getList().getTime());
  }
}
