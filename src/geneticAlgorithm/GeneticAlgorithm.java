/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import basic.Parser;
import geneticAlgorithm.mutation.BgaBasedMutator;
import geneticAlgorithm.recombination.RealRecombination;
import geneticAlgorithm.reinsertion.ElitistReinsertion;
import geneticAlgorithm.selection.RankingSelection;

/**
 *
 * @author Nestor
 */
public class GeneticAlgorithm extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {
  
  public GeneticAlgorithm(Parser parser) {
    super(parser, new RankingSelection(), new BgaBasedMutator(), new RealRecombination(), new ElitistReinsertion());
  }
  
  /**
   * This method has only mining in @see geneticAlgorithm.GeneticAlgorithmDcmaEs#exitCondition()
   * @return 
   */
  @Override
  public boolean exitCondition() {
    return false;
  }

  /**
   * Sometimes it is good to reevaluate the whole population.
   * @return true if the current iteration is multiple of 25
   */
  @Override
  public boolean reevaluate() {
    return (getCurrentIteration() > 0 && getCurrentIteration() % 25 == 0);
  }
      
}
