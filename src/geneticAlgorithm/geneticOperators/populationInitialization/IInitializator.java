/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.populationInitialization;

import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public interface IInitializator {
    
public Population createRandomPopulation(int populationSize);
  
}
