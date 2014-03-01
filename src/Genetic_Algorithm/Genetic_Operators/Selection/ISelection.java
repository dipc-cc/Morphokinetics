/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Selection;

import Genetic_Algorithm.Couple;
import Genetic_Algorithm.Population;

/**
 *
 * @author Nestor
 */
public interface ISelection {
    
    public Couple[]  Select(Population p, int couples);
    
    
}
