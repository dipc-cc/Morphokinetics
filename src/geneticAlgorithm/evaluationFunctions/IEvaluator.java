/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import geneticAlgorithm.Population;
import java.util.List;

/**
 *
 * @author Nestor
 */
public interface IEvaluator {

  /**
   * Evaluate all the elements of the population with the list of given evaluators. After, it orders
   * them from min to max error.
   *
   * @param p
   * @param functionWithSimulation
   * @param functions
   */
  public void evaluateAndOrder(Population p, AbstractPsdEvaluator functionWithSimulation, List<IEvaluation> functions);
}
