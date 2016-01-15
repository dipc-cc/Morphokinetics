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
  
  private final float maxCoverage; // This attribute defines which is the maximum coverage for a multi-flake simulation
  private int area;
  
  public AbstractGrowthKmc(ListConfiguration config, 
          boolean justCentralFlake, 
          float coverage,
          boolean useMaxPerimeter,
          short perimeterType) {
    super(config);
    this.justCentralFlake = justCentralFlake;
    if ((!justCentralFlake) && ((0f > coverage) || (1f < coverage))) {
      System.err.println("Chosen coverage is not permitted. Selecting the default one: %30");
      this.maxCoverage = 0.3f;
    } else {
      this.maxCoverage = coverage;
    }
    this.useMaxPerimeter = useMaxPerimeter;
    this.modifiedBuffer = new ModifiedBuffer();
    this.getList().autoCleanup(true);
    this.perimeterType = perimeterType;
  }

  @Override
  public final void setDepositionRate(double depositionRatePerSite) {
    this.area = calculateAreaAsInLattice();

    if (justCentralFlake) {
      getList().setDepositionProbability(depositionRatePerSite * calculateAreaAsInLattice());
    } else {
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
        destinationAtom = this.perimeter.getPerimeterReentrance(originAtom);
      }
      this.diffuseAtom(originAtom, destinationAtom);
    }

    if (perimeterMustBeEnlarged(destinationAtom)) {
      int nextRadius = this.perimeter.goToNextRadius();
      if (nextRadius > 0 && 
              nextRadius < lattice.getCartSizeX()/2 &&
              nextRadius < lattice.getCartSizeY()/2) {
        if (this.perimeterType == RoundPerimeter.CIRCLE) {
          this.perimeter.setAtomPerimeter(lattice.setInsideCircle(nextRadius));
        } else {
          this.perimeter.setAtomPerimeter(lattice.setInsideSquare(nextRadius));
        }
      } else {
        return true;
      }
    }
    return false;
  }

  @Override
  public int simulate() {
    if (justCentralFlake){
      return super.simulate();
    } else {
      while (lattice.getCoverage() < maxCoverage) {
      if (performSimulationStep()) {
        break;
      }
    }
    }
    return 0;
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
    return this.depositAtom(lattice.getAtom(iHexa, jHexa));
  }

  /*protected boolean extractAtom(AbstractGrowthAtom origin) {
    if (!origin.isOccupied()) {
      return false;
    }

    double probabilityChange = -origin.getProbability();
    lattice.subtractOccupied();
    getList().addTotalProbability(probabilityChange);
    lattice.extract(origin);
    modifiedBuffer.updateAtoms(getList(), lattice);
    return true;
  }*/

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
    double probabilityChange = -origin.getProbability();
    getList().addTotalProbability(probabilityChange);
    lattice.extract(origin);
    lattice.deposit(destination, forceNucleation);
    modifiedBuffer.updateAtoms(getList());

    return true;
  }

  private AbstractGrowthAtom depositNewAtom() {
    AbstractGrowthAtom destinationAtom;
    if (!justCentralFlake) {
      do {
        int i = (int) (StaticRandom.raw() * lattice.getHexaSizeI());
        int j = (int) (StaticRandom.raw() * lattice.getHexaSizeJ());
        destinationAtom = lattice.getAtom(i, j);
      } while (!this.depositAtom(destinationAtom));
    } else {
      do {
        destinationAtom = this.perimeter.getRandomPerimeterAtom();
      } while (!this.depositAtom(destinationAtom));
    }
    return destinationAtom;
  }

  private boolean perimeterMustBeEnlarged(AbstractGrowthAtom destinationAtom) {
    
    if (this.perimeterType == RoundPerimeter.SQUARE) {
      Point2D centreCart = lattice.getCentralCartesianLocation();
      double left = centreCart.getX() - this.perimeter.getCurrentRadius();
      double right = centreCart.getX() + this.perimeter.getCurrentRadius();
      double bottom = centreCart.getY() - this.perimeter.getCurrentRadius();
      double top = centreCart.getY() + this.perimeter.getCurrentRadius();
      Point2D  position = lattice.getCartesianLocation(destinationAtom.getX(),destinationAtom.getY());

      return (destinationAtom.getType() > 0) && (Math.abs(left - position.getX()) < 2
              || Math.abs(right - position.getX()) < 2
              || Math.abs(top - position.getY()) < 2
              || Math.abs(bottom - position.getY()) < 2);
    } else {
      return destinationAtom.getType() > 0 && justCentralFlake && lattice.getDistanceToCenter(destinationAtom.getX(), destinationAtom.getY()) >= (this.perimeter.getCurrentRadius() - 2);
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

    for (int i = 0; i < lattice.getHexaSizeJ(); i++) {
      for (int j = 0; j < lattice.getHexaSizeI(); j++) {
        if (lattice.getAtom(j, i).isOccupied()) {
          Point2D position = lattice.getCartesianLocation(j, i);
          surface[(int) ((position.getY() - corner1.getY()) * scaleY)][(int) ((position.getX() - corner1.getX()) * scaleX)] = 0;
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
   * area changes with the current. radius It is calculated as is done in KmcCanvas class
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
