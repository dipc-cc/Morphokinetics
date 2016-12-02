/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary;

import static ratesLibrary.IRates.kB;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgSimpleRates implements IRates {
  
  private final double[][] energies;
  private double diffusionMl;
  
  private final double prefactor;
  
  public AgSimpleRates() {
    diffusionMl = 0.000035;
    
    double e0 = 0.1587;
    double e1 = 0.2381;
    double e2 = 0.3175;
    double e3 = 0.3968;
    double eInf = 9999999;
    double e4 = eInf;
    double e5 = eInf;
    
    prefactor = 1e13;
    
    energies = new double[7][7];
    energies[0][0] = e0;
    energies[0][1] = e0;
    energies[0][2] = e0;
    energies[0][3] = e0;
    energies[0][4] = eInf;
    energies[0][5] = eInf;
    energies[0][6] = eInf;
    
    energies[1][0] = e1;
    energies[1][1] = e1;
    energies[1][2] = e1;
    energies[1][3] = e1;
    energies[1][4] = e1;
    energies[1][5] = eInf;
    energies[1][6] = eInf;
    
    energies[2][0] = e2;
    energies[2][1] = e2;
    energies[2][2] = e2;
    energies[2][3] = e2;
    energies[2][4] = e2;
    energies[2][5] = e2;
    energies[2][6] = eInf;
    
    energies[3][0] = e3;
    energies[3][1] = e3;
    energies[3][2] = e3;
    energies[3][3] = e3;
    energies[3][4] = e3;
    energies[3][5] = e3;
    energies[3][6] = eInf;
    
    energies[4][0] = eInf;
    energies[4][1] = eInf;
    energies[4][2] = eInf;
    energies[4][3] = eInf;
    energies[4][4] = e4;
    energies[4][5] = eInf;
    energies[4][6] = eInf;
    
    energies[5][0] = eInf;
    energies[5][1] = eInf;
    energies[5][2] = eInf;
    energies[5][3] = eInf;
    energies[5][4] = eInf;
    energies[5][5] = e5;
    energies[5][6] = eInf;
    
    energies[6][0] = eInf;
    energies[6][1] = eInf;
    energies[6][2] = eInf;
    energies[6][3] = eInf;
    energies[6][4] = eInf;
    energies[6][5] = eInf;
    energies[6][6] = eInf;
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
      return 1e-4;
    }
    if (temperature < 150) {//135 degrees Kelvin
      return 5e-5;
    }
    if (temperature < 165) {//150 degrees Kelvin
      return 4e-5;
    }
    if (temperature < 180) {//165 degrees Kelvin
      return 3e-5;
    }
    return 2e-5; //180 degrees Kelvin
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
}
