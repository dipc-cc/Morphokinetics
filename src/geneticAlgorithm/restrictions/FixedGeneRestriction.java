/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.restrictions;

import geneticAlgorithm.Individual;

/**
 *
 * @author Nestor
 */
public class FixedGeneRestriction extends GeneRestriction {

  private final double fixedValue;

  public FixedGeneRestriction(double fixedValue, int genePosition) {
    super(genePosition);
    this.fixedValue = fixedValue;
  }

  @Override
  public void restrictGene(Individual i) {
    i.setGene(getGenePosition(), fixedValue);
  }

  @Override
  public int getRestrictionType() {
    return GeneRestriction.FIXED_VALUE;
  }

}
