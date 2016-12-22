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
public class AgSimpleRates implements IRates {
  
  private final double[][] energies;
  private double diffusionMl;
  
  private final double prefactor;
  
  public AgSimpleRates() {
    diffusionMl = 0.000035;
    
    double e0 = 0.10;
    double e1 = 0.25;
    double e2 = 0.33;
    double e3 = 0.42;
    double eInf = 9999999;
    
    prefactor = 1e13;
    
    energies = new double[7][7];
    energies[0][0] = e0;
    energies[0][1] = e0;
    energies[0][2] = e0;
    energies[0][3] = e0;
    energies[0][4] = eInf;
    energies[0][5] = eInf;
    energies[0][6] = eInf;
    
    energies[1][0] = eInf;
    energies[1][1] = e1;
    energies[1][2] = e1;
    energies[1][3] = e1;
    energies[1][4] = e1;
    energies[1][5] = eInf;
    energies[1][6] = eInf;
    
    energies[2][0] = eInf;
    energies[2][1] = e2;
    energies[2][2] = e2;
    energies[2][3] = e2;
    energies[2][4] = e2;
    energies[2][5] = e2;
    energies[2][6] = eInf;
    
    energies[3][0] = eInf;
    energies[3][1] = eInf;
    energies[3][2] = eInf;
    energies[3][3] = e3;
    energies[3][4] = e3;
    energies[3][5] = e3;
    energies[3][6] = eInf;
    
    energies[4][0] = eInf;
    energies[4][1] = eInf;
    energies[4][2] = eInf;
    energies[4][3] = eInf;
    energies[4][4] = eInf;
    energies[4][5] = eInf;
    energies[4][6] = eInf;
    
    energies[5][0] = eInf;
    energies[5][1] = eInf;
    energies[5][2] = eInf;
    energies[5][3] = eInf;
    energies[5][4] = eInf;
    energies[5][5] = eInf;
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
    System.out.println("d "+diffusionMl);
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
}
