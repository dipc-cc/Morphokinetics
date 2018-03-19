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

import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import ratesLibrary.SiRatesFromPreGosalvez;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class SiSimulation extends AbstractEtchingSimulation {

  public SiSimulation(Parser parser) {
    super(parser);
  }

  @Override
  public void initialiseKmc() {
    super.initialiseKmc();

    setRates(new SiRatesFromPreGosalvez());
    setKmc(new SiKmc(getParser()));
    initialiseRates(getRates(), getParser());
  }

  /**
   * Does nothing.
   */
  @Override
  public void createFrame() {
  }

  /**
   * Show the result of the simulation in a frame.
   */
  @Override
  public void finishSimulation() {
    if (getParser().visualise()) {
      try {
        new SiFrame().drawKmc(getKmc());
      } catch (Exception e) {
        System.err.println("Error: The execution is not able to create the X11 frame");
        System.err.println("Finishing");
        throw e;
      }
    }
  } 
  
  @Override
  public void printRates(Parser parser) {
    double[] rates = getRates().getRates(parser.getTemperature());

    for (int i = 0; i < rates.length; i++) {
      System.out.printf("%1.3E  ", rates[i]);
    }
    System.out.println("");
  }
}
