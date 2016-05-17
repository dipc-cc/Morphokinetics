/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class SyntheticRatesBasicGrowth implements IGrowthRates {

  private double[][] rates;
  private double diffusionMl;
  private final double islandDensityPerSite;
  private static IGrowthRates experiments;

  public SyntheticRatesBasicGrowth()  {
    diffusionMl = 0.000035;
    islandDensityPerSite = 1 / 60000f;
    experiments = new RatesFromPrbCox();
  }
  
  @Override
  public double getRate(int sourceType, int destinationType, double temperature) {
    if (rates == null) {
      rates = new double[4][4];
      initialiseRates();
    }
    return rates[sourceType][destinationType];
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
  public double getIslandsDensityMl(double temperature) {
    return islandDensityPerSite;
  }

  private void initialiseRates() {

    rates[0][0] = 1e9;
    rates[0][1] = 1e9;
    rates[0][2] = 1e9;
    rates[0][3] = 1e9;

    rates[1][0] = 0;
    rates[1][1] = 100;
    rates[1][2] = 100;
    rates[1][3] = 100;

    rates[2][0] = 0;
    rates[2][1] = 100;
    rates[2][2] = 100;
    rates[2][3] = 100;

    rates[3][0] = 0;
    rates[3][1] = 10;
    rates[3][2] = 10;
    rates[3][3] = 10;
  }

  @Override
  public double getEnergy(int i, int j) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  /**
   * Diffusion Mono Layer (F). Utilised to calculate absorption rate. By default it F=0.000035 ML/s.
   * The perimeter deposition is calculated multiplying F (this) and island density.
   *
   * @param diffusionMl diffusion mono layer (deposition flux)
   */
  @Override
  public void setDiffusionMl(double diffusionMl) {
    this.diffusionMl = diffusionMl;
  }
}
