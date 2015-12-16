/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import utils.list.AbstractList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Nestor
 */
public class ModifiedBuffer {

  private final Set<AbstractGrowthAtom> buffer;
  private final Set<AbstractGrowthAtom> bufferL;

  public ModifiedBuffer() {

    buffer = new HashSet(8, 0.9f);
    bufferL = new HashSet(8, 0.9f);
  }

  public void addOwnAtom(AbstractGrowthAtom a) {
    buffer.add(a);

  }

  public void addBondAtom(AbstractGrowthAtom a) {
    bufferL.add(a);
  }

  public void updateAtoms(AbstractList list, AbstractGrowthLattice lattice) {

    Iterator<AbstractGrowthAtom> it = buffer.iterator();
    while (it.hasNext()) {
      updateAllRates(it.next(), list);
    }

    it = bufferL.iterator();
    while (it.hasNext()) {
      updateAllNeighbours(it.next(), lattice);
    }
    clear();
  }

  private void updateAllRates(AbstractGrowthAtom atom, AbstractList list) {

    if (atom.isEligible() && !atom.isOnList()) {
      list.addAtom(atom);
    }
    atom.updateAllRates();

  }

  private void updateAllNeighbours(AbstractGrowthAtom atom, AbstractGrowthLattice lattice) {
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      updateNeighbour(atom, i, lattice);
    }
  }

  private void updateNeighbour(AbstractGrowthAtom atom, int posNeighbour, AbstractGrowthLattice lattice) {
    AbstractGrowthAtom neighbour = lattice.getNeighbour(atom.getX(), atom.getY(), posNeighbour);
    if (neighbour.isEligible() && !buffer.contains(neighbour)) {
      neighbour.updateOneBound(posNeighbour);
    }
  }

  public void clear() {
    buffer.clear();
    bufferL.clear();
  }
}
