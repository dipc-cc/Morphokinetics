/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
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
 * @author N. Ferrando, J. Alberdi-Rodriguez
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

    psds[0].applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
    psds[0].applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);

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
    psds[individualPos].applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
    psds[individualPos].applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);

    double error = calculateFrobeniusNormErrorMatrix(psds[individualPos].getPsd());
    return error * getWheight();
  }

  private void calculatePsdOfPopulation(Population p) {
    psds = new PsdSignature2D[p.size()];

    times = new double[p.size()];
    for (int i = 0; i < p.size(); i++) {
      psds[i] = new PsdSignature2D(getPsdSizeY(), getPsdSizeX(), 1);
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
