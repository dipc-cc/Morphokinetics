/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.geometry.Point3D;
import static kineticMonteCarlo.atom.AbstractAtom.BULK;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.CatalysisAtom;
import static kineticMonteCarlo.atom.CatalysisAtom.EDGE;
import static kineticMonteCarlo.atom.CatalysisAtom.TERRACE;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.CatalysisLattice;
import kineticMonteCarlo.unitCell.CatalysisUc;
import utils.StaticRandom;

/**
 *
 * @author Karmele Valencia
 */
public class CatalysisKmc extends AbstractGrowthKmc {
  
  public CatalysisKmc(Parser parser) {
    super(parser);
    
    HopsPerStep distancePerStep = new HopsPerStep();
    CatalysisLattice catalysisLattice = new CatalysisLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    catalysisLattice.init();
    setLattice(catalysisLattice); 
    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("Ag"));
    }
    if (parser.useDevita()) {
      configureDevitaAccelerator(distancePerStep);
    }
  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
  }
  
  private void configureDevitaAccelerator(HopsPerStep distancePerStep) {
    setAccelerator(new DevitaAccelerator(getLattice(), distancePerStep));

    if (getAccelerator() != null) {
      getAccelerator().tryToSpeedUp(TERRACE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(100)
              .setMaxAccumulatedSteps(200)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(8));

      getAccelerator().tryToSpeedUp(EDGE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(30)
              .setMaxAccumulatedSteps(100)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(5));
    }
  }
   /**
   * Performs a simulation step.
   * 
   * @return true if a stop condition happened (all atom etched, all surface covered).
   */
  @Override
  protected boolean performSimulationStep() {
    CatalysisAtom originAtom = (CatalysisAtom) getList().nextEvent();
    CatalysisAtom destinationAtom;

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

    return false;
  }
  
  /**
   * This has to be called only once from AgKmc or GrapheneKmc.
   * 
   * @param occupiedSize Size of the seed in atoms. Number to calculate free space. 
   */
  @Override
  void setCurrentOccupiedArea(int occupiedSize) {
    currentArea = calculateAreaAsInKmcCanvas();
    freeArea = currentArea - occupiedSize;
  }
  
  /**
   * Internal method to select the perimeter size and type. Must be used in depositSeed() method
   * just before depositing the seed.
   */
  @Override
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

  private boolean depositAtom(CatalysisAtom atom) {
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
  private CatalysisAtom chooseRandomHop(CatalysisAtom originAtom, int times) {
    CatalysisAtom destinationAtom;
    if (accelerator != null) {
      destinationAtom = (CatalysisAtom) accelerator.chooseRandomHop(originAtom);
    } else {
      destinationAtom =  (CatalysisAtom) originAtom.chooseRandomHop();
    }

    if (justCentralFlake && destinationAtom.areTwoTerracesTogether()) {
      if (times > originAtom.getNumberOfNeighbours()*100) { // it is not possible to diffuse without forming a dimer. So returning originAtom itself to make possible to another originAtom to be choosen.
        return originAtom;
      }
        
      return chooseRandomHop(originAtom, times+1);
    }
    if (destinationAtom.isOutside()) {
      do {
        destinationAtom = (CatalysisAtom) perimeter.getPerimeterReentrance(originAtom);
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
    int islandCount = lattice.countIslands(outData);
    float printCoverage;
    String coverageFormat;
    if (coverage != null) {
      printCoverage = (float) (coverage) / 100;
      coverageFormat = "%2.2f";
    } else {
      printCoverage = getCoverage();
      coverageFormat = "%f";
    }

    lattice.getCentreOfMass();
    double gyradiusBasic = lattice.getCentreOfMassAndAverageGyradius();
    lattice.getDistancesToCentre();
    lattice.countPerimeter(null);
    //compute the average distances to centre.
    float avgGyradius = lattice.getAverageGyradius();
    int numberOfAtomFirstIsland = 0;
    try{
      numberOfAtomFirstIsland = lattice.getIsland(0).getNumberOfAtoms();
    } catch (NullPointerException | IndexOutOfBoundsException e) { // It may occur that there is no any island
    }
    outData.format(Locale.US, coverageFormat + outDataFormat, printCoverage, getTime(),
            nucleations, islandCount, (double) (depositionRatePerSite * freeArea),
            getList().getTotalProbability(), lattice.getMonomerCount(), simulatedSteps, sumProbabilities, avgGyradius,
            lattice.getInnerPerimeterLenght(), lattice.getOuterPerimeterLenght(), lattice.getDiffusivityDistance(), lattice.getMobileAtoms(), numberOfAtomFirstIsland, lattice.getTotalHops(),
            lattice.getAtomTypesCounter());
    sumProbabilities = 0.0d;
    if (aeOutput) {
      getActivationEnergy().printAe(outData, printCoverage);
    }
    outData.flush();
    if (extraOutput2) {
      outDeltaAttachments.flush();
      outPerAtom.flush();
    }
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
      CatalysisUc uc = (CatalysisUc) lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        if (uc.getAtom(j).isOccupied() || !uc.getAtom(j).isOutside()) {
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
      CatalysisUc uc = (CatalysisUc) lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        CatalysisAtom atom = uc.getAtom(j);
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
  private boolean diffuseAtom(CatalysisAtom originAtom, CatalysisAtom destinationAtom) {

    //Si no es elegible, sea el destino el mismo o diferente no se puede difundir.
    if (!originAtom.isEligible()) {
      return false;
    }

    // if the destination atom is occupied do not diffuse (even if it is itself)
    if (destinationAtom.isOccupied()) {
      return false;
    }
    
    boolean force = (forceNucleation && !justCentralFlake && destinationAtom.areTwoTerracesTogether()); //indica si 2 terraces se van a chocar
    if (force) {
      if (extraOutput) {
        printData(null);
      }
      nucleations++;
    }
    int oldType = originAtom.getRealType();
    double probabilityChange = lattice.extract(originAtom);
    getList().addTotalProbability(-probabilityChange); // remove the probability of the extracted atom

    lattice.deposit(destinationAtom, force);
    destinationAtom.setDepositionTime(originAtom.getDepositionTime());
    destinationAtom.setDepositionPosition(originAtom.getDepositionPosition());
    destinationAtom.setHops(originAtom.getHops() + 1);
    originAtom.setDepositionTime(0);
    originAtom.setDepositionPosition(null);
    originAtom.setHops(0);
    if (extraOutput2) {
      if (oldType == TERRACE && destinationAtom.getType() != TERRACE) { // atom gets attached to the island
        atomAttachedToIsland(destinationAtom);
      }
    }
    if (aeOutput) {
      getActivationEnergy().updateSuccess(oldType, destinationAtom.getRealType());
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
  private void atomAttachedToIsland(CatalysisAtom destination) {      
    int islandCount = lattice.countIslands(null);
    deltaTimeBetweenTwoAttachments.add(getTime() - previousTime);
    outDeltaAttachments.println(getCoverage() + " " + getTime() + " " + deltaTimeBetweenTwoAttachments.stream().min((a, b) -> a.compareTo(b)).get() + " "
            + deltaTimeBetweenTwoAttachments.stream().max((a, b) -> a.compareTo(b)).get() + " "
            + deltaTimeBetweenTwoAttachments.stream().mapToDouble(e -> e).average().getAsDouble() + " "
            + deltaTimePerAtom.stream().reduce(0.0, (a, b) -> a + b) + " "
            + getList().getTotalProbabilityFromList() + " "
            + islandCount);
    previousTime = getTime();
    deltaTimePerAtom.add(getTime() - destination.getDepositionTime());
    outPerAtom.println(getCoverage() + " " + getTime() + " " + deltaTimePerAtom.stream().min((a, b) -> a.compareTo(b)).get() + " "
            + deltaTimePerAtom.stream().max((a, b) -> a.compareTo(b)).get() + " "
            + deltaTimePerAtom.stream().mapToDouble(e -> e).average().getAsDouble() + " "
            + deltaTimePerAtom.stream().reduce(0.0, (a, b) -> a + b) + " "
            + getList().getTotalProbabilityFromList());
  }

  private CatalysisAtom depositNewAtom() {
    CatalysisAtom destinationAtom;
    int ucIndex = 0;
    if (justCentralFlake) {
      do {
        // Deposit in the perimeter
        destinationAtom = (CatalysisAtom) perimeter.getRandomPerimeterAtom();
        destinationAtom.setDepositionPosition(new Point3D(0, 0, 0));
      } while (destinationAtom.areTwoTerracesTogetherInPerimeter(destinationAtom) || !depositAtom(destinationAtom));
    } else {
      do {
        int random = StaticRandom.rawInteger(lattice.size() * lattice.getUnitCellSize());
        ucIndex = Math.floorDiv(random, lattice.getUnitCellSize());
        int atomIndex = random % lattice.getUnitCellSize();
        destinationAtom = (CatalysisAtom) lattice.getUc(ucIndex).getAtom(atomIndex);
      } while (!depositAtom(destinationAtom));
      // update the free area and the deposition rate counting just deposited atom
      freeArea--;
      getList().setDepositionProbability(depositionRatePerSite * freeArea);
    }
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(lattice.getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    
    return destinationAtom;
  }

  private CatalysisAtom depositInIslands() {
      List<CatalysisAtom> candidateAtoms = new ArrayList<>(100);
      for (int i = 0; i < lattice.getHexaSizeI(); i++) {
        for (int j = 0; j < lattice.getHexaSizeJ(); j++) {
          AbstractGrowthAtom atom = lattice.getAtom(i, j);
          if (!atom.isOccupied() && atom.getType() != TERRACE && atom.getType() != BULK ) {
            candidateAtoms.add((CatalysisAtom) atom);
          }
        }
      }
      
      int position = StaticRandom.rawInteger(candidateAtoms.size());
      CatalysisAtom perimeterAtom = candidateAtoms.get(position);
      
      for (int i = 0; i < perimeterAtom.getNumberOfNeighbours(); i++) {
        if (!perimeterAtom.getNeighbour(i).isOccupied() && perimeterAtom.getNeighbour(i).getType() == TERRACE){
          return perimeterAtom.getNeighbour(i);
        }
      }
      return null; 
      
      //return candidateAtoms.get(position); //returns random island perimeter atom
    }
      
  private boolean perimeterMustBeEnlarged(CatalysisAtom destinationAtom) {
    
    if (perimeterType == RoundPerimeter.SQUARE) {
      Point2D centreCart = lattice.getCentralCartesianLocation();
      double left = centreCart.getX() - perimeter.getCurrentRadius();
      double right = centreCart.getX() + perimeter.getCurrentRadius();
      double bottom = centreCart.getY() - perimeter.getCurrentRadius();
      double top = centreCart.getY() + perimeter.getCurrentRadius();
      Point2D  position = lattice.getCartesianLocation(destinationAtom.getiHexa(),destinationAtom.getjHexa());

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
}
