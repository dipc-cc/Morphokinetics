/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.populationInitialisation;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgReduced6Initialisator implements IInitialisator{
  
  /**
   * Initialises the terrace rates between 1e5 and 1e12, the rest of the genes between
   * 100 and 1e8
   * @param populationSize
   * @return 
   */
  @Override
  public Population createRandomPopulation(int populationSize) {
    Population p = new Population(populationSize);
    for (int ind = 0; ind < p.size(); ind++) {
      System.out.println("Individual " + ind);
      Individual i = new Individual(6, 4);
      i.setGene(0, 1E5 * Math.pow(1E12, StaticRandom.raw()));
      for (int j = 1; j < 6; j++) {
        i.setGene(j, 100 * Math.pow(1E8, StaticRandom.raw()));
      }
      p.setIndividual(i, ind);
    }
    return p;
  }
}
