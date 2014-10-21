/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Restrictions;

import Genetic_Algorithm.Individual;
import Genetic_Algorithm.Population;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Nestor
 */
public abstract class RestrictionOperator {

    protected List<Gene_restriction> genesRestriction= new ArrayList();


    
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

                if (genesRestriction.get(j).getGenePosition() == i && genesRestriction.get(j).getRestrictionType() > Gene_restriction.BOUNDED_VALUES) {
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
