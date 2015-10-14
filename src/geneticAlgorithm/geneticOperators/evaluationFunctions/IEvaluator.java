/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.Population;
import java.util.List;

/**
 *
 * @author Nestor
 */
public interface IEvaluator {

//evaluate all the elements of the population with the list of given evaluators
//after, it orders them from min to max error.
  public void evaluateAndOrder(Population p, AbstractPsdEvaluation functionWithSimulation, List<IEvaluation> functions);
}
