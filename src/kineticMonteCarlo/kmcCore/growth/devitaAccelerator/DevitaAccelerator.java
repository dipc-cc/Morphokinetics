/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth.devitaAccelerator;

import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.lattice.IDevitaLattice;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
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

  public AbstractGrowthAtom chooseRandomHop(AbstractGrowthAtom source) {
    int sourceAtomType = (int) source.getType();

    if (!hopsPerStep.isAccelerationEnabled(sourceAtomType)) {
      return source.chooseRandomHop();
    }

    int desiredHopDistance = hopsPerStep.getDistancePerStep(sourceAtomType, sourceAtomType);
    int remainingHops = desiredHopDistance * desiredHopDistance + remainingHopsMap.get(sourceAtomType);

    AbstractGrowthAtom destination;

    int remainingDistance = (int) Math.sqrt(remainingHops);
    int possibleDistance = lattice.getAvailableDistance(source, remainingDistance);

    if (possibleDistance <= 0) {
      destination = source.chooseRandomHop();
      possibleDistance = 1;
    } else {
      destination = lattice.getFarSite(source, possibleDistance);
    }

    remainingHops -= possibleDistance * possibleDistance;
    updateRemainingHops(sourceAtomType, remainingHops);
    updateDesiredHopDistances(remainingHops, desiredHopDistance, sourceAtomType);

    return destination;
  }

  private void updateRemainingHops(int sourceAtomType, int pendingJumps) {
    remainingHopsMap.put(sourceAtomType, pendingJumps);
  }

  private void updateDesiredHopDistances(int pendingJumps, int desiredHopDistance, int sourceAtomType) {
    DevitaHopsConfig config = devitaConfig.get(sourceAtomType);

    if (pendingJumps < config.getMinAccumulatedSteps() && desiredHopDistance < config.getMaxDistanceHops()) {
      hopsPerStep.setDistancePerStep(sourceAtomType, desiredHopDistance + 1);
    }
    if (pendingJumps > config.getMaxAccumulatedSteps() && desiredHopDistance > config.getMinDistanceHops()) {
      hopsPerStep.setDistancePerStep(sourceAtomType, desiredHopDistance >> 1);
    }
  }
}
