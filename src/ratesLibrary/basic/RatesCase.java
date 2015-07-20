/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary.basic;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class RatesCase implements IBasicRates {

  private double E0;
  private double E1;
  private double E2;
  private double E3;

  private double P0;
  private double P1;
  private double P2;

  private double P3;

  private double[] prefactors = new double[4];
  private double[] energies = new double[4];
  private static double kB = 8.617332e-5;
  
  public void setRates(double newEnergies[], double newPrefactors[]) {
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
  
  public double[] getRates(double temperature) {

    double[] rates = new double[4];

    for (int i = 0; i < 4; i++) {
      rates[i] = (this.getPrefactor(i) * Math.exp(-this.getEnergy(i) / (kB * temperature)));
    }
    return rates;
  }
}
