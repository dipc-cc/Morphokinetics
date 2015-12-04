package geneticAlgorithm;

import basic.Parser;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.OperationFactory;
import utils.akting.tests.TestSuite;
import geneticAlgorithm.mutation.CrossoverMutator;
import geneticAlgorithm.recombination.DifferentialRecombination;
import geneticAlgorithm.reinsertion.ElitistAllReinsertion;
import geneticAlgorithm.selection.RandomSelection;
import graphicInterfaces.gaConvergence.IgaProgressFrame;

public class GeneticAlgorithmDcmaEs extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {

  private DcmaEsConfig dcmaEsConfig;
  /** Stop if mean(fitness) - min(fitness) < stopFitness (minimization). */
  private final double stopFitness;
  
  private Integer[] offIndex;

  public GeneticAlgorithmDcmaEs(Parser parser) {
    super(parser);
    
    selection = new RandomSelection();
    mutation = null;
    recombination = null;
    reinsertion = null;
    
    stopFitness = 1e-12;
  }

  @Override
  public IGeneticAlgorithm initialise() {
    population = initialisation.createRandomPopulation(populationSize, 6, parser.getMinValueGene(), parser.getMaxValueGene(), parser.isExpDistribution());

    // Inicializamos la clase que contiene variables globales del algoritmo.
    dcmaEsConfig = new DcmaEsConfig(this, population.getIndividual(0).getGeneSize());
    recombination = new DifferentialRecombination(dcmaEsConfig, population);
    mutation = new CrossoverMutator(dcmaEsConfig);
    reinsertion = new ElitistAllReinsertion(dcmaEsConfig);

    restriction.apply(this.population);

    double[] fitness = mainEvaluator.evaluate(population);
    for (int i = 0; i < fitness.length; i++) {
      dcmaEsConfig.getOffFitness().set(i, fitness[i]);
      population.getIndividual(i).setError(0, fitness[i]);
    }

    ((DifferentialRecombination) recombination).initialise(population);

    Population p = population;
    p.order();
    System.out.println("=============");
    for (int i = 0; i < p.size(); i++) {
      System.out.print("Total error: " + p.getIndividual(i).getTotalError() + "| \tGenes: ");

      for (int a = 0; a < p.getIndividual(i).getGeneSize(); a++) {
        System.out.print(p.getIndividual(i).getGene(a) + " \t");
      }

      System.out.println();
    }

    if (graphics != null) {
      graphics.clear();
    }

    return this;
  }

  private void iterateOneStep() {
    IndividualGroup[] trios = selection.Select(population, populationSize);
    Population offspringPopulation = recombination.recombinate(trios);
    offspringPopulation.setIterationNumber(currentIteration);
    
    mutation.mutate(offspringPopulation, null);
    restriction.apply(offspringPopulation);
    double[] fitness = mainEvaluator.evaluate(offspringPopulation);
    for (int i = 0; i < fitness.length; i++) {
      offspringPopulation.getIndividual(i).setError(0, fitness[i]);
    }

    restriction.apply(population);
    population = reinsertion.Reinsert(population, offspringPopulation, 0);

    offIndex = dcmaEsConfig.getOffFitness().sortedIndexes();
    dcmaEsConfig.setOffX(new RichMatrix(population));
  }

  @Override
  public void iterate(int maxIterations) {
    totalIterations = maxIterations;

    while (currentIteration < maxIterations) {
      currentIteration++;

      iterateOneStep();

      addToGraphics();
      // Break if fitness is good enough or condition exceeds 1e14. Better termination methods are advisable
      boolean cond1 = dcmaEsConfig.getOffFitness().apply(OperationFactory.deduct(dcmaEsConfig.getOffFitness().min())).allLessOrEqualThan(stopFitness);
      boolean cond2 = ((DifferentialRecombination) recombination).isDtooLarge();
      if (cond1 || cond2) {
        System.out.println("Exiting for an unknown reason "+cond1+" "+cond2);
        break;
      }
      System.out.println("For iteration " + this.getCurrentIteration() + " the best error is " + this.getBestError());
      System.out.print("Best genes: ");
      for (int i = 0; i < getBestIndividual().getGenes().length; i++) {
        System.out.print(getBestIndividual().getGenes()[i] + " ");
      }
      System.out.println("");
      if (this.getBestError() < getStopError()) {
        System.out.println("Stopping because the error is "+this.getBestError()+" ("+getStopError()+")");
        break;
      }
    }

    offIndex = dcmaEsConfig.getOffFitness().sortedIndexes();
    dcmaEsConfig.setOffX(dcmaEsConfig.getOffX().recombinate(offIndex));
    double fmin = dcmaEsConfig.getOffFitness().get(0);
    RichArray xmin = dcmaEsConfig.getOffX().get(offIndex[0]);

    System.out.println(currentIteration + ": " + fmin);
    System.out.println(xmin);
  }

  private double[] myEvaluate(Population population) {
    double[] values = new double[population.size()];

    for (int i = 0; i < population.size(); i++) {
      values[i] = TestSuite.fschwefel(new RichArray(population.getIndividual(i)));
    }

    return values;
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
  public float[] getProgressPercent() {
    float[] progress = new float[3];
    progress[0] = currentIteration * 100.0f / totalIterations;
    progress[2] = mainEvaluator.getProgressPercent();

    return progress;
  }

  @Override
  public void setGraphics(IgaProgressFrame graphics) {
    this.graphics = graphics;
  }

  private void addToGraphics() {
    if (mainInterface != null) {
      mainInterface.addNewBestIndividual(getBestIndividual());
    }
    if (graphics != null) {
      graphics.addNewBestIndividual(getBestIndividual());
    }
  }

  public Individual getBestIndividual() {
    Population p = new Population(population.getIndividuals());
    p.order();
    return p.getIndividual(0);
  }

  @Override
  public double getBestError() {
    Population p = new Population(population.getIndividuals());
    p.order();
    return p.getIndividual(0).getTotalError();
  }

  @Override
  public Individual getIndividual(int pos) {
    return population.getIndividual(pos);
  }
}
