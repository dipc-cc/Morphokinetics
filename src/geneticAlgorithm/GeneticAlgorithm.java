/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.IEvaluation;
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
  private List<IEvaluation> otherEvaluators;
  
  public GeneticAlgorithm(Parser parser) {
    super(parser);
    selection = new RankingSelection();
    otherEvaluators = addNoMoreEvaluators();
  }

  @Override
  public IGeneticAlgorithm initialise() {
    population = initialisation.createRandomPopulation(getPopulationSize(), getDimensions(), parser.getMinValueGene(), parser.getMaxValueGene(), parser.isExpDistribution());
    restriction.apply(population);
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
    offspringPopulation.setIterationNumber(currentIteration);

    int geneSize = population.getIndividual(0).getGeneSize();
    mutation.mutate(offspringPopulation, restriction.getNonFixedGenes(geneSize));
    restriction.apply(offspringPopulation);
    evaluator.evaluateAndOrder(offspringPopulation, mainEvaluator, otherEvaluators);

    //sometimes it is good to reevaluate the whole population
    if (currentIteration > 0 && currentIteration % 25 == 0) {
      restriction.apply(population);
      this.evaluator.evaluateAndOrder(population, mainEvaluator, otherEvaluators);
    }

    reinsertion.Reinsert(population, offspringPopulation, getPopulationReplacements());

  }

  @Override
  public void iterate(int maxIterations) {
    totalIterations = maxIterations;
    for (int i = 0; i < maxIterations; i++) {
      currentIteration = i;
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
