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
import kineticMonteCarlo.kmcCore.catalysis.CatalysisAmmoniaKmc;
import ratesLibrary.CatalysisHongRates;
import ratesLibrary.IRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisAmmoniaSimulation extends AbstractCatalysisSimulation {

  public CatalysisAmmoniaSimulation(Parser parser) {
    super(parser);
  }
  
  @Override
  public void initialiseKmc() {
    super.initialiseKmc();
    setRates(new CatalysisHongRates(getParser().getTemperature()));
    setKmc(new CatalysisAmmoniaKmc(getParser(), getRestartFolderName()));
    initialiseRates(getRates(), getParser());
    getKmc().init(getParser());
  }
  
  @Override
  void initialiseRates(IRates rates, Parser parser) {
    CatalysisHongRates r = (CatalysisHongRates) rates;
    r.setPressureO2(parser.getPressureO2());
    r.setPressureCO(parser.getPressureCO());
    r.computeAdsorptionRates();
    getKmc().setRates(r);
  }
  
}
