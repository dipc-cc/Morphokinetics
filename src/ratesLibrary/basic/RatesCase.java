/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary.basic;

import ratesLibrary.IRatesFactory;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class RatesCase implements IBasicRates, IRatesFactory {

  private double[] prefactors = new double[4];
  private double[] energies = new double[4];
  private static double kB = 8.617332e-5;
  
  public final void setRates(double newEnergies[], double newPrefactors[]) {
    assert(newEnergies.length == 4);
    assert(newPrefactors.length == 4);
    for (int i = 0; i < 4; i++) {
      energies[i] = newEnergies[i];
      prefactors[i] = newPrefactors[i];
    }
  }
  
  @Override
  public double getPrefactor(int i) {
    return prefactors[i];
  }

  @Override
  public double getEnergy(int i) {
    return energies[i];
  }
  
  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[4];

    for (int i = 0; i < 4; i++) {
      rates[i] = (getPrefactor(i) * Math.exp(-getEnergy(i) / (kB * temperature)));
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
