/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package samples.worker;

import graphicInterfaces.siliconEtching.SiliconFrame;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
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

    SiEtchingKmcConfig config = configKmc();

    worker = new KmcWorker(new SiEtchingKmc(config),
            worker_ID);
    worker.start();

    worker.initialize(new SiRatesFactory().getRates(350));

    System.out.println("Launching worker...");
    worker.simulate(new SimpleKmcSimulationWithWorker(), work_ID);
    System.out.println("Continuing execution.");
  }

  private static SiEtchingKmcConfig configKmc() {
    new StaticRandom();
    ListConfiguration listConfig = new ListConfiguration()
            .setListType(ListConfiguration.BINNED_LIST)
            .setBinsPerLevel(16)
            .setExtraLevels(1);
    SiEtchingKmcConfig config = new SiEtchingKmcConfig()
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
    new SiliconFrame().drawKmc(worker.getKmc());
    worker.destroyWorker();

  }

}
