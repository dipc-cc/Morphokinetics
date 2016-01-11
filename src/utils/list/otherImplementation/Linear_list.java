package utils.list.otherImplementation;

import java.util.LinkedList;
import java.util.ListIterator;
import kineticMonteCarlo.atom.AbstractAtom;
import utils.StaticRandom;
import utils.list.AbstractList;

/**
 *
 *
 * Linked list basic implementation
 *
 */
public class Linear_list extends AbstractList {

  private final LinkedList<AbstractAtom> surface;

  public Linear_list() {
    super();
    surface = new LinkedList();
    this.setLevel(-1);
  }

  @Override
  public double getTotalProbabilityFromList() {
    double totalprobability = 0;

    ListIterator<AbstractAtom> LI = surface.listIterator();
    while (LI.hasNext()) {
      AbstractAtom AC = LI.next();
      if (AC.isEligible()) {
        totalprobability += AC.getProbability();

      }
    }
    return totalprobability;
  }

  @Override
  public void reset() {
    super.reset();
    surface.clear();
    setTotalAtoms(0);
  }

  @Override
  public int cleanup() {

    int temp = getTotalAtoms();
    ListIterator<AbstractAtom> LI = surface.listIterator();
    while (LI.hasNext()) {
      AbstractAtom AC = LI.next();
      if (!AC.isEligible()) {
        LI.remove();
        setTotalAtoms(getTotalAtoms() - 1);
      }
    }
    return (temp - getTotalAtoms());
  }

  @Override
  public void addAtom(AbstractAtom a) {
    surface.addFirst(a);
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
    double prob_current = 0;
    ListIterator<AbstractAtom> LI = surface.listIterator();
    AbstractAtom AC = null;
    outside:
    while (LI.hasNext()) {
      AC = LI.next();
      prob_current += AC.getProbability();
      if (prob_current > position) {
        LI.remove();
        setTotalAtoms(getTotalAtoms() - 1);
        return AC;
      }
    }

    if (AC != null) {
      LI.remove();
      setTotalAtoms(getTotalAtoms() - 1);
    }

    return AC;
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
