/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary.diffusion.agAgGrowth;

import ratesLibrary.diffusion.IDiffusionRates;
import ratesLibrary.IRatesFactory;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class AgAgGrowthRatesFactory implements IRatesFactory {

    private static Map<String, IDiffusionRates> experiments;

    public AgAgGrowthRatesFactory() {

        experiments = new HashMap();
        experiments.put("COX_PRB", new RatesFromPrbCox());
    }

  
    @Override
    public double[] getRates(String experimentName, double temperature) {

        IDiffusionRates experiment = experiments.get(experimentName);
        double[] rates = new double[49];

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                rates[i * 7 + j] = (experiment.getRate(i, j, temperature));
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
