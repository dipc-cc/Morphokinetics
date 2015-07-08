/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPSDEvaluation;
import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import java.util.List;

/**
 *
 * @author Nestor
 */
public interface IEvaluator {

//evaluate all the elements of the population with the list of given evaluators
//after, it orders them from min to max error.
    public void evaluate_and_order(Population p,AbstractPSDEvaluation functionWithSimulation, List<IEvaluation> functions);
}
