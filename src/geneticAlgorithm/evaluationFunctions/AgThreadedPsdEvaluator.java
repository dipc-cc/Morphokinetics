/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

import geneticAlgorithm.Individual;
import graphicInterfaces.growth.KmcCanvas;
import graphicInterfaces.growth.GrowthKmcFrame;
import kineticMonteCarlo.kmcCore.growth.AgKmc;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import kineticMonteCarlo.kmcCore.worker.IIntervalListener;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;

/**
 *
 * @author Nestor
 */
public class AgThreadedPsdEvaluator extends MultithreadedPsdEvaluator implements IFinishListener, IIntervalListener {

  public AgThreadedPsdEvaluator(AgKmc kmc, int repeats, int measureInterval, int numThreads, int psdSizeX, int psdSizeY) {

    super(repeats, measureInterval, numThreads);

    for (int i = 0; i < numThreads; i++) {
      GrowthKmcFrame frame = createGraphicsFrame(kmc);
      frame.setVisible(true);

      workers[i] = new KmcWorker(kmc, i);
      workers[i].start();
    }

    setPsdSizeX(psdSizeX);
    setPsdSizeY(psdSizeY);
  }

  private static GrowthKmcFrame createGraphicsFrame(AgKmc kmc) {
    GrowthKmcFrame frame = new GrowthKmcFrame(new KmcCanvas((AbstractGrowthLattice) kmc.getLattice()));
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

  /**
   * Calculates the hierarchy error based on the rates of Cox et al. 
   * @param ind Current individual
   * @return hierarchy error, at least 6.52e-2
   */
  @Override
  protected double calculateHierarchyError(Individual ind) {
    double error = 0;
    error += ind.getGene(4)/ind.getGene(0);
    error += ind.getGene(5)/ind.getGene(0);
    
    error += ind.getGene(5)/ind.getGene(4);
    
    error += ind.getGene(3)/ind.getGene(0);
    error += ind.getGene(3)/ind.getGene(1);
    error += ind.getGene(3)/ind.getGene(2);
    error += ind.getGene(3)/ind.getGene(4);
    error += ind.getGene(3)/ind.getGene(5);
    
    error += ind.getGene(4)/ind.getGene(1);
    error += ind.getGene(5)/ind.getGene(1);
    
    error += ind.getGene(2)/ind.getGene(1);
    
    return error;
  }

}
