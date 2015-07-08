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
public class Bounded_gene_restriction extends Gene_restriction {
    
        private double minValue,maxValue;

    public Bounded_gene_restriction(double minValue,double maxValue, int genePosition) {
        super(genePosition);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void restrictGene(Individual i) {
        if (i.getGene(genePosition)<minValue) i.setGene(genePosition,minValue);
        if (i.getGene(genePosition)>maxValue) i.setGene(genePosition,maxValue);
    }

    @Override
    public int getRestrictionType() {
        return Gene_restriction.BOUNDED_VALUES;
    }


    

    
    
    
    
}
