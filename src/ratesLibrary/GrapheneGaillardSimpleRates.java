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

import static ratesLibrary.IRates.kB;

/**
 * Rates based on paper P. Gaillard, T. Chanier, L. Henrard, P. Moskovkin, S. Lucas. Surface
 * Science, Volumes 637–638, July–August 2015, Pages 11-18,
 * http://dx.doi.org/10.1016/j.susc.2015.02.014.
 *
 * Only the first neighbour of {@link kineticMonteCarlo.atom.AbstractAtom} and
 * {@link kineticMonteCarlo.atom.GrapheneTypesTable} are considered. Therefore, TERRACE -> 0,
 * CORNER, ZIGZAG_EDGE, SICK, ARMCHAIR_EDGE, ZIGZAG_WITH_EXTRA -> 1, KINK -> 2, BULK -> 3 are the
 * equivalences.
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class GrapheneGaillardSimpleRates implements IRates {
  private final double[][] energies;
  private double diffusionMl;
  private final double islandDensityPerSite = 1 / 60000f;
  private final double prefactor;
  
  public GrapheneGaillardSimpleRates() {
    diffusionMl = 5e-4;
    energies = new double[4][4];
    initialiseEnergies();
    prefactor = 1e11; // s^-1
  }
  
  private double getRate(int sourceType, int destinationType, double temperature) {
    return prefactor * Math.exp(-energies[sourceType][destinationType] / (kB * temperature));
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

  private void initialiseEnergies() {
    double eDiff = 0.5;
    double eDetach = 3.4;;
    double eInc = 1.8;
    double eDec = 2.6;
    double Einf = 9999999;
    
    energies[0][0] = eDiff;
    energies[0][1] = eDiff;
    energies[0][2] = eDiff;
    energies[0][3] = eDiff;
    
    energies[1][0] = eDec;
    energies[1][1] = eInc;
    energies[1][2] = eInc;
    energies[1][3] = Einf;
    
    energies[2][0] = eDetach;
    energies[2][1] = eDec;
    energies[2][2] = eDec;
    energies[2][3] = Einf;

    energies[3][0] = Einf;
    energies[3][1] = Einf;
    energies[3][2] = Einf;
    energies[3][3] = Einf;
  }
  
  @Override
  public double getEnergy(int sourceType, int destinationType) {
    return energies[sourceType][destinationType];
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
   * We don't use the temperature by now.
   *
   * @param temperature
   * @return rates[64]
   */ 
  
  @Override
  public double[] getRates(double temperature) {
    double[] ratesVector = new double[16];

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        ratesVector[i * 4 + j] = (getRate(i, j, temperature));
      }
    }
    return ratesVector;
  }
}
