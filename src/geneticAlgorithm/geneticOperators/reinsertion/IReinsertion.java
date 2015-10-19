/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticAlgorithm.geneticOperators.reinsertion;

import geneticAlgorithm.Population;

/**
 *
 * Performs the reinsertion of one population into another. The populations are supposed to be
 * ordered from best (less error) to worse (more error).
 *
 * @author Nestor
 */
public interface IReinsertion {

  public Population Reinsert(Population origin, Population offpring, int substitutions);

}
