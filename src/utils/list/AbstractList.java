package utils.list;

import kineticMonteCarlo.atom.AbstractAtom;
import java.util.ListIterator;

public abstract class AbstractList implements IProbabilityHolder {

  protected static final int EVENTS_PER_CLEANUP = 2048;
  private int removalsSinceLastCleanup;
  private boolean autoCleanup;

  private double time;
  private double depositionProbability;
  private int totalAtoms;
  /** Sum of all probabilities. Useful to measure the time. */
  private double totalProbability;
  private IProbabilityHolder parent;
  private int level;
  
  public AbstractList() {
    time = 0;
    depositionProbability = 0;
    autoCleanup = false;
    removalsSinceLastCleanup = 0;
  }

  public abstract void addAtom(AbstractAtom a);
  
  public abstract void deleteAtom(AbstractAtom a);

  public abstract AbstractAtom nextEvent();

  public double getTime() {
    return time;
  }
  
  public void addTime(double time) {
    this.time += time;
  }

  public abstract int cleanup();

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

  public int getRemovalsSinceLastCleanup() {
    return removalsSinceLastCleanup;
  }
  
  public void resetRemovalsSinceLastCleanup() {
    removalsSinceLastCleanup = 0;
  }
  
  public void addRemovalsSinceLastCleanup() {
    removalsSinceLastCleanup++;
  }

  void setTotalProbability(double prob) {
    this.totalProbability = prob;
  }
  
  public void setParent(IProbabilityHolder parent) {
    this.parent = parent;
  }
  
  public IProbabilityHolder getParent() {
    return parent;
  }

  public boolean autoCleanup() {
    return autoCleanup;
  }
  
  public abstract double getTotalProbabilityFromList();

  /**
   *
   * @return total probability (always >= 0)
   */
  public double getTotalProbability() {
    return totalProbability > 0 ? totalProbability : 0;
    //return totalProbability;
  }

  public void reset() {
    time = 0;
    totalProbability = 0;
    totalAtoms = 0;
    removalsSinceLastCleanup = 0;
  }
    
  public abstract AbstractAtom getAtomAt(int pos);

  public abstract int getSize();

  public abstract ListIterator getIterator();

  public int getTotalAtoms() {
    return totalAtoms;
  }

  public void setTotalAtoms(int totalAtoms) {
    this.totalAtoms = totalAtoms;
  }

  /**
   * @return the level
   */
  public int getLevel() {
    return level;
  }

  /**
   * @param level the level to set
   */
  public final void setLevel(int level) {
    this.level = level;
  }
}
