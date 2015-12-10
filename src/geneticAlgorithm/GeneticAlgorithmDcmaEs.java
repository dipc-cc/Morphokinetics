package geneticAlgorithm;

import basic.Parser;
import geneticAlgorithm.evaluationFunctions.IEvaluation;

import utils.akting.RichArray;
import utils.akting.operations.OperationFactory;
import utils.akting.tests.TestSuite;
import geneticAlgorithm.mutation.CrossoverMutator;
import geneticAlgorithm.recombination.DifferentialRecombination;
import geneticAlgorithm.reinsertion.ElitistAllReinsertion;
import geneticAlgorithm.selection.RandomSelection;
import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithmDcmaEs extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {

  private Population population;
  private final List<IEvaluation> otherEvaluators;
  private final RandomSelection selection;
  private final CrossoverMutator mutation;
  private final DifferentialRecombination recombination;
  private final ElitistAllReinsertion reinsertion;
  
  private DcmaEsConfig dcmaEsConfig;
  /** Stop if mean(fitness) - min(fitness) < stopFitness (minimization). */
  private final double stopFitness;
  
  public GeneticAlgorithmDcmaEs(Parser parser) {
    super(parser);
    
    
    stopFitness = 1e-12;
    // Inicializamos la clase que contiene variables globales del algoritmo.
    dcmaEsConfig = new DcmaEsConfig(getPopulationSize(), getDimensions());
    selection = new RandomSelection();
    mutation = new CrossoverMutator(dcmaEsConfig);
    recombination = new DifferentialRecombination(dcmaEsConfig, getPopulationSize(), getDimensions());
    reinsertion = new ElitistAllReinsertion(dcmaEsConfig);
    otherEvaluators = addNoMoreEvaluators();
  }

  @Override
  public IGeneticAlgorithm initialise() {
    population = getInitialisation().createRandomPopulation(getPopulationSize(), getDimensions(), getMinValueGene(), getMaxValueGene(), isExpDistribution());
    getRestriction().apply(this.population);

    double[] fitness = mainEvaluator.evaluate(population);
    for (int i = 0; i < fitness.length; i++) {
      dcmaEsConfig.getOffFitness().set(i, fitness[i]);
      population.getIndividual(i).setError(0, fitness[i]);
    }

    recombination.initialise(population);

    Population p = population;
    p.order();

    System.out.println("==================================");
    System.out.println("Finished initial random population");
    System.out.println("==================================");
    clearGraphics();

    return this;
  }

  @Override
  public void iterateOneStep() {
    IndividualGroup[] trios = selection.Select(population, getPopulationSize());
    Population offspringPopulation = recombination.recombinate(trios);
    offspringPopulation.setIterationNumber(getCurrentIteration());
    
    mutation.mutate(offspringPopulation, null);
    getRestriction().apply(offspringPopulation);
    double[] fitness = mainEvaluator.evaluate(offspringPopulation);
    for (int i = 0; i < fitness.length; i++) {
      offspringPopulation.getIndividual(i).setError(0, fitness[i]);
    }

    getRestriction().apply(population);
    reinsertion.Reinsert(population, offspringPopulation, getPopulationReplacements());
  }

  private double[] myEvaluate(Population population) {
    double[] values = new double[population.size()];

    for (int i = 0; i < population.size(); i++) {
      values[i] = TestSuite.fschwefel(new RichArray(population.getIndividual(i)));
    }

    return values;
  }
  
  /**
   * Break if fitness is good enough or condition exceeds 1e14. Better termination methods are advisable
   * @return 
   */
  @Override
  public boolean exitCondition() {
    boolean cond1 = dcmaEsConfig.getOffFitness().apply(OperationFactory.deduct(dcmaEsConfig.getOffFitness().min())).allLessOrEqualThan(stopFitness);
    boolean cond2 = ((DifferentialRecombination) recombination).isDtooLarge();
    if (cond1 || cond2) {
      System.out.println("Exiting for an unknown reason " + cond1 + " " + cond2);
      return false;
    }
    return false;
  }

  @Override
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
  
  private List<IEvaluation> addNoMoreEvaluators() {
    List<IEvaluation> evaluation = new ArrayList();
    return evaluation;
  }
}
