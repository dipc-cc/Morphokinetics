package utils.list;

import basic.Parser;
import java.util.ArrayList;
import java.util.ListIterator;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.kmcCore.growth.ActivationEnergy;
import utils.StaticRandom;

/**
 * Linked list basic implementation.
 */
public class LinearList extends AbstractList implements IProbabilityHolder{

  private final ArrayList<AbstractAtom> surface;
  /**
   * Stores if the current totalProbability and the probability calculated from the list are the same.
   */
  private boolean clean;
  private double Ri_DeltaI;
  
  private final ActivationEnergy activationEnergy;
  
  public LinearList(Parser parser, ActivationEnergy activationEnergy) {
    super();
    surface = new ArrayList();
    this.setLevel(-1);
    clean = false;
    Ri_DeltaI = 0.0;
    
    this.activationEnergy = activationEnergy;
  }
  
  @Override
  public double getTotalProbabilityFromList() {
    if (clean) {
      return getTotalProbability();
    } else {
      double totalProbability = 0;

      ListIterator<AbstractAtom> li = surface.listIterator();
      while (li.hasNext()) {
        AbstractAtom atom = li.next();
        if (atom.isEligible()) {
          totalProbability += atom.getProbability();
        }
      }
      clean = true;
      setTotalProbability(totalProbability);
      return totalProbability;
    }
  }  
  
  /**
   * Updates the total probability
   * @param probabilityChange probability change
   */
  @Override
  public void addTotalProbability(double probabilityChange) {
    clean = false;
    setTotalProbability(getTotalProbability() + probabilityChange);
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
    ListIterator<AbstractAtom> li = surface.listIterator();
    while (li.hasNext()) {
      AbstractAtom atom = li.next();
      if (!atom.isEligible()) {
        li.remove();
        setTotalAtoms(getTotalAtoms() - 1);
      }
    }
    return (tmp - getTotalAtoms());
  }

  @Override
  public void addAtom(AbstractAtom atom) {
    clean = false;
    surface.add(0, atom);
    atom.setList(true);
    setTotalAtoms(getTotalAtoms() + 1);
  }
  
  @Override
  public void deleteAtom(AbstractAtom atom) {
    surface.remove(atom);
  }

  @Override
  public AbstractAtom nextEvent() {
    addRemovalsSinceLastCleanup();
    if (autoCleanup() && getRemovalsSinceLastCleanup() > EVENTS_PER_CLEANUP) {
      this.cleanup();
      resetRemovalsSinceLastCleanup();
    }

    double position = StaticRandom.raw() * (getTotalProbability() + getDepositionProbability());
    
    double elapsedTime = -Math.log(StaticRandom.raw()) / (getTotalProbability() + getDepositionProbability());
    addTime(elapsedTime);

    activationEnergy.updatePossibles(surface, getTotalProbability() + getDepositionProbability(), elapsedTime);
    Ri_DeltaI += (getTotalProbability() + getDepositionProbability()) * elapsedTime; // should be always 1

    if (position < getDepositionProbability()) {
      return null; //toca añadir un átomo nuevo
    }
    position -= getDepositionProbability();
    double currentProbability = 0;

    AbstractAtom atom = null;
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
  public AbstractAtom getAtomAt(int position) {
    return surface.get(position);

  }

  @Override
  public ListIterator<AbstractAtom> getIterator() {
    return surface.listIterator();
  }

  public double getRi_DeltaI() {
    return Ri_DeltaI;
  }  
  
  public double[][] getHistogramPossible() {
    return activationEnergy.getHistogramPossible();
  }
  
  public long[][] getHistogramPossibleCounter() {
    return activationEnergy.getHistogramPossibleCounter();
  }
  
}
