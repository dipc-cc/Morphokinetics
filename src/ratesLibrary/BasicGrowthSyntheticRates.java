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

import static kineticMonteCarlo.site.BasicGrowthSite.EDGE;
import static kineticMonteCarlo.site.BasicGrowthSite.ISLAND;
import static kineticMonteCarlo.site.BasicGrowthSite.KINK;
import static kineticMonteCarlo.site.BasicGrowthSite.TERRACE;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthSyntheticRates extends AbstractBasicGrowthRates {

  public BasicGrowthSyntheticRates()  { 
    double Ed = 0.200;
    double Ef = 0.360;
    double Ea = 0.350;
    double Eb = 0.435;
    double Ec = 0.45;
    double Eg = 0.535;
    double Einf = 9999999;
    
    double[][] energies = new double[4][4];
    energies[TERRACE][TERRACE] = Ed;
    energies[TERRACE][EDGE] = Ed;
    energies[TERRACE][KINK] = Ed;
    energies[TERRACE][ISLAND] = Ed;

    energies[EDGE][TERRACE] = Ec;
    energies[EDGE][EDGE] = Ef;
    energies[EDGE][KINK] = Ea;
    energies[EDGE][ISLAND] = Ea;

    energies[KINK][TERRACE] = Eg;
    energies[KINK][EDGE] = Eb;
    energies[KINK][KINK] = Eb;
    energies[KINK][ISLAND] = Eb;

    energies[ISLAND][TERRACE] = Einf;
    energies[ISLAND][EDGE] = Einf;
    energies[ISLAND][KINK] = Einf;
    energies[ISLAND][ISLAND] = Einf;
    
    setEnergies(energies);
  }
}
