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

import kineticMonteCarlo.atom.AbstractGrowthSite;
import kineticMonteCarlo.lattice.IDevitaLattice;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class DevitaAccelerator {

  private final IDevitaLattice lattice;
  private final Map<Integer, Integer> remainingHopsMap;
  private final Map<Integer, DevitaHopsConfig> devitaConfig;
  private HopsPerStep hopsPerStep;

  public DevitaAccelerator(IDevitaLattice lattice, HopsPerStep hopsPerSteps) {
    this.lattice = lattice;
    this.hopsPerStep = hopsPerSteps;
    remainingHopsMap = new HashMap();
    devitaConfig = new HashMap();
  }

  public void tryToSpeedUp(int type, DevitaHopsConfig config) {
    hopsPerStep.setDistancePerStep(type, 1);
    devitaConfig.put(type, config);
    updateRemainingHops(type, 0);
  }

  public AbstractGrowthSite chooseRandomHop(AbstractGrowthSite originAtom) {
    int originAtomType = (int) originAtom.getType();

    if (!hopsPerStep.isAccelerationEnabled(originAtomType)) {
      return originAtom.chooseRandomHop();
    }

    int desiredHopDistance = hopsPerStep.getDistancePerStep(originAtomType, originAtomType);
    int remainingHops = desiredHopDistance * desiredHopDistance + remainingHopsMap.get(originAtomType);

    AbstractGrowthSite destinationAtom;

    int remainingDistance = (int) Math.sqrt(remainingHops);
    int possibleDistance = lattice.getAvailableDistance(originAtom, remainingDistance);

    if (possibleDistance <= 0) {
      destinationAtom = originAtom.chooseRandomHop();
      possibleDistance = 1;
    } else {
      destinationAtom = lattice.getFarSite(originAtom, possibleDistance);
    }

    remainingHops -= possibleDistance * possibleDistance;
    updateRemainingHops(originAtomType, remainingHops);
    updateDesiredHopDistances(remainingHops, desiredHopDistance, originAtomType);

    return destinationAtom;
  }

  private void updateRemainingHops(int originAtomType, int pendingJumps) {
    remainingHopsMap.put(originAtomType, pendingJumps);
  }

  private void updateDesiredHopDistances(int pendingJumps, int desiredHopDistance, int originAtomType) {
    DevitaHopsConfig config = devitaConfig.get(originAtomType);

    if (pendingJumps < config.getMinAccumulatedSteps() && desiredHopDistance < config.getMaxDistanceHops()) {
      hopsPerStep.setDistancePerStep(originAtomType, desiredHopDistance + 1);
    }
    if (pendingJumps > config.getMaxAccumulatedSteps() && desiredHopDistance > config.getMinDistanceHops()) {
      hopsPerStep.setDistancePerStep(originAtomType, desiredHopDistance >> 1);
    }
  }
}
