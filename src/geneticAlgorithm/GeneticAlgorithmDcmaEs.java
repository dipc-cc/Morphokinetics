package geneticAlgorithm;

import basic.Parser;

import utils.akting.RichArray;
import utils.akting.operations.OperationFactory;
import utils.akting.tests.TestSuite;
import geneticAlgorithm.mutation.CrossoverMutator;
import geneticAlgorithm.recombination.DifferentialRecombination;
import geneticAlgorithm.reinsertion.ElitistAllReinsertion;
import geneticAlgorithm.selection.RandomSelection;

public class GeneticAlgorithmDcmaEs extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {

  private DcmaEsConfig dcmaEsConfig;
  /** Stop if mean(fitness) - min(fitness) < stopFitness (minimization). */
  private final double stopFitness;
  
  public GeneticAlgorithmDcmaEs(Parser parser, DcmaEsConfig config) {
    super(parser, new RandomSelection(), new CrossoverMutator(config), new DifferentialRecombination(config, parser.getPopulationSize(), 6), new ElitistAllReinsertion(config));
    
    
    stopFitness = 1e-12;
    // Inicializamos la clase que contiene variables globales del algoritmo.
    dcmaEsConfig = config;
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
    boolean cond2 = isDtooLarge();
    if (cond1 || cond2) {
      System.out.println("Exiting for an unknown reason " + cond1 + " " + cond2);
      return false;
    }
    return false;
  }

  /**
   * There is no need to reevaluate with DCMA-ES.
   * @return Always false
   */
  @Override
  public boolean reevaluate() {
    return false;
  }
}
