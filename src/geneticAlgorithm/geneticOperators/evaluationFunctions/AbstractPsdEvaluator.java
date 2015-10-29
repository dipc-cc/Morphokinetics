/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import basic.io.Restart;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import sun.misc.MetaIndex;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public abstract class AbstractPsdEvaluator extends AbstractEvaluator {

  private int psdSizeX;
  private int psdSizeY;
  
  private float oneNormOfVector;
  private float twoNormOfVector;
  private float infiniteNormOfVector;
  private float oneNormOfMatrix;
  private float infiniteNormOfMatrix;
  private float frobeniusNormOfMatrix;
  
  protected PsdSignature2D psd;
  protected float[][] sampledSurface;
  protected float[][] difference;

  protected int repeats;
  protected float[][] experimentalPsd;
  protected int measureInterval;
  protected Population currentPopulation;
  protected int currentSimulation;

  public AbstractPsdEvaluator(int repeats, int measureInterval) {
    super();
    this.repeats = repeats;
    this.measureInterval = measureInterval;
  }

  /**
   * Set the reference PSD matrix for future comparisons and starts its norms.
   * @param experimentalPsd
   * @return 
   */
  public AbstractPsdEvaluator setPsd(float[][] experimentalPsd) {
    this.experimentalPsd = experimentalPsd;
    System.out.println("Setting experimental PSD");
    oneNormOfVector = calculateOneNormVector(experimentalPsd);
    twoNormOfVector = calculateTwoNormVector(experimentalPsd);
    infiniteNormOfVector = calculateInfiniteNormVector(experimentalPsd);
    
    oneNormOfMatrix = calculateOneNormMatrix(experimentalPsd);
    infiniteNormOfMatrix = calculateInfiniteNormMatrix(experimentalPsd);
    frobeniusNormOfMatrix = calculateFrobeniusNorm(experimentalPsd);
    
    return this;
  }
  
  /**
   * ||x||_1 = SUM_i(|x_i|)
   * @param vector
   * @return 
   */
  private float calculateOneNormVector(float[][] vector) {
    float result = 0.0f;
    for (int i = 0; i < vector.length; i++) {
      for (int j = 0; j < vector[0].length; j++) {
        result += Math.abs(vector[i][j]);
      }
    }
    return result;
  }
  
  /**
   * ||x||_2 = (SUM_i(|x_i|2))^1/2
   * @param vector
   * @return 
   */
  private float calculateTwoNormVector(float[][] vector) {
    float result = 0.0f;
    for (int i = 0; i < vector.length; i++) {
      for (int j = 0; j < vector[0].length; j++) {
        result += Math.pow(vector[i][j],2);
      }
    }
    result = (float) Math.sqrt(result);
    return result;
  }

  /**
   * ||x||_inf = max_i |x_i|
   * @param vector
   * @return 
   */
  private float calculateInfiniteNormVector(float[][] vector) {
    float result = 0.0f;
    for (int i = 0; i < vector.length; i++) {
      for (int j = 0; j < vector[0].length; j++) {
        if (Math.abs(vector[i][j]) > result) {
          result = Math.abs(vector[i][j]);
        }
      }
    }
    return result;
  }

  /**
   * It is simply the maximum absolute column sum of the matrix.
   * ||A||_1 = max_j SUM_i(|a_ij|)
   * @param matrix
   * @return 
   */
  private float calculateOneNormMatrix(float[][] matrix) {
    float result = 0.0f;
    for (int j = 0; j < matrix[0].length; j++) {
      float tmp = 0.0f;
      for (int i = 0; i < matrix.length; i++) {
        tmp += Math.abs(matrix[i][j]);
      }
      if (tmp > result) result = tmp;
    }
    return result;
  }
   
  /**
   * It is simply the maximum absolute row sum of the matrix.
   * ||A||_1 = max_j SUM_i(|a_ij|)
   * @param matrix
   * @return 
   */
  private float calculateInfiniteNormMatrix(float[][] matrix) {
    float result = 0.0f;
    for (int i = 0; i < matrix.length; i++) {
      float tmp = 0.0f;
      for (int j = 0; j < matrix[0].length; j++) {
        tmp += Math.abs(matrix[i][j]);
      }
      if (tmp > result) result = tmp;
    }
    return result;
  }
  
  /**
   * ||A||_F = (SUM(|A_ij|^2))^1/2
   * @param matrix
   * @return 
   */
  private float calculateFrobeniusNorm(float[][] matrix) {
    float result = 0.0f;
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        result += Math.pow(matrix[i][j],2);
      }
    }
    result = (float) Math.sqrt(result);
    return result;
  }
    

  public abstract float[][] calculatePsdFromIndividual(Individual i);

  /*protected void calculateRelativeDifference(float[][] difference, PsdSignature2D psd) {
    for (int a = 0; a < difference.length; a++) {
      for (int b = 0; b < difference[0].length; b++) {
        difference[a][b] = (psd.getPsd()[a][b] - experimentalPsd[a][b]) / Math.min(experimentalPsd[a][b], psd.getPsd()[a][b]);
      }
    }

  }*/
  
  /**
   * This is the first step of Frobenius norm. It is missing the addition and the square root.
   * (http://www.netlib.org/lapack/lug/node75.html).
   * @param difference
   * @param psd 
   */
  protected void calculateRelativeDifference(float[][] difference, PsdSignature2D psd) {
    for (int a = 0; a < difference.length; a++) {
      for (int b = 0; b < difference[0].length; b++) {
        difference[a][b] = (float) Math.sqrt(Math.pow((psd.getPsd()[a][b] - experimentalPsd[a][b]) / experimentalPsd[a][b],2));
      }
    }
  }

  private float calculateOneNormErrorVector(PsdSignature2D psd) {
   float error;
   float sum = 0.0f;
    for (int i = 0; i < psdSizeX; i++) {
      for (int j = 0; j < psdSizeY; j++) {
        sum += Math.abs(psd.getPsd()[i][j] - experimentalPsd[i][j]);
      }
    }
   error = sum/oneNormOfVector;
   return error;
  }  
  
  private float calculateTwoNormErrorVector(PsdSignature2D psd) {
   float error;
   float sum = 0.0f;
    for (int i = 0; i < psdSizeX; i++) {
      for (int j = 0; j < psdSizeY; j++) {
        sum += Math.pow(psd.getPsd()[i][j] - experimentalPsd[i][j],2);
      }
    }
   error = (float)Math.sqrt(sum)/twoNormOfVector;
   return error;
  }
    
  private float calculateInfiniteNormErrorVector(PsdSignature2D psd) {
   float error;
   float max = 0.0f;
    for (int i = 0; i < psdSizeX; i++) {
      for (int j = 0; j < psdSizeY; j++) {
        if (Math.abs(psd.getPsd()[i][j] - experimentalPsd[i][j]) > max) {
          max = Math.abs(psd.getPsd()[i][j] - experimentalPsd[i][j]);
        }
      }
    }
   error = max/infiniteNormOfVector;
   return error;
  }

  private float calculateOneNormErrorMatrix(PsdSignature2D psd) {
    float error;
    float max = 0.0f;
    for (int j = 0; j < psdSizeY; j++) {
      float tmp = 0.0f;
      for (int i = 0; i < psdSizeX; i++) {
        tmp += Math.abs(psd.getPsd()[i][j] - experimentalPsd[i][j]);
      }
      if (tmp > max) max = tmp;
    }
    error = max / oneNormOfMatrix;
    return error;
  }
  
  private float calculateInfiniteNormErrorMatrix(PsdSignature2D psd) {
    float error;
    float max = 0.0f;
    for (int i = 0; i < psdSizeX; i++) {
      float tmp = 0.0f;
      for (int j = 0; j < psdSizeY; j++) {
        tmp += Math.abs(psd.getPsd()[i][j] - experimentalPsd[i][j]);
      }
      if (tmp > max) max = tmp;
    }
    error = max / infiniteNormOfMatrix;
    return error;
  }
  
  private float calculatefrobeniusNormErrorMatrix(PsdSignature2D psd) {
    float error;
    float sum = 0.0f;
    for (int i = 0; i < psdSizeX; i++) {
      for (int j = 0; j < psdSizeY; j++) {
        sum += Math.pow(psd.getPsd()[i][j] - experimentalPsd[i][j],2);
      }
    }
    error = (float) Math.sqrt(sum)/frobeniusNormOfMatrix;
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
    calculatePsdFromIndividual(ind);
    calculateRelativeDifference(difference, psd);

    difference = MathUtils.avgFilter(difference, 5);
    double error = 0;
    for (int a = 0; a < psdSizeX; a++) {
      for (int b = 0; b < psdSizeY; b++) {
        error += Math.abs(difference[a][b]);
      }
    }
    System.out.println(" errors: "+calculateOneNormErrorVector(psd)+"\t"+calculateTwoNormErrorVector(psd)+"\t"+calculateInfiniteNormErrorVector(psd)+"\t"+
            calculateOneNormErrorMatrix(psd)+"\t"+calculateInfiniteNormErrorMatrix(psd)+"\t"+calculatefrobeniusNormErrorMatrix(psd)+"\t"+error);
    String errors = " Errors: OneNormVector\tTwoNormVector\tInfiniteNormVector\tOneNormMatrix\tInfiniteNormMatrix\tFrobeniusNormMatrix\toldError\n";
    errors = errors+"\t"+calculateOneNormErrorVector(psd)+"\t"+calculateTwoNormErrorVector(psd)+"\t"+calculateInfiniteNormErrorVector(psd)+"\t"+
            calculateOneNormErrorMatrix(psd)+"\t"+calculateInfiniteNormErrorMatrix(psd)+"\t"+calculatefrobeniusNormErrorMatrix(psd)+"\t"+error;
    String folderName = "gaResults/population"+currentPopulation.getIterationNumber()+"/individual"+((currentSimulation-repeats)/repeats);
    Restart restart = new Restart(folderName);
    String fileName = "errors.txt";
    restart.writeTextString(errors, fileName);
    return error * wheight;
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
}
