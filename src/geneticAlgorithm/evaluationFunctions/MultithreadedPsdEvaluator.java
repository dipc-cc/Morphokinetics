/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import java.util.Set;
import kineticMonteCarlo.kmcCore.worker.IFinishListener;
import kineticMonteCarlo.kmcCore.worker.IIntervalListener;
import kineticMonteCarlo.kmcCore.worker.KmcWorker;
import java.util.concurrent.Semaphore;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public abstract class MultithreadedPsdEvaluator extends AbstractPsdEvaluator implements IFinishListener, IIntervalListener {

  protected static final int FPS_GRAPHICS = 2;

  private PsdSignature2D[] psds; // TODO joseba might not be initialised
  private double[] times;

  protected KmcWorker[] workers;
  private final int numThreads;
  private int finishedSimulation;
  private final Semaphore evalationComplete;

  public MultithreadedPsdEvaluator(int repeats, int measureInterval, int numThreads, Set flags) {
    super(repeats, measureInterval, flags, null);

    workers = new KmcWorker[numThreads];
    this.numThreads = numThreads;
    evalationComplete = new Semaphore(0);
  }

  @Override
  public synchronized void handleSimulationFinish(int workerID, int workID) {

    finishedSimulation++;
    if (getCurrentSimulation() < getCurrentPopulation().size() * getRepeats()) {
      assignNewWork(workerID);
    }

    if (finishedSimulation == getCurrentPopulation().size() * getRepeats()) {
      evalationComplete.release();
    }
  }

  @Override
  public void handleSimulationIntervalFinish(int workerId, int workId) {

    float[][] surface = workers[workerId].getSampledSurface(getPsdSizeY(), getPsdSizeX());
    times[workId] += workers[workerId].getKmc().getTime();
    addToPsd(workId, surface);

  }

  private void addToPsd(int workId, float[][] surface) {
    psds[workId].addSurfaceSample(surface);
  }

  private void assignNewWork(int workerId) {

    int individual = getCurrentSimulation() / getRepeats();

    workers[workerId].initialise(getCurrentPopulation().getIndividual(individual).getGenes());
    workers[workerId].simulate(this, this, getMeasureInterval(), individual);
    setCurrentSimulation(getCurrentSimulation() + 1);
  }

  @Override
  public void dispose() {
    for (int i = 0; i < workers.length; i++) {
      workers[i].destroyWorker();
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
    calculatePsdOfPopulation(p);

    psds[0].applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    psds[0].applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    return psds[0].getPsd();
  }

  private double[] calculateDifferenceWithRealPsd() {
    double[] results = new double[getCurrentPopulation().size()];
    for (int i = 0; i < getCurrentPopulation().size(); i++) {
      results[i] = evaluateIndividual(i);
    }
    return results;
  }

  private double evaluateIndividual(int individualPos) {
    psds[individualPos].applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    psds[individualPos].applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    double error = calculateFrobeniusNormErrorMatrix(psds[individualPos].getPsd());
    return error * getWheight();
  }

  private void calculatePsdOfPopulation(Population p) {
    psds = new PsdSignature2D[p.size()];

    times = new double[p.size()];
    for (int i = 0; i < p.size(); i++) {
      psds[i] = new PsdSignature2D(getPsdSizeY(), getPsdSizeX());
    }

    setCurrentPopulation(p);
    setCurrentSimulation(0);
    finishedSimulation = 0;

    for (int i = 0; i < numThreads; i++) {
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
      times[i] /= getRepeats();
      p.getIndividual(i).setSimulationTime(times[i]);
    }
  }
}
