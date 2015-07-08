/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphic_interfaces.GA_convergence;

import geneticAlgorithm.IProgressable;
import geneticAlgorithm.Individual;

/**
 *
 * @author Nestor
 */
public interface IGA_progress_frame {
    
    public void addNewBestIndividual(Individual i);
    public void clear();
    
    
}
