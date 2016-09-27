/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.populationInitialisation;

import geneticAlgorithm.Population;

/**
 * This class only considers 6 different genes for Ag/Ag growth.
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgReduced6Initialisator extends AbstractInitialisator implements IInitialisator {

  /**
   * Initialises all the rates between 1 and 1e12
   *
   * @param populationSize
   * @return new Population with 6 genes in the interval of 1 and 1e12
   */
  @Override
  public Population createRandomPopulation(int populationSize) {
    return createRandomPopulation(populationSize, 6, 1, 1e12, true);
  }
}
