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
package ratesLibrary.basic;

/**
 * Etch rates data obtained from Gosalvez Et al - Physical Review E 68 (2003) 031604
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class RatesCaseH extends RatesCase {

  public RatesCaseH() {

    double[] prefactors = new double[4];
    double[] energies = new double[4];
    energies[0] = 0.0;
    energies[1] = 0.0;
    energies[2] = 0.4;
    energies[3] = 0.75;

    prefactors[0] = 1.0;
    prefactors[1] = 1.0;
    prefactors[2] = 8.0e4;
    prefactors[3] = 5.0e5;

    setRates(energies, prefactors);
  }

}

