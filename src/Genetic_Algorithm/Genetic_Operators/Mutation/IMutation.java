/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Mutation;

import Genetic_Algorithm.Population;
import java.util.List;

/**
 *
 * @author Nestor
 */
public interface IMutation {
    
    
    public void mutate(Population p,List nonFixedGenes);
    
}
