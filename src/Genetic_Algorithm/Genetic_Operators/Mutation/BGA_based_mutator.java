/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Mutation;

import Genetic_Algorithm.Individual;
import Genetic_Algorithm.Population;
import java.util.List;

/**
 *
 * @author Nestor
 */
public class BGA_based_mutator implements IMutation {

    private float r = /*0.1f*/ 1f;           //mutation range
    private float k = 7;                     //mutation precission  
    private float mutRate = 0.5f;            //mutation rate as percentage

    public void mutate(Population p,List nonFixedGenes) {

        for (int ind = 0; ind < p.size(); ind++) {

            Individual child = p.getIndividual(ind);
            
            int mutations = (int) Math.round(utils.StaticRandom.raw() * nonFixedGenes.size() * mutRate);

            for (int m = 0; m < mutations; m++) {

                int posList = (int) (utils.StaticRandom.raw() * nonFixedGenes.size());
                int pos=(Integer)nonFixedGenes.get(posList);


                double a = (Math.pow(2, utils.StaticRandom.raw() * k) / Math.pow(2, k));
                double s = utils.StaticRandom.raw() * 2 - 1;
                double oldValue = child.getGene(pos);
                double newValue = oldValue + s*r*a*oldValue;
                               
                //System.out.println(pos+","+oldValue+" "+(newValue));
                child.setGene(pos, newValue);

            }



        }

    }
}
