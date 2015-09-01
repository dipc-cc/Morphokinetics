/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import utils.list.AbstractList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Nestor
 */
public class ModifiedBuffer {

  private final Set<Abstract2DDiffusionAtom> buffer;
  private final Set<Abstract2DDiffusionAtom> bufferL;

  public ModifiedBuffer() {

    buffer = new HashSet(8, 0.9f);
    bufferL = new HashSet(8, 0.9f);
  }

  public void addOwnAtom(Abstract2DDiffusionAtom a) {
    buffer.add(a);

  }

  public void addBondAtom(Abstract2DDiffusionAtom a) {
    bufferL.add(a);
  }

  public void updateAtoms(AbstractList list, Abstract2DDiffusionLattice lattice) {

    Iterator<Abstract2DDiffusionAtom> it = buffer.iterator();
    while (it.hasNext()) {
      updateAllRates(it.next(), list);
    }

    it = bufferL.iterator();
    while (it.hasNext()) {
      updateAllNeighbours(it.next(), lattice);
    }
    clear();
  }

  private void updateAllRates(Abstract2DDiffusionAtom atom, AbstractList list) {

    if (atom.isEligible() && !atom.isOnList()) {
      list.addAtom(atom);
    }
    atom.updateAllRates();

  }

  private void updateAllNeighbours(Abstract2DDiffusionAtom atom, Abstract2DDiffusionLattice lattice) {
    for (int i = 0; i < atom.getNeighbourCount(); i++) {
      updateNeighbour(atom, i, lattice);
    }
  }

  private void updateNeighbour(Abstract2DDiffusionAtom atom, int posNeighbour, Abstract2DDiffusionLattice lattice) {
    Abstract2DDiffusionAtom neighbour = lattice.getNeighbour(atom.getX(), atom.getY(), posNeighbour);
    if (neighbour.isEligible() && !buffer.contains(neighbour)) {
      neighbour.updateOneBound(posNeighbour);
    }
  }

  public void clear() {
    buffer.clear();
    bufferL.clear();
  }
}
