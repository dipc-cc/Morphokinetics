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
  
  /**
   * Calculates rates from the genes. Some of the rates are 0, the rest is calculated from the given
   * genes.
   *
   * Ratio (energy type) | ratio index
   * 0) E_d                    (0,j) Terrace to any 
   * 1) E_c                    (1,0) Edge to terrace
   * 2) E_f                    (1,1) Edge to edge
   * 3) E_a                    (1,2)=(1,3) Edge to kink or island
   * 4) E_b                    (2,1)=(2,2)=(2,3) Kink to any (but terrace)
   * @param temperature 
   * @return rates[5]
   */
  public double[] getReduced5Rates(int temperature) {
    double[] rates = new double[5];
    rates[0] = experiments.getRate(0, 0, temperature);
    rates[1] = experiments.getRate(1, 0, temperature);
    rates[2] = experiments.getRate(1, 1, temperature);
    rates[3] = experiments.getRate(1, 2, temperature);
    rates[4] = experiments.getRate(2, 1, temperature);
            
    return rates;
  }
  
  public double[] getReduced5Energies() {
    double[] rates = new double[5];
    rates[0] = experiments.getEnergy(0, 0);
    rates[1] = experiments.getEnergy(1, 0);
    rates[2] = experiments.getEnergy(1, 1);
    rates[3] = experiments.getEnergy(1, 2);
    rates[4] = experiments.getEnergy(2, 1);

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
