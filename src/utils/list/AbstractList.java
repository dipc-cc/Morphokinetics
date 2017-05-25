package utils.list;

import kineticMonteCarlo.atom.AbstractAtom;
import java.util.ListIterator;
import utils.StaticRandom;

public abstract class AbstractList implements IProbabilityHolder {

  protected static final int EVENTS_PER_CLEANUP = 2048;
  public static final byte ADSORPTION = 0;
  public static final byte DESORPTION = 1;
  public static final byte REACTION = 2;
  public static final byte DIFFUSION = 3;
  
  private int removalsSinceLastCleanup;
  private boolean autoCleanup;

  private double time;
  private double depositionProbability;
  private double desorptionProbability;
  private double reactionProbability;
  private int totalAtoms;
  /** Sum of all probabilities. Useful to measure the time. */
  private double totalProbability;
  private IProbabilityHolder parent;
  private int level;
  private boolean computeTime;
  private double deltaTime;
  
  public AbstractList() {
    time = 0;
    depositionProbability = 0;
    desorptionProbability = 0;
    reactionProbability = 0;
    autoCleanup = false;
    removalsSinceLastCleanup = 0;
    deltaTime = 0;
    computeTime = true;
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
  public void addTime() {
    deltaTime = getDeltaTime(computeTime);
    computeTime = false;
    time += deltaTime;
  }
  
  public double getDeltaTime(boolean compute) {
    if (compute) {
      deltaTime = -Math.log(StaticRandom.raw()) / (getTotalProbability() + getDepositionProbability() 
              + getDesorptionProbability() + getReactionProbability());
      return deltaTime;
    } else {
      return deltaTime;
    }
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
  
  public double getDesorptionProbability() {
    return desorptionProbability;
  }

  public void setDesorptionProbability(double desorptionProbability) {
    this.desorptionProbability = desorptionProbability;
  }

  public double getReactionProbability() {
    return reactionProbability;
  }

  public void setReactionProbability(double reactionProbability) {
    this.reactionProbability = reactionProbability;
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

  public void setTotalProbability(double prob) {
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
   * Total hops probability.
   * 
   * @return total probability (always >= 0)
   */
  public double getTotalProbability() {
    return totalProbability > 0 ? totalProbability : 0;
  }
  
  /**
   * Total hops probability plus deposition probability.
   * 
   * @return total movement probability + deposition probability
   */
  public double getGlobalProbability() {
    return getTotalProbability() + getDepositionProbability() + getDesorptionProbability() + getReactionProbability();
  }

  public void reset() {
    time = 0;
    totalProbability = 0;
    totalAtoms = 0;
    removalsSinceLastCleanup = 0;
    computeTime = true;
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
  
  /**
   * Equivalent to nextEvent, but only valid for catalysis.
   *
   * @return next reaction type that should be executed.
   */
  public byte nextReaction() {
    double position = StaticRandom.raw() * (getTotalProbability() + getDepositionProbability()
            + getDesorptionProbability() + getReactionProbability());

    addTime();

    if (position < getDepositionProbability()) {
      return ADSORPTION;
    }
    if (position < getDepositionProbability() + getDesorptionProbability()) {
      return DESORPTION;
    }
    if (position < getDepositionProbability() + getDesorptionProbability() + getReactionProbability()) {
      return REACTION;
    }
    return DIFFUSION;
  }
}
