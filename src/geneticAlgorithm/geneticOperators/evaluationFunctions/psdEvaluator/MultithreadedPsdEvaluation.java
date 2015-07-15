/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator;

import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import kineticMonteCarlo.kmcCore.worker.IIntervalListener;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import java.util.concurrent.Semaphore;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public abstract class MultithreadedPsdEvaluation extends AbstractPSDEvaluation implements IFinishListener, IIntervalListener {

  protected static final int FPS_GRAPHICS = 2;

  protected int PSD_size_X;
  protected int PSD_size_Y;
  protected PsdSignature2D[] PSDs;
  protected double[] times;
  protected long time_last_render;

  protected KmcWorker[] workers;
  protected int numThreads;
  protected int finishedSimulation;
  protected Semaphore evalation_complete;

  public MultithreadedPsdEvaluation(int repeats, int measureInterval, int num_threads) {
    super(repeats, measureInterval);

    this.workers = new KmcWorker[num_threads];
    this.numThreads = num_threads;
    evalation_complete = new Semaphore(0);
  }

  @Override
  public synchronized void handleSimulationFinish(int workerID, int workID) {

    finishedSimulation++;
    if (currentSimulation < currentPopulation.size() * repeats) {
      assignNewWork(workerID);
    }

    if (finishedSimulation == currentPopulation.size() * repeats) {
      evalation_complete.release();
    }
  }

  protected void assignNewWork(int workerID) {

    int individual = currentSimulation / repeats;

    workers[workerID].initialize(currentPopulation.getIndividual(individual).getGenes());
    workers[workerID].simulate(this, this, measureInterval, individual);
    currentSimulation++;
  }

  @Override
  public void dispose() {

    for (int i = 0; i < workers.length; i++) {
      workers[i].destroy();
    }

  }

}
