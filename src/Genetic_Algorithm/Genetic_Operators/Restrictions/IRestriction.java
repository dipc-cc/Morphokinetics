/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Genetic_Algorithm.Genetic_Operators.Restrictions;

import Genetic_Algorithm.Population;
import java.util.List;

/**
 *
 * @author Nestor
 */
public interface IRestriction {
    
    
    public void initialize();
    
    public void apply(Population p);
    
    public List getNonFixedGenes(int geneSize);
    
    
    
}
