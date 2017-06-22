package utils.list;

import kineticMonteCarlo.atom.AbstractAtom;
import java.util.ListIterator;
import static kineticMonteCarlo.atom.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.atom.CatalysisProcess.DESORPTION;
import static kineticMonteCarlo.atom.CatalysisProcess.DIFFUSION;
import static kineticMonteCarlo.atom.CatalysisProcess.REACTION;
import utils.StaticRandom;

public abstract class AbstractList implements IProbabilityHolder {

  protected static final int EVENTS_PER_CLEANUP = 2048;
  
  private int removalsSinceLastCleanup;
  private boolean autoCleanup;

  private double time;
  private double depositionProbability;
  private double desorptionProbability;
  private double reactionProbability;
  private double diffusionProbability;
  private int totalAtoms;
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
      deltaTime = -Math.log(StaticRandom.raw()) / getGlobalProbability();
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

  public void setDiffusionProbability(double probability) {
    this.diffusionProbability = probability;
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
  
  public abstract double getDiffusionProbabilityFromList();

  /**
   * Total hops probability.
   * 
   * @return total probability (always >= 0)
   */
  public double getDiffusionProbability() {
    return diffusionProbability > 0 ? diffusionProbability : 0;
  }
  
  /**
   * Total hops probability plus deposition probability plus desorption probability plus reaction
   * probability.
   *
   * @return total movement + deposition + desorption + reaction.
   */
  public double getGlobalProbability() {
    return getDiffusionProbability() + depositionProbability + desorptionProbability + reactionProbability;
  }

  public void reset() {
    time = 0;
    diffusionProbability = 0;
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
    double position = StaticRandom.raw() * getGlobalProbability();

    addTime();

    if (position < depositionProbability) {
      return ADSORPTION;
    }
    if (position < depositionProbability + desorptionProbability) {
      return DESORPTION;
    }
    if (position < depositionProbability + desorptionProbability + reactionProbability) {
      return REACTION;
    }
    return DIFFUSION;
  }
}
