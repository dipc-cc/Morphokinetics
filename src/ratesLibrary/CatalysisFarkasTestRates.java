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

import static kineticMonteCarlo.site.CatalysisSite.CO;

/**
 * Farkas, Hess, Over J Phys. Chem C (2011). Supporting Information.
 * 
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisFarkasTestRates extends CatalysisRates {
  
  private double K = 2;
  
  public CatalysisFarkasTestRates(float temperature) {
    super(temperature);
    setCorrectionFactor(0.5);
  }
  
  @Override
  public double[] getDesorptionRates(int type) {
    double[] rates;
    if (type == CO) {
      rates = new double[2];
      rates[0] = 0.00;
      rates[1] = 0.00;
    } else {
      rates = new double[4];
      double kDes = 1;
      rates[0] = kDes;
      rates[1] = kDes;
      rates[2] = kDes;
      rates[3] = kDes;
    }
    return rates;
  }
  
  /**
   * Adsorption rate of 0.29, 0.15 or 0.03.
   * 
   * @param atomType CO or O. ignored.
   * @return adsorption rate.
   */
  @Override
  public double getAdsorptionRate(int atomType) {
    if (atomType == CO) {
      return 0.00;
    } else {
      return K;
    }
  }
}
