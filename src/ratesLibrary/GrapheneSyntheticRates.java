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

import static kineticMonteCarlo.site.AbstractSite.ARMCHAIR_EDGE;
import static kineticMonteCarlo.site.AbstractSite.BULK;
import static kineticMonteCarlo.site.AbstractSite.CORNER;
import static kineticMonteCarlo.site.AbstractSite.KINK;
import static kineticMonteCarlo.site.AbstractSite.SICK;
import static kineticMonteCarlo.site.AbstractSite.TERRACE;
import static kineticMonteCarlo.site.AbstractSite.ZIGZAG_EDGE;
import static kineticMonteCarlo.site.AbstractSite.ZIGZAG_WITH_EXTRA;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class GrapheneSyntheticRates implements IRates {

  private final double[][] rates;
  private final double[][] energies;
  private double diffusionMl = 0.000035;
  private final double islandDensityPerSite = 1 / 60000f;
  private final double prefactor;

  public GrapheneSyntheticRates() {
    rates = new double[8][8];
    initialiseRates();
    energies = new double[8][8];
    initialiseEnergies();
    prefactor = 1e11;
  }
  
  private double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
  }

  @Override
  public double getDepositionRatePerSite() {
    return diffusionMl;
  }
  
  /**
   * Returns the island density mono layer depending on the temperature. How many islands per area
   * site are generated at current temperature. Usually with higher temperature less islands are
   * created, and thus, island density is lower. And the other way around.
   *
   * @param temperature Not implemented yet: temperature in Kelvin.
   * @return island density
   */
  @Override
  public double getIslandDensity(double temperature) {
    return islandDensityPerSite;
  }

  private void initialiseRates() {
    rates[TERRACE][TERRACE] = 1e9;
    rates[TERRACE][CORNER] = 1e9;
    rates[TERRACE][ZIGZAG_EDGE] = 1e9;
    rates[TERRACE][ARMCHAIR_EDGE] = 1e9;
    rates[TERRACE][ZIGZAG_WITH_EXTRA] = 1e9;
    rates[TERRACE][SICK] = 1e9;
    rates[TERRACE][KINK] = 1e9;
    rates[TERRACE][BULK] = 1e9;

    rates[CORNER][TERRACE] = 0;
    rates[CORNER][CORNER] = 100;
    rates[CORNER][ZIGZAG_EDGE] = 100;
    rates[CORNER][ARMCHAIR_EDGE] = 100;
    rates[CORNER][ZIGZAG_WITH_EXTRA] = 100;
    rates[CORNER][SICK] = 100;
    rates[CORNER][KINK] = 100;
    rates[CORNER][BULK] = 100;

    rates[ZIGZAG_EDGE][TERRACE] = 0;
    rates[ZIGZAG_EDGE][CORNER] = 100;
    rates[ZIGZAG_EDGE][ZIGZAG_EDGE] = 100;
    rates[ZIGZAG_EDGE][ARMCHAIR_EDGE] = 100;
    rates[ZIGZAG_EDGE][ZIGZAG_WITH_EXTRA] = 100;
    rates[ZIGZAG_EDGE][SICK] = 100;
    rates[ZIGZAG_EDGE][KINK] = 100;
    rates[ZIGZAG_EDGE][BULK] = 100;

    rates[ARMCHAIR_EDGE][TERRACE] = 0;
    rates[ARMCHAIR_EDGE][CORNER] = 10;
    rates[ARMCHAIR_EDGE][ZIGZAG_EDGE] = 10;
    rates[ARMCHAIR_EDGE][ARMCHAIR_EDGE] = 10;
    rates[ARMCHAIR_EDGE][ZIGZAG_WITH_EXTRA] = 10;
    rates[ARMCHAIR_EDGE][SICK] = 10;
    rates[ARMCHAIR_EDGE][KINK] = 10;
    rates[ARMCHAIR_EDGE][BULK] = 10;

    rates[ZIGZAG_WITH_EXTRA][TERRACE] = 0;
    rates[ZIGZAG_WITH_EXTRA][CORNER] = 0.01;
    rates[ZIGZAG_WITH_EXTRA][ZIGZAG_EDGE] = 0.01;
    rates[ZIGZAG_WITH_EXTRA][ARMCHAIR_EDGE] = 0.01;
    rates[ZIGZAG_WITH_EXTRA][ZIGZAG_WITH_EXTRA] = 0.01;
    rates[ZIGZAG_WITH_EXTRA][SICK] = 0.01;
    rates[ZIGZAG_WITH_EXTRA][KINK] = 0.01;
    rates[ZIGZAG_WITH_EXTRA][BULK] = 0.01;

    rates[SICK][TERRACE] = 0;
    rates[SICK][CORNER] = 0.00001;
    rates[SICK][ZIGZAG_EDGE] = 0.00001;
    rates[SICK][ARMCHAIR_EDGE] = 0.00001;
    rates[SICK][ZIGZAG_WITH_EXTRA] = 0.00001;
    rates[SICK][SICK] = 0.00001;
    rates[SICK][KINK] = 0.00001;
    rates[SICK][BULK] = 0.00001;

    rates[KINK][TERRACE] = 0;
    rates[KINK][CORNER] = 0;
    rates[KINK][ZIGZAG_EDGE] = 0;
    rates[KINK][ARMCHAIR_EDGE] = 0;
    rates[KINK][ZIGZAG_WITH_EXTRA] = 0;
    rates[KINK][SICK] = 0;
    rates[KINK][KINK] = 0;
    rates[KINK][BULK] = 0;

    rates[BULK][TERRACE] = 0;
    rates[BULK][CORNER] = 0;
    rates[BULK][ZIGZAG_EDGE] = 0;
    rates[BULK][ARMCHAIR_EDGE] = 0;
    rates[BULK][ZIGZAG_WITH_EXTRA] = 0;
    rates[BULK][SICK] = 0;
    rates[BULK][KINK] = 0;
    rates[BULK][BULK] = 0;

  }

  /**
   * Using energies instead of direct rates. I choose prefactor (1e11) and temperature (1273) to fix
   * the diffusion energy closest possible to 0.5 eV based on P. Gaillard, T. Chanier, L. Henrard,
   * P. Moskovkin, S. Lucas. Surface Science, Volumes 637–638, July–August 2015, Pages 11-18,
   * http://dx.doi.org/10.1016/j.susc.2015.02.014.
   */
  private void initialiseEnergies() {
    double Ed = 0.5051808896;
    double Ea = 2.2733140032;
    double Eb = 2.525904448;
    double Ec = 3.2836757825;
    double Ef = 4.0414471169;
    double Einf = 9999999;
    
    energies[TERRACE][TERRACE] = Ed;
    energies[TERRACE][CORNER] = Ed;
    energies[TERRACE][ZIGZAG_EDGE] = Ed;
    energies[TERRACE][ARMCHAIR_EDGE] = Ed;
    energies[TERRACE][ZIGZAG_WITH_EXTRA] = Ed;
    energies[TERRACE][SICK] = Ed;
    energies[TERRACE][KINK] = Ed;
    energies[TERRACE][BULK] = Ed;

    energies[CORNER][TERRACE] = Einf;
    energies[CORNER][CORNER] = Ea;
    energies[CORNER][ZIGZAG_EDGE] = Ea;
    energies[CORNER][ARMCHAIR_EDGE] = Ea;
    energies[CORNER][ZIGZAG_WITH_EXTRA] = Ea;
    energies[CORNER][SICK] = Ea;
    energies[CORNER][KINK] = Ea;
    energies[CORNER][BULK] = Ea;

    energies[ZIGZAG_EDGE][TERRACE] = Einf;
    energies[ZIGZAG_EDGE][CORNER] = Ea;
    energies[ZIGZAG_EDGE][ZIGZAG_EDGE] = Ea;
    energies[ZIGZAG_EDGE][ARMCHAIR_EDGE] = Ea;
    energies[ZIGZAG_EDGE][ZIGZAG_WITH_EXTRA] = Ea;
    energies[ZIGZAG_EDGE][SICK] = Ea;
    energies[ZIGZAG_EDGE][KINK] = Ea;
    energies[ZIGZAG_EDGE][BULK] = Ea;

    energies[ARMCHAIR_EDGE][TERRACE] = Einf;
    energies[ARMCHAIR_EDGE][CORNER] = Eb;
    energies[ARMCHAIR_EDGE][ZIGZAG_EDGE] = Eb;
    energies[ARMCHAIR_EDGE][ARMCHAIR_EDGE] = Eb;
    energies[ARMCHAIR_EDGE][ZIGZAG_WITH_EXTRA] = Eb;
    energies[ARMCHAIR_EDGE][SICK] = Eb;
    energies[ARMCHAIR_EDGE][KINK] = Eb;
    energies[ARMCHAIR_EDGE][BULK] = Eb;

    energies[ZIGZAG_WITH_EXTRA][TERRACE] = Einf;
    energies[ZIGZAG_WITH_EXTRA][CORNER] = Ec;
    energies[ZIGZAG_WITH_EXTRA][ZIGZAG_EDGE] = Ec;
    energies[ZIGZAG_WITH_EXTRA][ARMCHAIR_EDGE] = Ec;
    energies[ZIGZAG_WITH_EXTRA][ZIGZAG_WITH_EXTRA] = Ec;
    energies[ZIGZAG_WITH_EXTRA][SICK] = Ec;
    energies[ZIGZAG_WITH_EXTRA][KINK] = Ec;
    energies[ZIGZAG_WITH_EXTRA][BULK] = Ec;

    energies[SICK][TERRACE] = Einf;
    energies[SICK][CORNER] = Ef;
    energies[SICK][ZIGZAG_EDGE] = Ef;
    energies[SICK][ARMCHAIR_EDGE] = Ef;
    energies[SICK][ZIGZAG_WITH_EXTRA] = Ef;
    energies[SICK][SICK] = Ef;
    energies[SICK][KINK] = Ef;
    energies[SICK][BULK] = Ef;

    energies[KINK][TERRACE] = Einf;
    energies[KINK][CORNER] = Einf;
    energies[KINK][ZIGZAG_EDGE] = Einf;
    energies[KINK][ARMCHAIR_EDGE] = Einf;
    energies[KINK][ZIGZAG_WITH_EXTRA] = Einf;
    energies[KINK][SICK] = Einf;
    energies[KINK][KINK] = Einf;
    energies[KINK][BULK] = Einf;

    energies[BULK][TERRACE] = Einf;
    energies[BULK][CORNER] = Einf;
    energies[BULK][ZIGZAG_EDGE] = Einf;
    energies[BULK][ARMCHAIR_EDGE] = Einf;
    energies[BULK][ZIGZAG_WITH_EXTRA] = Einf;
    energies[BULK][SICK] = Einf;
    energies[BULK][KINK] = Einf;
    energies[BULK][BULK] = Einf;
  }
  
  @Override
  public double getEnergy(int sourceType, int destinationType) {
    return energies[sourceType][destinationType];
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
   * We don't use the temperature by now.
   *
   * @param temperature
   * @return rates[64]
   */ 
  
  @Override
  public double[] getRates(double temperature) {
    double[] ratesVector = new double[64];

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        ratesVector[i * 8 + j] = (getRate(i, j, temperature));
      }
    }
    return ratesVector;
  }
}
