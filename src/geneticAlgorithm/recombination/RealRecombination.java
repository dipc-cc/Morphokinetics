/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.recombination;

import geneticAlgorithm.Individual;
import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class RealRecombination implements IRecombination {

  private final float outBounds;

  /**
   * Each gene of the child will be between the father and mother gene value +-15%.
   */
  public RealRecombination() {
    this.outBounds = 0.1f;
  }

  @Override
  public Population recombinate(IndividualGroup[] groups) {
    Population offspring = new Population(groups.length);

    for (int i = 0; i < (offspring.size()); i++) {
      Individual child = new Individual(groups[0].get(0).getGeneSize(),
              groups[0].get(0).getErrorsSize());

      for (int a = 0; a < child.getGeneSize(); a++) {

        double e = StaticRandom.raw() * (1 + 2 * outBounds) - outBounds;
        child.setGene(a, Math.max(0.0, groups[i].get(0).getGene(a) * e + groups[i].get(1).getGene(a) * (1 - e)));

      }
      offspring.setIndividual(child, i);
    }

    return offspring;
  }

  /**
   * Does nothing
   * @param population 
   */
  @Override
  public void initialise(Population population) {
    // Do nothing 
  }

  /**
   * Does nothing
   * @return false always
   */
  @Override
  public boolean isDtooLarge() {
    return false; // Do nothing
  }
}
