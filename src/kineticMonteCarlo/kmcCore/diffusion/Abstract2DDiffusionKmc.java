/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.diffusion;

import kineticMonteCarlo.kmcCore.*;
import kineticMonteCarlo.atom.diffusion.Abstract2DDiffusionAtom;
import kineticMonteCarlo.atom.diffusion.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.atom.diffusion.ModifiedBuffer;
import kineticMonteCarlo.lattice.AbstractLattice;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;
import kineticMonteCarlo.list.ListConfiguration;
import java.awt.geom.Point2D;
import utils.MathUtils;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public abstract class Abstract2DDiffusionKmc extends AbstractKmc {

  protected Abstract2DDiffusionLattice lattice;
  protected ModifiedBuffer modifiedBuffer;
  protected boolean justCentralFlake;
  protected RoundPerimeter perimeter;
  protected DevitaAccelerator accelerator;

  public Abstract2DDiffusionKmc(ListConfiguration config, boolean justCentralFlake) {

    super(config);
    this.justCentralFlake = justCentralFlake;
    this.modifiedBuffer = new ModifiedBuffer();
    this.list.autoCleanup(true);
  }

  public void setIslandDensityAndDepositionRate(double depositionRateML, double islandDensitySite) {

    if (justCentralFlake) {
      list.setDepositionProbability(depositionRateML / islandDensitySite);
    } else {
      list.setDepositionProbability(depositionRateML * lattice.getSizeX() * lattice.getSizeY());
    }
  }

  @Override
  public void initializeRates(double[] rates) {

    lattice.reset();
    list.reset();
    //we modify the 1D array into a 2D array;
    int length = (int) Math.sqrt(rates.length);
    double[][] processProbs2D = new double[length][length];

    for (int i = 0; i < length; i++) {
      for (int j = 0; j < length; j++) {
        processProbs2D[i][j] = rates[i * length + j];
      }
    }
    lattice.configure(processProbs2D);
    depositSeed();
  }

  @Override
  public AbstractLattice getLattice() {
    return lattice;
  }

  @Override
  protected boolean performSimulationStep() {

    Abstract2DDiffusionAtom originAtom = ((Abstract2DDiffusionAtom) list.next_event(RNG));
    Abstract2DDiffusionAtom destinationAtom;

    if (originAtom == null) {
      destinationAtom = depositNewAtom();

    } else {
      destinationAtom = chooseRandomHop(originAtom);
      if (destinationAtom.is_outside()) {
        destinationAtom = this.perimeter.getPerimeterReentrance(originAtom);
      }
      this.diffuseAtom(originAtom, destinationAtom);
    }

    if (PerimeterMustBeEnlarged(destinationAtom)) {
      int nextRadius = this.perimeter.goToNextRadius();
      if (nextRadius > 0) {
        this.perimeter.setAtomPerimeter(lattice.setInside(nextRadius));
      } else {
        return true;
      }
    }
    return false;
  }

  @Override
  public void simulate(int iterations) {

    int radius = perimeter.getCurrentRadius();
    int num_events = 0;// contador de eventos desde el ultimo cambio de radio

    iterationsForLastSimulation = 0;

    for (int i = 0; i < iterations; i++) {
      if (performSimulationStep()) {
        break;
      }

      iterationsForLastSimulation++;
      num_events++;

      if (radius == 20 && radius == perimeter.getCurrentRadius()) {//En la primera etapa no hay una referencia de eventos por lo que se pone un numero grande
        if (num_events == 4000000) {
          break;
        }
      } else if (radius != perimeter.getCurrentRadius()) {//Si cambia de radio se vuelve a empezar a contar el nuevo numero de eventos
        radius = perimeter.getCurrentRadius();
        num_events = 0;
      } else {
        if ((iterationsForLastSimulation - num_events) * 2 <= num_events) //Si los eventos durante la ultima etapa son 1.X veces mayores que los habidos hasta la etapa anterior Fin.
        {
          break;
        }

      }

    }

    list.cleanup();
  }

  private Abstract2DDiffusionAtom chooseRandomHop(Abstract2DDiffusionAtom source) {
    if (accelerator != null) {
      return accelerator.chooseRandomHop(source);
    }
    return source.chooseRandomHop();
  }

  protected boolean depositAtom(int X, int Y) {
    return this.depositAtom(lattice.getAtom(X, Y));
  }

  protected boolean extractAtom(Abstract2DDiffusionAtom origin) {
    if (!origin.isOccupied()) {
      return false;
    }

    origin.extract();

    modifiedBuffer.updateAtoms(list, lattice);
    return true;
  }

  protected boolean depositAtom(Abstract2DDiffusionAtom origin) {
    if (origin.isOccupied()) {
      return false;
    }

    boolean force_nucleation = (!justCentralFlake && origin.areTwoTerracesTogether()); //indica si 2 terraces se van a chocar    
    origin.deposit(force_nucleation);
    modifiedBuffer.updateAtoms(list, lattice);
    return true;

  }

  protected boolean diffuseAtom(Abstract2DDiffusionAtom origin, Abstract2DDiffusionAtom destination) {

    //Si no es elegible, sea el destino el mismo o diferente no se puede difundir.
    if (!origin.isEligible()) {
      return false;
    }

    if (destination.isOccupied() && !origin.equals(destination)) {
      return false;
    }

    boolean force_nucleation = (!justCentralFlake && destination.areTwoTerracesTogether()); //indica si 2 terraces se van a chocar    
    origin.extract();
    destination.deposit(force_nucleation);
    modifiedBuffer.updateAtoms(list, lattice);

    return true;
  }

  protected Abstract2DDiffusionAtom depositNewAtom() {
    Abstract2DDiffusionAtom destinationAtom;
    if (!justCentralFlake) {
      do {
        int X = (int) (StaticRandom.raw() * lattice.getSizeX());
        int Y = (int) (StaticRandom.raw() * lattice.getSizeY());
        destinationAtom = lattice.getAtom(X, Y);
      } while (!this.depositAtom(destinationAtom));
    } else {
      do {
        destinationAtom = this.perimeter.getRandomPerimeterAtom();
      } while (!this.depositAtom(destinationAtom));
    }
    return destinationAtom;
  }

  protected boolean PerimeterMustBeEnlarged(Abstract2DDiffusionAtom destinationAtom) {
    return destinationAtom.getType() > 0 && justCentralFlake && lattice.getDistanceToCenter(destinationAtom.getX(), destinationAtom.getY()) >= (this.perimeter.getCurrentRadius() - 2);
  }

  @Override
  public void getSampledSurface(float[][] surface) {
    int binY = surface.length;
    int binX = surface[0].length;

    Point2D corner1 = lattice.getSpatialLocation(0, 0);
    double scaleX = Math.abs(binX / (lattice.getSpatialSizeX()));
    double scaleY = Math.abs(binY / (lattice.getSpatialSizeY()));

    if (scaleX > 1 || scaleY > 1) {
      System.err.println("Error:Sampled surface more detailed than model surface, sampling requires not implemented additional image processing operations");
      return;
    }

    for (int i = 0; i < surface.length; i++) {
      for (int j = 0; j < surface[0].length; j++) {
        surface[i][j] = -1;
      }
    }

    for (int i = 0; i < lattice.getSizeY(); i++) {
      for (int j = 0; j < lattice.getSizeX(); j++) {
        if (lattice.getAtom(j, i).isOccupied()) {
          Point2D position = lattice.getSpatialLocation(j, i);
          surface[(int) ((position.getY() - corner1.getY()) * scaleY)][(int) ((position.getX() - corner1.getX()) * scaleX)] = 0;
        }
      }
    }
    MathUtils.applyGrowthAccordingDistanceToPerimeter(surface);
  }

  protected abstract void depositSeed();
}
