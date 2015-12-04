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

  /**
   * This is tuned to work with only 6 "genes".
   * Gene 0 from Ed   (0,j)
   * Gene 1 from Ec   (1,1)(1,2)(1,6)
   * Gene 2 from Ee   (1,3)(1,5)
   * Gene 3 from Ef   (2,3)(5,2)(5,3)(5,4)(5,6)
   * Gene 4 from Ea   (2,2)
   * Gene 5 from Eb   (5,5)
   * @param temperature
   * @return 
   */
  public double[] getReduced6Rates(int temperature) {
    double[] rates = new double[6];
    rates[0] = experiments.getRate(0, 0, temperature);
    rates[1] = experiments.getRate(1, 1, temperature);
    rates[2] = experiments.getRate(1, 3, temperature);
    rates[3] = experiments.getRate(2, 3, temperature);
    rates[4] = experiments.getRate(2, 2, temperature);
    rates[5] = experiments.getRate(5, 5, temperature);
            
    return rates;
  }
  
  public double[] getReduced6Energies() {
    double[] rates = new double[6];
    rates[0] = experiments.getEnergy(0, 0);
    rates[1] = experiments.getEnergy(1, 1);
    rates[2] = experiments.getEnergy(1, 3);
    rates[3] = experiments.getEnergy(2, 3);
    rates[4] = experiments.getEnergy(2, 2);
    rates[5] = experiments.getEnergy(5, 5);
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
