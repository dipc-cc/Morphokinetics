/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary.basic;

import ratesLibrary.IRatesFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class BasicEtchRatesFactory implements IRatesFactory {

  private static Map<String, IBasicRates> experiments;
  private static double kB = 8.617332e-5;

  public BasicEtchRatesFactory() {

    experiments = new HashMap();
    experiments.put("Basic_E", new RatesCaseE());
    experiments.put("Basic_F", new RatesCaseF());
    experiments.put("Basic_OTHER", new RatesCaseOther());
    experiments.put("Basic_H", new RatesCaseH());
  }

  @Override
  public double[] getRates(String experimentName, double temperature) {

    IBasicRates experiment = experiments.get(experimentName);
    double[] rates = new double[4];

    for (int i = 0; i < 4; i++) {
      rates[i] = (experiment.getPrefactor(i) * Math.exp(-experiment.getEnergy(i) / (kB * temperature)));
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
