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
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.growth.BdaKmc;
import ratesLibrary.IRates;
import ratesLibrary.bda.AbstractBdaRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaSimulation extends AbstractGrowthSimulation {
    
  public BdaSimulation(Parser parser) {
    super(parser);
  }
  
 @Override
  public void initialiseKmc() {
    super.initialiseKmc();
    AbstractBdaRates rates = null;
    String className;
    className = "ratesLibrary.bda.Bda" + getParser().getRatesLibrary() + "Rates";
    try {
      Class<?> genericClass = Class.forName(className);
      rates = (AbstractBdaRates) genericClass.getConstructors()[0].newInstance(getParser().getTemperature());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      Logger.getLogger(ConcertedSimulation.class.getName()).log(Level.SEVERE, null, ex);
    }
    setRates(rates);
    setKmc(new BdaKmc(getParser(), getRestartFolderName()));
    initialiseRates(getRates(), getParser());
  }
  
  @Override
  void initialiseRates(IRates rates, Parser parser) {
    AbstractBdaRates r = (AbstractBdaRates) rates;
    
    double depositionRatePerSite;
    rates.setDepositionFlux(parser.getDepositionFlux());
    depositionRatePerSite = rates.getDepositionRatePerSite();
//    double islandDensity = rates.getIslandDensity(parser.getTemperature());
    double islandDensity = -1;
    getKmc().setDepositionRate(depositionRatePerSite, islandDensity);
    ((BdaKmc) getKmc()).setRates(r);
  }
  
  @Override
  public void printRates(Parser parser) {
  }
}
