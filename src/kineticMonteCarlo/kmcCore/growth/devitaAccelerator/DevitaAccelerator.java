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

  public AbstractGrowthAtom chooseRandomHop(AbstractGrowthAtom originAtom) {
    int originAtomType = (int) originAtom.getType();

    if (!hopsPerStep.isAccelerationEnabled(originAtomType)) {
      return originAtom.chooseRandomHop();
    }

    int desiredHopDistance = hopsPerStep.getDistancePerStep(originAtomType, originAtomType);
    int remainingHops = desiredHopDistance * desiredHopDistance + remainingHopsMap.get(originAtomType);

    AbstractGrowthAtom destinationAtom;

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
