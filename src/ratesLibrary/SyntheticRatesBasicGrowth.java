/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static kineticMonteCarlo.atom.BasicGrowthAtom.EDGE;
import static kineticMonteCarlo.atom.BasicGrowthAtom.ISLAND;
import static kineticMonteCarlo.atom.BasicGrowthAtom.KINK;
import static kineticMonteCarlo.atom.BasicGrowthAtom.TERRACE;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class SyntheticRatesBasicGrowth implements IGrowthRates {

  private double[][] rates;
  private double diffusionMl;
  private final double islandDensityPerSite;

  public SyntheticRatesBasicGrowth()  {
    diffusionMl = 0.000035;
    islandDensityPerSite = 1 / 60000f;
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

    rates[TERRACE][TERRACE] = 1e9;
    rates[TERRACE][EDGE] = 1e9;
    rates[TERRACE][KINK] = 1e9;
    rates[TERRACE][ISLAND] = 1e9;

    rates[EDGE][TERRACE] = 0;
    rates[EDGE][EDGE] = 100;
    rates[EDGE][KINK] = 100;
    rates[EDGE][ISLAND] = 100;

    rates[KINK][TERRACE] = 0;
    rates[KINK][EDGE] = 1;
    rates[KINK][KINK] = 100;
    rates[KINK][ISLAND] = 100;

    rates[ISLAND][TERRACE] = 0;
    rates[ISLAND][EDGE] = 0;
    rates[ISLAND][KINK] = 0;
    rates[ISLAND][ISLAND] = 0;
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
