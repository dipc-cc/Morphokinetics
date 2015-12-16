/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.etching;

import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.lattice.AbstractEtchingLattice;
import utils.list.ListConfiguration;

/**
 *
 * @author Nestor
 */
public abstract class AbstractEtchingKmc extends AbstractKmc {

  public AbstractEtchingKmc(ListConfiguration config) {
    super(config);
  }

  @Override
  public AbstractEtchingLattice getLattice() {
    return (AbstractEtchingLattice) getLattice();
  }

}
