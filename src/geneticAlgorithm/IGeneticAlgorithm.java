/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm;

import graphicInterfaces.gaConvergence.IgaProgressFrame;

/**
 *
 * @author Nestor
 */
public interface IGeneticAlgorithm extends IProgressable {
    
    public IGeneticAlgorithm initialize();
    public int getCurrentIteration();
    public int getTotalIterations();
    public void setGraphics(IgaProgressFrame graphics);
    public double getBestError();
    public Individual getIndividual(int pos);
    public void iterate(int steps);
    
    
}
