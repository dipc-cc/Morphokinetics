/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.restrictions;

import geneticAlgorithm.Individual;
import geneticAlgorithm.Population;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nestor
 */
public abstract class RestrictionOperator {

  protected List<GeneRestriction> genesRestriction = new ArrayList();

  public abstract void initialize();

  public void apply(Population p) {
    for (int ind = 0; ind < p.size(); ind++) {

      Individual individual = p.getIndividual(ind);

      for (int i = 0; i < genesRestriction.size(); i++) {
        genesRestriction.get(i).restrictGene(individual);
      }
    }
  }

  public List getNonFixedGenes(int geneSize) {
    List<Integer> result = new ArrayList();

    for (int i = 0; i < geneSize; i++) {
      boolean restricted = false;
      for (int j = 0; j < genesRestriction.size(); j++) {

        if (genesRestriction.get(j).getGenePosition() == i && genesRestriction.get(j).getRestrictionType() > GeneRestriction.BOUNDED_VALUES) {
          restricted = true;
          break;
        }
      }
      if (!restricted) {
        result.add(i);
      }
    }
    return result;
  }

}
