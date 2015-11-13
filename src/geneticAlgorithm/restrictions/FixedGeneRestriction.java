/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.restrictions;

import geneticAlgorithm.Individual;

/**
 *
 * @author Nestor
 */
public class FixedGeneRestriction extends GeneRestriction {

  private double fixedValue;

  public FixedGeneRestriction(double fixedValue, int genePosition) {
    super(genePosition);
    this.fixedValue = fixedValue;
  }

  @Override
  public void restrictGene(Individual i) {
    i.setGene(genePosition, fixedValue);
  }

  @Override
  public int getRestrictionType() {
    return GeneRestriction.FIXED_VALUE;
  }

}
