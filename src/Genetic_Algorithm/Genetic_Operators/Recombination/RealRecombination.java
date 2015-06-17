/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Recombination;

import Genetic_Algorithm.Individual;
import Genetic_Algorithm.IndividualGroup;
import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public class RealRecombination implements IRecombination {

    private float OutBounds = 0.1f; // each gene of the child will be between the father and mother gene value +-15%

    @Override
    public Population recombinate(IndividualGroup[] groups) {

        Population offspring = new Population(groups.length);


        for (int i = 0; i < (offspring.size()); i++) {
            Individual child = new Individual(groups[0].get(0).getGeneSize(),
                    groups[0].get(0).getErrorsSize());

            for (int a = 0; a < child.getGeneSize(); a++) {

                double e = utils.StaticRandom.raw() * (1 + 2 * OutBounds) - OutBounds;
                child.setGene(a, Math.max(0.0, groups[i].get(0).getGene(a) * e + groups[i].get(1).getGene(a) * (1 - e)));

            }
         offspring.setIndividual(child,i);   
        }

        return offspring;

    }
}
