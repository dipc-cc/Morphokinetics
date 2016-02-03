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
import static kineticMonteCarlo.atom.AbstractAtom.TERRACE;
import kineticMonteCarlo.kmcCore.AbstractKmc;
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
  private RoundPerimeter perimeter;
  private final boolean useMaxPerimeter;
  private final short perimeterType;
  private DevitaAccelerator accelerator;
  
  /**
   * This attribute defines which is the maximum coverage for a multi-flake simulation.
   */
  private final float maxCoverage; 
  /**
   * Total area of a single flake simulation
   */
  private int area;
  /**
   * 
   */
  private int currentArea;
  /**
   * Chooses to deposit in all area in a single flake simulation.
   */
  private final boolean depositInAllArea;
  private double depositionRatePerSite;
  private int freeArea;
  private int islandCount;
  private double previousTime;
  private List<Double> deltaTimeBetweenTwoAttachments;
  private List<Double> deltaTimePerAtom;
  
  public AbstractGrowthKmc(ListConfiguration config, 
          boolean justCentralFlake, 
          float coverage,
          boolean useMaxPerimeter,
          short perimeterType,
          boolean depositInAllArea) {
    super(config);
    this.justCentralFlake = justCentralFlake;
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
    this.depositInAllArea = depositInAllArea;
    previousTime = 0;
    deltaTimeBetweenTwoAttachments = new ArrayList<>();
    deltaTimePerAtom = new ArrayList<>();  
    
    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("deltaTimeBetweenTwoAttachments.txt", false)))) {
      out.println("Time difference between two attachments to the islands [coverage, time, min, max, average] ");
    } catch (IOException e) {
      //Do nothing, it doesn't matter if fails
    }
    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("deltaTimePerAtom.txt", false)))) {
      out.println("Time difference between deposition and attachment to the islands for a single atom[coverage, time, min, max, average] ");
    } catch (IOException e) {
      //Do nothing, it doesn't matter if fails
    }
  }

  @Override
  public final void setDepositionRate(double depositionRateML, double islandDensitySite) {
    area = calculateAreaAsInLattice();
    depositionRatePerSite = depositionRateML;
    
    if (justCentralFlake) {
      if (depositInAllArea) {
        currentArea = calculateAreaAsInKmcCanvas();
        freeArea = currentArea;
        getList().setDepositionProbability(depositionRatePerSite * calculateAreaAsInLattice());
      } else {
        getList().setDepositionProbability(depositionRateML / islandDensitySite);
      }
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
    lattice.configure(processProbs2D);
  }

  @Override
  public void reset() {
    lattice.reset();
    getList().reset();
    freeArea = lattice.getHexaSizeI() * lattice.getHexaSizeJ();
    
    for (int i = 0; i < lattice.getHexaSizeI(); i++) {
      for (int j = 0; j < lattice.getHexaSizeJ(); j++) {
        lattice.getAtom(i, j).setVisited(false);
        lattice.getAtom(i, j).setDepositionTime(0);
      }
    }
    deltaTimeBetweenTwoAttachments.clear();
    deltaTimePerAtom.clear();
    previousTime = 0;
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
      destinationAtom = chooseRandomHop(originAtom);
      if (destinationAtom.isOutside()) {
        destinationAtom = perimeter.getPerimeterReentrance(originAtom);
        // Add to the time the inverse of the probability to go from terrace to terrace, multiplied by steps done outside the perimeter (from statistics).
        getList().addTime(perimeter.getNeededSteps() / lattice.getAtom(0, 0).getProbability(0, 0));
      }
      diffuseAtom(originAtom, destinationAtom);
    }

    if (perimeterMustBeEnlarged(destinationAtom)) {
      int nextRadius = perimeter.goToNextRadius();
      if (nextRadius > 0 && 
              nextRadius < lattice.getCartSizeX()/2 &&
              nextRadius < lattice.getCartSizeY()/2) {
        if (perimeterType == RoundPerimeter.CIRCLE) {
          perimeter.setAtomPerimeter(lattice.setInsideCircle(nextRadius));
          int newArea;
          newArea = calculateAreaAsInKmcCanvas();
          freeArea += newArea - currentArea;
          currentArea = newArea;
        } else {
          perimeter.setAtomPerimeter(lattice.setInsideSquare(nextRadius));
        }
      } else {
        return true;
      }
    }
    return false;
  }

  @Override
  public int simulate() {
    if (justCentralFlake) {
      return super.simulate();
    } else {
      while (lattice.getCoverage() < maxCoverage) {
        if (performSimulationStep()) {
          break;
        }
      }
    }
    
    islandCount = 0;
    for (int i = 0; i < lattice.getHexaSizeI(); i++) {
      for (int j = 0; j < lattice.getHexaSizeJ(); j++) {
        identifyIsland(i, j, false);
      }
    }
    
    return 0;
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
   * @param i i hexagonal coordinate
   * @param j j hexagonal coordinate
   * @param fromNeighbour whether is called from outside or recursively
   */
  private void identifyIsland(int i, int j, boolean fromNeighbour) {
    AbstractGrowthAtom atom = lattice.getAtom(i, j);
    if (!atom.isVisited() && atom.isOccupied() && !fromNeighbour && !atom.isIsolated()) {
      islandCount++;
    }
    atom.setVisited(true);
    if (atom.isOccupied()) {
      atom.setIslandNumber(islandCount);
      for (int pos = 0; pos < atom.getNumberOfNeighbours(); pos++) {
        AbstractGrowthAtom neighbour = atom.getNeighbour(pos);
        if (!neighbour.isVisited()) {
          identifyIsland(neighbour.getiHexa(), neighbour.getjHexa(), true);
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

  private AbstractGrowthAtom chooseRandomHop(AbstractGrowthAtom source) {
    if (accelerator != null) {
      return accelerator.chooseRandomHop(source);
    }
    return source.chooseRandomHop();
  }

  protected boolean depositAtom(int iHexa, int jHexa) {
    return depositAtom(lattice.getAtom(iHexa, jHexa));
  }

  private boolean depositAtom(AbstractGrowthAtom atom) {
    if (atom.isOccupied()) {
      return false;
    }

    boolean forceNucleation = (!justCentralFlake && atom.areTwoTerracesTogether()); //indica si 2 terraces se van a chocar
    lattice.deposit(atom, forceNucleation);
    lattice.addOccupied();
    modifiedBuffer.updateAtoms(getList());
    
    return true;

  }

  private boolean diffuseAtom(AbstractGrowthAtom origin, AbstractGrowthAtom destination) {

    //Si no es elegible, sea el destino el mismo o diferente no se puede difundir.
    if (!origin.isEligible()) {
      return false;
    }

    if (destination.isOccupied() && !origin.equals(destination)) {
      return false;
    }

    boolean forceNucleation = (!justCentralFlake && destination.areTwoTerracesTogether()); //indica si 2 terraces se van a chocar
    lattice.extract(origin);

    int oldType = destination.getType();
    lattice.deposit(destination, forceNucleation);
    destination.setDepositionTime(origin.getDepositionTime());
    origin.setDepositionTime(0);
    if (oldType == TERRACE && destination.getType() != TERRACE) { // atom gets attached to the island
      atomAttachedToIsland(destination);
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
    deltaTimeBetweenTwoAttachments.add(getTime() - previousTime);
    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("deltaTimeBetweenTwoAttachments.txt", true)))) {
      out.println(getCoverage() + " " + getTime() + " " + deltaTimeBetweenTwoAttachments.stream().min((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimeBetweenTwoAttachments.stream().max((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimeBetweenTwoAttachments.stream().mapToDouble(e -> e).average().getAsDouble());
    } catch (IOException e) {
      //Do nothing, it doesn't matter if fails
    }
    previousTime = getTime();
    deltaTimePerAtom.add(getTime() - destination.getDepositionTime());
    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("deltaTimePerAtom.txt", true)))) {
      out.println(getCoverage() + " " + getTime() + " " + deltaTimePerAtom.stream().min((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimePerAtom.stream().max((a, b) -> a.compareTo(b)).get() + " "
              + deltaTimePerAtom.stream().mapToDouble(e -> e).average().getAsDouble());
    } catch (IOException e) {
      //Do nothing, it doesn't matter if fails
    }
  }

  private AbstractGrowthAtom depositNewAtom() {
    AbstractGrowthAtom destinationAtom;
    int i;
    int j;
    if (!justCentralFlake) {
      do {
        i = (int) (StaticRandom.raw() * lattice.getHexaSizeI());
        j = (int) (StaticRandom.raw() * lattice.getHexaSizeJ());
        destinationAtom = lattice.getAtom(i, j);
      } while (!depositAtom(destinationAtom));
    } else {
      do {
        if (depositInAllArea) {
          // Get random angle and radius 
          double angle = StaticRandom.raw() * 2;
          double randomRadius = StaticRandom.raw() * perimeter.getCurrentRadius();
          // convert to Cartesian
          double x = randomRadius * Math.cos(angle * Math.PI);
          double y = randomRadius * Math.sin(angle * Math.PI);
          // and, centre them
          x += lattice.getCentralCartesianLocation().getX();
          y += lattice.getCentralCartesianLocation().getY();
          // Convert to lattice vector (hexagonal) coordinates
          i = lattice.getiHexa(x, y);
          j = lattice.getjHexa(y);

          destinationAtom = lattice.getAtom(i, j);
        } else { // old default case: deposit in the perimeter
          destinationAtom = perimeter.getRandomPerimeterAtom();
        }
      } while (!depositAtom(destinationAtom));
    }
    destinationAtom.setDepositionTime(getTime());
    if (depositInAllArea) { // update the free area and the deposition rate counting just deposited atom
      freeArea--;
      getList().setDepositionProbability(depositionRatePerSite * freeArea);
    }
    return destinationAtom;
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
      return destinationAtom.getType() > 0 && justCentralFlake && lattice.getDistanceToCenter(destinationAtom.getiHexa(), destinationAtom.getY()) >= (perimeter.getCurrentRadius() - 2);
    }
  }

  @Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];
    
    Point2D corner1 = lattice.getCartesianLocation(0, 0);
    double scaleX = Math.abs(binX / (lattice.getCartSizeX()));
    double scaleY = Math.abs(binY / (lattice.getCartSizeY()));

    if (scaleX > 1 || scaleY > 1) {
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
    for (int i = 0; i < lattice.getHexaSizeJ(); i++) {
      for (int j = 0; j < lattice.getHexaSizeI(); j++) {
        if (lattice.getAtom(j, i).isOccupied()) {
          Point2D position = lattice.getCartesianLocation(j, i);
          y = (int) ((position.getY() - corner1.getY()) * scaleY);
          x = (int) ((position.getX() - corner1.getX()) * scaleX);
          surface[x][y] = 0;
        }
      }
    }
    MathUtils.applyGrowthAccordingDistanceToPerimeter(surface);
    return surface;
  }
  
  /**
   * Returns the coverage of the simulation. 
   * Thus, the number of occupied locations divided by the total number of locations
   * @return A value between 0 and 1
   */
  @Override
  public float getCoverage() {
    if (justCentralFlake) {
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
    for (int j = 0; j < lattice.getHexaSizeJ(); j++) {
      for (int i = 0; i < lattice.getHexaSizeI(); i++) {
        if (lattice.getAtom(i, j).isOccupied() || !lattice.getAtom(i, j).isOutside()) {
          totalArea++;
        }
      }
    }
    return totalArea;
  }

  /**
   * Calculates the total area of a single flake simulation. 
   * @return 
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
   * @param occupied Size of the seed in atoms. Number to calculate free space. 
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
      perimeter.setMaxPerimeter();
    } else {
      perimeter.setMinRadius();
    }
    if (perimeterType == RoundPerimeter.CIRCLE) {
      perimeter.setAtomPerimeter(lattice.setInsideCircle(perimeter.getCurrentRadius()));
    } else {
      perimeter.setAtomPerimeter(lattice.setInsideSquare(perimeter.getCurrentRadius()));
    }
  }
}
