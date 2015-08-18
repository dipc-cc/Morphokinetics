/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import kineticMonteCarlo.kmcCore.worker.IIntervalListener;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import java.util.concurrent.Semaphore;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public abstract class MultithreadedPsdEvaluation extends AbstractPsdEvaluation implements IFinishListener, IIntervalListener {

  protected static final int FPS_GRAPHICS = 2;

  protected PsdSignature2D[] psds; // TODO joseba might not be initialized
  protected double[] times;
  protected long timeLastRender;

  protected KmcWorker[] workers;
  protected int numThreads;
  protected int finishedSimulation;
  protected Semaphore evalationComplete;

  public MultithreadedPsdEvaluation(int repeats, int measureInterval, int numThreads) {
    super(repeats, measureInterval);

    this.workers = new KmcWorker[numThreads];
    this.numThreads = numThreads;
    evalationComplete = new Semaphore(0);
  }

  @Override
  public synchronized void handleSimulationFinish(int workerID, int workID) {

    finishedSimulation++;
    if (currentSimulation < currentPopulation.size() * repeats) {
      assignNewWork(workerID);
    }

    if (finishedSimulation == currentPopulation.size() * repeats) {
      evalationComplete.release();
    }
  }

  @Override
  public void handleSimulationIntervalFinish(int workerId, int workId) {

    float[][] surface = workers[workerId].getSampledSurface(psdSizeY, psdSizeX);
    times[workId] += workers[workerId].getKmc().getTime();
    addToPsd(workId, surface);

  }

  private void addToPsd(int workId, float[][] surface) {
    psds[workId].addSurfaceSample(surface);
  }

  protected void assignNewWork(int workerId) {

    int individual = currentSimulation / repeats;

    workers[workerId].initialize(currentPopulation.getIndividual(individual).getGenes());
    workers[workerId].simulate(this, this, measureInterval, individual);
    currentSimulation++;
  }

  @Override
  public void dispose() {

    for (int i = 0; i < workers.length; i++) {
      workers[i].destroy();
    }

  }

  @Override
  public double[] evaluate(Population p) {
    calculatePsdOfPopulation(p);
    double[] results = calculateDifferenceWithRealPsd();
    return results;
  }

  @Override
  public float[][] calculatePsdFromIndividual(Individual i) {

    Population p = new Population(1);
    p.setIndividual(i, 0);
    this.calculatePsdOfPopulation(p);

    psds[0].applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    psds[0].applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    return psds[0].getPsd();
  }

  private double[] calculateDifferenceWithRealPsd() {
    double[] results = new double[currentPopulation.size()];
    for (int i = 0; i < currentPopulation.size(); i++) {
      results[i] = evaluateIndividual(i);
    }
    return results;
  }

  private double evaluateIndividual(int individualPos) {

    double error = 0;
    float[][] difference = new float[psdSizeY][psdSizeX];

    psds[individualPos].applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    psds[individualPos].applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    calculateRelativeDifference(difference, psds[individualPos]);

    difference = MathUtils.avgFilter(difference, 5);

    for (int a = 0; a < psdSizeY; a++) {
      for (int b = 0; b < psdSizeX; b++) {
        error += Math.abs(difference[a][b]);
      }
    }
    return error * wheight;
  }

  private void calculatePsdOfPopulation(Population p) {
    psds = new PsdSignature2D[p.size()];

    times = new double[p.size()];
    for (int i = 0; i < p.size(); i++) {
      psds[i] = new PsdSignature2D(psdSizeY, psdSizeX);
    }

    currentPopulation = p;
    currentSimulation = 0;
    finishedSimulation = 0;

    for (int i = 0; i < this.numThreads; i++) {
      assignNewWork(i);
    }

    try {
      evalationComplete.acquire();
    } catch (Exception e) {
    }
    storeSimulationTimes(p);

  }

  private void storeSimulationTimes(Population p) {
    for (int i = 0; i < p.size(); i++) {
      times[i] /= repeats;
      p.getIndividual(i).setSimulationTime(times[i]);
    }
  }
}
