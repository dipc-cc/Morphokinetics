/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez, E. Sanchez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package geneticAlgorithm;

import basic.Parser;
import geneticAlgorithm.mutation.BgaBasedMutator;
import geneticAlgorithm.recombination.RealRecombination;
import geneticAlgorithm.reinsertion.ElitistReinsertion;
import geneticAlgorithm.selection.RankingSelection;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez, E. Sanchez
 */
public class GeneticAlgorithm extends AbstractGeneticAlgorithm implements IGeneticAlgorithm {
  
  public GeneticAlgorithm(Parser parser) {
    super(parser, new RankingSelection(), new BgaBasedMutator(), new RealRecombination(), new ElitistReinsertion());
  }
  
  /**
   * This method has only meaning in 
   * {@link geneticAlgorithm.GeneticAlgorithmDcmaEs#exitCondition()}.
   * 
   * @return always false
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
