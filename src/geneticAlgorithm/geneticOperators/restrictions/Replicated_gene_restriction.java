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
public class Replicated_gene_restriction extends Gene_restriction {

    private int genePositionToReplicate;

    public Replicated_gene_restriction(int genePositionToReplicate, int genePosition) {
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
            return Gene_restriction.NO_RESTRICTION;
        } else {
            return Gene_restriction.REPLICATE_GENE;
        }
    }
}
