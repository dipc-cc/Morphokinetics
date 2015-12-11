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
public class ReplicatedGeneRestriction extends GeneRestriction {

  private final int genePositionToReplicate;

  public ReplicatedGeneRestriction(int genePositionToReplicate, int genePosition) {
    super(genePosition);
    this.genePositionToReplicate = genePositionToReplicate;
  }

  @Override
  public void restrictGene(Individual i) {
    i.setGene(getGenePosition(), i.getGene(genePositionToReplicate));
  }

  @Override
  public int getRestrictionType() {
    if (getGenePosition() == genePositionToReplicate) {
      return GeneRestriction.NO_RESTRICTION;
    } else {
      return GeneRestriction.REPLICATE_GENE;
    }
  }
}
