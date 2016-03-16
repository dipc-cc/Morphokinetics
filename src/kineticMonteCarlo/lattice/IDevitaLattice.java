/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractGrowthAtom;

/**
 *
 * @author Nestor
 */
public interface IDevitaLattice {

  public int getAvailableDistance(AbstractGrowthAtom atom, int thresholdDistance);

  public AbstractGrowthAtom getFarSite(AbstractGrowthAtom atom, int distance);

}
