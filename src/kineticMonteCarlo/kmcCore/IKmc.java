/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore;

import kineticMonteCarlo.lattice.AbstractLattice;
import utils.list.AbstractList;

/**
 *
 * @author Nestor
 */
public interface IKmc {

  public void initializeRates(double[] rates);

  public AbstractLattice getLattice();

  public void simulate();

  public void simulate(double endtime);

  public void simulate(int iterations);

  public AbstractList getSurfaceList();

  public double getTime();

  public int getIterations();

  /**
   * Returns a sampled topological measurement of the KMC surface
   *
   * @param surface destination array.
   */
  public void getSampledSurface(float[][] surface);

}
