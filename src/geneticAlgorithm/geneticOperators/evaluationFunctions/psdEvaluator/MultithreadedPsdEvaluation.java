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

  @Override
  public double[] evaluate(Population p) {
    calculate_PSD_of_population(p);
    double[] results = calculate_difference_with_RealPSD();
    return results;
  }

  @Override
  public float[][] calculate_PSD_from_individual(Individual i) {

    Population p = new Population(1);
    p.setIndividual(i, 0);
    this.calculate_PSD_of_population(p);

    PSDs[0].applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    PSDs[0].applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    return PSDs[0].getPSD();
  }

  private double[] calculate_difference_with_RealPSD() {
    double[] results = new double[currentPopulation.size()];
    for (int i = 0; i < currentPopulation.size(); i++) {
      results[i] = evaluate_individual(i);
    }
    return results;
  }

  private double evaluate_individual(int individual_pos) {

    double error = 0;
    float[][] difference = new float[PSD_size_Y][PSD_size_X];

    PSDs[individual_pos].applySimmetryFold(PsdSignature2D.HORIZONTAL_SIMMETRY);
    PSDs[individual_pos].applySimmetryFold(PsdSignature2D.VERTICAL_SIMMETRY);

    calculateRelativeDifference(difference, PSDs[individual_pos]);

    difference = MathUtils.avg_Filter(difference, 5);

    for (int a = 0; a < PSD_size_Y; a++) {
      for (int b = 0; b < PSD_size_X; b++) {
        error += Math.abs(difference[a][b]);
      }
    }
    return error * wheight;
  }

  private void calculate_PSD_of_population(Population p) {
    PSDs = new PsdSignature2D[p.size()];

    times = new double[p.size()];
    for (int i = 0; i < p.size(); i++) {
      PSDs[i] = new PsdSignature2D(PSD_size_Y, PSD_size_X);
    }

    currentPopulation = p;
    currentSimulation = 0;
    finishedSimulation = 0;

    for (int i = 0; i < this.numThreads; i++) {
      assignNewWork(i);
    }

    try {
      evalation_complete.acquire();
    } catch (Exception e) {
    }
    store_simulation_times(p);

  }

  private void store_simulation_times(Population p) {
    for (int i = 0; i < p.size(); i++) {
      times[i] /= repeats;
      p.getIndividual(i).setSimulationTime(times[i]);
    }
  }
}
