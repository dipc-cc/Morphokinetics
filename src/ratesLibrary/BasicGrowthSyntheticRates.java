/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static kineticMonteCarlo.atom.BasicGrowthAtom.EDGE;
import static kineticMonteCarlo.atom.BasicGrowthAtom.ISLAND;
import static kineticMonteCarlo.atom.BasicGrowthAtom.KINK;
import static kineticMonteCarlo.atom.BasicGrowthAtom.TERRACE;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthSyntheticRates implements IRates {

  private final double[][] energies;
  private double diffusionMl;
  
  private final double Ea;
  private final double Eb;
  private final double Ec;
  private final double Ed;
  private final double Ef;
  private final double Einf;
  private final double prefactor;

  public BasicGrowthSyntheticRates()  { 
    diffusionMl = 0.000035;
    
    Ed = 0.200;
    Ef = 0.360;
    Ea = 0.350;
    Eb = 0.435;
    Ec = 0.45;
    Einf = 9999999;
    
    prefactor = 1e13;
    
    energies = new double[4][4];
    energies[TERRACE][TERRACE] = Ed;
    energies[TERRACE][EDGE] = Ed;
    energies[TERRACE][KINK] = Ed;
    energies[TERRACE][ISLAND] = Ed;

    energies[EDGE][TERRACE] = Ec;
    energies[EDGE][EDGE] = Ef;
    energies[EDGE][KINK] = Ea;
    energies[EDGE][ISLAND] = Ea;

    energies[KINK][TERRACE] = Einf;
    energies[KINK][EDGE] = Eb;
    energies[KINK][KINK] = Eb;
    energies[KINK][ISLAND] = Eb;

    energies[ISLAND][TERRACE] = Einf;
    energies[ISLAND][EDGE] = Einf;
    energies[ISLAND][KINK] = Einf;
    energies[ISLAND][ISLAND] = Einf;
  }
  
  @Override
  public double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
  }

  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
  }
  
  /**
   * Returns the island density mono layer depending on the temperature. 
   * These values are taken from section 4 of the paper of Cox et al.
   * 
   * (But are not consistent with, for example, the multi-flake
   * simulations: 180K, 250x250)
   * @param temperature
   * @return a double value from 1e-4 to 2e-5
   */
  @Override
  public double getIslandDensity(double temperature) {
    if (temperature < 135) {//120 degrees Kelvin
      return 1e-2;
    }
    if (temperature < 150) {//135 degrees Kelvin
      return 5e-3;
    }
    if (temperature < 165) {//150 degrees Kelvin
      return 4e-3;
    }
    if (temperature < 180) {//165 degrees Kelvin
      return 3e-3;
    }
    return 2e-3; //180 degrees Kelvin
  }

  @Override
  public double getEnergy(int i, int j) {
    return energies[i][j];
  }

  /**
   * Diffusion Mono Layer (F). Utilised to calculate absorption rate. By default it F=0.000035 ML/s.
   * The perimeter deposition is calculated multiplying F (this) and island density.
   *
   * @param diffusionMl diffusion mono layer (deposition flux)
   */
  @Override
  public void setDepositionFlux(double diffusionMl) {
    this.diffusionMl = diffusionMl;
  }

  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[16];

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        rates[i * 4 + j] = (getRate(i, j, temperature));
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
    rates[0] = getRate(0, 0, temperature);
    rates[1] = getRate(1, 0, temperature);
    rates[2] = getRate(1, 1, temperature);
    rates[3] = getRate(1, 2, temperature);
    rates[4] = getRate(2, 1, temperature);
    return rates;
  }
  
  public double[] getReduced5Energies() {
    double[] rates = new double[5];
    rates[0] = getEnergy(0, 0);
    rates[1] = getEnergy(1, 0);
    rates[2] = getEnergy(1, 1);
    rates[3] = getEnergy(1, 2);
    rates[4] = getEnergy(2, 1);
    return rates;
  }
}
