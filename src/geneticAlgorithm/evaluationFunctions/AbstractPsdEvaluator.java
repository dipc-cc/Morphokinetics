/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

import geneticAlgorithm.evaluationFunctions.EvaluatorType.evaluatorFlag;
import basic.io.Restart;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import java.util.Set;

/**
 *
 * @author Nestor
 */
public abstract class AbstractPsdEvaluator extends AbstractEvaluator {

  private int psdSizeX;
  private int psdSizeY;
  
  private double frobeniusNormOfMatrix;

  private float[][] difference;

  private int repeats;
  private float[][] experimentalPsd;
  private final int measureInterval;

  private Population currentPopulation;

  private int currentSimulation;
  private double expectedSimulationTime;
  private double error;
  private float[][] psd;
  
  private final Set flags;
  private final String hierarchyEvaluator;
  private boolean kmcError;
  
  /**
   *
   * @param repeats
   * @param measureInterval
   * @param flags
   * @param hierarchyEvaluator either "basic", "step", "reference" or "Frobenius". It is safe to
   * assign null. It will be used only if the hierarchy flag is enabled
   */
  public AbstractPsdEvaluator(int repeats, int measureInterval, Set flags, String hierarchyEvaluator) {
    super();
    this.repeats = repeats;
    this.measureInterval = measureInterval;
    // If no flag is given only use the PSD to evaluate the function
    if (flags == null) {
      EvaluatorType et = new EvaluatorType();
      flags = et.getStatusFlags(1);
    }
    if (hierarchyEvaluator == null) {
      hierarchyEvaluator = "";
    }
    this.flags = flags;
    this.hierarchyEvaluator = hierarchyEvaluator;
    this.kmcError = false;
    this.psd = null;
  }
  
  /**
   * Set the reference PSD matrix for future comparisons and starts its norms.
   * @param experimentalPsd
   * @return 
   */
  public AbstractPsdEvaluator setPsd(float[][] experimentalPsd) {
    this.experimentalPsd = experimentalPsd;
    for (int i = 0; i < experimentalPsd.length; i++) {
      for (int j = 0; j < experimentalPsd[0].length; j++) {
        this.experimentalPsd[i][j] = (float)Math.log(experimentalPsd[i][j]);        
      }
    }
    frobeniusNormOfMatrix = calculateFrobeniusNorm(experimentalPsd);
    
    return this;
  }
  
  public void setExpectedSimulationTime(double expectedSimulationTime) {
    this.expectedSimulationTime = Math.log(expectedSimulationTime);
  }
  
  /**
   * ||A||_F = (SUM(|A_ij|^2))^1/2
   * @param matrix
   * @return 
   */
  private double calculateFrobeniusNorm(float[][] matrix) {
    double result = 0.0f;
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        result += Math.pow(matrix[i][j],2);
      }
    }
    result = Math.sqrt(result);
    return result;
  }
    

  public abstract float[][] calculatePsdFromIndividual(Individual i);
  
  protected abstract double calculateHierarchyError(Individual i);
  
  protected abstract double calculateHierarchyErrorFromReference(Individual i);
  protected abstract double calculateHierarchyErrorDiscrete(Individual ind);
  
  protected double calculateFrobeniusNormErrorMatrix(float[][] currentPsd) {
    double FrobeniusError;
    double sum = 0.0f;
    difference = new float[psdSizeX][psdSizeY];
    // Apply the filter to smooth it
    for (int i = 0; i < psdSizeX; i++) {
      for (int j = 0; j < psdSizeY; j++) {
        // Apply the log_e and calculate the difference
        difference[i][j] = (float) Math.pow(Math.log(currentPsd[i][j]) - experimentalPsd[i][j],2);
      }
    }
    
    for (int i = 0; i < psdSizeX; i++) {
      for (int j = 0; j < psdSizeY; j++) {
        sum += difference[i][j];
      }
    }
    
    FrobeniusError = Math.sqrt(sum)/frobeniusNormOfMatrix;
    return FrobeniusError;
  }
  
  @Override
  public float getProgressPercent() {
    if (currentPopulation != null) {
      return currentSimulation * 100.0f / (repeats * currentPopulation.size());
    } else {
      return 0;
    }
  }

  public int getRepeats() {
    return repeats;
  }

  public int getMeasureInterval() {
    return measureInterval;
  }
  
  public void setRepeats(int repeats) {
    this.repeats = repeats;
  } 
  
  public Population getCurrentPopulation() {
    return currentPopulation;
  }

  public void setCurrentPopulation(Population currentPopulation) {
    this.currentPopulation = currentPopulation;
  }

  public int getCurrentSimulation() {
    return currentSimulation;
  }

  public void setCurrentSimulation(int currentSimulation) {
    this.currentSimulation = currentSimulation;
  }

  public void resetKmcError() {
    this.kmcError = false;
  }
  
  public void kmcHasFailed() {
    this.kmcError = true;
  }
  
  public boolean hasKmcFailed() {
    return kmcError;
  }

  @Override
  public double getCurrentError() {
    return error;
  }
  
  @Override
  public float[][] getCurrentPsd() {
    return psd;
  }
  
  @Override
  public float[][] getCurrentDifference() {
    return difference;
  }
  
  @Override
  public double[] evaluate(Population p) {
    this.currentPopulation = p;
    this.currentSimulation = 0;
    double[] results = new double[p.size()];

    for (int i = 0; i < p.size(); i++) {
      results[i] = evaluateIndividual(p.getIndividual(i));
    }

    return results;
  }

  private double evaluateIndividual(Individual ind) {
    printGenes(ind.getGenes());
    double psdError = 0;
    double timeError = 0;
    double hierarchyError = 0;
    boolean runKmc = flags.contains(evaluatorFlag.PSD) || flags.contains(evaluatorFlag.TIME);
    if (runKmc) {
      resetKmcError();
      psd = calculatePsdFromIndividual(ind); // Do KMC run and calculate its PSD
    }
    if (flags.contains(evaluatorFlag.PSD)) {
      psdError = calculateFrobeniusNormErrorMatrix(psd); // Calculate corresponding error with the reference
    }
    if (flags.contains(evaluatorFlag.TIME)) {
      timeError = Math.pow(Math.log(ind.getSimulationTime()) - expectedSimulationTime, 2) / expectedSimulationTime; // Calculate simulated time error with the reference
    }
    // There is no need to run the KMC to evaluate the hierachies
    if (flags.contains(evaluatorFlag.HIERARCHY)) {
      if (hierarchyEvaluator.equals("basic")) {
        hierarchyError = ((AgBasicPsdEvaluator)this).calculateHierarchyError(ind);
      }
      if (hierarchyEvaluator.equals("step")) {
        hierarchyError = ((AgBasicPsdEvaluator)this).calculateHierarchyErrorDiscrete(ind);
      }
      if (hierarchyEvaluator.equals("order")) {
        System.out.println("Order evaluator");
        hierarchyError = ((AgBasicPsdEvaluator)this).calculateHierarchyErrorOrder(ind);
      }
      
      if (hierarchyEvaluator.equals("reference")) {
        hierarchyError = ((AgBasicPsdEvaluator)this).calculateHierarchyErrorFromReference(ind);
      }
      if (hierarchyEvaluator.equals("Frobenius")) {
        hierarchyError = ((AgBasicPsdEvaluator)this).calculateHierarchyErrorFrobenius(ind);
      }
    }

    error = psdError + timeError + hierarchyError; // Sum up all errors: Frobenius psd, time and hierarchy
    if (kmcError && runKmc) error = 1e30; // If the KMC execution did not finish properly, set huge error

    // Print to standard output and file
    System.out.println("  errors: "+"\t"+psdError+"\t"+timeError+"\t"+hierarchyError+"\t"+error);
    System.out.println("  simul. time: "+ind.getSimulationTime()+"\t("+expectedSimulationTime+")");
    String errors = " Errors: FrobeniusNormMatrix\ttimeError\tHierarchy\tError\n";
    errors = errors+"\t"+psdError+"\t"+timeError+"\t"+hierarchyError+"\t"+error;
    String folderName = "gaResults/population"+currentPopulation.getIterationNumber()+"/individual"+((currentSimulation-repeats)/repeats);
    Restart restart = new Restart(folderName);
    String fileName = "errors.txt";
    restart.writeTextString(errors, fileName);
    fileName = "genes.txt";
    String genes = "";
    for (int i = 0; i < 6; i++) {
      genes += "\t"+ind.getGene(i);
    }
    restart.writeTextString(genes, fileName);
    if (runKmc) {
      int[] sizes = new int[2];
      sizes[0] = psdSizeX;
      sizes[1] = psdSizeY;
      restart.writeSurfaceText2D(2, sizes, difference, "difference");
    }
    
    return error * getWheight();
  }
   
  private void printGenes(double[] genes) {
    System.out.print("population" + getCurrentIteration() + "/individual" + currentSimulation / repeats);
    for (int i = 0; i < 6; i++) {
      System.out.print(" " + genes[i]);;
    }
    System.out.println(" ");
  }
    
  public final void setPsdSizeX(int psdSizeX) {
    this.psdSizeX = psdSizeX;
  }

  public final void setPsdSizeY(int psdSizeY) {
    this.psdSizeY = psdSizeY;
  }

  public final int getPsdSizeX() {
    return psdSizeX;
  }

  public final int getPsdSizeY() {
    return psdSizeY;
  }
  
  protected int getCurrentIteration() {
    if (currentPopulation != null) {
      return currentPopulation.getIterationNumber();
    } else {
      return -1;
    }
  }
  
  /**
   * Undefined here
   * @return -1
   */
  @Override
  public int getSimulationCount() {
    return -1;
  }
  
  /**
   * Calculates the count (number) of the current simulation
   * @return 
   */
  @Override
  public int getIndividualCount() {
    return getCurrentSimulation() / getRepeats();
  }
}
