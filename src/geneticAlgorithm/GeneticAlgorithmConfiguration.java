/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import geneticAlgorithm.geneticOperators.evaluationFunctions.IEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPsdEvaluation;
import geneticAlgorithm.geneticOperators.mutation.IMutation;
import geneticAlgorithm.geneticOperators.populationInitialization.IInitializator;
import geneticAlgorithm.geneticOperators.recombination.IRecombination;
import geneticAlgorithm.geneticOperators.reinsertion.IReinsertion;
import geneticAlgorithm.geneticOperators.restrictions.RestrictionOperator;
import geneticAlgorithm.geneticOperators.selection.ISelection;
import java.util.List;

/**
 *
 * @author Nestor
 */
public class GeneticAlgorithmConfiguration {

    public AbstractPsdEvaluation mainEvaluator;
    public List<IEvaluation> otherEvaluators;
    public IMutation mutation;
    public IInitializator initialization;
    public IRecombination recombination;
    public IReinsertion reinsertion;
    public RestrictionOperator restriction;
    public ISelection selection;
    public int populationSize;
    public int offspringSize;
    public int populationReplacements;
    public double expectedSimulationTime;

    public GeneticAlgorithmConfiguration setExperimentalPsd(float[][] experimentalPsd) {
        mainEvaluator.setPsd(experimentalPsd);
        return this;
    }
}