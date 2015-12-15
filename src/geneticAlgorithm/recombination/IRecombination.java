/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.recombination;

import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public interface IRecombination {

  public void initialise(Population population);
  
  public Population recombinate(Population population, IndividualGroup[] groups);

  public boolean isDtooLarge();
}
