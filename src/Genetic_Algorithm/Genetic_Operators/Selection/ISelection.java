/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Selection;

import Genetic_Algorithm.IndividualGroup;
import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public interface ISelection {
    
    public IndividualGroup[] Select(Population p, int groups);
}
