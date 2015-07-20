/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import ratesLibrary.IRatesFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class SiEtchRatesFactory implements IRatesFactory {

  private static Map<String, ISiRates> experiments;
  private static double kB = 8.617332e-5;

  public SiEtchRatesFactory() {

    experiments = new HashMap();
    experiments.put("Gosalvez_PRE", new RatesFromPreGosalvez());
  }

  @Override
  public double[] getRates(String experimentName, double temperature) {

    ISiRates experiment = experiments.get(experimentName);
    double[] rates = new double[64];

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 16; j++) {
        rates[i * 16 + j] = (experiment.getPrefactor(i, j) * Math.exp(-experiment.getEnergy(i, j) / (kB * temperature)));
      }
    }
    return rates;
  }

  @Override
  public double getDepositionRate(String experimentName, double temperature) {
    throw new UnsupportedOperationException("This KMC does not support deposition of surface atoms.");
  }

  @Override
  public double getIslandDensity(String experimentName, double temperature) {
    throw new UnsupportedOperationException("This KMC does does not form islands.");
  }

}
