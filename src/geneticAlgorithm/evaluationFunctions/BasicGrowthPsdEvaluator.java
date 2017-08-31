/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

import basic.io.Restart;
import geneticAlgorithm.Individual;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.growth.BasicGrowthKmc;
import ratesLibrary.AgRatesFromPrbCox;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public class BasicGrowthPsdEvaluator extends AbstractPsdEvaluator {

  private BasicGrowthKmc kmc;

  private double g4g0;
  private double g3g0;
  private double g3g1;
  private double g3g2;
  private double g3g4;
  private double g4g1;
  private double g2g1;
  private double hierarchyFrobeniusRef;
  private boolean searchEnergies;
  
  private PsdSignature2D psd;
  private int kmcError;
  
  private int simulationCount;
  private float[][] sampledSurface;
  /** Temperature of the simulation. Useful when searching for energies. */
  private final float temperature;
  
  /**
   * Current thread, responsible to run many KMCs to compute a PSD.
   */
  private Runner currentRun;
  
  public BasicGrowthPsdEvaluator(BasicGrowthKmc kmc, int repeats, int measureInterval, int psdSizeX, int psdSizeY, Set flags, String hierarchyEvaluator, String evolutionarySearchType, float temperature) {
    super(repeats, measureInterval, flags, hierarchyEvaluator);

    setPsdSizeX(psdSizeX);
    setPsdSizeY(psdSizeY);

    this.kmc = kmc;
    psd = new PsdSignature2D(psdSizeY, psdSizeX, 1);
    if (evolutionarySearchType != null) {
      searchEnergies = evolutionarySearchType.equals("energies");
    }
    sampledSurface = null;
    this.temperature = temperature;
    kmc.setTerraceToTerraceProbability(kmc.getLattice().getUc(0).getAtom(0).getProbability(0, 0));
  }
  
   /**
   * Inner class responsible to update the interface.
   */
  final class Runner extends Thread {
    private Individual ind;
    float[][] resultPsd;
    public Runner() {
      //resultPsd = new float[1][1];
    }

    public void setIndividual(Individual ind) {
      this.ind = ind;
    }
    
    public float[][] getResultPsd() {
      if (resultPsd == null) {
        return new float[getPsdSizeX()][getPsdSizeY()];
      }
      return resultPsd;
    }
    
    /**
     * Every 100 ms updates the interface with the current progress.
     */
    @Override
    public void run() {
      psd.reset();
      double time = 0.0;
      int avgSteps = 0;
      String folderName = "gaResults/population" + getCurrentIteration() + "/individual" + getIndividualCount();
      Restart restart = new Restart(folderName);
      psd.setRestart(restart);
      kmc.initialiseRates(getRates5(ind.getGenes()));
      kmcError = 0;
      for (simulationCount = 0; simulationCount < getRepeats(); simulationCount++) {
        kmc.reset();
        kmc.depositSeed();
        int max = (int) getMaxIteration();
        while (true) {
          int kmcReturn = kmc.simulate(max);
          sampledSurface = kmc.getSampledSurface(getPsdSizeY(), getPsdSizeX());
          if (kmcReturn == -1) {
            kmcError++;
          } else {
            psd.addSurfaceSample(sampledSurface);
          }
          avgSteps += kmcReturn;
          restart.writeSurfaceBinary2D(sampledSurface, simulationCount);
          if (kmcError > 2) { // Allow 3 errors or strange surfaces. Exit individual with more
            System.out.println("Skipping individual");
            kmcError = -1;
            time = Integer.MAX_VALUE;
            break;
          }
          if (kmc.getCoverage() < 0.05) {
            continue;
          }
          if (kmc.getIterations() < getMeasureInterval()) {
            time += kmc.getTime();
            break;
          }
        }
        if (kmcError == -1) {
          setCurrentSimulation(getCurrentSimulation() + getRepeats() - simulationCount);
          kmcHasFailed();
          break;
        }
        setCurrentSimulation(getCurrentSimulation() + 1);
      }
      simulationCount = getRepeats() - 1;
      avgSteps = (avgSteps / getRepeats());
      System.out.println("Average number of steps " + avgSteps);

      ind.setSimulationTime(time / getRepeats());
      psd.applySymmetryFold(PsdSignature2D.HORIZONTAL_SYMMETRY);
      psd.applySymmetryFold(PsdSignature2D.VERTICAL_SYMMETRY);
      psd.printAvgToFile();
      resultPsd = psd.getPsd();
    }
  }
  
  @Override
  public float[][] calculatePsdFromIndividual(Individual ind) {
    int timeLimit = 10000; // wait at most one minute per individual
    currentRun = new Runner();
    currentRun.setIndividual(ind);
    currentRun.start();
    try {
      currentRun.join(timeLimit);
    } catch (InterruptedException ex) {
      System.out.println("Current individual could not finish withing " + timeLimit / 1000 + "s");
      Logger.getLogger(BasicGrowthPsdEvaluator.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.out.println("Finished individual ");
    currentRun.interrupt();
    System.out.println("is alive? " + currentRun.isAlive());
    return currentRun.getResultPsd();
  }

  public void killThread() {
    currentRun.interrupt();
  }

  @Override
  public float[][] getSurface() {
    return sampledSurface;
  }
  
  /**
   * Method thought to be called from the main interface updater. 
   * @return number of the current simulation
   */
  @Override 
  public int getSimulationCount() {
    return simulationCount;
  }
  
  /**
   * Sets the expected rates hierarchies, based on the objective rates (genes). Not implemented (only a copy of Ag)
   * @param genes 
   */
  public void setHierarchy(double[] genes) {
    double sum = 0;
    g4g0 = genes[4] / genes[0];
    g3g0 = genes[3] / genes[0];
    g3g1 = genes[3] / genes[1];
    g3g2 = genes[3] / genes[2];
    g3g4 = genes[3] / genes[4];
    g4g1 = genes[4] / genes[1];
    g2g1 = genes[2] / genes[1];
    
    sum += Math.pow(g4g0, 2);
    sum += Math.pow(g3g0, 2);
    sum += Math.pow(g3g1, 2);
    sum += Math.pow(g3g2, 2);
    sum += Math.pow(g3g4, 2);
    sum += Math.pow(g4g1, 2);
    sum += Math.pow(g2g1, 2);
    
    hierarchyFrobeniusRef = Math.sqrt(sum);
  }
  
  /**
   * If the difference is lower than 1, error is not added; otherwise, 
   * error is added by one.
   * 
   * @param ind
   * @return calculated error
   */
  @Override
  protected double calculateHierarchyErrorDiscrete(Individual ind) {
    double error = 0;

    error += stepFunction(ind.getGene(4) / ind.getGene(0));
    error += stepFunction(ind.getGene(5) / ind.getGene(0));
    error += stepFunction(ind.getGene(5) / ind.getGene(4));
    error += stepFunction(ind.getGene(3) / ind.getGene(0));
    error += stepFunction(ind.getGene(3) / ind.getGene(1));
    error += stepFunction(ind.getGene(3) / ind.getGene(2));
    error += stepFunction(ind.getGene(3) / ind.getGene(4));
    error += stepFunction(ind.getGene(3) / ind.getGene(5));
    error += stepFunction(ind.getGene(4) / ind.getGene(1));
    error += stepFunction(ind.getGene(5) / ind.getGene(1));
    error += stepFunction(ind.getGene(2) / ind.getGene(1));
    error += stepFunction(ind.getGene(0) / ind.getGene(2)); // not physically meaningful
    
    return error;
  }
  
  private double stepFunction(double division) {
    if (division < 1) return 0;
    else return 1;
  }
  
  /**
   * Adds the error if the next condition is not fulfilled:
   * E_f > E_b > E_a > E_d > E_e > E_c
   * g(3) > g(5) > g(4) > g(0) > g(2) > g(1)
   * @param ind
   * @return calculated error
   */
  protected double calculateHierarchyErrorOrder(Individual ind) {
    if ( ind.getGene(3) > ind.getGene(5) &&
         ind.getGene(5) > ind.getGene(4) &&
         ind.getGene(4) > ind.getGene(0) &&
         ind.getGene(0) > ind.getGene(2) &&
         ind.getGene(2) > ind.getGene(1)) {
      return 0;
    } else {
      return 1;
    }
  }
  
  /**
   * Calculates the hierarchy error based on the rates of Cox et al. 
   * @param ind Current individual
   * @return normalised hierarchy error
   */
  @Override
  protected double calculateHierarchyErrorFromReference(Individual ind) {
    double error = 0;
    error += Math.sqrt(Math.pow(ind.getGene(4)/ind.getGene(0) - g4g0, 2))/g4g0;
    error += Math.sqrt(Math.pow(ind.getGene(3)/ind.getGene(0) - g3g0, 2))/g3g0;
    error += Math.sqrt(Math.pow(ind.getGene(3)/ind.getGene(1) - g3g1, 2))/g3g1;
    error += Math.sqrt(Math.pow(ind.getGene(3)/ind.getGene(2) - g3g2, 2))/g3g2;
    error += Math.sqrt(Math.pow(ind.getGene(3)/ind.getGene(4) - g3g4, 2))/g3g4;
    error += Math.sqrt(Math.pow(ind.getGene(4)/ind.getGene(1) - g4g1, 2))/g4g1;
    error += Math.sqrt(Math.pow(ind.getGene(2)/ind.getGene(1) - g2g1, 2))/g2g1;
    
    return error;
  }  
  
  protected double calculateHierarchyErrorFrobenius(Individual ind) {
    double error;
    double sum = 0;
    sum += Math.sqrt(Math.pow(ind.getGene(4) / ind.getGene(0) - g4g0, 2));
    sum += Math.sqrt(Math.pow(ind.getGene(3) / ind.getGene(0) - g3g0, 2));
    sum += Math.sqrt(Math.pow(ind.getGene(3) / ind.getGene(1) - g3g1, 2));
    sum += Math.sqrt(Math.pow(ind.getGene(3) / ind.getGene(2) - g3g2, 2));
    sum += Math.sqrt(Math.pow(ind.getGene(3) / ind.getGene(4) - g3g4, 2));
    sum += Math.sqrt(Math.pow(ind.getGene(4) / ind.getGene(1) - g4g1, 2));
    sum += Math.sqrt(Math.pow(ind.getGene(2) / ind.getGene(1) - g2g1, 2));

    error = Math.sqrt(sum) / hierarchyFrobeniusRef;
    return error;
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
  
  @Override
  public void dispose() {
    psd = null;
    kmc = null;
  }
  
  /**
   * Calculates rates from the genes. Some of the rates are 0, the rest is calculated from the given
   * genes.
   *
   * Ratio (energy type) | ratio index
   * 0) E_d                    (0,j) Terrace to any 
   * 1) E_c                    (1,0) Edge to terrace
   * 2) E_f                    (1,1) Edge to edge
   * 3) E_a                    (1,2)=(1,3) Edge to kink or island
   * 4) E_b                    (2,1)=(2,2)=(2,3) Kink to any (but terrace)
   * @param genes
   * @return rates
   */
  public double[] getRates5(double[] genes) {   
    double[] rates = new double[16];

    for (int i = 0; i < rates.length; i++) {
      rates[i] = 0; // All rates to 0
    }
    for (int i = 0; i < 4; i++) {
      rates[i] = getRate(genes[0]); // Terrace to any E_d
    }

    rates[1 * 4 + 0] = getRate(genes[1]); // E_c
    rates[1 * 4 + 1] = getRate(genes[2]); // E_f
    rates[1 * 4 + 2] = getRate(genes[3]); // E_a
    rates[1 * 4 + 3] = getRate(genes[3]); // E_a
    
    rates[1 * 4 + 1] = getRate(genes[4]); // E_b
    rates[1 * 4 + 2] = getRate(genes[4]); // E_b
    rates[1 * 4 + 3] = getRate(genes[4]); // E_b
    return rates;
  }
  
  private double getRate(double gene) {
    if (searchEnergies) {
      return AgRatesFromPrbCox.getRate(temperature, gene);
    }
    return gene;
  }
  
  private double getMaxIteration() {
    if (temperature == 120) 
      return 1e10;
    return 1e10; 
  }
}
