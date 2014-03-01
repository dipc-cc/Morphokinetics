/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Restrictions;

import Genetic_Algorithm.Individual;

/**
 *
 * @author Nestor
 */
public class Fixed_gene_restriction extends Gene_restriction {
    
        private double fixedValue;

    public Fixed_gene_restriction(double fixedValue, int genePosition) {
        super(genePosition);
        this.fixedValue = fixedValue;
    }

    @Override
    public void restrictGene(Individual i) {
       i.setGene(genePosition, fixedValue);
    }

    @Override
    public int getRestrictionType() {
        return Gene_restriction.FIXED_VALUE;
    }


    

    
    
    
    
}
