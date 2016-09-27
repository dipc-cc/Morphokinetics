/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.populationInitialisation;

import geneticAlgorithm.Population;

/**
 * This class only considers 5 different genes for basic growth.
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthInitialisator extends AbstractInitialisator implements IInitialisator {

  /**
   * Initialises all the rates between 1e-6 and 1e9.
   *
   * @param populationSize
   * @return new Population with 5 genes in the interval of 1e-6 and 1e9
   */
  @Override
  public Population createRandomPopulation(int populationSize) {
    return createRandomPopulation(populationSize, 5, 1e-6, 1e9, true);
  }
}
