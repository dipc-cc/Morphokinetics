/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static ratesLibrary.IRates.kB;

/**
 * Rates based on paper Gaillard, P., Chanier, T., Henrard, L., Moskovkin, P., & Lucas, S. (2015).
 * Multiscale simulations of the early stages of the growth of graphene on copper. Surface Science,
 * 637, 11-18.
 *
 * Only the first neighbour of {@link kineticMonteCarlo.atom.AbstractAtom} and
 * {@link kineticMonteCarlo.atom.GrapheneTypesTable} are considered.
 *
 * @author Nestor, J. Alberdi-Rodriguez
 */
public class GrapheneGaillardRates implements IRates {
  private final double[][] energies;
  private double diffusionMl = 0.000035;
  private final double islandDensityPerSite = 1 / 60000f;
  private final double prefactor;
  
  private final double eDiff;
  private final double eNn;
  private final double eNnn;
  //private final double eBar = xxx; n_NN*E_NN + n_NNN*E_NNN
  private final double eInc;
  
  public GrapheneGaillardRates() {
    eDiff = 0.5;
    eNn = 1.3;
    eNnn = 0.6;
    eInc = 1.0;
    energies = new double[8][8];
    prefactor = 1e11;
  }
  
  public double getRate(int noFirstNeighbours, int noSecondNeighbours, double temperature) {
    double energy;
    if (noFirstNeighbours == 0) {
      energy = eDiff;
    } else {
      energy = noFirstNeighbours * eNn + noSecondNeighbours * eNnn;
    }
    return prefactor * Math.exp(-energy / (kB * temperature));
  }
  
  /*private double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
  }*/

  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
  }
  
  /**
   * Returns the island density mono layer depending on the temperature. How many islands per area
   * site are generated at current temperature. Usually with higher temperature less islands are
   * created, and thus, island density is lower. And the other way around.
   *
   * @param temperature Not implemented yet: temperature in Kelvin.
   * @return island density
   */
  @Override
  public double getIslandDensity(double temperature) {
    return islandDensityPerSite;
  }

 
  /**
   * Diffusion Mono Layer (F). Utilised to calculate absorption rate. By default it F=0.000035 ML/s.
   * The perimeter deposition is calculated multiplying F (this) and island density.
   *
   * @param diffusionMl diffusion mono layer (deposition flux)
   */
  @Override
  public void setDepositionFlux(double diffusionMl) {
    this.diffusionMl = diffusionMl;
  }
    
  /**
   * Does nothing
   *
   * @param temperature
   * @return rates[64]
   */ 
  
  @Override
  public double[] getRates(double temperature) {
    double[] ratesVector = new double[64];
/*
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        ratesVector[i * 8 + j] = (getRate(i, j, temperature));
      }
    }*/
    return ratesVector;
  }

  @Override
  public double getEnergy(int i, int j) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
