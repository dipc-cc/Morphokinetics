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
package ratesLibrary.bda;

import ratesLibrary.IRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractBdaRates implements IRates{
  
  private double diffusionMl;
  private final double prefactor;
  /** Temperature (K). */
  private final float temperature;
  
  public AbstractBdaRates(float temperature) {
    this.temperature = temperature;
    prefactor = 1e13;
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
  
  /**
   * In principle, deposition rate is constant.
   *
   * @return diffusion mono layer (or deposition flux)
   */
  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
  }
  

  public double[] getDesorptionRates() {
    double[] rates = new double[12];
    for (int i = 0; i < rates.length; i++) {
      rates[i] = 1e-5;      
    }
    return rates;
  }
  
  public double[][] getDiffusionRates() {
    double[][] rates = new double[4][4];

    for (int i = 0; i < 4; i++) { // type (number of neighbours)
      for (int j = 0; j < 4; j++) { // direction
        double base = 20;
        if (j % 2 == 0) {
          base = 5;
        }
        //rates[i][j] = getRate(i, j, temperature);
        rates[i][j] = base / (Math.pow(100, i));
      }
    }
    return rates;
  }
  
}
