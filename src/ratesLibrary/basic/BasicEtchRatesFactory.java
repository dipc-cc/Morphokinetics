/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary.basic;

import ratesLibrary.IRatesFactory;

/**
 * Is never used in the code!
 * @author Nestor
 */
public abstract class BasicEtchRatesFactory implements IRatesFactory {
  protected static IBasicRates experiments;
  private static double kB = 8.617332e-5;

  public BasicEtchRatesFactory() {
  }

  @Override
  public double[] getRates(double temperature) {

    double[] rates = new double[4];

    for (int i = 0; i < 4; i++) {
      rates[i] = (experiments.getPrefactor(i) * Math.exp(-experiments.getEnergy(i) / (kB * temperature)));
    }
    return rates;
  }

  @Override
  public double getDepositionRate(double temperature) {
    throw new UnsupportedOperationException("This KMC does not support deposition of surface atoms.");
  }

  @Override
  public double getIslandDensity(double temperature) {
    throw new UnsupportedOperationException("This KMC does does not form islands.");
  }

}
