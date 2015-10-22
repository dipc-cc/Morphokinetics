package geneticAlgorithm;

import basic.Parser;
import java.util.Arrays;

import utils.akting.RichArray;
import utils.akting.RichMatrix;
import utils.akting.operations.OperationFactory;
import utils.akting.tests.TestSuite;
import geneticAlgorithm.geneticOperators.mutation.CrossoverMutator;
import geneticAlgorithm.geneticOperators.recombination.DifferentialRecombination;
import geneticAlgorithm.geneticOperators.reinsertion.ElitistAllReinsertion;
import geneticAlgorithm.geneticOperators.selection.RandomSelection;
import graphicInterfaces.gaConvergence.IgaProgressFrame;

public class GeneticAlgorithmDcmaEs extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {

  private DcmaEsConfig dcmaEsConfig;

  Integer[] offIndex;
  Integer[] reducedIndex;

  public GeneticAlgorithmDcmaEs(Parser parser) {
    super(parser);
    
    selection = new RandomSelection();
    mutation = null;
    recombination = null;
    reinsertion = null;
  }

  @Override
  public IGeneticAlgorithm initialize() {
    population = initialization.createRandomPopulation(populationSize);

    // Inicializamos la clase que contiene variables globales del algoritmo.
    dcmaEsConfig = new DcmaEsConfig(this, population.getIndividual(0).getGeneSize());
    recombination = new DifferentialRecombination(dcmaEsConfig);
    mutation = new CrossoverMutator(dcmaEsConfig);
    reinsertion = new ElitistAllReinsertion(dcmaEsConfig);

    restriction.apply(this.population);

    //evaluator.evaluateAndOrder(this.population, config.mainEvaluator, config.otherEvaluators);
    double[] fitness = mainEvaluator.evaluate(population);
    //double[] fitness = myEvaluate(population);
    for (int i = 0; i < fitness.length; i++) {
      dcmaEsConfig.getOffFitness().set(i, fitness[i]);
      population.getIndividual(i).setError(0, fitness[i]);
      dcmaEsConfig.addCountEval();
    }

    offIndex = dcmaEsConfig.getOffFitness().sortedIndexes();
    reducedIndex = Arrays.copyOfRange(offIndex, 0, Double.valueOf(dcmaEsConfig.getMu()).intValue());
    dcmaEsConfig.setOffX(new RichMatrix(population));
    dcmaEsConfig.setXmean(dcmaEsConfig.getOffX().recombinate(reducedIndex).multiply(dcmaEsConfig.getWeights()));
    dcmaEsConfig.setSigma(dcmaEsConfig.getOffX().recombinate(reducedIndex).transpose().std().std());

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
    Population offspring = recombination.recombinate(trios);

    mutation.mutate(offspring, null);
    restriction.apply(offspring);
    /*for (int i = 0; i < offspring.size(); i++) {
     Individual ind = offspring.getIndividual(i);
     for (int j = 0; j < ind.getGeneSize(); j++) {
     double val = ind.getGene(j);
     ind.setGene(j, Math.max(-500, Math.min(500, val)));
     }
     }*/
    //this.evaluator.evaluateAndOrder(offspring, this.config.mainEvaluator, this.config.otherEvaluators);
    double[] fitness = mainEvaluator.evaluate(offspring);
    //double[] fitness = myEvaluate(offspring);
    for (int i = 0; i < fitness.length; i++) {
      //dcmaEsConfig.offFitness.set(i, fitness[i]);
      offspring.getIndividual(i).setError(0, fitness[i]);
      dcmaEsConfig.addCountEval();
    }

        //sometimes it is good to reevaluate the whole population
    // if (currentIteration > 0 && currentIteration % 25 == 0) {
    restriction.apply(population);
    /*for (int i = 0; i < population.size(); i++) {
     Individual ind = population.getIndividual(i);
     for (int j = 0; j < ind.getGeneSize(); j++) {
     double val = ind.getGene(j);
     ind.setGene(j, Math.max(-500, Math.min(500, val)));
     }
     }*/
            //this.evaluator.evaluateAndOrder(this.population, this.config.mainEvaluator, this.config.otherEvaluators);;

		//fitness = config.mainEvaluator.evaluate(population);
		//fitness = myEvaluate(population);
    /*	
     for (int i = 0; i < fitness.length; i++) {
     dcmaEsConfig.offFitness.set(i, fitness[i]);
     population.getIndividual(i).setError(0, fitness[i]);
     dcmaEsConfig.counteval++;
     }
     }*/
    population = reinsertion.Reinsert(population, offspring, 0);

    offIndex = dcmaEsConfig.getOffFitness().sortedIndexes();
    dcmaEsConfig.setOffX(new RichMatrix(population));
  }

  @Override
  public void iterate(int steps) {
    totalIterations = steps;

    while (dcmaEsConfig.getCounteval() < dcmaEsConfig.getStopEval() && currentIteration < steps) {
      currentIteration++;

      iterateOneStep();

      addToGraphics();

      if (dcmaEsConfig.getOffFitness().apply(OperationFactory.deduct(dcmaEsConfig.getOffFitness().min())).allLessOrEqualThan(dcmaEsConfig.getStopFitness())
              || dcmaEsConfig.getD().max() > 1e7 * dcmaEsConfig.getD().min()) {
        break;
      }
      if (this.getBestError() < 180) {
        break;
      }
    }

    offIndex = dcmaEsConfig.getOffFitness().sortedIndexes();
    dcmaEsConfig.setOffX(dcmaEsConfig.getOffX().recombinate(offIndex));
    double fmin = dcmaEsConfig.getOffFitness().get(0);
    RichArray xmin = dcmaEsConfig.getOffX().get(offIndex[0]);

    System.out.println(dcmaEsConfig.getCounteval() + ": " + fmin);
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

    float[] subprogress = evaluator.getProgressPercent();
    progress[1] = subprogress[0];
    progress[2] = subprogress[1];

    return progress;
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

  private void scaleIndividualRates(Population population) {
    for (int i = 0; i < population.size(); i++) {
      Individual individual = population.getIndividual(i);
      double factor = expectedSimulationTime / individual.getSimulationTime();
      for (int j = 0; j < individual.getGeneSize(); j++) {
        individual.setGene(j, individual.getGene(j) / factor);
      }
    }
  }
}
