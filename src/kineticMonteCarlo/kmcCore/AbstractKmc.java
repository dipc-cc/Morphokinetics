/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore;

import kineticMonteCarlo.lattice.AbstractLattice;
import utils.list.AbstractList;
import utils.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public abstract class AbstractKmc implements IKmc {

  protected AbstractList list;
  protected AbstractLattice lattice;
  protected int iterationsForLastSimulation;

  public AbstractKmc(ListConfiguration config) {
    list = config.createList();
  }

  /**
   * Initialises the rates of the simulation. It has to be called once, and only once (not within a loop.
   * @param rates 
   */
  @Override
  public abstract void initialiseRates(double[] rates);

  /**
   * Resets the lattice and the list of atoms. This method has to be called just before
   * this.depositSeed() and this.simulate().
   */
  @Override
  public void reset() {
    lattice.reset();
    list.reset();
  }
  
  /**
   * Performs a simulation step.
   * @return true if a stop condition happened (all atom etched, all surface covered)
   */
  protected abstract boolean performSimulationStep();

  @Override
  public int getIterations() {
    return iterationsForLastSimulation;
  }

  /**
   * Does the actual simulation. It has to be called after this.reset() and this.depositSeed().
   */
  @Override
  public int simulate() {
    iterationsForLastSimulation = 0;
    int max = (int) 1E6;
    while (!performSimulationStep() && iterationsForLastSimulation < max) {
      iterationsForLastSimulation++;
    }
    if (iterationsForLastSimulation >= max) {
      System.out.println("Too many simulation steps. Simulation steps: "+iterationsForLastSimulation);
      return -1;
    }
    return iterationsForLastSimulation;
  }

  /**
   * Does the actual simulation. It has to be called after this.reset() and this.depositSeed().
   * @param endtime 
   */
  @Override
  public void simulate(double endtime) {
    iterationsForLastSimulation = 0;
    while (list.getTime() < endtime) {
      if (performSimulationStep()) {
        break;
      }
      iterationsForLastSimulation++;
    }
  }

  /**
   * Does the actual simulation. It has to be called after this.reset() and this.depositSeed().
   * @param iterations 
   */
  @Override
  public int simulate(int iterations) {
    iterationsForLastSimulation = 0;
    for (int i = 0; i < iterations; i++) {
      if (performSimulationStep()) {
        break;
      }
      iterationsForLastSimulation++;
    }
    
    list.cleanup();

    if (iterationsForLastSimulation == iterations ) {
      System.out.println("Too many simulation steps. Simulation steps: "+iterationsForLastSimulation);
      return -1;
    }
    return iterationsForLastSimulation;
  }

  @Override
  public AbstractList getSurfaceList() {
    return list;
  }

  @Override
  public double getTime() {
    return list.getTime();
  }

  @Override
  public void setIslandDensityAndDepositionRate(double depositionRate, double landDensity) {
    throw new UnsupportedOperationException("Not supported for this simulation mode."); //To change body of generated methods, choose Tools | Templates.
  }
  
  /**
   * Coverage is not defined in the etching. So, by default is not defined and returns -1
   * @return 
   */
  public float getCoverage() {
    return -1;
  }
}
