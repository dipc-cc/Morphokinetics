/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.evaluationFunctions;

import geneticAlgorithm.evaluationFunctions.EvaluatorType.evaluatorFlag;
import basic.io.Restart;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import graphicInterfaces.MainInterface;
import java.util.Set;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public abstract class AbstractPsdEvaluator extends AbstractEvaluator {

  private int psdSizeX;
  private int psdSizeY;
  
  private double frobeniusNormOfMatrix;
  
  protected MainInterface mainInterface;
  
  protected PsdSignature2D psd;
  protected float[][] sampledSurface;
  protected float[][] difference;

  protected int repeats;
  private float[][] experimentalPsd;
  protected int measureInterval;
  protected Population currentPopulation;
  protected int currentSimulation;
  private double expectedSimulationTime;

  protected int kmcError;
  private final Set flags;
  
  public AbstractPsdEvaluator(int repeats, int measureInterval, Set flags) {
    super();
    this.repeats = repeats;
    this.measureInterval = measureInterval;
    // If no flag is given only use the PSD to evaluate the function
    if (flags == null) {
      EvaluatorType et = new EvaluatorType();
      flags = et.getStatusFlags(1);
    }
    this.flags = flags;
    kmcError = 0;
  }
  
  public void setMainInterface(MainInterface mainInterface) {
    this.mainInterface = mainInterface;
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
  
  protected double calculateFrobeniusNormErrorMatrix(PsdSignature2D psd) {
    double error;
    double sum = 0.0f;
    // Apply the filter to smooth it
    float[][] currentPsd = psd.getPsd();
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
    
    error = Math.sqrt(sum)/frobeniusNormOfMatrix;
    return error;
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

  public void setRepeats(int repeats) {
    this.repeats = repeats;
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
      calculatePsdFromIndividual(ind); // Do KMC run and calculate its PSD
    }
    if (flags.contains(evaluatorFlag.PSD)) {
      psdError = calculateFrobeniusNormErrorMatrix(psd); // Calculate corresponding error with the reference
    }
    if (flags.contains(evaluatorFlag.TIME)) {
      timeError = Math.pow(Math.log(ind.getSimulationTime()) - expectedSimulationTime, 2) / expectedSimulationTime; // Calculate simulated time error with the reference
    }
    // There is no need to run the KMC to evaluate the hierachies
    if (flags.contains(evaluatorFlag.HIERARCHY)) {
      hierarchyError = ((AgBasicPsdEvaluator)this).calculateHierarchyErrorDiscrete(ind);
      //((AgBasicPsdEvaluator)this).calculateHierarchyErrorFrobenius(ind);
    }

    double error = psdError + timeError + hierarchyError; // Sum up all errors: Frobenius psd, time and hierarchy
    if (kmcError == -1 && runKmc) error = 1e30; // If the KMC execution did not finish properly, set huge error

    // Print to standard output, file and GUI
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
    int[] sizes = new int[2];
    sizes[0] = psdSizeX;
    sizes[1] = psdSizeY;
    restart.writeSurfaceText2D(2, sizes, difference, "difference");
    
    if (mainInterface != null) {
      mainInterface.setSimulationMesh(psd.getPsd());
      mainInterface.setSurface(sampledSurface);
      mainInterface.setDifference(difference);
      mainInterface.setError(error);
    }
    return error * wheight;
  }
   
  private void printGenes(double[] genes) {
    System.out.print("population" + getCurrentIteration() + "/individual" + currentSimulation / repeats);
    for (int i = 0; i < 6; i++) {
      System.out.print(" " + genes[i]);;
    }
    System.out.println(" ");
    currentSimulation++;
  }
    
  public void setPsdSizeX(int psdSizeX) {
    this.psdSizeX = psdSizeX;
  }

  public void setPsdSizeY(int psdSizeY) {
    this.psdSizeY = psdSizeY;
  }

  public int getPsdSizeX() {
    return psdSizeX;
  }

  public int getPsdSizeY() {
    return psdSizeY;
  }
  
  protected int getCurrentIteration() {
    if (currentPopulation != null) {
      return currentPopulation.getIterationNumber();
    } else {
      return -1;
    }
  }
}
