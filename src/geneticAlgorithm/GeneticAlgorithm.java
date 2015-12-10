/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.IEvaluation;
import geneticAlgorithm.mutation.BgaBasedMutator;
import geneticAlgorithm.recombination.RealRecombination;
import geneticAlgorithm.reinsertion.ElitistReinsertion;
import geneticAlgorithm.selection.RankingSelection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nestor
 */
public class GeneticAlgorithm extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {

  private Population population;
  private final List<IEvaluation> otherEvaluators;
  private final RankingSelection selection;
  private final BgaBasedMutator mutation;
  private final RealRecombination recombination;
  private final ElitistReinsertion reinsertion;
  
  public GeneticAlgorithm(Parser parser) {
    super(parser);
    selection = new RankingSelection();
    mutation = new BgaBasedMutator();
    recombination = new RealRecombination();
    reinsertion = new ElitistReinsertion();
    otherEvaluators = addNoMoreEvaluators();
  }

  @Override
  public IGeneticAlgorithm initialise() {
    population = getInitialisation().createRandomPopulation(getPopulationSize(), getDimensions(), getMinValueGene(), getMaxValueGene(), isExpDistribution());
    getRestriction().apply(population);
    this.evaluator.evaluateAndOrder(population, mainEvaluator, otherEvaluators);

    System.out.println("=============");
    for (int i = 0; i < population.size(); i++) {
      System.out.print(population.getIndividual(i).getTotalError() + "| \t");
      for (int a = 0; a < population.getIndividual(i).getGeneSize(); a++) {
        System.out.print(population.getIndividual(i).getGene(a) + " \t");
      }
      System.out.println();
    }
    clearGraphics();
    
    return this;
  }

  @Override
  public void iterateOneStep() {
    IndividualGroup[] couples = selection.Select(population, getOffspringSize());
    Population offspringPopulation = recombination.recombinate(couples);
    offspringPopulation.setIterationNumber(getCurrentIteration());

    int geneSize = population.getIndividual(0).getGeneSize();
    mutation.mutate(offspringPopulation, getRestriction().getNonFixedGenes(geneSize));
    getRestriction().apply(offspringPopulation);
    evaluator.evaluateAndOrder(offspringPopulation, mainEvaluator, otherEvaluators);

    //sometimes it is good to reevaluate the whole population
    if (getCurrentIteration() > 0 && getCurrentIteration() % 25 == 0) {
      getRestriction().apply(population);
      this.evaluator.evaluateAndOrder(population, mainEvaluator, otherEvaluators);
    }

    reinsertion.Reinsert(population, offspringPopulation, getPopulationReplacements());

  }
  
  /**
   * This method has only mining in @see geneticAlgorithm.GeneticAlgorithmDcmaEs#exitCondition()
   * @return 
   */
  @Override
  public boolean exitCondition() {
    return false;
  }

  @Override
  public double getBestError() {
    return population.getIndividual(0).getTotalError();
  }

  @Override
  public Individual getIndividual(int pos) {
    return population.getIndividual(pos);
  }

  @Override
  public Individual getBestIndividual() {
    return population.getIndividual(0);
  }

  private List<IEvaluation> addNoMoreEvaluators() {
    List<IEvaluation> evaluation = new ArrayList();
    return evaluation;
  }
}
