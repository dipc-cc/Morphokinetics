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

import static kineticMonteCarlo.atom.AbstractSite.ARMCHAIR_EDGE;
import static kineticMonteCarlo.atom.AbstractSite.BULK;
import static kineticMonteCarlo.atom.AbstractSite.CORNER;
import static kineticMonteCarlo.atom.AbstractSite.KINK;
import static kineticMonteCarlo.atom.AbstractSite.SICK;
import static kineticMonteCarlo.atom.AbstractSite.TERRACE;
import static kineticMonteCarlo.atom.AbstractSite.ZIGZAG_EDGE;
import static kineticMonteCarlo.atom.AbstractSite.ZIGZAG_WITH_EXTRA;
import static ratesLibrary.IRates.kB;

/**
 * Rates based on paper P. Gaillard, T. Chanier, L. Henrard, P. Moskovkin, S. Lucas. Surface
 * Science, Volumes 637–638, July–August 2015, Pages 11-18,
 * http://dx.doi.org/10.1016/j.susc.2015.02.014.
 *
 * Only the first neighbour of {@link kineticMonteCarlo.atom.AbstractSite} and
 * {@link kineticMonteCarlo.atom.GrapheneTypesTable} are considered. Therefore, TERRACE -> 0,
 * CORNER, ZIGZAG_EDGE, SICK, ARMCHAIR_EDGE, ZIGZAG_WITH_EXTRA -> 1, KINK -> 2, BULK -> 3 are the
 * equivalences.
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class GrapheneGaillardOneNeighbourRates implements IRates {
  private final double[][] energies;
  private double diffusionMl;
  private final double islandDensityPerSite = 1 / 60000f;
  private final double prefactor;
  
  public GrapheneGaillardOneNeighbourRates() {
    diffusionMl = 5e-4;
    energies = new double[8][8];
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
    double eDetach = 3.9;;
    double eInc = 1.8;
    double eDec = 2.6;
    double Einf = 9999999;
    
    // 0 neighbours to any. "E_Diff is the activation energy for free diffusion or to attach to an island
    // (event 1 and reversed events 2, 3 and 5)"
    energies[TERRACE][TERRACE] =                     eDiff; // 0 -> 0
    energies[TERRACE][CORNER] =                      eDiff; // 0 -> 1
    energies[TERRACE][ZIGZAG_EDGE] =                 eDiff; // 0 -> 1
    energies[TERRACE][ARMCHAIR_EDGE] =               eDiff; // 0 -> 1
    energies[TERRACE][ZIGZAG_WITH_EXTRA] =           eDiff; // 0 -> 1
    energies[TERRACE][SICK] =                        eDiff; // 0 -> 1
    energies[TERRACE][KINK] =                        eDiff; // 0 -> 2 
    energies[TERRACE][BULK] =                        Einf;// imposible to happen

    energies[CORNER][TERRACE] =                      eDec; // 1 -> 0
    energies[CORNER][CORNER] =                       eInc; // 1 -> 1
    energies[CORNER][ZIGZAG_EDGE] =                  eInc; // 1 -> 1
    energies[CORNER][ARMCHAIR_EDGE] =                eInc; // 1 -> 1
    energies[CORNER][ZIGZAG_WITH_EXTRA] =            eInc; // 1 -> 1
    energies[CORNER][SICK] =                         eInc; // 1 -> 1
    energies[CORNER][KINK] =                         eInc; // 1 -> 2
    energies[CORNER][BULK] =                         Einf; // imposible to happen

    energies[ZIGZAG_EDGE][TERRACE] =                 eDec; // 1 -> 0
    energies[ZIGZAG_EDGE][CORNER] =                  eInc; // 1 -> 1
    energies[ZIGZAG_EDGE][ZIGZAG_EDGE] =             eInc; // 1 -> 1
    energies[ZIGZAG_EDGE][ARMCHAIR_EDGE] =           eInc; // 1 -> 1
    energies[ZIGZAG_EDGE][ZIGZAG_WITH_EXTRA] =       eInc; // 1 -> 1
    energies[ZIGZAG_EDGE][SICK] =                    eInc; // 1 -> 1
    energies[ZIGZAG_EDGE][KINK] =                    eInc; // 1 -> 2
    energies[ZIGZAG_EDGE][BULK] =                    Einf;// imposible to happen

    energies[ARMCHAIR_EDGE][TERRACE] =               eDec; // 1 -> 0
    energies[ARMCHAIR_EDGE][CORNER] =                eInc; // 1 -> 1
    energies[ARMCHAIR_EDGE][ZIGZAG_EDGE] =           eInc; // 1 -> 1
    energies[ARMCHAIR_EDGE][ARMCHAIR_EDGE] =         eInc; // 1 -> 1
    energies[ARMCHAIR_EDGE][ZIGZAG_WITH_EXTRA] =     eInc; // 1 -> 1
    energies[ARMCHAIR_EDGE][SICK] =                  eInc; // 1 -> 1
    energies[ARMCHAIR_EDGE][KINK] =                  eInc; // 1 -> 2
    energies[ARMCHAIR_EDGE][BULK] =                  Einf; // imposible to happen

    energies[ZIGZAG_WITH_EXTRA][TERRACE] =           eDec; // 1 -> 0
    energies[ZIGZAG_WITH_EXTRA][CORNER] =            eInc; // 1 -> 1
    energies[ZIGZAG_WITH_EXTRA][ZIGZAG_EDGE] =       eInc; // 1 -> 1
    energies[ZIGZAG_WITH_EXTRA][ARMCHAIR_EDGE] =     eInc; // 1 -> 1
    energies[ZIGZAG_WITH_EXTRA][ZIGZAG_WITH_EXTRA] = eInc; // 1 -> 1
    energies[ZIGZAG_WITH_EXTRA][SICK] =              eInc; // 1 -> 1
    energies[ZIGZAG_WITH_EXTRA][KINK] =              eInc; // 1 -> 2
    energies[ZIGZAG_WITH_EXTRA][BULK] =              Einf; // imposible to happen

    energies[SICK][TERRACE] =                        eDec; // 1 -> 0
    energies[SICK][CORNER] =                         eInc; // 1 -> 1
    energies[SICK][ZIGZAG_EDGE] =                    eInc; // 1 -> 1
    energies[SICK][ARMCHAIR_EDGE] =                  eInc; // 1 -> 1
    energies[SICK][ZIGZAG_WITH_EXTRA] =              eInc; // 1 -> 1
    energies[SICK][SICK] =                           eInc; // 1 -> 1
    energies[SICK][KINK] =                           eInc; // 1 -> 2
    energies[SICK][BULK] =                           Einf; // imposible to happen

    energies[KINK][TERRACE] =                        eDetach; // 2 -> 0
    energies[KINK][CORNER] =                         eDec; // 2 -> 1
    energies[KINK][ZIGZAG_EDGE] =                    eDec; // 2 -> 1
    energies[KINK][ARMCHAIR_EDGE] =                  eDec; // 2 -> 1
    energies[KINK][ZIGZAG_WITH_EXTRA] =              eDec; // 2 -> 1
    energies[KINK][SICK] =                           eDec; // 2 -> 1
    energies[KINK][KINK] =                           eDec; // 2 -> 1
    energies[KINK][BULK] =                           Einf; // imposible to happen

    energies[BULK][TERRACE] =                        Einf; // imposible to happen
    energies[BULK][CORNER] =                         Einf; // imposible to happen
    energies[BULK][ZIGZAG_EDGE] =                    Einf; // imposible to happen
    energies[BULK][ARMCHAIR_EDGE] =                  Einf; // imposible to happen
    energies[BULK][ZIGZAG_WITH_EXTRA] =              Einf; // imposible to happen
    energies[BULK][SICK] =                           Einf; // imposible to happen
    energies[BULK][KINK] =                           Einf; // imposible to happen
    energies[BULK][BULK] =                           Einf; // imposible to happen
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
    double[] ratesVector = new double[64];

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        ratesVector[i * 8 + j] = (getRate(i, j, temperature));
      }
    }
    return ratesVector;
  }
}
