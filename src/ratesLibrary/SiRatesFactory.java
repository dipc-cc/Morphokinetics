/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

/**
 *
 * @author Nestor
 */
public class SiRatesFactory implements IRatesFactory {

  private static ISiRates experiments;
  private static double kB = 8.617332e-5;

  public SiRatesFactory() {
    experiments = new SiRatesFromPreGosalvez();
  }

  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[64];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 16; j++) {
        rates[i * 16 + j] = (experiments.getPrefactor(i, j) * Math.exp(-experiments.getEnergy(i, j) / (kB * temperature)));
      }
    }
    return rates;
  }

  @Override
  public double getDepositionRatePerSite() {
    throw new UnsupportedOperationException("This KMC does not support deposition of surface atoms.");
  }

  @Override
  public double getIslandDensity(double temperature) {
    throw new UnsupportedOperationException("This KMC does does not form islands.");
  }
 
  @Override
  public void setDepositionFlux(double depositionFlux) {
    throw new UnsupportedOperationException("This KMC does does not form islands.");
  }
}
