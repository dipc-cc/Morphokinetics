/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import Graphic_interfaces.GA_convergence.IGA_progress_frame;

/**
 *
 * @author Nestor
 */
public interface IGenetic_algorithm extends IProgressable {
    
    public IGenetic_algorithm initialize();
    public int getCurrentIteration();
    public int getTotalIterations();
    public void setGraphics(IGA_progress_frame graphics);
    public double getBestError();
    public Individual getIndividual(int pos);
    public void iterate(int steps);
    
    
}
