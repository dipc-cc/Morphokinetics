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
public class ReplicatedGeneRestriction extends GeneRestriction {

    private int genePositionToReplicate;

    public ReplicatedGeneRestriction(int genePositionToReplicate, int genePosition) {
        super(genePosition);
        this.genePositionToReplicate = genePositionToReplicate;
    }

    @Override
    public void restrictGene(Individual i) {
        i.setGene(genePosition, i.getGene(genePositionToReplicate));
    }

    @Override
    public int getRestrictionType() {
        if (genePosition == genePositionToReplicate) {
            return GeneRestriction.NO_RESTRICTION;
        } else {
            return GeneRestriction.REPLICATE_GENE;
        }
    }
}
