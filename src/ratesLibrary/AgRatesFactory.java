/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public class AgRatesFactory implements IRatesFactory {

  private static IGrowthRates experiments;

  public AgRatesFactory() {
    experiments = new RatesFromPrbCox();
  }

  @Override
  public double[] getRates(double temperature) {

    double[] rates = new double[49];

    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        rates[i * 7 + j] = (experiments.getRate(i, j, temperature));
      }
    }
    return rates;
  }

  @Override
  public double getDepositionRate(double temperature) {
    return experiments.getDepositionRate();
  }

  @Override
  public double getIslandDensity(double temperature) {
    return experiments.getIslandsDensityMl(temperature);
  }
}
