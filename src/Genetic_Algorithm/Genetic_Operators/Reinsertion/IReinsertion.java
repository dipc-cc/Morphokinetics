/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Reinsertion;

import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 * 
 * performs the reinsertion of one population into another. 
 * The populations are supposed to be ordered from best (less error) to worse (more error).
 * 
 */
public interface IReinsertion {
    
    
    public Population Reinsert(Population origin, Population offpring,int substitutions);
    
}
