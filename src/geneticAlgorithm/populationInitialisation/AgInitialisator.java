/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.populationInitialisation;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgInitialisator implements IInitialisator {

  /**
   * Robust initialisation methods, it uses a logarithmic distribution of process rates, more
   * similar to what is expected from a real system.
   *
   * @param populationSize
   * @return
   */
  @Override
  public Population createRandomPopulation(int populationSize) {
    Population p = new Population(populationSize);
    for (int ind = 0; ind < p.size(); ind++) {
      Individual i = new Individual(49, 4);
      for (int a = 0; a < 7; a++) {
        for (int j = 0; j < 7; j++) {
          if (a == 1) {
            i.setGene(a * 7 + j, Math.max(0.1, 1000 * Math.pow(150000, StaticRandom.raw())));
          } else {
            i.setGene(a * 7 + j, Math.max(0.1, 0.1 * Math.pow(10000000, StaticRandom.raw())));
          }
        }
      }
      p.setIndividual(i, ind);
    }
    return p;
  }
}
