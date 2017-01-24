/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import basic.io.OutputType;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import static kineticMonteCarlo.atom.AbstractAtom.BULK;
import static kineticMonteCarlo.atom.AbstractAtom.TERRACE;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import utils.MathUtils;
import utils.StaticRandom;
import utils.list.LinearList;

/**
 *
 * @author Nestor
 */
public abstract class AbstractGrowthKmc extends AbstractKmc {

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
  private double previousTime;
  private List<Double> deltaTimeBetweenTwoAttachments;
  private List<Double> deltaTimePerAtom;
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
  /**
   * If two terraces are together freeze them, in multi-flake simulation mode.
   */
  private final boolean forceNucleation;
  
  private double terraceToTerraceProbability;
  /**
   * Attribute to count processes that happened. Used to compute activation energy per each rate.
   */
  private int[][] histogramSuccess;
  
  private PrintWriter outDeltaAttachments;
  private PrintWriter outPerAtom;
  private PrintWriter outData;
  private String outDataFormat;
  private long simulatedSteps;
  private double sumProbabilities;

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
    previousTime = 0;
    deltaTimeBetweenTwoAttachments = new ArrayList<>();
    deltaTimePerAtom = new ArrayList<>();  
    
    extraOutput2 = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA2);
    if (extraOutput2) {
      extraOutput = extraOutput2;
    } else {
      extraOutput = parser.getOutputFormats().contains(OutputType.formatFlag.EXTRA);
    }
    aeOutput = parser.getOutputFormats().contains(OutputType.formatFlag.AE);
    if (extraOutput2) {
      try {
        File file = new File("results/deltaTimeBetweenTwoAttachments.txt");
        outDeltaAttachments = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        outDeltaAttachments.println("# Time difference between two attachments to the islands [1. coverage, 2. time, 3. min, 4. max, 5. average, 6. sum, 7. total probability, 8. No. islands] ");
      } catch (IOException e) {
        Logger.getLogger(AbstractGrowthKmc.class.getName()).log(Level.SEVERE, null, e);
      }
      try {
        outPerAtom = new PrintWriter(new BufferedWriter(new FileWriter("results/deltaTimePerAtom.txt")));
        outPerAtom.println("# Time difference between deposition and attachment to the islands for a single atom[1. coverage, 2. time, 3. min, 4. max, 5. average, 6. sum, 7. total probability] ");
      } catch (IOException e) {
        Logger.getLogger(AbstractGrowthKmc.class.getName()).log(Level.SEVERE, null, e);
      }
    }
    if (extraOutput) {
      try {
        outData = new PrintWriter(new BufferedWriter(new FileWriter("results/dataEvery1percentAndNucleation.txt")));
        outData.println("# Information about the system every 1% of coverage and every deposition\n[1. coverage, 2. time, 3. nucleations, 4. islands, 5. depositionProbability, 6. totalProbability, 7. numberOfMonomers, 8. numberOfEvents, 9. sumOfProbabilities, 10. avgRadiusOfGyration, 11. innerPerimeter, 12. outerPerimeter, 13. diffusivity distance 14. numberOfMobileAtoms 15. numberOfAtomsFirstIsland 16. and so on, different atom types] ");
        outDataFormat = "\t%g\t%d\t%d\t%f\t%f\t%d\t%d\t%f\t%f\t%d\t%d\t%f\t%d\t%d%s\n";
      } catch (IOException e) {
        Logger.getLogger(AbstractGrowthKmc.class.getName()).log(Level.SEVERE, null, e);
      }
    }
    nucleations = 0;
  }
    
  /**
   * 
   * @param depositionRateML deposition rate per site (synonyms: deposition flux and diffusion mono layer).
   * @param islandDensitySite only used for single flake simulations to properly calculate deposition rate.
   */
  @Override
  public final void setDepositionRate(double depositionRateML, double islandDensitySite) {
    area = calculateAreaAsInLattice();
    depositionRatePerSite = depositionRateML;
    
    if (justCentralFlake) {
      getList().setDepositionProbability(depositionRateML / islandDensitySite);
    } else {
      freeArea = calculateAreaAsInKmcCanvas();
      getList().setDepositionProbability(depositionRatePerSite * freeArea);
    }
    if (extraOutput) {
      outData.println("0\t0\t0\t0\t" + depositionRatePerSite * freeArea + "\t" + getList().getTotalProbabilityFromList());
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
  @Override
  public int getCurrentRadius() {
    return perimeter.getCurrentRadius();
  }
  
  
  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    float[][] surface = new float[lattice.getHexaSizeI()][lattice.getHexaSizeJ()];
    for (int i = 0; i < lattice.getHexaSizeI(); i++) {
      for (int j = 0; j < lattice.getHexaSizeJ(); j++) {
        surface[i][j] = -1;
      }
    }
    for (int i = 0; i < lattice.getHexaSizeJ(); i++) {
      for (int j = 0; j < lattice.getHexaSizeI(); j++) {
        if (lattice.getAtom(j, i).isOccupied()) {
          surface[j][i] = 0;
        }
      }
    }

    MathUtils.applyGrowthAccordingDistanceToPerimeter(surface);
    MathUtils.normalise(surface);
    return surface;
  }
  
  @Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];
    
    Point2D corner1 = lattice.getCartesianLocation(0, 0);
    double scaleX = binX / lattice.getCartSizeX();
    double scaleY = binY / lattice.getCartSizeY();

    if (scaleX > 1.01 || scaleY > 1.02) {
      System.err.println("Error:Sampled surface more detailed than model surface, sampling requires not implemented additional image processing operations");
      System.err.println("The size of the surface should be " + binX + " and it is " + lattice.getCartSizeX() + "/" + scaleX+" (hexagonal size is "+lattice.getHexaSizeI()+")");
      System.err.println("The size of the surface should be " + binY + " and it is " + lattice.getCartSizeY() + "/" + scaleY+" (hexagonal size is "+lattice.getHexaSizeJ()+")");
      System.err.println("X scale is " + scaleX + " Y scale is " + scaleY);
      return null;
    }

    for (int i = 0; i < binX; i++) {
      for (int j = 0; j < binY; j++) {
        surface[i][j] = -1;
      }
    }
    int x;
    int y;
    for (int i = 0; i < lattice.size(); i++) {
      AbstractGrowthUc uc = lattice.getUc(i);
      double posUcX = uc.getPos().getX();
      double posUcY = uc.getPos().getY();
      for (int j = 0; j < uc.size(); j++) {
        if (uc.getAtom(j).isOccupied()) {
          double posAtomX = uc.getAtom(j).getPos().getX();
          double posAtomY = uc.getAtom(j).getPos().getY();
          x = (int) ((posUcX + posAtomX - corner1.getX()) * scaleX);
          y = (int) ((posUcY + posAtomY - corner1.getY()) * scaleY);

          surface[x][y] = 0;
        }
      }
    }
    MathUtils.applyGrowthAccordingDistanceToPerimeter(surface);
    MathUtils.normalise(surface);
    return surface;
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
  }

  @Override
  public void reset() {
    lattice.reset();
    getList().reset();
    freeArea = calculateAreaAsInKmcCanvas();
    
    for (int i = 0; i < lattice.size(); i++) {
      AbstractGrowthUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        uc.getAtom(j).clear();
      }
    }

    deltaTimeBetweenTwoAttachments.clear();
    deltaTimePerAtom.clear();
    previousTime = 0;
    nucleations = 0;
  }
  
  @Override
  public AbstractGrowthLattice getLattice() {
    return lattice;
  }
  
  @Override
  public int simulate() {
    int coverageThreshold = 1;
    int limit = 10000;
    int returnValue = 0;
    simulatedSteps = 0;
    sumProbabilities = 0.0d;
    terraceToTerraceProbability = lattice.getUc(0).getAtom(0).getProbability(0, 0);
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
          if (performSimulationStep()) {
            break;
          }
          simulatedSteps++;
          sumProbabilities += getList().getTotalProbabilityFromList();
          if (extraOutput && getCoverage() * limit >= coverageThreshold) { // print extra data every 1% of coverage, previously every 1/1000 and 1/10000
            if (coverageThreshold == 10 && limit > 100) { // change the interval of printing
              limit = limit / 10;
              coverageThreshold = 1;
            }
            printData(null);
            coverageThreshold++;
          }
        }
      }
    }
    if (aeOutput) {
      double ri = ((LinearList) getList()).getRi_DeltaI();
      double time = getList().getTime();
      System.out.println("Needed steps " + simulatedSteps + " time " + time + " Ri_DeltaI " + ri + " R " + ri / time + " R " + simulatedSteps / time);
      printHistogram();
    } 
    
    // Dirty mode to have only one interface of countIslands
    PrintWriter standardOutputWriter = new PrintWriter(System.out);
    lattice.countIslands(standardOutputWriter);
    lattice.countPerimeter(standardOutputWriter);
    lattice.getCentreOfMass();
    lattice.getAverageGyradius();
    lattice.getDistancesToCentre();
    standardOutputWriter.flush();
    if (extraOutput) {
      outData.flush();
    }
    if (extraOutput2) {
      outDeltaAttachments.flush();
      outPerAtom.flush();
    }
    return returnValue;
  }
  
  @Deprecated
  public void simulateOld(int iterations) {
    int radius = perimeter.getCurrentRadius();
    int numEvents = 0;// contador de eventos desde el ultimo cambio de radio

    setIterations(0);

    for (int i = 0; i < iterations; i++) {
      if (performSimulationStep()) {
        break;
      }

      setIterations(getIterations() + 1);
      numEvents++;

      if (radius == 10 && radius == perimeter.getCurrentRadius()) {//En la primera etapa no hay una referencia de eventos por lo que se pone un numero grande
        if (numEvents == 4000000) {
          break;
        }
      } else if (radius != perimeter.getCurrentRadius()) {//Si cambia de radio se vuelve a empezar a contar el nuevo numero de eventos
        radius = perimeter.getCurrentRadius();
        numEvents = 0;
      } else {
        if ((getIterations() - numEvents) * 2 <= numEvents) {//Si los eventos durante la ultima etapa son 1.X veces mayores que los habidos hasta la etapa anterior Fin. 
          break;
        }

      }

    }

    getList().cleanup();
  }

  @Override
  public void depositSeed() {
    getLattice().resetOccupied();
    if (justCentralFlake) {
      setAtomPerimeter();
      setCurrentOccupiedArea(8); // Seed will have 8 atoms
      int depositedAtoms = 1;
      AbstractGrowthAtom centralAtom = lattice.getCentralAtom();
      deposition:
      while (true) {
        depositAtom(centralAtom);
        for (int i = 0; i < centralAtom.getNumberOfNeighbours(); i++) {
          AbstractGrowthAtom atom = centralAtom.getNeighbour(i);
          if (depositAtom(atom)) {
            depositedAtoms++;
          }
          if (depositedAtoms > 7) {
            break deposition;
          }
        }
        centralAtom = centralAtom.getNeighbour(1);
      }
    } else {
      for (int i = 0; i < 3; i++) {
        depositNewAtom();
      }
    }
  }
  
  /**
   * Performs a simulation step.
   * 
   * @return true if a stop condition happened (all atom etched, all surface covered).
   */
  @Override
  protected boolean performSimulationStep() {
    AbstractGrowthAtom originAtom = (AbstractGrowthAtom) getList().nextEvent();
    AbstractGrowthAtom destinationAtom;

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
        if (extraOutput) {
          printData(null);
        }
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

  boolean depositAtom(AbstractGrowthAtom atom) {
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
   * Initialises histogram to store the happened transition from atom type to atom type.
   * 
   * @param atomTypes number of different atom types.
   */
  void initHistogramSucces(int atomTypes) {
    histogramSuccess = new  int[atomTypes][atomTypes];
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

  /**
   * Selects the next step randomly. If there is not accelerator, an neighbour atom of originAtom is
   * chosen. With Devita accelerator many steps far away atom can be chosen.
   *
   * @param originAtom atom that has to be moved.
   * @param times how many times it has been called recursively
   * @return destinationAtom.
   */
  private AbstractGrowthAtom chooseRandomHop(AbstractGrowthAtom originAtom, int times) {
    AbstractGrowthAtom destinationAtom;
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
  
  private void printHistogram() {
    for (int origin = 0; origin < histogramSuccess.length; origin++) {
      System.out.print("AeSuccess ");
      for (int destination = 0; destination < histogramSuccess[0].length; destination++) {
        System.out.print(histogramSuccess[origin][destination] + " ");
      }
      System.out.println();
    }
    double[][] histogramPossible;
    histogramPossible = ((LinearList) getList()).getHistogramPossible();
    System.out.println("Ae");
    for (int origin = 0; origin < histogramPossible.length; origin++) {
      System.out.print("AePossibleFromList ");
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        System.out.print(histogramPossible[origin][destination] + " ");
      }
      System.out.println();
    }

    long[][] histogramPossibleCounter = ((LinearList) getList()).getHistogramPossibleCounter();
    System.out.println("Ae");
    for (int origin = 0; origin < histogramPossibleCounter.length; origin++) {
      System.out.print("AePossibleDiscrete ");
      for (int destination = 0; destination < histogramPossibleCounter[0].length; destination++) {
        System.out.print(histogramPossibleCounter[origin][destination] + " ");
      }
      System.out.println();
    }
    
    double[][] ratioTimesPossible = new double[histogramPossible.length][histogramPossible[0].length];
    System.out.println("Ae");
    for (int origin = 0; origin < histogramPossible.length; origin++) {
      System.out.print("AeRatioTimesPossible ");
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        ratioTimesPossible[origin][destination] = lattice.getUc(0).getAtom(0).getProbability(origin, destination) * histogramPossible[origin][destination];
        System.out.print(ratioTimesPossible[origin][destination] + " ");
      }
      System.out.println();
    }
    
    double[][] multiplicity = new double[histogramPossible.length][histogramPossible[0].length];
    System.out.println("Ae");
    for (int origin = 0; origin < histogramPossible.length; origin++) {
      System.out.print("AeMultiplicity ");
      for (int destination = 0; destination < histogramPossible[0].length; destination++) {
        multiplicity[origin][destination] = histogramSuccess[origin][destination] / ratioTimesPossible[origin][destination];
        System.out.print(multiplicity[origin][destination] + " ");
      }
      System.out.println();
    }
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
            lattice.getInnerPerimeterLenght(), lattice.getOuterPerimeterLenght(), lattice.getDiffusivityDistance(), lattice.getMobileAtoms(), numberOfAtomFirstIsland,
            lattice.getAtomTypesCounter());
    sumProbabilities = 0.0d;
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
      AbstractGrowthUc uc = lattice.getUc(i);
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
      AbstractGrowthUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
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
  private boolean diffuseAtom(AbstractGrowthAtom originAtom, AbstractGrowthAtom destinationAtom) {

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
    originAtom.setDepositionTime(0);
    originAtom.setDepositionPosition(null);
    if (extraOutput2) {
      if (oldType == TERRACE && destinationAtom.getType() != TERRACE) { // atom gets attached to the island
        atomAttachedToIsland(destinationAtom);
      }
    }
    if (aeOutput) {
      histogramSuccess[oldType][destinationAtom.getRealType()]++;
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
  private void atomAttachedToIsland(AbstractGrowthAtom destination) {      
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

  private AbstractGrowthAtom depositNewAtom() {
    AbstractGrowthAtom destinationAtom;
    int ucIndex = 0;
    if (justCentralFlake) {
      do {
        // Deposit in the perimeter
        destinationAtom = perimeter.getRandomPerimeterAtom();
      } while (destinationAtom.areTwoTerracesTogetherInPerimeter(destinationAtom) || !depositAtom(destinationAtom));
    } else {
      do {
        int random = StaticRandom.rawInteger(lattice.size() * lattice.getUnitCellSize());
        ucIndex = Math.floorDiv(random, lattice.getUnitCellSize());
        int atomIndex = random % lattice.getUnitCellSize();
        destinationAtom = lattice.getUc(ucIndex).getAtom(atomIndex);
      } while (!depositAtom(destinationAtom));
      // update the free area and the deposition rate counting just deposited atom
      freeArea--;
      getList().setDepositionProbability(depositionRatePerSite * freeArea);
    }
    destinationAtom.setDepositionTime(getTime());
    destinationAtom.setDepositionPosition(lattice.getUc(ucIndex).getPos().add(destinationAtom.getPos()));
    
    return destinationAtom;
  }

  private AbstractGrowthAtom depositInIslands() {
      List<AbstractGrowthAtom> candidateAtoms = new ArrayList<>(100);
      for (int i = 0; i < lattice.getHexaSizeI(); i++) {
        for (int j = 0; j < lattice.getHexaSizeJ(); j++) {
          AbstractGrowthAtom atom = lattice.getAtom(i, j);
          if (!atom.isOccupied() && atom.getType() != TERRACE && atom.getType() != BULK ) {
            candidateAtoms.add(atom);
          }
        }
      }
      
      int position = StaticRandom.rawInteger(candidateAtoms.size());
      AbstractGrowthAtom perimeterAtom = candidateAtoms.get(position);
      
      for (int i = 0; i < perimeterAtom.getNumberOfNeighbours(); i++) {
        if (!perimeterAtom.getNeighbour(i).isOccupied() && perimeterAtom.getNeighbour(i).getType() == TERRACE){
          return perimeterAtom.getNeighbour(i);
        }
      }
      return null; 
      
      //return candidateAtoms.get(position); //returns random island perimeter atom
    }
      
  private boolean perimeterMustBeEnlarged(AbstractGrowthAtom destinationAtom) {
    
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
