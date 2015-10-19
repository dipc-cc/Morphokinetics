/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.recombination;

import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public interface IRecombination {

  public Population recombinate(IndividualGroup[] groups);

}
