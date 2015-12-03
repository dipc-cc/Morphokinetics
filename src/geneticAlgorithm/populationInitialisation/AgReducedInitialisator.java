/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.populationInitialisation;

import geneticAlgorithm.Population;

/**
 * This class only considers 10 different genes for Ag/Ag growth.
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgReducedInitialisator extends GeneralInitialisator implements IInitialisator {

  /**
   * Initialises all the rates between 1 and 1e12
   *
   * @param populationSize
   * @return
   */
  @Override
  public Population createRandomPopulation(int populationSize) {
    return createRandomPopulation(populationSize, 10, 1, 1e12, true);
  }
}
