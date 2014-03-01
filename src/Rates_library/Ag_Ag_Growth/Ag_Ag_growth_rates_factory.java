/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Rates_library.Ag_Ag_Growth;

import Rates_library.IRatesFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class Ag_Ag_growth_rates_factory implements IRatesFactory {

    private static Map<String, IRates> experiments;

    public Ag_Ag_growth_rates_factory() {

        experiments = new HashMap();
        experiments.put("COX_PRB", new Rates_from_PRB_Cox());
    }

  
    @Override
    public double[] getRates(String experimentName, double temperature) {

        IRates experiment = experiments.get(experimentName);
        double[] rates = new double[49];

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                rates[i * 7 + j] = (experiment.getRate(i, j, temperature));
            }
        }
        return rates;
    }

    public double getDepositionRate(String experimentName, double temperature) {

        return experiments.get(experimentName).getDepositionRate();
    }

    public double getIslandDensity(String experimentName, double temperature) {

        return experiments.get(experimentName).getIslandsDensityML(temperature);
    }
}
