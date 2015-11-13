/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.selection;

import geneticAlgorithm.IndividualGroup;
import geneticAlgorithm.Population;

/**
 *
 * @author Nestor
 */
public interface ISelection {

  public IndividualGroup[] Select(Population p, int groups);
}
