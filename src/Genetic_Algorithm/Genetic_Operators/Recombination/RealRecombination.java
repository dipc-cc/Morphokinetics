/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Recombination;

import Genetic_Algorithm.Couple;
import Genetic_Algorithm.Individual;
import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public class RealRecombination implements IRecombination {

    private float OutBounds = 0.1f; // each gene of the child will be between the father and mother gene value +-15%

    @Override
    public Population recombinate(Couple[] couples) {

        Population offspring = new Population(couples.length);


        for (int i = 0; i < (offspring.size()); i++) {
            Individual child = new Individual(couples[0].individual1.getGeneSize(),
                    couples[0].individual1.getErrorsSize());

            for (int a = 0; a < child.getGeneSize(); a++) {

                double e = utils.StaticRandom.raw() * (1 + 2 * OutBounds) - OutBounds;
                child.setGene(a, Math.max(0.0, couples[i].individual1.getGene(a) * e + couples[i].individual2.getGene(a) * (1 - e)));

            }
         offspring.setIndividual(child,i);   
        }

        return offspring;

    }
}
