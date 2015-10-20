/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import graphicInterfaces.diffusion2DGrowth.KmcCanvas;
import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import kineticMonteCarlo.kmcCore.diffusion.AgAgKmc;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import kineticMonteCarlo.kmcCore.worker.IIntervalListener;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;

/**
 *
 * @author Nestor
 */
public class AgThreadedPsdEvaluator extends MultithreadedPsdEvaluator implements IFinishListener, IIntervalListener {

  public AgThreadedPsdEvaluator(AgAgKmc kmc, int repeats, int measureInterval, int numThreads) {

    super(repeats, measureInterval, numThreads);

    for (int i = 0; i < numThreads; i++) {
      DiffusionKmcFrame frame = createGraphicsFrame(kmc);
      frame.setVisible(true);

      workers[i] = new KmcWorker(kmc, i);
      workers[i].start();
    }

    setPsdSizeX(64);
    setPsdSizeY(64);
  }

  private static DiffusionKmcFrame createGraphicsFrame(AgAgKmc kmc) {
    DiffusionKmcFrame frame = new DiffusionKmcFrame(new KmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
    return frame;
  }

  @Override
  public AbstractEvaluator setShowGraphics(boolean showGraphics) {
    super.setShowGraphics(showGraphics);

    return this;
  }

  @Override
  public synchronized void handleSimulationFinish(int workerID, int workID) {
    if (showGraphics && (System.currentTimeMillis() - timeLastRender) > 1000.0f / FPS_GRAPHICS) {
      timeLastRender = System.currentTimeMillis();
    }

    super.handleSimulationFinish(workerID, workID);
  }

}
