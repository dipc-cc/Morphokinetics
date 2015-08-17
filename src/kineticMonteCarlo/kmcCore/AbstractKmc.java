/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore;

import utils.list.AbstractList;
import utils.list.ListConfiguration;
import utils.edu.cornell.lassp.houle.rngPack.Ranecu;

/**
 *
 * @author Nestor
 */
public abstract class AbstractKmc implements IKmc {

  protected AbstractList list;
  protected static Ranecu RNG;
  protected int iterationsForLastSimulation;

  public AbstractKmc(ListConfiguration config, boolean randomise) {
    if (randomise) {
      RNG = new Ranecu(System.nanoTime());
    } else {
      // for testing purposes
      RNG = new Ranecu(1234512345,678967890); // Joseba: To create allways the same "Randoom" numbers
    }
    list = config.createList();
  }

  @Override
  public abstract void initializeRates(double[] rates);

  /**
   * @return true if a stop condition happened (all atom etched, all surface covered)
   */
  protected abstract boolean performSimulationStep();

  @Override
  public int getIterations() {
    return iterationsForLastSimulation;
  }

  @Override
  public void simulate() {
    iterationsForLastSimulation = 0;
    while (!performSimulationStep()) {
      iterationsForLastSimulation++;
    }

  }

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

  @Override
  public void simulate(int iterations) {

    iterationsForLastSimulation = 0;
    for (int i = 0; i < iterations; i++) {
      if (performSimulationStep()) {
        break;
      }
      iterationsForLastSimulation++;
    }

    list.cleanup();
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

}
