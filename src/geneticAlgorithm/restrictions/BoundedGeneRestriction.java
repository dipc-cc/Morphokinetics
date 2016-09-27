/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.restrictions;

import geneticAlgorithm.Individual;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class BoundedGeneRestriction extends GeneRestriction {

  private final double minValue;
  private final double maxValue;

  public BoundedGeneRestriction(double minValue, double maxValue, int genePosition) {
    super(genePosition);
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  @Override
  public void restrictGene(Individual i) {
    if (i.getGene(getGenePosition()) < minValue) {
      i.setGene(getGenePosition(), minValue);
    }
    if (i.getGene(getGenePosition()) > maxValue) {
      i.setGene(getGenePosition(), maxValue);
    }
    // If gene is not defined initialise again with random number. In any case, the algorithm seems to be lost by now
    if (Double.isNaN(i.getGene(getGenePosition()))) {
      double newGene = minValue * Math.pow(maxValue / minValue, StaticRandom.raw());
      System.err.println("Setting new random gene to position " + getGenePosition() + " because it turned to be NaN. Value: " + newGene);
      System.out.println("Setting new random gene to position " + getGenePosition() + " because it turned to be NaN. Value: " + newGene);
      i.setGene(getGenePosition(), newGene);
    }
  }

  @Override
  public int getRestrictionType() {
    return GeneRestriction.BOUNDED_VALUES;
  }

}
