/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator;

import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractEvaluation;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import utils.MathUtils;
import utils.psdAnalysis.PsdSignature2D;

/**
 *
 * @author Nestor
 */
public abstract class AbstractPsdEvaluation extends AbstractEvaluation {

  protected int psdSizeX;
  protected int psdSizeY;
  protected PsdSignature2D psd;
  protected float[][] sampledSurface;
  protected float[][] difference;

  protected int repeats;
  protected float[][] experimentalPsd;
  protected int measureInterval;
  protected Population currentPopulation;
  protected int currentSimulation;

  public AbstractPsdEvaluation(int repeats, int measureInterval) {
    super();
    this.repeats = repeats;
    this.measureInterval = measureInterval;

  }

  public AbstractPsdEvaluation setPsd(float[][] ExperimentalPSD) {
    this.experimentalPsd = ExperimentalPSD;
    return this;
  }

  public abstract float[][] calculatePsdFromIndividual(Individual i);

  protected void calculateRelativeDifference(float[][] difference, PsdSignature2D psd) {

    for (int a = 0; a < difference.length; a++) {
      for (int b = 0; b < difference[0].length; b++) {

        difference[a][b] = (psd.getPsd()[a][b] - experimentalPsd[a][b]) / Math.min(experimentalPsd[a][b], psd.getPsd()[a][b]);
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
    return error * wheight;
  }
}
