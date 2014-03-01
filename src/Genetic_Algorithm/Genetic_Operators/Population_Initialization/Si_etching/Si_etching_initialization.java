/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Population_Initialization.Si_etching;

import Genetic_Algorithm.Genetic_Operators.Population_Initialization.IInitializator;
import Genetic_Algorithm.Individual;
import Genetic_Algorithm.Population;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class Si_etching_initialization implements IInitializator {

    //simplest way of initialization, a pure random value. Ag_ag initialization has a more robuts initialization method, recommended.
    @Override
    public Population createRandomPopulation(int populationSize) {

        Population p = new Population(populationSize);

        for (int ind = 0; ind < p.size(); ind++) {

            Individual i = new Individual(16*4, 4);
            for (int gene = 0; gene < i.getGeneSize(); gene++) {

                i.setGene(gene, Math.max(1e-7*Math.pow(1e7, StaticRandom.raw()), 1e-8));
                //i.setGene(gene,Math.max(StaticRandom.raw()*0.1,1e-8));
                //System.out.println(Math.max(1e-6*Math.pow(1e6, StaticRandom.raw()), 1e-8));
                // i.setGene(gene, Math.max(StaticRandom.raw()*0.1, 1e-8));
                //i.setProbabilidad(Math.max(rand.raw()*0.1,1e-8), a);
                
            }
            p.setIndividual(i, ind);
        }
        return p;
    }
}
