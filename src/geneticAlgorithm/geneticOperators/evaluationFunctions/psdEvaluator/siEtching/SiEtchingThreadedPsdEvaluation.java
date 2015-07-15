/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.siEtching;

import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.MultithreadedPsdEvaluation;
import graphicInterfaces.siliconEtching.SiliconFrame;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.siEtching.SiEtchingKmcConfig;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import kineticMonteCarlo.kmcCore.worker.IIntervalListener;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;

/**
 *
 * @author Nestor
 */
public class SiEtchingThreadedPsdEvaluation extends MultithreadedPsdEvaluation implements IFinishListener, IIntervalListener {

  private SiliconFrame frame;

  public SiEtchingThreadedPsdEvaluation(SiEtchingKmcConfig config, int repeats, int measureInterval, int num_threads) {

    super(repeats, measureInterval, num_threads);

    for (int i = 0; i < num_threads; i++) {
      workers[i] = new KmcWorker(new SiEtchingKmc(config), i);
      workers[i].start();
    }
    psdSizeX = config.sizeX_UC * 2;
    psdSizeY = config.sizeY_UC * 2;
  }



  @Override
  public AbstractEvaluation setShowGraphics(boolean showGraphics) {
    super.setShowGraphics(showGraphics);
    if (showGraphics && frame == null) {
      frame = new SiliconFrame();
    }
    if (!showGraphics && frame != null) {
      frame.dispose();
      frame = null;
    }
    return this;
  }

  @Override
  public synchronized void handleSimulationFinish(int workerID, int workID) {
    if (showGraphics && (System.currentTimeMillis() - time_last_render) > 1000.0f / FPS_GRAPHICS) {
      frame.drawKMC(workers[workerID].getKMC());
      time_last_render = System.currentTimeMillis();
    }

    super.handleSimulationFinish(workerID, workID);
  }

}
