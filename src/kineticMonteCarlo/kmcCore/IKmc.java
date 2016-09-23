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

  public int getIterations();

  public double getTime();

  public void setDepositionRate(double depositionRatePerSite, double islandDensity);

  public float[][] getHexagonalPeriodicSurface(int binX, int binY);

  /**
   * Returns a sampled topological measurement of the KMC surface
   *
   * @param binX
   * @param binY
   * @return calculated surface
   */
  public float[][] getSampledSurface(int binX, int binY);

  public int simulate();

  public void simulate(double endtime);

  public int simulate(int iterations);
    
  public void initialiseRates(double[] rates);
  
  public void reset();
  
  public void depositSeed();

  public AbstractLattice getLattice();

  public AbstractList getList();
}
