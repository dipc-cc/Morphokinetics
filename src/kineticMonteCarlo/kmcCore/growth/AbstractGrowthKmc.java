/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import utils.list.ListConfiguration;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import static kineticMonteCarlo.atom.AbstractAtom.BULK;
import static kineticMonteCarlo.atom.AbstractAtom.TERRACE;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.unitCell.IUc;
import utils.MathUtils;
import utils.StaticRandom;

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
  private int islandCount;
  private double previousTime;
  private List<Double> deltaTimeBetweenTwoAttachments;
  private List<Double> deltaTimePerAtom;
  private int nucleations;
  private final boolean extraOutput;
  
  private double terraceToTerraceProbability;
  
  public AbstractGrowthKmc(ListConfiguration config, 
          boolean justCentralFlake, 
          boolean periodicSingleFlake,
          float coverage,
          boolean useMaxPerimeter,
          short perimeterType,
          boolean extraOutput) {
    super(config);
    this.justCentralFlake = justCentralFlake;
    this.periodicSingleFlake = periodicSingleFlake;
    if ((!justCentralFlake) && ((0f > coverage) || (1f < coverage))) {
      System.err.println("Chosen coverage is not permitted. Selecting the default one: %30");
      maxCoverage = 0.3f;
    } else {
      maxCoverage = coverage;
    }
    this.useMaxPerimeter = useMaxPerimeter;
    modifiedBuffer = new ModifiedBuffer();
    getList().autoCleanup(true);
    this.perimeterType = perimeterType;
    previousTime = 0;
    deltaTimeBetweenTwoAttachments = new ArrayList<>();
    deltaTimePerAtom = new ArrayList<>();  
    
    this.extraOutput = extraOutput;
    if (extraOutput) {
      try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results/deltaTimeBetweenTwoAttachments.txt", false)))) {
        out.println("Time difference between two attachments to the islands [1. coverage, 2. time, 3. min, 4. max, 5. average, 6. sum, 7. total probability, 8. No. islands] ");
      } catch (IOException e) {
        //Do nothing, it doesn't matter if fails
      }
      try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results/deltaTimePerAtom.txt", false)))) {
        out.println("Time difference between deposition and attachment to the islands for a single atom[1. coverage, 2. time, 3. min, 4. max, 5. average, 6. sum, 7. total probability] ");
      } catch (IOException e) {
        //Do nothing, it doesn't matter if fails
      }
      try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results/dataEvery1percentAndNucleation.txt", false)))) {
        out.println("Information about the system every 1% of coverage and every deposition\n[coverage, time, nucleations, islands, depositionProbability, totalProbability] ");
      } catch (IOException e) {
        //Do nothing, it doesn't matter if fails
      }
    }
    nucleations = 0;
  }

  /**
   * 
   * @param depositionRateML deposition rate per site (synonyms: deposition flux and diffusion mono layer)
   * @param islandDensitySite only used for single flake simulations to properly calculate deposition rate
   */
  @Override
  public final void setDepositionRate(double depositionRateML, double islandDensitySite) {
    area = calculateAreaAsInLattice();
    depositionRatePerSite = depositionRateML;
    
    if (justCentralFlake) {
      getList().setDepositionProbability(depositionRateML / islandDensitySite);
    } else {
      freeArea = lattice.getHexaSizeI() * lattice.getHexaSizeJ();
      getList().setDepositionProbability(depositionRatePerSite * lattice.getHexaSizeI() * lattice.getHexaSizeJ());
    }
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
    freeArea = lattice.getHexaSizeI() * lattice.getHexaSizeJ();
    
    for (int i = 0; i < lattice.size(); i++) {
      lattice.getAtom(i).clear();
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
    
  /**
   * Performs a simulation step.
   * @return true if a stop condition happened (all atom etched, all surface covered)
   */
  @Override
  protected boolean performSimulationStep() {
    AbstractGrowthAtom originAtom = (AbstractGrowthAtom) getList().nextEvent();
    AbstractGrowthAtom destinationAtom;

    if (originAtom == null) {
      destinationAtom = depositNewAtom();

    } else {
      do {
        destinationAtom = chooseRandomHop(originAtom);
        if (destinationAtom.isOutside()) {
          destinationAtom = perimeter.getPerimeterReentrance(originAtom);
          // Add to the time the inverse of the probability to go from terrace to terrace, multiplied by steps done outside the perimeter (from statistics).
          getList().addTime(perimeter.getNeededSteps() / terraceToTerraceProbability);
        }
      } while (!diffuseAtom(originAtom, destinationAtom));
    }

    if (justCentralFlake && perimeterMustBeEnlarged(destinationAtom)) {
      int nextRadius = perimeter.goToNextRadius();
      if (nextRadius > 0
              && nextRadius < lattice.getCartSizeX() / 2
              && nextRadius < lattice.getCartSizeY() / 2) {
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

  @Override
  public int simulate() {
    int k = 1;
    int returnValue = 0;
    terraceToTerraceProbability = lattice.getAtom(0).getProbability(0, 0);
    if (justCentralFlake) {
      returnValue = super.simulate();
    } else {
      while (lattice.getCoverage() < maxCoverage) {
        if (performSimulationStep()) {
          break;
        }
        if (extraOutput && getCoverage() * 100 > k) {
          k++;
          printData();
        }
      }
    }
    
    countIslands();
    
    return returnValue;
  }
  
  private void printData() {
    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results/dataEvery1percentAndNucleation.txt", true)))) {
      out.println(getCoverage() + "\t" + getTime() + "\t" + nucleations + "\t" + countIslands() + "\t" + depositionRatePerSite * freeArea + "\t" + getList().getTotalProbabilityFromList());
    } catch (IOException e) {
      //Do nothing, it doesn't matter if fails
    }
  }
  
  private int countIslands() {
    // reset all the atoms
    
    for (int i = 0; i < lattice.size(); i++) {
      lattice.getAtom(i).setVisited(false);
      lattice.getAtom(i).setIslandNumber(0);
    }
    
    // do the count
    islandCount = 0;
    for (int i = 0; i < lattice.size(); i++) {
      // visit all the atoms within the unit cell
      IUc uc = lattice.getUc(i);
      for (int j=0; j< uc.size(); j++) {
        AbstractGrowthAtom atom = uc.getAtom(j);
        identifyIsland(atom, false);
        
      }
    }
    
    // create a histogram with the number of atoms per island
    List<Integer> histogram = new ArrayList(islandCount + 1); // count also non occupied area
    for (int i = 0; i < islandCount + 1; i++) {
      histogram.add(0);
    }
    // iterate all atoms and add to the corresponding island
    for (int i = 0; i < lattice.size(); i++) {
      int island = lattice.getAtom(i).getIslandNumber();
      histogram.set(island, histogram.get(island) + 1);
    }
    System.out.println("histogram " + histogram.toString());
    return islandCount;
  }
    
  /**
   * After having count them, returns the number of islands that the simulation has.
   * @return number of islands of the simulation
   */
  @Override
  public int getIslandCount() {
    return islandCount;
  }
  
  /**
   * Counts the number of islands that the simulation has. It iterates trough all neighbours, to set
   * all them the same island number.
   *
   * @param index hexagonal index coordinate
   * @param fromNeighbour whether is called from outside or recursively
   */
  private void identifyIsland(AbstractGrowthAtom atom, boolean fromNeighbour) {
    if (!atom.isVisited() && atom.isOccupied() && !fromNeighbour && !atom.isIsolated()) {
      islandCount++;
    }
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

  /**
   * Selects the next step randomly. If there is not accelerator, an neighbour atom of originAtom is
   * chosen. With Devita accelerator many steps far away atom can be chosen.
   *
   * @param originAtom atom that has to be moved
   * @return destinationAtom
   */
  private AbstractGrowthAtom chooseRandomHop(AbstractGrowthAtom originAtom) {
    if (accelerator != null) {
      return accelerator.chooseRandomHop(originAtom);
    }
    return originAtom.chooseRandomHop();
  }

  protected boolean depositAtom(int iHexa, int jHexa) {
    int index = jHexa * lattice.getHexaSizeI() + iHexa;
    return depositAtom(lattice.getAtom(index));
  }

  boolean depositAtom(AbstractGrowthAtom atom) {
    if (atom.isOccupied()) {
      return false;
    }

    boolean forceNucleation = (!justCentralFlake && atom.areTwoTerracesTogether()); //indica si 2 terraces se van a chocar
    lattice.deposit(atom, forceNucleation);
    lattice.addOccupied();
    modifiedBuffer.updateAtoms(getList());
    
    return true;

  }

  /**
   * Moves an atom from origin to destination
   * @param originAtom origin atom
   * @param destinationAtom destination atom
   * @return true if atom has moved, false otherwise
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

    boolean forceNucleation = (!justCentralFlake && destinationAtom.areTwoTerracesTogether()); //indica si 2 terraces se van a chocar
    if (forceNucleation) {
      nucleations++;
      if (extraOutput) {
        printData();
      }
    }
    int oldType = originAtom.getType();
    lattice.extract(originAtom);

    lattice.deposit(destinationAtom, forceNucleation);
    destinationAtom.setDepositionTime(originAtom.getDepositionTime());
    originAtom.setDepositionTime(0);
    if (extraOutput) {
      if (oldType == TERRACE && destinationAtom.getType() != TERRACE) { // atom gets attached to the island
        atomAttachedToIsland(destinationAtom);
      }
    }
    modifiedBuffer.updateAtoms(getList());

    return true;
  }
  
  /**
   * An atom has been attached to an island an so printing this to output files.
   *
   * @param destination destination atom is required to compute time difference
   *
   */
  private void atomAttachedToIsland(AbstractGrowthAtom destination) {      
    countIslands();
    deltaTimeBetweenTwoAttachments.add(getTime() - previousTime);
    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results/deltaTimeBetweenTwoAttachments.txt", true)))) {
      out.println(getCoverage() + " " + getTime() + " " + deltaTimeBetweenTwoAttachments.stream().min((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimeBetweenTwoAttachments.stream().max((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimeBetweenTwoAttachments.stream().mapToDouble(e -> e).average().getAsDouble() + " "
              + deltaTimePerAtom.stream().reduce(0.0, (a, b) -> a + b) + " "
              + getList().getTotalProbabilityFromList() + " "
              + getIslandCount());
    } catch (IOException e) {
      //Do nothing, it doesn't matter if fails
    }
    previousTime = getTime();
    deltaTimePerAtom.add(getTime() - destination.getDepositionTime());
    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results/deltaTimePerAtom.txt", true)))) {
      out.println(getCoverage() + " " + getTime() + " " + deltaTimePerAtom.stream().min((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimePerAtom.stream().max((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimePerAtom.stream().mapToDouble(e -> e).average().getAsDouble() + " "
              + deltaTimePerAtom.stream().reduce(0.0, (a, b) -> a + b) + " "
              + +getList().getTotalProbabilityFromList());
    } catch (IOException e) {
      //Do nothing, it doesn't matter if fails
    }
  }

  private AbstractGrowthAtom depositNewAtom() {
    AbstractGrowthAtom destinationAtom;
    if (justCentralFlake) {
      do {
        // Deposit in the perimeter
        destinationAtom = perimeter.getRandomPerimeterAtom();
      } while (!depositAtom(destinationAtom));
    } else {
      do {
        int i = (int) (StaticRandom.raw() * lattice.getHexaSizeI());
        int j = (int) (StaticRandom.raw() * lattice.getHexaSizeJ());
        int index = j * lattice.getHexaSizeI() + i;
        destinationAtom = lattice.getAtom(index);
      } while (!depositAtom(destinationAtom));
      // update the free area and the deposition rate counting just deposited atom
      freeArea--;
      getList().setDepositionProbability(depositionRatePerSite * freeArea);
    }
    destinationAtom.setDepositionTime(getTime());
    
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
      return atomType && distance;
    }
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
    double scaleX = Math.abs(binX / (lattice.getCartSizeX()));
    double scaleY = Math.abs(binY / (lattice.getCartSizeY()));

    if (scaleX > 1.01 || scaleY > 1.01) {
      System.err.println("Error:Sampled surface more detailed than model surface, sampling requires not implemented additional image processing operations");
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
      IUc uc = lattice.getUc(i);
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
   * Adds an empty area, maintaining the island size. It is useful to compare different size
   * simulations.
   *
   * Converts this island:
   * <pre> {@code 
:::::::::::::::::::::::::
:::::::::::::::::::::::::
::::@::@.::::::::::::::::
::::`@@@:::@@@:::::::::::
:::::;@@@:::@';::::::;@::
::::::,@@@;;.@+;:#`@@@,::
::::::::+@@@ +@:.@@';@@':
:::::::::+@@@@@@@#@:::..:
:::::::::+ ,@'+# @@+:::::
::::::::::@@@@,+.::::::::
:::::::::::@+#:::::::::::
::::::::;@@@@::::::::::::
::::::::::;@@::::::::::::
:::::::::::::::::::::::::
:::::::::::::::::::::::::
:::::::::::::::::::::::::
} </pre>
    into this filled one:
    * <pre> {@code
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
:::::::::::::::::::@::::::::::::::::::::::::::::::
::::::::::::::::,@:;@:::::::::::::::::::::::::::::
:::::::::::::::::'@@.:::+@::::::::,.::::::::::::::
:::::::::::::::::::@@@@::@+@:::@@@@@::::::::::::::
::::::::::::::::::::'@@@+ @.:@@@@'@.::::::::::::::
::::::::::::::::::::::@@@@@;@@@:::#@::::::::::::::
:::::::::::::::::::::.@ @@@@ @@@::::::::::::::::::
::::::::::::::::::::::#@@@:@@::;::::::::::::::::::
::::::::::::::::::::::::@@::::::::::::::::::::::::
::::::::::::::::::::.@+@@:::::::::::::::::::::::::
:::::::::::::::::::::'@@@,::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
::::::::::::::::::::::::::::::::::::::::::::::::::
  }</pre>
   * @param inputArea input area
   * @param scale how much is going to increase (must be > 1)
   * @return output bigger area
   */
  @Override
  public float[][] increaseEmptyArea(float[][] inputArea, double scale){
    // ensure that scale is bigger than one
    if (scale <= 1 ) {
      return inputArea;
    }
    
    int sizeX = inputArea.length;
    int sizeY = inputArea[0].length;
    float[][] outputArea = new float[(int) (sizeX * scale)][(int) (sizeY * scale)];
    for (int x = 0; x < outputArea.length; x++) {
      for (int y = 0; y < outputArea[0].length; y++) {
        outputArea[x][y] = -1;
      }
    }
    int padX = (int) ((outputArea.length - sizeX) / 2);
    int padY = (int) ((outputArea[0].length - sizeY) / 2);
    for (int x = 0; x < sizeX; x++) {
      System.arraycopy(inputArea[x], 0, outputArea[x + padX], padY, sizeY);
    }
    return outputArea;
  }
  /**
   * Returns the coverage of the simulation. 
   * Thus, the number of occupied locations divided by the total number of locations
   * @return A value between 0 and 1
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

  /**
   * Calculates current area or, i.e. the number of current places that simulation has. This total
   * area changes with the current radius. It is calculated as is done in KmcCanvas class
   * @return simulated area
   */
  private int calculateAreaAsInKmcCanvas() {
    int totalArea = 0;
    for (int i = 0; i < lattice.size(); i++) {
      if (lattice.getAtom(i).isOccupied() || !lattice.getAtom(i).isOutside()) {
        totalArea++;
      }
    }
    return totalArea;
  }

  /**
   * Calculates the total area of a single flake simulation. 
   * 
   * @return total area
   */
  private int calculateAreaAsInLattice() {
    int totalArea = 0;
    // Get the minimum radius of both coordinates
    float minRadius = Math.min(lattice.getCartSizeX()/2,lattice.getCartSizeY()/2);
    // Get the maximum radius multiple of 5
    float radius = ((float) Math.floor(minRadius/5f)*5f);
    
    for (int jHexa = 0; jHexa < lattice.getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < lattice.getHexaSizeI(); iHexa++) {
        double distance = lattice.getDistanceToCenter(iHexa, jHexa);
        if (radius > distance) {
          totalArea++;
        } 
      }
    }
    return totalArea;
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

  /**
   * @param perimeter the perimeter to set
   */
  public final void setPerimeter(RoundPerimeter perimeter) {
    this.perimeter = perimeter;
  }

  /**
   * @param area the area to set
   */
  public void setArea(int area) {
    this.area = area;
  }
  
  /**
   * This has to be called only once from AgKmc or GrapheneKmc.
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
}
