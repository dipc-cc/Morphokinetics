/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package utils.list;

import basic.Parser;
import java.util.ArrayList;
import java.util.ListIterator;
import kineticMonteCarlo.site.AbstractSite;
import utils.StaticRandom;

/**
 * Linked list basic implementation.
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class LinearList extends AbstractList implements IProbabilityHolder {

  private final ArrayList<AbstractSite> surface;
  /**
   * Stores if the current totalProbability and the probability calculated from the list are the same.
   */
  private boolean clean;
  private double Ri_DeltaI;
  
  
  public LinearList(Parser parser) {
    super(parser);
    surface = new ArrayList();
    this.setLevel(-1);
    clean = false;
    Ri_DeltaI = 0.0;
  }
  
  @Override
  public double getDiffusionProbabilityFromList() {
    if (clean) {
      return getDiffusionProbability();
    } else {
      double diffusionProbability = 0;

      ListIterator<AbstractSite> li = surface.listIterator();
      while (li.hasNext()) {
        AbstractSite atom = li.next();
        if (atom.isEligible()) {
          diffusionProbability += atom.getProbability();
        }
      }
      clean = true;
      setDiffusionProbability(diffusionProbability);
      return diffusionProbability;
    }
  }  
  
  /**
   * Updates the diffusion probability.
   * 
   * @param probabilityChange probability change.
   */
  @Override
  public void addDiffusionProbability(double probabilityChange) {
    clean = false;
    setDiffusionProbability(getDiffusionProbability() + probabilityChange);
  }

  @Override
  public void reset() {
    super.reset();
    surface.clear();
    clean = false;
    Ri_DeltaI = 0.0;
  }

  @Override
  public int cleanup() {
    int tmp = getTotalAtoms();
    ListIterator<AbstractSite> li = surface.listIterator();
    while (li.hasNext()) {
      AbstractSite atom = li.next();
      if (!atom.isEligible()) {
        li.remove();
        setTotalAtoms(getTotalAtoms() - 1);
      }
    }
    return (tmp - getTotalAtoms());
  }

  @Override
  public void addAtom(AbstractSite atom) {
    clean = false;
    surface.add(0, atom);
    atom.setList(true);
    setTotalAtoms(getTotalAtoms() + 1);
  }
  
  @Override
  public void deleteAtom(AbstractSite atom) {
    surface.remove(atom);
  }

  @Override
  public AbstractSite nextEvent() {
    addRemovalsSinceLastCleanup();
    if (autoCleanup() && getRemovalsSinceLastCleanup() > EVENTS_PER_CLEANUP) {
      this.cleanup();
      resetRemovalsSinceLastCleanup();
    }

    double position = StaticRandom.raw() * getGlobalProbability();
    
    addTime();

    Ri_DeltaI += (getDiffusionProbability() + getDepositionProbability()) * getDeltaTime(false); // should be always 1

    if (position < getDepositionProbability()) {
      return null; //toca añadir un átomo nuevo
    }
    position -= getDepositionProbability();
    double currentProbability = 0;

    AbstractSite atom = null;
    for (int i = 0; i < surface.size(); i++) {
      clean = false;
      atom = surface.get(i);
      currentProbability += atom.getProbability();
      if (currentProbability >= position) {
        surface.remove(i);
        setTotalAtoms(getTotalAtoms() - 1);
        return atom;
      }
    }

    if (atom != null) {
      clean = false;
      surface.remove(surface.size() - 1); // Remove from the list the last element
      setTotalAtoms(getTotalAtoms() - 1); // Update accordingly the number of atoms
    }
    
    return atom;
  }

  @Override
  public int getSize() {
    return surface.size();
  }

  @Override
  public AbstractSite getAtomAt(int position) {
    return surface.get(position);
  }

  @Override
  public ListIterator<AbstractSite> getIterator() {
    return surface.listIterator();
  }

  public double getRi_DeltaI() {
    return Ri_DeltaI;
  }
}
