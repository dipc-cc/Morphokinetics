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
import kineticMonteCarlo.site.AbstractSite;
import java.util.ListIterator;
import kineticMonteCarlo.process.BdaProcess;
import kineticMonteCarlo.process.CatalysisProcess;
import static kineticMonteCarlo.process.CatalysisProcess.ADSORPTION;
import kineticMonteCarlo.process.ConcertedProcess;
import utils.StaticRandom;

public abstract class AbstractList implements IProbabilityHolder {

  protected static final int EVENTS_PER_CLEANUP = 2048;
  
  private int removalsSinceLastCleanup;
  private boolean autoCleanup;

  private double time;
  private double[] rates;
  private int totalAtoms;
  private IProbabilityHolder parent;
  private int level;
  private boolean computeTime;
  private double deltaTime;
  private final int ratesLength;
  private final int diffusionIndex;
  
  public AbstractList(Parser parser) {
    time = 0;
    switch (parser.getCalculationMode()) {
      case "catalysis":
        ratesLength = 4; // Adsorption, desorption, reaction, diffusion
        diffusionIndex = CatalysisProcess.DIFFUSION;
        break;
      case "concerted":
        ratesLength = 3; // Adsorption, single (diffusion), concerted (diffusion)
        diffusionIndex = ConcertedProcess.SINGLE;
        break;
      case "bda":
        ratesLength = 6; // Adsorption, desorption, reaction (not used), diffusion, rotation, transformation
        diffusionIndex = BdaProcess.DIFFUSION;
        break;
      default:
        ratesLength = 2; // Adsorption, diffusion
        diffusionIndex = 1;
    }
    rates = new double[ratesLength];
    for (int i = 0; i < ratesLength; i++) {
      rates[i] = 0.0;
    }
    autoCleanup = false;
    removalsSinceLastCleanup = 0;
    deltaTime = 0;
    computeTime = true;
  }

  public abstract void addAtom(AbstractSite a);
  
  public abstract void deleteAtom(AbstractSite a);

  public abstract AbstractSite nextEvent();

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

  double getDepositionProbability() {
    return rates[ADSORPTION];
  }

  public void setDepositionProbability(double depositionProbability) {
    rates[ADSORPTION] = depositionProbability;
  }

  public void setRates(double[] rates) {
    this.rates = rates;
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

  void setDiffusionProbability(double probability) {
    this.rates[diffusionIndex] = probability;
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
    return rates[diffusionIndex] > 0 ? rates[diffusionIndex] : 0;
  }
  
  /**
   * Total hops probability plus deposition probability plus desorption probability plus reaction
   * probability.
   *
   * @return total movement + deposition + desorption + reaction.
   */
  public double getGlobalProbability() {
    double globalProbability = 0.0;
    for (int i = 0; i < rates.length; i++) {
      globalProbability += rates[i];
    }
    return globalProbability;
  }

  public void reset() {
    time = 0;
    if (ratesLength > 2) { // adsorption rate does not have to be reset in growth.
      for (int i = 0; i < rates.length; i++) {
        rates[i] = 0;
      }
    } else {
      rates[diffusionIndex] = 0;
    }
    totalAtoms  = 0;
    removalsSinceLastCleanup = 0;
    computeTime = true;
  }
  
  /**
   * Method to set time to zero. Useful for catalysis to measure TOF.
   */
  public void resetTime() {
    time = 0;
  }
    
  public abstract AbstractSite getAtomAt(int pos);

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

    double sumRate = 0.0;
    for (byte process = 0; process < rates.length; process++) { //Adsorption, desorption, reaction, diffusion
      sumRate += rates[process];
      if (position < sumRate) {
        return process;
      }
    }
    return (byte) (rates.length - 1);
  }
}
