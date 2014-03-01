/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm;

import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.IEvaluation;
import Genetic_Algorithm.Genetic_Operators.Evaluation_Functions.PSD_Evaluator.AbstractPSDEvaluation;
import Genetic_Algorithm.Genetic_Operators.Mutation.IMutation;
import Genetic_Algorithm.Genetic_Operators.Population_Initialization.IInitializator;
import Genetic_Algorithm.Genetic_Operators.Recombination.IRecombination;
import Genetic_Algorithm.Genetic_Operators.Reinsertion.IReinsertion;
import Genetic_Algorithm.Genetic_Operators.Restrictions.IRestriction;
import Genetic_Algorithm.Genetic_Operators.Selection.ISelection;
import java.util.List;

/**
 *
 * @author Nestor
 */
public class Genetic_algorithm_configuration {

    public AbstractPSDEvaluation mainEvaluator;
    public List<IEvaluation> otherEvaluators;
    public IMutation mutation;
    public IInitializator initialization;
    public IRecombination recombination;
    public IReinsertion reinsertion;
    public IRestriction restriction;
    public ISelection selection;
    public int population_size;
    public int offspring_size;
    public int population_replacements;
    public double expected_simulation_time;

    public Genetic_algorithm_configuration setExperimentalPSD(float[][] PSD_experimental) {
        mainEvaluator.setPSD(PSD_experimental);
        return this;
    }
}