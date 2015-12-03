/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.populationInitialisation;

import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public class SiInitialisator extends GeneralInitialisator implements IInitialisator {

  /**
   * Simplest way of initialisation, a pure random value. AgAg initialisation has a more robust
   * initialisation method, recommended.
   *
   * @param populationSize
   * @return
   */
  @Override
  public Population createRandomPopulation(int populationSize) {
    return createRandomPopulation(populationSize, 16 * 4, 1e-8, 1e7, true);
  }
}
