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
public class GrapheneRatesFactory implements IRatesFactory {

  private static Map<String, IDiffusionRates> experiments;

  public GrapheneRatesFactory() {

    experiments = new HashMap();
    experiments.put("synthetic", new SyntheticRates());
  }

  /**
   * We don't use the temperature by now.
   * @param experimentName
   * @param temperature
   * @return 
   */ 
  @Override
  public double[] getRates(String experimentName, double temperature) {

    IDiffusionRates experiment = experiments.get(experimentName);
    double[] rates = new double[64];

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        rates[i * 8 + j] = (experiment.getRate(i, j, temperature));
      }
    }
    return rates;
  }

  @Override
  public double getDepositionRate(String experimentName, double temperature) {

    return experiments.get(experimentName).getDepositionRate();
  }

  @Override
  public double getIslandDensity(String experimentName, double temperature) {

    return experiments.get(experimentName).getIslandsDensityML(temperature);
  }

}
