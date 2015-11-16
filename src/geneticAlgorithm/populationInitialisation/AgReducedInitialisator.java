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
public class AgReducedInitialisator implements IInitialisator{
  
  @Override
  public Population createRandomPopulation(int populationSize) {
    Population p = new Population(populationSize);
    for (int ind = 0; ind < p.size(); ind++) {
      System.out.println("Individual " + ind);
      Individual i = new Individual(10, 4);
      for (int j = 0; j < 10; j++) {
        i.setGene(j, Math.max(0.1, 1000 * Math.pow(150000, StaticRandom.raw())));
        //System.out.println("Individual"+ind+" random gene "+a+" "+j+" "+ i.getGene(a*7+j));
      }
      p.setIndividual(i, ind);
    }
    return p;
  }
}
