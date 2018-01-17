/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package ratesLibrary;

import static java.lang.Math.pow;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractBasicGrowthRates implements IRates {
  
  private double diffusionMl;
  private final double prefactor;
  private double[][] energies;

  public AbstractBasicGrowthRates() {
    prefactor = 1e13;
    diffusionMl = 0.000035;
  }
  
  public final void setEnergies(double[][] energies) {
    this.energies = energies;
  }
   
  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
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
  public double[] getReduced5Rates(float temperature) {
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
  
  private double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
  }
}
