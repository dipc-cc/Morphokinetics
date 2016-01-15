/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public class GrapheneRatesFactory implements IRatesFactory {

  private static IGrowthRates experiments;

  public GrapheneRatesFactory() {
    experiments = new SyntheticRates();
  }

  /**
   * We don't use the temperature by now.
   * @param temperature
   * @return 
   */ 
  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[64];

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        rates[i * 8 + j] = (experiments.getRate(i, j, temperature));
      }
    }
    return rates;
  }

  @Override
  public double getDepositionRatePerSite(double temperature) {
    return experiments.getDepositionRatePerSite(temperature);
  }

  @Override
  public double getIslandDensity(double temperature) {
    return experiments.getIslandsDensityMl(temperature);
  }

}
