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
package samples.worker;

import basic.Parser;
import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import ratesLibrary.SiRatesFromPreGosalvez;
import utils.StaticRandom;

/**
 *
 * @author N. Ferrando
 */
public class SimpleKmcSimulationWithWorker implements IFinishListener {

  private static KmcWorker worker;

  public static void main(String args[]) {

    System.out.println("Simple simulation of a KMC using a non-blocking threaded worker");

    int workerId = 0;
    int work_ID = 0;

    new StaticRandom();
    Parser parser = new Parser();
    parser.setListType("binned");
    parser.setBinsLevels(16);
    parser.setExtraLevels(1);
    parser.setMillerX(1);
    parser.setMillerY(0);
    parser.setMillerZ(0);
    parser.setCartSizeX(128);
    parser.setCartSizeY(128);
    parser.setCartSizeZ(32);

    worker = new KmcWorker(new SiKmc(parser),
            workerId);
    worker.start();

    worker.initialise(new SiRatesFromPreGosalvez().getRates(350));

    System.out.println("Launching worker...");
    worker.simulate(new SimpleKmcSimulationWithWorker(), work_ID);
    System.out.println("Continuing execution.");
  }

  @Override
  public void handleSimulationFinish(int workerId, int workId) {
    System.out.println("Worker simulation finished.");
    new SiFrame().drawKmc(worker.getKmc());
    worker.destroyWorker();
  }
}
