/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.recombination;

import geneticAlgorithm.Individual;
import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public class RealRecombination implements IRecombination {

  private float outBounds = 0.1f; // each gene of the child will be between the father and mother gene value +-15%

  @Override
  public Population recombinate(IndividualGroup[] groups) {
    Population offspring = new Population(groups.length);

    for (int i = 0; i < (offspring.size()); i++) {
      Individual child = new Individual(groups[0].get(0).getGeneSize(),
              groups[0].get(0).getErrorsSize());

      for (int a = 0; a < child.getGeneSize(); a++) {

        double e = utils.StaticRandom.raw() * (1 + 2 * outBounds) - outBounds;
        child.setGene(a, Math.max(0.0, groups[i].get(0).getGene(a) * e + groups[i].get(1).getGene(a) * (1 - e)));

      }
      offspring.setIndividual(child, i);
    }

    return offspring;
  }
}
