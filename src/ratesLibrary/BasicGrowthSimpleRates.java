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

import static kineticMonteCarlo.atom.BasicGrowthAtom.EDGE;
import static kineticMonteCarlo.atom.BasicGrowthAtom.ISLAND;
import static kineticMonteCarlo.atom.BasicGrowthAtom.KINK;
import static kineticMonteCarlo.atom.BasicGrowthAtom.TERRACE;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthSimpleRates extends AbstractBasicGrowthRates {

  public BasicGrowthSimpleRates()  { 
    double E0 = 0.1;
    double E1 = 0.2;
    double E2 = 0.3;
    double Einf = 9999999;
    
    double[][] energies = new double[4][4];
    energies[TERRACE][TERRACE] = E0;
    energies[TERRACE][EDGE]    = E0;
    energies[TERRACE][KINK]    = E0;
    energies[TERRACE][ISLAND]  = E0;

    energies[EDGE][TERRACE]    = E1;
    energies[EDGE][EDGE]       = E1;
    energies[EDGE][KINK]       = E1;
    energies[EDGE][ISLAND]     = E1;

    energies[KINK][TERRACE]    = E2;
    energies[KINK][EDGE]       = E2;
    energies[KINK][KINK]       = E2;
    energies[KINK][ISLAND]     = E2;

    energies[ISLAND][TERRACE] = Einf;
    energies[ISLAND][EDGE] = Einf;
    energies[ISLAND][KINK] = Einf;
    energies[ISLAND][ISLAND] = Einf;
    
    setEnergies(energies);
  }
  
}
