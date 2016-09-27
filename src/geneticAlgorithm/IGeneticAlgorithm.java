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

  public IGeneticAlgorithm initialise();

  public int getCurrentIteration();

  public int getTotalIterations();

  public void setGraphics(IgaProgressFrame graphics);

  public Individual getIndividual(int pos);
  
  public Individual getBestIndividual();

  public void iterate();
  
  public void iterateOneStep();
  
  public boolean exitCondition();
  
  public boolean reevaluate();

}
