/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractAtom;

/**
 *
 * @author Nestor
 */
public abstract class AbstractEtchingLattice extends AbstractLattice {

  protected AbstractAtom[] lattice;
  
  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < lattice.length; i++) {
      lattice[i].initialize(probabilities);
    }
  }

}
