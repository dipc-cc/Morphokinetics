/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static java.lang.Math.pow;
import static ratesLibrary.IRates.kB;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Concerted6Rates implements IRates {
  
  private final double[][] energies;
  private double diffusionMl;
  
  private final double prefactor;
  /** Temperature (K). */
  private final float temperature;
  
  public Concerted6Rates(float temperature) {
    this.temperature = temperature;
    diffusionMl = 10;
 
    double eImpossible = 9999999;
     
    prefactor = 1e13;
    
    energies = new double[7][7];
    energies[0][0] = 0.052;
    energies[0][1] = 0.040;
    energies[0][2] = 0.007;
    energies[0][3] = 0.205;
    energies[0][4] = eImpossible;
    energies[0][5] = eImpossible;
    energies[0][6] = eImpossible;
    
    energies[1][0] = 0.428;
    energies[1][1] = 0.038;
    energies[1][2] = 0.016;
    energies[1][3] = 0.062;
    energies[1][4] = 0.417;
    energies[1][5] = eImpossible;
    energies[1][6] = eImpossible;
    
    energies[2][0] = 0.716;
    energies[2][1] = 0.361;
    energies[2][2] = 0.448;
    energies[2][3] = 0.351;
    energies[2][4] = 0.507;
    energies[2][5] = 0.032;
    energies[2][6] = eImpossible;
    
    energies[3][0] = 1.0;
    energies[3][1] = 0.845;
    energies[3][2] = 0.014;
    energies[3][3] = 0.008;
    energies[3][4] = 0.863;
    energies[3][5] = 0.034;
    energies[3][6] = eImpossible;
    
    energies[4][0] = eImpossible;
    energies[4][1] = 0.413;
    energies[4][2] = 0.110;
    energies[4][3] = 0.083;
    energies[4][4] = 0.319;
    energies[4][5] = 0.080;
    energies[4][6] = eImpossible;
    
    energies[5][0] = eImpossible;
    energies[5][1] = eImpossible;
    energies[5][2] = 0.872;
    energies[5][3] = 0.561;
    energies[5][4] = 1.182;
    energies[5][5] = 0.029;
    energies[5][6] = eImpossible;
    
    energies[6][0] = eImpossible;
    energies[6][1] = eImpossible;
    energies[6][2] = eImpossible;
    energies[6][3] = eImpossible;
    energies[6][4] = eImpossible;
    energies[6][5] = eImpossible;
    energies[6][6] = eImpossible;
  }
  
  private double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
  }

  /**
   * In principle, deposition rate is constant to 0.0035 ML/s. What changes is island density.
   * Consequently, deposition rate in practice varies with the temperature.
   *
   * @return diffusion mono layer (or deposition flux)
   */
  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
  }
  
  /**
   * Returns the island density mono layer depending on the temperature. These values are taken from
   * many run of multi flake with 200x200 lattice points. The formula to get the number of islands
   * is:
   *
   * N = F^{0.23}·c·(r_tt/F^{1/3})^x
   *
   * Therefore, we can get island density for single flake.
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
    if (temperature > 300) {
      c = 1e3;
      slope = -0.8;
    } else {
      c = 1.8e-3;
      slope = -0.13;
    }
    rtt = getRate(0, 0, temperature);
    return pow(flux, 0.23d) * c * pow(rtt / pow(flux, 1.d / 3.d), slope);
  }
  
  @Override
  public double getEnergy(int i, int j) {
    return energies[i][j];
  }

  /**
   * Diffusion Mono Layer (F). Utilised to calculate absorption rate. Cox et al. define to be 
   * F=0.0035 ML/s. The perimeter deposition is calculated multiplying F (this) and island density.
   * @param diffusionMl diffusion mono layer (deposition flux)
   */
  
  @Override
  public void setDepositionFlux(double diffusionMl) {
    this.diffusionMl = diffusionMl;
  }

  @Override
  public double[] getRates(double temperature) {
    double[] rates = new double[49];

    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        rates[i * 7 + j] = (getRate(i, j, temperature));
      }
    }
    return rates;
  }

  public double[][] getDiffusionRates() {
    double[][] rates = new double[7][7];

    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {
        rates[i][j] = (getRate(i, j, temperature));
      }
    }
    return rates;
  }
}
