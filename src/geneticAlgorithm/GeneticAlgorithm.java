/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import basic.Parser;
import geneticAlgorithm.selection.RankingSelection;
import graphicInterfaces.gaConvergence.IgaProgressFrame;

/**
 *
 * @author Nestor
 */
public class GeneticAlgorithm extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {

  public GeneticAlgorithm(Parser parser) {
    super(parser);
    selection = new RankingSelection();
  }

  @Override
  public IGeneticAlgorithm initialise() {
    population = initialisation.createRandomPopulation(populationSize);
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

    this.scaleIndividualRates(population);

    if (graphics != null) {
      graphics.clear();
    }

    return this;
  }

  private void iterateOneStep() {
    IndividualGroup[] couples = selection.Select(population, offspringSize);
    Population offspringPopulation = recombination.recombinate(couples);
    offspringPopulation.setIterationNumber(currentIteration);

    int geneSize = population.getIndividual(0).getGeneSize();
    mutation.mutate(offspringPopulation, restriction.getNonFixedGenes(geneSize));
    restriction.apply(offspringPopulation);
    evaluator.evaluateAndOrder(offspringPopulation, mainEvaluator, otherEvaluators);

    //sometimes it is good to reevaluate the whole population
    if (currentIteration > 0 && currentIteration % 25 == 0) {
      this.scaleIndividualRates(population);
      restriction.apply(population);
      this.evaluator.evaluateAndOrder(population, mainEvaluator, otherEvaluators);
    }

    reinsertion.Reinsert(population, offspringPopulation, populationReplacements);

  }

  @Override
  public void iterate(int maxIterations) {
    totalIterations = maxIterations;
    for (int i = 0; i < maxIterations; i++) {
      currentIteration = i;
      iterateOneStep();
      addToGraphics();
      System.out.println("For iteration " + this.getCurrentIteration() + " the best error is " + this.getBestError());
      if (this.getBestError() < 0.086) {
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
  public float[] getProgressPercent() {

    float[] progress = new float[3];

    progress[0] = currentIteration * 100.0f / totalIterations;

    float[] subprogress = evaluator.getProgressPercent();
    progress[1] = subprogress[0];
    progress[2] = subprogress[1];

    return progress;
  }

  @Override
  public int getCurrentIteration() {
    return currentIteration;
  }

  @Override
  public int getTotalIterations() {
    return totalIterations;
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

  private void scaleIndividualRates(Population population) {
    for (int i = 0; i < population.size(); i++) {
      Individual individual = population.getIndividual(i);
      double factor;
      if (expectedSimulationTime == 0) {
        factor = 1;
      } else {
        factor = expectedSimulationTime / individual.getSimulationTime();
      }
      for (int j = 0; j < individual.getGeneSize(); j++) {
        individual.setGene(j, individual.getGene(j) / factor);
      }
    }
  }
}
