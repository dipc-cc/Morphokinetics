/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public class GrapheneSyntheticRates implements IRates {

  private final double[][] rates;
  private final double[][] energies;
  private double diffusionMl = 0.000035;
  private final double islandDensityPerSite = 1 / 60000f;
  private final double prefactor;

  public GrapheneSyntheticRates() {
    rates = new double[8][8];
    initialiseRates();
    energies = new double[8][8];
    initialiseEnergies();
    prefactor = 1e11;
  }
  
  private double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
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

  private void initialiseRates() {
    rates[0][0] = 1e9;
    rates[0][1] = 1e9;
    rates[0][2] = 1e9;
    rates[0][3] = 1e9;
    rates[0][4] = 1e9;
    rates[0][5] = 1e9;
    rates[0][6] = 1e9;
    rates[0][7] = 1e9;

    rates[1][0] = 0;
    rates[1][1] = 100;
    rates[1][2] = 100;
    rates[1][3] = 100;
    rates[1][4] = 100;
    rates[1][5] = 100;
    rates[1][6] = 100;
    rates[1][7] = 100;

    rates[2][0] = 0;
    rates[2][1] = 100;
    rates[2][2] = 100;
    rates[2][3] = 100;
    rates[2][4] = 100;
    rates[2][5] = 100;
    rates[2][6] = 100;
    rates[2][7] = 100;

    rates[3][0] = 0;
    rates[3][1] = 10;
    rates[3][2] = 10;
    rates[3][3] = 10;
    rates[3][4] = 10;
    rates[3][5] = 10;
    rates[3][6] = 10;
    rates[3][7] = 10;

    rates[4][0] = 0;
    rates[4][1] = 0.01;
    rates[4][2] = 0.01;
    rates[4][3] = 0.01;
    rates[4][4] = 0.01;
    rates[4][5] = 0.01;
    rates[4][6] = 0.01;
    rates[4][7] = 0.01;

    rates[5][0] = 0;
    rates[5][1] = 0.00001;
    rates[5][2] = 0.00001;
    rates[5][3] = 0.00001;
    rates[5][4] = 0.00001;
    rates[5][5] = 0.00001;
    rates[5][6] = 0.00001;
    rates[5][7] = 0.00001;

    rates[6][0] = 0;
    rates[6][1] = 0;
    rates[6][2] = 0;
    rates[6][3] = 0;
    rates[6][4] = 0;
    rates[6][5] = 0;
    rates[6][6] = 0;
    rates[6][7] = 0;

    rates[7][0] = 0;
    rates[7][1] = 0;
    rates[7][2] = 0;
    rates[7][3] = 0;
    rates[7][4] = 0;
    rates[7][5] = 0;
    rates[7][6] = 0;
    rates[7][7] = 0;

  }

  /**
   * Using energies instead of direct rates. I choose prefactor (1e11) and temperature (1273) to fix
   * the diffusion energy closest possible to 0.5 eV based on P. Gaillard, T. Chanier, L. Henrard,
   * P. Moskovkin, S. Lucas. Surface Science, Volumes 637–638, July–August 2015, Pages 11-18,
   * http://dx.doi.org/10.1016/j.susc.2015.02.014.
   */
  private void initialiseEnergies() {
    double Ed = 0.5051808896;
    double Ea = 2.2733140032;
    double Eb = 2.525904448;
    double Ec = 3.2836757825;
    double Ef = 4.0414471169;
    double Einf = 9999999;
    
    energies[0][0] = Ed;
    energies[0][1] = Ed;
    energies[0][2] = Ed;
    energies[0][3] = Ed;
    energies[0][4] = Ed;
    energies[0][5] = Ed;
    energies[0][6] = Ed;
    energies[0][7] = Ed;

    energies[1][0] = Einf;
    energies[1][1] = Ea;
    energies[1][2] = Ea;
    energies[1][3] = Ea;
    energies[1][4] = Ea;
    energies[1][5] = Ea;
    energies[1][6] = Ea;
    energies[1][7] = Ea;

    energies[2][0] = Einf;
    energies[2][1] = Ea;
    energies[2][2] = Ea;
    energies[2][3] = Ea;
    energies[2][4] = Ea;
    energies[2][5] = Ea;
    energies[2][6] = Ea;
    energies[2][7] = Ea;

    energies[3][0] = Einf;
    energies[3][1] = Eb;
    energies[3][2] = Eb;
    energies[3][3] = Eb;
    energies[3][4] = Eb;
    energies[3][5] = Eb;
    energies[3][6] = Eb;
    energies[3][7] = Eb;

    energies[4][0] = Einf;
    energies[4][1] = Ec;
    energies[4][2] = Ec;
    energies[4][3] = Ec;
    energies[4][4] = Ec;
    energies[4][5] = Ec;
    energies[4][6] = Ec;
    energies[4][7] = Ec;

    energies[5][0] = Einf;
    energies[5][1] = Ef;
    energies[5][2] = Ef;
    energies[5][3] = Ef;
    energies[5][4] = Ef;
    energies[5][5] = Ef;
    energies[5][6] = Ef;
    energies[5][7] = Ef;

    energies[6][0] = Einf;
    energies[6][1] = Einf;
    energies[6][2] = Einf;
    energies[6][3] = Einf;
    energies[6][4] = Einf;
    energies[6][5] = Einf;
    energies[6][6] = Einf;
    energies[6][7] = Einf;

    energies[7][0] = Einf;
    energies[7][1] = Einf;
    energies[7][2] = Einf;
    energies[7][3] = Einf;
    energies[7][4] = Einf;
    energies[7][5] = Einf;
    energies[7][6] = Einf;
    energies[7][7] = Einf;
  }
  
  @Override
  public double getEnergy(int sourceType, int destinationType) {
    return energies[sourceType][destinationType];
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
   * We don't use the temperature by now.
   *
   * @param temperature
   * @return rates[64]
   */ 
  
  @Override
  public double[] getRates(double temperature) {
    double[] ratesVector = new double[64];

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        ratesVector[i * 8 + j] = (getRate(i, j, temperature));
      }
    }
    return ratesVector;
  }
}
