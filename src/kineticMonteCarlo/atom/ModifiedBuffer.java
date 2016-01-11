/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

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
  
  public void updateAtoms(AbstractList list) {
    
    Iterator<AbstractGrowthAtom> it = buffer.iterator();
    while (it.hasNext()) {
      updateAllRates(it.next(), list);
    }

    it = bufferL.iterator();
    while (it.hasNext()) {
      updateAllNeighbours(it.next(), list);
    }
    clear();
  }

  private void updateAllRates(AbstractGrowthAtom atom, AbstractList list) {
    double probabilityChange = atom.updateRate();
    if (list != null) {
      if (atom.isEligible()&& !atom.isOnList()) {
        list.addAtom(atom);
      }
      list.addTotalProbability(probabilityChange);
    }
  }

  private void updateAllNeighbours(AbstractGrowthAtom atom, AbstractList list) {
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      AbstractGrowthAtom neighbour = atom.getNeighbour(i);
      if (neighbour.isEligible() && !buffer.contains(neighbour)) {
        double probabilityChange = neighbour.updateOneBound(i);
        list.addTotalProbability(probabilityChange);
      }
    }
  }

  public void clear() {
    buffer.clear();
    bufferL.clear();
  }
}
