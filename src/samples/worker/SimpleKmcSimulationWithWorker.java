/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.worker;

import basic.Parser;
import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import ratesLibrary.SiRatesFactory;
import utils.StaticRandom;

/**
 *
 * @author Nestor
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

    worker.initialise(new SiRatesFactory().getRates(350));

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
