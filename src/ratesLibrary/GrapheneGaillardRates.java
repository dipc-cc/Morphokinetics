/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static ratesLibrary.IRates.kB;

/**
 * Rates based on paper Gaillard, P., Chanier, T., Henrard, L., Moskovkin, P., & Lucas, S. (2015).
 * Multiscale simulations of the early stages of the growth of graphene on copper. Surface Science,
 * 637, 11-18, http://dx.doi.org/10.1016/j.susc.2015.02.014.
 *
 * Only the first neighbour of {@link kineticMonteCarlo.atom.AbstractAtom} and
 * {@link kineticMonteCarlo.atom.GrapheneTypesTable} are considered.
 *
 * @author Nestor, J. Alberdi-Rodriguez
 */
public class GrapheneGaillardRates implements IRates {
  private double diffusionMl = 0.000035;
  private final double islandDensityPerSite = 1 / 60000f;
  private final double prefactor;
  
  private final double eDiff;
  private final double eNn;
  private final double eNnn;
  private final double eInc;
  
  public GrapheneGaillardRates() {
    eDiff = 0.5;
    eNn = 1.3;
    eNnn = 0.6;
    eInc = 1.0;
    prefactor = 1e13;
  }
  
  /**
   * Rate is calculated with the formulas 8, 9 and 10 of the paper.
   *
   * @param originN1
   * @param originN2
   * @param destinationN1
   * @param destinationN2
   * @param temperature
   * @return
   */
  public double getRate(int originN1, int originN2, int destinationN1, int destinationN2, double temperature) {
    double energy;
    int n1 = originN1;
    int n2 = originN2;
    if (n1 < 0 || n2 < 0) {
      throw new IllegalArgumentException("Number of occupied neighbours can't be negative. Exiting");
    }
    if ((n1 == 0 && n2 == 0) || (n1 == 0 && destinationN2 == 0)) { // diffusion events
      energy = eDiff;
    } else if ((destinationN1 > originN1)) { // events 4 reversed and 6
      energy = eInc;
    } else { // general case
      energy = n1 * eNn + n2 * eNnn;
    }
    double rate = prefactor * Math.exp(-energy / (kB * temperature));
    return rate;
  }

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
