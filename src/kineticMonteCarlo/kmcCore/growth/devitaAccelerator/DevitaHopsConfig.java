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
package kineticMonteCarlo.kmcCore.growth.devitaAccelerator;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
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
