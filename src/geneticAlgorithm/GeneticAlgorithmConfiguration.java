/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import geneticAlgorithm.geneticOperators.evaluationFunctions.IEvaluation;
import geneticAlgorithm.geneticOperators.evaluationFunctions.AbstractPsdEvaluation;
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

  private AbstractPsdEvaluation mainEvaluator;
  private List<IEvaluation> otherEvaluators;
  private IMutation mutation;
  private IInitializator initialization;
  private IRecombination recombination;
  private IReinsertion reinsertion;
  private RestrictionOperator restriction;
  private ISelection selection;
  private int populationSize;
  private int offspringSize;
  private int populationReplacements;
  private double expectedSimulationTime;

  public void setMainEvaluator(AbstractPsdEvaluation mainEvaluator) {
    this.mainEvaluator = mainEvaluator;
  }

  public void setOtherEvaluators(List<IEvaluation> otherEvaluators) {
    this.otherEvaluators = otherEvaluators;
  }

  public void setMutation(IMutation mutation) {
    this.mutation = mutation;
  }

  public void setInitialization(IInitializator initialization) {
    this.initialization = initialization;
  }

  public void setRecombination(IRecombination recombination) {
    this.recombination = recombination;
  }

  public void setReinsertion(IReinsertion reinsertion) {
    this.reinsertion = reinsertion;
  }

  public void setRestriction(RestrictionOperator restriction) {
    this.restriction = restriction;
  }

  public void setSelection(ISelection selection) {
    this.selection = selection;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  public void setOffspringSize(int offspringSize) {
    this.offspringSize = offspringSize;
  }

  public void setPopulationReplacements(int populationReplacements) {
    this.populationReplacements = populationReplacements;
  }

  public void setExpectedSimulationTime(double expectedSimulationTime) {
    this.expectedSimulationTime = expectedSimulationTime;
  }

  public AbstractPsdEvaluation getMainEvaluator() {
    return mainEvaluator;
  }

  public List<IEvaluation> getOtherEvaluators() {
    return otherEvaluators;
  }

  public IMutation getMutation() {
    return mutation;
  }

  public IInitializator getInitialization() {
    return initialization;
  }

  public IRecombination getRecombination() {
    return recombination;
  }

  public IReinsertion getReinsertion() {
    return reinsertion;
  }

  public RestrictionOperator getRestriction() {
    return restriction;
  }

  public ISelection getSelection() {
    return selection;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getOffspringSize() {
    return offspringSize;
  }

  public int getPopulationReplacements() {
    return populationReplacements;
  }

  public double getExpectedSimulationTime() {
    return expectedSimulationTime;
  }

  public GeneticAlgorithmConfiguration setExperimentalPsd(float[][] experimentalPsd) {
    mainEvaluator.setPsd(experimentalPsd);
    return this;
  }
}
