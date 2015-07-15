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

  protected int PSD_size_X;
  protected int PSD_size_Y;
  protected PsdSignature2D PSD ;
  protected float[][] sampledSurface ;
  protected float[][] difference;
  
  protected int repeats;
  protected float[][] experimentalPSD;
  protected int measureInterval;
  protected Population currentPopulation;
  protected int currentSimulation;

  public AbstractPsdEvaluation(int repeats, int measureInterval) {
    super();
    this.repeats = repeats;
    this.measureInterval = measureInterval;

  }

  public AbstractPsdEvaluation setPSD(float[][] ExperimentalPSD) {
    this.experimentalPSD = ExperimentalPSD;
    return this;
  }

  public abstract float[][] calculate_PSD_from_individual(Individual i);

  protected void calculateRelativeDifference(float[][] difference, PsdSignature2D PSD) {

    for (int a = 0; a < difference.length; a++) {
      for (int b = 0; b < difference[0].length; b++) {

        difference[a][b] = (PSD.getPSD()[a][b] - experimentalPSD[a][b]) / Math.min(experimentalPSD[a][b], PSD.getPSD()[a][b]);
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
            results[i] = evaluate_individual(p.getIndividual(i));
           
        }

        return results;
    }

    private double evaluate_individual(Individual ind) {
        
        calculate_PSD_from_individual(ind);
        calculateRelativeDifference(difference, PSD);

        difference=MathUtils.avg_Filter(difference, 5);
        double error = 0;
        for (int a = 0; a < PSD_size_X; a++) {
            for (int b = 0; b < PSD_size_Y; b++) {
                
                error += Math.abs(difference[a][b]);
            }
        }        
        return error * wheight;
    }
}
