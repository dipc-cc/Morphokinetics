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
import ratesLibrary.IRates;
import ratesLibrary.SiRatesFromPreGosalvez;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractEtchingSimulation extends AbstractSimulation {

  public AbstractEtchingSimulation(Parser parser) {
    super(parser);
  }

  @Override
  void initialiseRates(IRates rates, Parser parser) {
    getKmc().initialiseRates(new SiRatesFromPreGosalvez().getRates(getParser().getTemperature()));
  }

}
