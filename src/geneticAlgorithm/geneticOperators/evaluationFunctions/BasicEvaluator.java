/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.evaluationFunctions;

import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.IProgressable;
import geneticAlgorithm.Population;
import java.util.List;

/**
 *
 * @author Nestor
 *
 * This is a basic class that sequentially applies all the evaluation functions
 * over a population
 *
 * More advanced implementations can do parallel evaluation of several functions
 * by using multithreading.
 *
 *
 */
public class BasicEvaluator implements IEvaluator, IProgressable {

    private IEvaluation currentEvaluator;
    private float progressPercent;

//evaluate all the elements of the population with the list of given evaluators
//after, it orders them from min to max error.
    @Override
    public void evaluate_and_order(Population p, AbstractPsdEvaluation functionWithSimulation, List<IEvaluation> functions) {

        currentEvaluator=functionWithSimulation;
        double[] results = functionWithSimulation.evaluate(p);
        for (int i = 0; i < results.length; i++) {
            p.getIndividual(i).setError(0, results[i]);
        }


        for (int f = 0; f < functions.size(); f++) {
            currentEvaluator = functions.get(f);
            results = currentEvaluator.evaluate(p);
            for (int i = 0; i < results.length; i++) {
                p.getIndividual(i).setError(f + 1, results[i]);
            }
            progressPercent = f * 100.0f / results.length;
        }
        p.order();
    }

    @Override
    public float[] getProgressPercent() {

        float[] progress = new float[2];
        progress[0] = progressPercent;
        if (currentEvaluator != null) {
            progress[1] = currentEvaluator.getProgressPercent();
        }

        return progress;
    }
}