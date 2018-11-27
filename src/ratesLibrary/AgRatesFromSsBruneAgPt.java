/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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

import static kineticMonteCarlo.site.AgSite.TERRACE;
import static kineticMonteCarlo.site.AgSite.CORNER;
import static kineticMonteCarlo.site.AgSite.EDGE_A;
import static kineticMonteCarlo.site.AgSite.EDGE_B;
import static kineticMonteCarlo.site.AgSite.KINK_A;
import static kineticMonteCarlo.site.AgSite.KINK_B;
import static kineticMonteCarlo.site.AgSite.ISLAND;

/**
 *
 * Etch rates data obtained from Cox et al. - PHYSICAL REVIEW B 71, 11541 2005
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class AgRatesFromSsBruneAgPt implements IRates {

  /**
   * Diffusion Mono Layer (F). Utilised to calculate absorption rate. Cox et al. define to be 
   * F=0.0035 ML/s. The perimeter deposition is calculated multiplying F (this) and island density.
   */
  private double diffusionMl;
  private final double prefactor;
  /**
   * Energy from edge A to edge A
   */
  private final double Eaa;
  /**
   * Energy from edge B to edge B
   */
  private final double Ebb;
  /**
   * Energy from corner to edge A
   */
  private final double Eca;
  /**
   * Energy from corner to edge B
   */
  private final double Ecb;
  /**
   * Energy from corner to corner
   */
  private final double Ecc;
  /**
   * Energy from edge A to corner
   */
  private final double Eac;
  /**
   * Energy from edge B to corner
   */
  private final double Ebc;
  private final double Ed;
  /**
   * Infinite energy. To make impossible the transition.
   */
  private final double Einf;
  private final double[][] energies;

  /**
   * Atom types are documented in class AbstractAtom. In any case are;
   *
   * 0: terrace | 1: corner | 2: A side | 3: kink A | 4: bulk | 5: B side | 6: kink B
   *                               edge                 island       edge
   */
  public AgRatesFromSsBruneAgPt(double temperature) { 
    diffusionMl = 0.0035;
    prefactor = 1e13;
    
    if (temperature > 130) {
      Ed = 0.120;
      Ecb = 2;//0.320;
    } else {
      Ed = 0.160;
      Ecb = 2;//0.50;
    }
    Eca = 0.160;
    Eaa = 0.187;
    Ebb = 0.389;
    Eac = 0.244;
    Ebc = 0.415;
    Einf = 9999999;
    Ecc = Math.min(Eca, Ecb);
    energies = new double[7][7];

    //[source type][destination type]
    energies[TERRACE][TERRACE] = Ed;
    energies[TERRACE][CORNER] = Ed;
    energies[TERRACE][EDGE_A] = Ed;
    energies[TERRACE][KINK_A] = Ed;
    energies[TERRACE][ISLAND] = Ed;
    energies[TERRACE][EDGE_B] = Ed;
    energies[TERRACE][KINK_B] = Ed;

    energies[CORNER][TERRACE] = Einf; // Impossible. Forbid it
    energies[CORNER][CORNER] = Einf;
    energies[CORNER][EDGE_A] = Eca;
    energies[CORNER][KINK_A] = Eca;
    energies[CORNER][ISLAND] = Math.max(Eca, Ecb);
    energies[CORNER][EDGE_B] = Ecb;
    energies[CORNER][KINK_B] = Ecb;

    energies[EDGE_A][TERRACE] = Einf; // Impossible. Forbid it
    energies[EDGE_A][CORNER] = Eac;
    energies[EDGE_A][EDGE_A] = Eaa;
    energies[EDGE_A][KINK_A] = Eaa;
    energies[EDGE_A][ISLAND] = Eaa;
    energies[EDGE_A][EDGE_B] = Eaa;
    energies[EDGE_A][KINK_B] = Eaa;

    energies[KINK_A][TERRACE] = Einf; // Impossible. Forbid it
    energies[KINK_A][CORNER] = Einf; // Impossible. Forbid it
    energies[KINK_A][EDGE_A] = Einf; // Impossible. Forbid it
    energies[KINK_A][KINK_A] = Einf; // Impossible. Forbid it
    energies[KINK_A][ISLAND] = Einf; // Impossible. Forbid it
    energies[KINK_A][EDGE_B] = Einf; // Impossible. Forbid it
    energies[KINK_A][KINK_B] = Einf; // Impossible. Forbid it

    energies[ISLAND][TERRACE] = Einf; // Impossible. Forbid it
    energies[ISLAND][CORNER] = Einf; // Impossible. Forbid it
    energies[ISLAND][EDGE_A] = Einf; // Impossible. Forbid it
    energies[ISLAND][KINK_A] = Einf; // Impossible. Forbid it
    energies[ISLAND][ISLAND] = Einf; // Impossible. Forbid it
    energies[ISLAND][EDGE_B] = Einf; // Impossible. Forbid it
    energies[ISLAND][KINK_B] = Einf; // Impossible. Forbid it

    energies[EDGE_B][TERRACE] = Einf; // Impossible. Forbid it
    energies[EDGE_B][CORNER] = Ebc;
    energies[EDGE_B][EDGE_A] = Ebb;
    energies[EDGE_B][KINK_A] = Ebb;
    energies[EDGE_B][ISLAND] = Ebb;
    energies[EDGE_B][EDGE_B] = Ebb;
    energies[EDGE_B][KINK_B] = Ebb;

    energies[KINK_B][TERRACE] = Einf; // Impossible. Forbid it
    energies[KINK_B][CORNER] = Einf; // Impossible. Forbid it
    energies[KINK_B][EDGE_A] = Einf; // Impossible. Forbid it
    energies[KINK_B][KINK_A] = Einf; // Impossible. Forbid it
    energies[KINK_B][ISLAND] = Einf; // Impossible. Forbid it
    energies[KINK_B][EDGE_B] = Einf; // Impossible. Forbid it
    energies[KINK_B][KINK_B] = Einf; // Impossible. Forbid it
  }

  private double getRate(int i, int j, double temperature) {
    return (prefactor * Math.exp(-energies[i][j] / (kB * temperature)));
  }
  
  /**
   *  Giving energy it returns a rate with a prefactor of 1e13
   * 
   * @param temperature system temperature
   * @param energy input energy
   * @return rate
   */
  public static double getRate(double temperature, double energy) {
      return (1e13 * Math.exp(-energy / (kB * temperature)));
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
    // TERRECE to anywhere is an special case. In the code in general it is used a different prefactor (1e11).
    // This number comes from applying the same prefactor (1e13)
    if (i == TERRACE) return 0.15357378552368;
    // Return the table value otherwise
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
  
  /**
   * This is tuned to work with only 6 "genes".
   * Gene 0 from Ed   (0,j)
   * Gene 1 from Ec   (1,1)(1,2)(1,6)
   * Gene 2 from Ee   (1,3)(1,5)
   * Gene 3 from Ef   (2,3)(5,2)(5,3)(5,4)(5,6)
   * Gene 4 from Ea   (2,2)
   * Gene 5 from Eb   (5,5)
   * @param temperature
   * @return rates[6]
   */
  public double[] getReduced6Rates(float temperature) {
    double[] rates = new double[6];
    rates[0] = getRate(0, 0, temperature);
    rates[1] = getRate(1, 1, temperature);
    rates[2] = getRate(1, 3, temperature);
    rates[3] = getRate(2, 3, temperature);
    rates[4] = getRate(2, 2, temperature);
    rates[5] = getRate(5, 5, temperature);
    return rates;
  }
  
  public double[] getReduced6Energies() {
    double[] rates = new double[6];
    rates[0] = getEnergy(0, 0);
    rates[1] = getEnergy(1, 1);
    rates[2] = getEnergy(1, 3);
    rates[3] = getEnergy(2, 3);
    rates[4] = getEnergy(2, 2);
    rates[5] = getEnergy(5, 5);
    return rates;
  }
}
