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

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public interface IRates {
  /**
   * Boltzmann constant (eV/K).
   */
  static double kB = 8.617332e-5;
  /**
   * Boltzmann constant (J/K).
   */
  static double kBInt = 1.381e-23;
  /**
   * Planck constant (J·s).
   */
  static double h = 6.6260695729e-34;
  /**
   * Planck constant (eV·s).
   */
  static double hEv = 4.136e-15;
  /**
   * Avogadro constant (1/mol) * 1000 (g -> kg). 6.022e23·1000.
   */
  static double Na = 6.022e26;
  
  public double[] getRates(double temperature);

  public double getEnergy(int i, int j);  
  
  public double getDepositionRatePerSite();

  /**
   * Returns the island density mono layer depending on the temperature. How many islands per area
   * site are generated at current temperature. Usually with higher temperature less islands are
   * created, and thus, island density is lower. And the other way around.
   *
   * @param temperature temperature in Kelvin.
   * @return island density
   */
  public double getIslandDensity(double temperature);
  
  public void setDepositionFlux(double diffusionMl);
}
