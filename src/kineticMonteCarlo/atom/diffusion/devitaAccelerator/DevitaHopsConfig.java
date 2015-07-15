/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom.diffusion.devitaAccelerator;

/**
 *
 * @author Nestor
 */
public class DevitaHopsConfig {

  private int maxAccumulatedSteps = 100;
  private int minAccumulatedSteps = 30;
  private int maxDistanceHops = 5;
  private int minDistanceHops = 1;

  public int getMaxAccumulatedSteps() {
    return maxAccumulatedSteps;
  }

  public DevitaHopsConfig setMaxAccumulatedSteps(int maxAccumulatedSteps) {
    this.maxAccumulatedSteps = maxAccumulatedSteps;
    return this;
  }

  public int getMinAccumulatedSteps() {
    return minAccumulatedSteps;
  }

  public DevitaHopsConfig setMinAccumulatedSteps(int minAccumulatedSteps) {
    this.minAccumulatedSteps = minAccumulatedSteps;
    return this;
  }

  public int getMaxDistanceHops() {
    return maxDistanceHops;
  }

  public DevitaHopsConfig setMaxDistanceHops(int maxDistanceHops) {
    this.maxDistanceHops = maxDistanceHops;
    return this;
  }

  public int getMinDistanceHops() {
    return minDistanceHops;
  }

  public DevitaHopsConfig setMinDistanceHops(int minDistanceHops) {
    this.minDistanceHops = minDistanceHops;
    return this;
  }

}
