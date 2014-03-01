/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Recombination;

import Genetic_Algorithm.Couple;
import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public interface IRecombination {
    
public Population recombinate(Couple[] couples);    
    
    
}
