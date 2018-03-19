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
package basic;

import kineticMonteCarlo.kmcCore.growth.BasicGrowthKmc;
import ratesLibrary.BasicGrowth2Rates;
import ratesLibrary.BasicGrowth3Rates;
import ratesLibrary.BasicGrowthSimpleRates;
import ratesLibrary.BasicGrowthSyntheticRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthSimulation extends AbstractGrowthSimulation{
  
  public BasicGrowthSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    switch (getParser().getRatesLibrary()) {
      case "simple":
        setRates(new BasicGrowthSimpleRates());
        break;
      case "version2":
        setRates(new BasicGrowth2Rates());
        break;
      case "version3":
        setRates(new BasicGrowth3Rates());
        break;
      default:
        setRates(new BasicGrowthSyntheticRates());
    }
    setKmc(new BasicGrowthKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }
}
