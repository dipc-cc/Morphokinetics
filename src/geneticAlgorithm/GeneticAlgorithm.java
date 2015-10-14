/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import geneticAlgorithm.geneticOperators.evaluationFunctions.BasicEvaluator;
import graphicInterfaces.gaConvergence.IgaProgressFrame;

/**
 *
 * @author Nestor
 */
public class GeneticAlgorithm implements IGeneticAlgorithm {

  private Population population;
  private GeneticAlgorithmConfiguration config;
  private BasicEvaluator evaluator;
  private int currentIteration = 0;
  private int totalIterations = 1;
  private IgaProgressFrame graphics;

  public GeneticAlgorithm(GeneticAlgorithmConfiguration configuration) {
    this.config = configuration;
    this.evaluator = new BasicEvaluator();
  }

  public IGeneticAlgorithm initialize() {
    this.population = config.initialization.createRandomPopulation(config.populationSize);
    this.config.restriction.apply(this.population);
    this.evaluator.evaluateAndOrder(this.population, this.config.mainEvaluator, this.config.otherEvaluators);

    System.out.println("=============");
    for (int i = 0; i < this.population.size(); i++) {
      System.out.print(population.getIndividual(i).getTotalError() + "| \t");
      for (int a = 0; a < population.getIndividual(i).getGeneSize(); a++) {
        System.out.print(population.getIndividual(i).getGene(a) + " \t");
      }
      System.out.println();
    }

    this.scaleIndividualRates(this.population);

    if (graphics != null) {
      graphics.clear();
    }

    return this;
  }

  private void iterateOneStep() {
    IndividualGroup[] couples = this.config.selection.Select(this.population, this.config.offspringSize);
    Population offspring = this.config.recombination.recombinate(couples);

    int geneSize = population.getIndividual(0).getGeneSize();
    this.config.mutation.mutate(offspring, this.config.restriction.getNonFixedGenes(geneSize));
    this.config.restriction.apply(offspring);
    this.evaluator.evaluateAndOrder(offspring, this.config.mainEvaluator, this.config.otherEvaluators);

    //sometimes it is good to reevaluate the whole population
    if (currentIteration > 0 && currentIteration % 25 == 0) {
      this.scaleIndividualRates(this.population);
      this.config.restriction.apply(this.population);
      this.evaluator.evaluateAndOrder(this.population, this.config.mainEvaluator, this.config.otherEvaluators);;
    }

    this.config.reinsertion.Reinsert(population, offspring, this.config.populationReplacements);

  }

  public void iterate(int steps) {
    totalIterations = steps;
    for (int i = 0; i < steps; i++) {
      currentIteration = i;
      iterateOneStep();
      addToGraphics();

    }
  }

  public double getBestError() {
    return population.getIndividual(0).getTotalError();
  }

  public Individual getIndividual(int pos) {
    return population.getIndividual(pos);
  }

  public Individual getBestIndividual() {
    return population.getIndividual(0);
  }

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
      double factor = config.expectedSimulationTime / individual.getSimulationTime();
      for (int j = 0; j < individual.getGeneSize(); j++) {
        individual.setGene(j, individual.getGene(j) / factor);
      }
    }
  }
}
