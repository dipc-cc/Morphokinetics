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
public class LinearList extends AbstractList implements IProbabilityHolder{

  private final ArrayList<AbstractAtom> surface;

  public LinearList() {
    super();
    surface = new ArrayList();
    this.setLevel(-1);
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
    super.reset();
    surface.clear();
  }

  @Override
  public int cleanup() {

    int tmp = getTotalAtoms();
    ListIterator<AbstractAtom> li = surface.listIterator();
    while (li.hasNext()) {
      AbstractAtom AC = li.next();
      if (!AC.isEligible()) {
        AC.setList(null);
        li.remove();
        setTotalAtoms(getTotalAtoms() - 1);
      }
    }
    return (tmp - getTotalAtoms());
  }

  @Override
  public void addAtom(AbstractAtom a) {
    surface.add(0, a);
    a.setList(this);
    addTotalProbability(a.getProbability());
    setTotalAtoms(getTotalAtoms() + 1);
  }

  @Override
  public AbstractAtom nextEvent() {
    addRemovalsSinceLastCleanup();
    if (autoCleanup() && getRemovalsSinceLastCleanup() > EVENTS_PER_CLEANUP) {
      this.cleanup();
      resetRemovalsSinceLastCleanup();
    }

    double position = StaticRandom.raw() * (getTotalProbability() + getDepositionProbability());

    addTime(-Math.log(StaticRandom.raw()) / (getTotalProbability() + getDepositionProbability()));

    if (position < getDepositionProbability()) {
      return null; //toca añadir un átomo nuevo
    }
    position -= getDepositionProbability();
    double currentProbability = 0;

    AbstractAtom atom = null;
    outside:
    for (int i = 0; i < surface.size(); i++) {
      atom = surface.get(i);
      currentProbability += atom.getProbability();
      if (currentProbability >= position) {
        surface.remove(i);
        setTotalAtoms(getTotalAtoms() - 1);
        return atom;
      }
    }

    if (atom != null) {
      surface.remove(surface.size() - 1);
      setTotalAtoms(getTotalAtoms() - 1);
    }

    return atom;
  }

  @Override
  public int getSize() {
    return surface.size();
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
