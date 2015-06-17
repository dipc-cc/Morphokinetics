/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Recombination;

import Genetic_Algorithm.IndividualGroup;
import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public interface IRecombination {
    
public Population recombinate(IndividualGroup[] groups);    
    
    
}
