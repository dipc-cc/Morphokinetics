/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public interface IEvaluation {

  public double[] evaluate(Population p);

  public float getProgressPercent();

  public double getWheight();

  public IEvaluation setWheight(float wheight);
}
