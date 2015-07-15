package kineticMonteCarlo.list;

import kineticMonteCarlo.atom.AbstractAtom;
import java.util.ListIterator;
import utils.edu.cornell.lassp.houle.rngPack.RandomSeedable;

public abstract class AbstractList implements IProbabilityHolder {

  protected static final int EVENTS_PER_CLEANUP = 2048;
  protected int removalsSinceLastCleanup = 0;
  protected boolean autoCleanup = false;

  protected double time;
  protected double depositionProbability = 0;
  protected int totalAtoms;
  protected double totalProbability;
  protected IProbabilityHolder parent;
  protected int level;

  public abstract void addAtom(AbstractAtom a);

  public abstract AbstractAtom nextEvent(RandomSeedable RNG);

  public double getTime() {
    return time;
  }

  public abstract int cleanup();

  public int getTotalAtoms() {
    return totalAtoms;
  }

  public AbstractList autoCleanup(boolean auto) {
    this.autoCleanup = auto;
    return this;
  }

  public double getDepositionProbability() {
    return depositionProbability;
  }

  public void setDepositionProbability(double depositionProbability) {
    this.depositionProbability = depositionProbability;
  }

  @Override
  public void addTotalProbability(double prob) {
    if (prob != 0) {
      totalProbability += prob;
      if (this.parent != null) {
        this.parent.addTotalProbability(prob);
      }
    }
  }

  public void setParent(IProbabilityHolder parent) {
    this.parent = parent;
  }

  public abstract double getTotalProbabilityFromList();

  public abstract double getTotalProbability();

  public abstract void reset();

  public abstract AbstractAtom getAtomAt(int pos);

  public abstract int getSize();

  public abstract ListIterator getIterator();
}
