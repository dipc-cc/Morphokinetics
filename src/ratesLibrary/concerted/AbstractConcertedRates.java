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
package ratesLibrary.concerted;

import static java.lang.Math.pow;
import ratesLibrary.IRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AbstractConcertedRates implements IRates {
  
  private double diffusionMl;
  private double[][] energies;
  private double[] concertedEnergies;
  private double[] multiAtomEnergies;
  
    private final double prefactor;
  /** Temperature (K). */
  private final float temperature;
  
  public AbstractConcertedRates(float temperature) {
    this.temperature = temperature;
    prefactor = 1e13;
  }
  
  final void setEnergies(double[][] energies) {
    this.energies = energies;
  }
  
  final void setConcertedEnergies(double[] energies) {
    this.concertedEnergies = energies;
  }
  
  final void setMultiAtomEnergies(double[] energies) {
    this.multiAtomEnergies = energies;
  }
  
  private double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
  }

  /**
   * Concerted version.
   */
  private double getRate(int size, double temperature) {
    return prefactor * Math.exp(-concertedEnergies[size] / (kB * temperature));
  }
  
  /**
   * Multi atom version.
   */
  private double getMultiAtomRate(int size, double temperature) {
    return prefactor * Math.exp(-multiAtomEnergies[size] / (kB * temperature));
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
    double[] rates = new double[192];

    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 16; j++) {
        rates[i * 16 + j] = getRate(i, j, temperature);
      }
    }
    return rates;
  }

  public double[][] getDiffusionRates() {
    double[][] rates = new double[12][16];

    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 16; j++) {
        rates[i][j] = getRate(i, j, temperature);
      }
    }
    return rates;
  }
  
  public double[] getIslandDiffusionRates() {
    double[] rates = new double[concertedEnergies.length];
    for (int i = 0; i < concertedEnergies.length; i++) {
      rates[i] = getRate(i, temperature);
    }
    return rates;
  }
  
  public double[] getMultiAtomRates() {
    double[] rates = new double[multiAtomEnergies.length];
    for (int i = 0; i < multiAtomEnergies.length; i++) {
      rates[i] = getMultiAtomRate(i, temperature);
    }
    return rates;
  }
}
