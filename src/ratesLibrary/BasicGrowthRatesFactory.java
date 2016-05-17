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
public class BasicGrowthRatesFactory implements IRatesFactory {

  private static IGrowthRates experiments;

  public BasicGrowthRatesFactory() {
    experiments = new SyntheticRatesBasicGrowth();
  }

  /**
   * We don't use the temperature by now.
   *
   * @param temperature
   * @return rates[64]
   */ 
  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[16];

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        rates[i * 4 + j] = (experiments.getRate(i, j, temperature));
      }
    }
    return rates;
  }
  
  @Override
  public double getDepositionRatePerSite() {
    return experiments.getDepositionRatePerSite();
  }

  @Override
  public double getIslandDensity(double temperature) {
    return experiments.getIslandsDensityMl(temperature);
  }
 
  @Override
  public void setDepositionFlux(double depositionFlux) {
    experiments.setDiffusionMl(depositionFlux);
  }
}
