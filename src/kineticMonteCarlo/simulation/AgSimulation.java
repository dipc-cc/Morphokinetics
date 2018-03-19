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
package kineticMonteCarlo.simulation;

import basic.Parser;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import ratesLibrary.AgRatesFromPrbCox;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgSimulation extends AbstractGrowthSimulation {

  public AgSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    setRates(new AgRatesFromPrbCox());
    if (getParser().getHexaSizeI() == -1 || getParser().getHexaSizeJ() == -1) {
      double area = 1 / getRates().getIslandDensity(getParser().getTemperature()); // the inverse of the density is the area of the island
      int sizeX = (int) Math.ceil(Math.sqrt(area));
      int sizeY = (int) Math.ceil(Math.sqrt(area));
      getParser().setCartSizeX(sizeX);
      getParser().setCartSizeY(sizeY);
      System.out.println("Automatic size of the island is " + area + " " + sizeX + "x" + sizeY);
    }
    setKmc(new AgKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }
}
