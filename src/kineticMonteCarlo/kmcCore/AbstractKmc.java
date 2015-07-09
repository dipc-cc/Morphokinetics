/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore;

import kineticMonteCarlo.list.AbstractList;
import kineticMonteCarlo.list.ListConfiguration;
import utils.edu.cornell.lassp.houle.rngPack.Ranecu;

/**
 *
 * @author Nestor
 */
public abstract class AbstractKmc implements IKmc {

  protected AbstractList list;
  protected static Ranecu RNG;
  protected int iterations_for_last_simulation;

  public AbstractKmc(ListConfiguration config) {
    RNG = new Ranecu(System.nanoTime());
    list = config.create_list();
  }

  @Override
  public abstract void initializeRates(double[] rates);

  /**
   * @return true if a stop condition happened (all atom etched, all surface covered)
   */
  protected abstract boolean performSimulationStep();

  @Override
  public int getIterations() {
    return iterations_for_last_simulation;
  }

  @Override
  public void simulate() {
    iterations_for_last_simulation = 0;
    while (!performSimulationStep()) {
      iterations_for_last_simulation++;
    }

  }

  @Override
  public void simulate(double endtime) {
    iterations_for_last_simulation = 0;
    while (list.getTime() < endtime) {
      if (performSimulationStep()) {
        break;
      }
      iterations_for_last_simulation++;
    }
  }

  @Override
  public void simulate(int iterations) {

    iterations_for_last_simulation = 0;
    for (int i = 0; i < iterations; i++) {
      if (performSimulationStep()) {
        break;
      }
      iterations_for_last_simulation++;
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

}
