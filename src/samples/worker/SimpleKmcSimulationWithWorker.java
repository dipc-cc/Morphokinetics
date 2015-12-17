/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.worker;

import graphicInterfaces.etching.SiFrame;
import kineticMonteCarlo.kmcCore.etching.SiKmc;
import kineticMonteCarlo.kmcCore.etching.SiKmcConfig;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import utils.list.ListConfiguration;
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

    int worker_ID = 0;
    int work_ID = 0;

    SiKmcConfig config = configKmc();

    worker = new KmcWorker(new SiKmc(config),
            worker_ID);
    worker.start();

    worker.initialise(new SiRatesFactory().getRates(350));

    System.out.println("Launching worker...");
    worker.simulate(new SimpleKmcSimulationWithWorker(), work_ID);
    System.out.println("Continuing execution.");
  }

  private static SiKmcConfig configKmc() {
    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(16)
            .setExtraLevels(1);
    SiKmcConfig config = new SiKmcConfig()
            .setMillerX(1)
            .setMillerY(0)
            .setMillerZ(0)
            .setSizeX_UC(128)
            .setSizeY_UC(128)
            .setSizeZ_UC(32)
            .setListConfig(listConfig);
    return config;
  }

  @Override
  public void handleSimulationFinish(int workerID, int work_ID) {
    System.out.println("Worker simulation finished.");
    new SiFrame().drawKmc(worker.getKmc());
    worker.destroyWorker();

  }

}
