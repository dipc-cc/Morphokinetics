/* 
 * Copyright (C) 2018 N. Ferrando
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
package samples.silicon;

import basic.Parser;
import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import ratesLibrary.SiRatesFromPreGosalvez;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando
 */
public class SimpleSiliconKmcSimulation {

  public static void main(String args[]) {

    System.out.println("Simple simulation of the Silicon etching KMC");

    new StaticRandom();
    Parser parser = new Parser();
    parser.setListType("binned");
    parser.setBinsLevels(20);
    parser.setExtraLevels(1);
    parser.setMillerX(0);
    parser.setMillerY(1);
    parser.setMillerZ(1);
    parser.setCartSizeX(96);
    parser.setCartSizeY(96);
    parser.setCartSizeZ(16);

    SiKmc kmc = new SiKmc(parser);

    long start = System.nanoTime();
    kmc.reset();
    kmc.initialiseRates(new SiRatesFromPreGosalvez().getRates(350));
    kmc.depositSeed();
    kmc.simulate();

    System.out.println((System.nanoTime() - start) / 1000000);

    new SiFrame().drawKmc(kmc);
  }
}
