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
public class BoundedGeneRestriction extends GeneRestriction {

  private double minValue, maxValue;

  public BoundedGeneRestriction(double minValue, double maxValue, int genePosition) {
    super(genePosition);
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  @Override
  public void restrictGene(Individual i) {
    if (i.getGene(genePosition) < minValue) {
      i.setGene(genePosition, minValue);
    }
    if (i.getGene(genePosition) > maxValue) {
      i.setGene(genePosition, maxValue);
    }
  }

  @Override
  public int getRestrictionType() {
    return GeneRestriction.BOUNDED_VALUES;
  }

}
