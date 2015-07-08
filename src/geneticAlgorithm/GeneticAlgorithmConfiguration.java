/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import geneticAlgorithm.geneticOperators.evaluationFunctions.IEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.psdEvaluator.AbstractPSDEvaluation;
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

    public AbstractPSDEvaluation mainEvaluator;
    public List<IEvaluation> otherEvaluators;
    public IMutation mutation;
    public IInitializator initialization;
    public IRecombination recombination;
    public IReinsertion reinsertion;
    public RestrictionOperator restriction;
    public ISelection selection;
    public int population_size;
    public int offspring_size;
    public int population_replacements;
    public double expected_simulation_time;

    public GeneticAlgorithmConfiguration setExperimentalPSD(float[][] PSD_experimental) {
        mainEvaluator.setPSD(PSD_experimental);
        return this;
    }
}