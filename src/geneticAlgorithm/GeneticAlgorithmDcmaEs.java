package geneticAlgorithm;

import basic.Parser;

import utils.akting.RichArray;
import utils.akting.tests.TestSuite;
import geneticAlgorithm.mutation.CrossoverMutator;
import geneticAlgorithm.recombination.DifferentialRecombination;
import geneticAlgorithm.reinsertion.ElitistAllReinsertion;
import geneticAlgorithm.selection.RandomSelection;

public class GeneticAlgorithmDcmaEs extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {
  
  public GeneticAlgorithmDcmaEs(Parser parser) {
    super(parser, new RandomSelection(), new CrossoverMutator(), new DifferentialRecombination(parser.getPopulationSize(), 6), new ElitistAllReinsertion());
  }

  private double[] myEvaluate(Population population) {
    double[] values = new double[population.size()];

    for (int i = 0; i < population.size(); i++) {
      values[i] = TestSuite.fschwefel(new RichArray(population.getIndividual(i)));
    }

    return values;
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
