package utils.list;

import java.util.ArrayList;
import java.util.ListIterator;
import kineticMonteCarlo.atom.AbstractAtom;
import utils.edu.cornell.lassp.houle.rngPack.RandomSeedable;

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
    double totalprobability = 0;

    ListIterator<AbstractAtom> li = surface.listIterator();
    while (li.hasNext()) {
      AbstractAtom atom = li.next();
      if (atom.isEligible()) {
        totalprobability += atom.getProbability();

      }
    }
    return totalprobability;
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
        AC.setOnList(null);
        li.remove();
        totalAtoms--;
      }
    }
    return (tmp - totalAtoms);
  }

  @Override
  public void addAtom(AbstractAtom a) {
    surface.add(0, a);
    a.setOnList(this);
    totalProbability += a.getProbability();
    totalAtoms++;
  }

  @Override
  public AbstractAtom nextEvent(RandomSeedable rng) {
    removalsSinceLastCleanup++;
    if (autoCleanup && removalsSinceLastCleanup > EVENTS_PER_CLEANUP) {
      this.cleanup();
      removalsSinceLastCleanup = 0;
    }

    double position = rng.raw() * (totalProbability + depositionProbability);

    time -= Math.log(rng.raw()) / (totalProbability + depositionProbability);

    if (position < depositionProbability) {
      return null; //toca añadir un átomo nuevo
    }
    position -= depositionProbability;
    double prob_current = 0;

    AbstractAtom AC = null;
    outside:
    for (int i = 0; i < surface.size(); i++) {
      AC = surface.get(i);
      prob_current += AC.getProbability();
      if (prob_current >= position) {
        surface.remove(i);
        totalAtoms--;
        return AC;
      }
    }

    if (AC != null) {
      surface.remove(surface.size() - 1);
      totalAtoms--;
    }

    return AC;
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
