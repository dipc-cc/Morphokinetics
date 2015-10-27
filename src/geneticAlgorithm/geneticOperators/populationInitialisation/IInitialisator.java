/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.populationInitialisation;

import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public interface IInitialisator {

  public Population createRandomPopulation(int populationSize);

}
