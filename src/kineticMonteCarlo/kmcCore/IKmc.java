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

  public void initialiseRates(double[] rates);
  
  public void reset();
  
  public void depositSeed();

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
   * @param binX
   * @param binY
   * @return 
   */
  public float[][] getSampledSurface(int binX, int binY);
  
  public void setIslandDensityAndDepositionRate(double depositionRate, double landDensity);

}
