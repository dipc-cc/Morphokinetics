/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static java.lang.Math.pow;
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
  
  private final double prefactor;

  public BasicGrowthSyntheticRates()  { 
    diffusionMl = 0.000035;
    
    double Ed = 0.200;
    double Ef = 0.360;
    double Ea = 0.350;
    double Eb = 0.435;
    double Ec = 0.45;
    double Eg = 0.535;
    double Einf = 9999999;
    
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

    energies[KINK][TERRACE] = Eg;
    energies[KINK][EDGE] = Eb;
    energies[KINK][KINK] = Eb;
    energies[KINK][ISLAND] = Eb;

    energies[ISLAND][TERRACE] = Einf;
    energies[ISLAND][EDGE] = Einf;
    energies[ISLAND][KINK] = Einf;
    energies[ISLAND][ISLAND] = Einf;
  }
  
  private double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
  }

  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
  }
  
  /**
   * Returns the island density mono layer depending on the temperature. 
   * These values are taken from many run of multi flake with 400x400 lattice points
   * 
   * @param temperature
   * @return a double density value
   */
  @Override
  public double getIslandDensity(double temperature) {
    double flux = diffusionMl;
    double c;
    double slope;
    double rtt;
    if (temperature > 250) {
      c = 220;
      slope = -(2.d / 3.d);
    } else {
      c = 0.25;
      slope = -(1.d / 3.d);
    }
    rtt = getRate(0, 0, temperature);
     return pow(flux, 0.23d) * c * pow(rtt / pow(flux, 1.d / 3.d), slope);
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
