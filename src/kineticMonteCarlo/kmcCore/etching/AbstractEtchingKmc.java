/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.etching;

import kineticMonteCarlo.kmcCore.*;
import kineticMonteCarlo.lattice.etching.AbstractEtchingLattice;
import kineticMonteCarlo.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public abstract class AbstractEtchingKmc extends AbstractKmc {

  public AbstractEtchingKmc(ListConfiguration config) {
    super(config);
  }

  protected AbstractEtchingLattice lattice;

  @Override
  public AbstractEtchingLattice getLattice() {
    return lattice;
  }

}
