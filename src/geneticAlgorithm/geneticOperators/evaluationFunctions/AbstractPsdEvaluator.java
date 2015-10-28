/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
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

  public AbstractPsdEvaluator setPsd(float[][] experimentalPsd) {
    this.experimentalPsd = experimentalPsd;
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
        difference[a][b] = (float) Math.pow((psd.getPsd()[a][b] - experimentalPsd[a][b]) / experimentalPsd[a][b],2);
      }
    }

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
    error = (float) Math.sqrt(error);
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
