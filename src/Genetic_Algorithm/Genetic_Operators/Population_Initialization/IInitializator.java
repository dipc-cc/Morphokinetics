/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Population_Initialization;

import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public interface IInitializator {
    
public Population createRandomPopulation(int populationSize);
  
}
