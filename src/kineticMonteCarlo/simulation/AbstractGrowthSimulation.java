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
import graphicInterfacesCommon.growth.IGrowthKmcFrame;
import kineticMonteCarlo.kmcCore.growth.AbstractGrowthKmc;
import ratesLibrary.IRates;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractGrowthSimulation extends AbstractSurfaceSimulation {
  
  public AbstractGrowthSimulation(Parser parser) {
    super(parser);
  }
  
  @Override
  public AbstractGrowthKmc getKmc() {
    return (AbstractGrowthKmc) super.getKmc();
  }
  
  @Override
  public void printRates(Parser parser) {
    double[] rates = getRates().getRates(parser.getTemperature());
    //we modify the 1D array into a 2D array;
    int length = (int) Math.sqrt(rates.length);

    for (int i = 0; i < length; i++) {
      for (int j = 0; j < length; j++) {
        System.out.printf("%1.3E  ", rates[i * length + j]);
      }
      System.out.println(" ");
    }
    System.out.println("Deposition rate (per site): " + getRates().getDepositionRatePerSite());
    System.out.println("Island density:             " + getRates().getIslandDensity(parser.getTemperature()));
  }
  
  @Override
  void initialiseRates(IRates rates, Parser parser) {
    double depositionRatePerSite;
    rates.setDepositionFlux(parser.getDepositionFlux());
    depositionRatePerSite = rates.getDepositionRatePerSite();
    double islandDensity = rates.getIslandDensity(parser.getTemperature());
    getKmc().setDepositionRate(depositionRatePerSite, islandDensity);
    getKmc().initialiseRates(rates.getRates(parser.getTemperature()));
  }
  
  @Override
  IGrowthKmcFrame constructFrame(Class<?> genericClass, int max) throws Exception {
    return (IGrowthKmcFrame) genericClass.getConstructors()[0].newInstance(getKmc().getLattice(), ((AbstractGrowthKmc) getKmc()).getPerimeter(), max);
  }
  
  @Override
  int countIslands() {
    return getKmc().getLattice().getIslandCount();
  }
  
  @Override
  float getGyradius() {
    return getKmc().getLattice().getAverageGyradius();
  }
  
  @Override
  int calculateCurrentProgress() {
    int progress;
    if (getParser().justCentralFlake()) {
      progress = getKmc().getCurrentRadius();
    } else {
      progress = (int) Math.floor(getCoverage()[0] * 100);
    }
    return progress;
  }
  
  @Override
  boolean isGrowth() {
    return true;
  }
  
  @Override
  boolean shouldPrint() {
    return ((getParser().justCentralFlake() && getKmc().getCurrentRadius() >= getCurrentProgress())
            || (getKmc().getCoverage() * 100 >= getCurrentProgress()));
  }
}
