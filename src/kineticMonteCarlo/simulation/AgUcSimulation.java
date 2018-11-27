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
import kineticMonteCarlo.kmcCore.growth.AgUcKmc;
import ratesLibrary.AgRatesFromPrbCox;
import ratesLibrary.AgRatesFromSsBruneAgAg;
import ratesLibrary.AgRatesFromSsBruneAgAgPt;
import ratesLibrary.AgRatesFromSsBruneAgPt;
import ratesLibrary.AgSimpleRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgUcSimulation  extends AbstractGrowthSimulation {

  public AgUcSimulation(Parser parser) {
    super(parser);
  }
  
 @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    switch (getParser().getRatesLibrary()) {
      case "simple":
        setRates(new AgSimpleRates());
        break;
      case "bruneAgAg":
        setRates(new AgRatesFromSsBruneAgAg());
        break;
      case "bruneAgPt":
        setRates(new AgRatesFromSsBruneAgPt(getParser().getTemperature()));
        break;
      case "bruneAgAgPt":
        setRates(new AgRatesFromSsBruneAgAgPt(getParser().getTemperature()));
        break;
      default:
        setRates(new AgRatesFromPrbCox());
    }
    if (getParser().getHexaSizeI() == -1 || getParser().getHexaSizeJ() == -1) {
      double area = 1 / getRates().getIslandDensity(getParser().getTemperature()); // the inverse of the density is the area of the island
      int sizeX = (int) Math.ceil(Math.sqrt(area));
      int sizeY = (int) Math.ceil(Math.sqrt(area));
      getParser().setCartSizeX(sizeX);
      getParser().setCartSizeY(sizeY);
      System.out.println("Automatic size of the island is " + area + " " + sizeX + "x" + sizeY);
    }
    setKmc(new AgUcKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }
}
