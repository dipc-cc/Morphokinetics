/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import graphicInterfaces.siliconEtching.SiliconFrame;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmc;
import kineticMonteCarlo.kmcCore.etching.SiEtchingKmcConfig;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import kineticMonteCarlo.kmcCore.worker.IIntervalListener;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;

/**
 *
 * @author Nestor
 */
public class SiThreadedPsdEvaluator extends MultithreadedPsdEvaluator implements IFinishListener, IIntervalListener {

  private SiliconFrame frame;

  public SiThreadedPsdEvaluator(SiEtchingKmcConfig config, int repeats, int measureInterval, int numThreads) {

    super(repeats, measureInterval, numThreads);

    for (int i = 0; i < numThreads; i++) {
      workers[i] = new KmcWorker(new SiEtchingKmc(config), i);
      workers[i].start();
    }
    setPsdSizeX(config.sizeX_UC * 2);
    setPsdSizeY(config.sizeY_UC * 2);
  }

  @Override
  public AbstractEvaluator setShowGraphics(boolean showGraphics) {
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
  public synchronized void handleSimulationFinish(int workerId, int workId) {
    if (showGraphics && (System.currentTimeMillis() - timeLastRender) > 1000.0f / FPS_GRAPHICS) {
      frame.drawKmc(workers[workerId].getKmc());
      timeLastRender = System.currentTimeMillis();
    }

    super.handleSimulationFinish(workerId, workId);
  }

}
