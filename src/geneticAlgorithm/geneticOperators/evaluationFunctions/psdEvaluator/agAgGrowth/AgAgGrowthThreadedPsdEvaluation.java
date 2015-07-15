/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.agAgGrowth;

import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.MultithreadedPsdEvaluation;
import graphicInterfaces.diffusion2DGrowth.agAgGrowth.AgAgKmcCanvas;
import graphicInterfaces.diffusion2DGrowth.DiffusionKmcFrame;
import kineticMonteCarlo.kmcCore.diffusion.agAgGrowth.AgAgKmc;
import kineticMonteCarlo.kmcCore.diffusion.agAgGrowth.AgAgKmcConfig;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import kineticMonteCarlo.kmcCore.worker.IIntervalListener;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import kineticMonteCarlo.lattice.diffusion.Abstract2DDiffusionLattice;

/**
 *
 * @author Nestor
 */
public class AgAgGrowthThreadedPsdEvaluation extends MultithreadedPsdEvaluation implements IFinishListener, IIntervalListener {

  public AgAgGrowthThreadedPsdEvaluation(AgAgKmcConfig config, int repeats, int measureInterval, int num_threads) {

    super(repeats, measureInterval, num_threads);

    for (int i = 0; i < num_threads; i++) {
      AgAgKmc kmc = new AgAgKmc(config, true);
      DiffusionKmcFrame frame = create_graphics_frame(kmc);
      frame.setVisible(true);

      workers[i] = new KmcWorker(kmc, i);
      workers[i].start();
    }

    PSD_size_X = 64;
    PSD_size_Y = 64;
  }

  private static DiffusionKmcFrame create_graphics_frame(AgAgKmc kmc) {
    DiffusionKmcFrame frame = new DiffusionKmcFrame(new AgAgKmcCanvas((Abstract2DDiffusionLattice) kmc.getLattice()));
    return frame;
  }

  @Override
  public void handleSimulationIntervalFinish(int workerID, int workID) {

    float[][] surface = new float[PSD_size_Y][PSD_size_X];
    workers[workerID].getSampledSurface(surface);
    times[workID] += workers[workerID].getKMC().getTime();
    addToPSD(workID, surface);

    System.out.println("Worker " + workerID + " finished a simulation :(" + workID + ")");
  }

  private void addToPSD(int workID, float[][] surface) {
    PSDs[workID].addSurfaceSample(surface);
  }

  @Override
  public AbstractEvaluation setShowGraphics(boolean showGraphics) {
    super.setShowGraphics(showGraphics);

    return this;
  }

  @Override
  public synchronized void handleSimulationFinish(int workerID, int workID) {
    if (showGraphics && (System.currentTimeMillis() - time_last_render) > 1000.0f / FPS_GRAPHICS) {
      time_last_render = System.currentTimeMillis();
    }

    super.handleSimulationFinish(workerID, workID);
  }

}
