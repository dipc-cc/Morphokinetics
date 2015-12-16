package utils.list;

import java.util.ArrayList;
import java.util.ListIterator;
import kineticMonteCarlo.atom.AbstractAtom;
import utils.StaticRandom;

/**
 *
 *
 * Linked list basic implementation
 *
 */
public class LinearList extends AbstractList {

  private final ArrayList<AbstractAtom> surface;

  public LinearList() {
    surface = new ArrayList();
    this.level = -1;
  }

  @Override
  public double getTotalProbabilityFromList() {
    double totalProbability = 0;

    ListIterator<AbstractAtom> li = surface.listIterator();
    while (li.hasNext()) {
      AbstractAtom atom = li.next();
      if (atom.isEligible()) {
        totalProbability += atom.getProbability();
      }
    }
    return totalProbability;
  }

  @Override
  public void reset() {
    surface.clear();
    time = 0;
    totalProbability = 0;
    totalAtoms = 0;
  }

  @Override
  public int cleanup() {

    int tmp = totalAtoms;
    ListIterator<AbstractAtom> li = surface.listIterator();
    while (li.hasNext()) {
      AbstractAtom AC = li.next();
      if (!AC.isEligible()) {
        AC.setList(null);
        li.remove();
        totalAtoms--;
      }
    }
    return (tmp - totalAtoms);
  }

  @Override
  public void addAtom(AbstractAtom a) {
    surface.add(0, a);
    a.setList(this);
    totalProbability += a.getProbability();
    totalAtoms++;
  }

  @Override
  public AbstractAtom nextEvent() {
    removalsSinceLastCleanup++;
    if (autoCleanup && removalsSinceLastCleanup > EVENTS_PER_CLEANUP) {
      this.cleanup();
      removalsSinceLastCleanup = 0;
    }

    double position = StaticRandom.raw() * (totalProbability + depositionProbability);

    time -= Math.log(StaticRandom.raw()) / (totalProbability + depositionProbability);

    if (position < depositionProbability) {
      return null; //toca añadir un átomo nuevo
    }
    position -= depositionProbability;
    double currentProbability = 0;

    AbstractAtom atom = null;
    outside:
    for (int i = 0; i < surface.size(); i++) {
      atom = surface.get(i);
      currentProbability += atom.getProbability();
      if (currentProbability >= position) {
        surface.remove(i);
        totalAtoms--;
        return atom;
      }
    }

    if (atom != null) {
      surface.remove(surface.size() - 1);
      totalAtoms--;
    }

    return atom;
  }

  @Override
  public int getSize() {
    return surface.size();
  }

  @Override
  public double getTotalProbability() {
    return totalProbability;
  }

  @Override
  public AbstractAtom getAtomAt(int position) {
    return surface.get(position);

  }

  @Override
  public ListIterator<AbstractAtom> getIterator() {
    return surface.listIterator();
  }

}
