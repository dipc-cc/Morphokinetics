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
import graphicInterfaces.gaConvergence.IgaProgressFrame;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nestor
 */
public class GeneticAlgorithm extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {

  private Population population;
  private IgaProgressFrame graphics;
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
    population = getInitialisation().createRandomPopulation(getPopulationSize(), getDimensions(), parser.getMinValueGene(), parser.getMaxValueGene(), parser.isExpDistribution());
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

    if (graphics != null) {
      graphics.clear();
    }

    return this;
  }

  private void iterateOneStep() {
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

  @Override
  public void iterate() {
    for (int i = 0; i < getTotalIterations(); i++) {
      setCurrentIteration(i);
      iterateOneStep();
      addToGraphics();
      System.out.println("For iteration " + this.getCurrentIteration() + " the best error is " + this.getBestError());
      if (this.getBestError() < getStopError()) {
        System.out.println("Stopping because the error is "+this.getBestError());
        break;
      }

    }
  }

  @Override
  public double getBestError() {
    return population.getIndividual(0).getTotalError();
  }

  @Override
  public Individual getIndividual(int pos) {
    return population.getIndividual(pos);
  }

  public Individual getBestIndividual() {
    return population.getIndividual(0);
  }

  @Override
  public void setGraphics(IgaProgressFrame graphics) {
    this.graphics = graphics;
  }

  private void addToGraphics() {
    if (graphics != null) {
      graphics.addNewBestIndividual(getBestIndividual());
    }
  }

  private List<IEvaluation> addNoMoreEvaluators() {
    List<IEvaluation> evaluation = new ArrayList();
    return evaluation;
  }
}
