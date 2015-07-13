/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom.diffusion.devitaAccelerator;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class HopsPerStep {

  private final Map<Integer, Integer> desiredHopDistancesMap = new HashMap();

  public int getDistancePerStep(int sourceType, int destinationType) {

    if (sourceType != destinationType) {
      return 1;
    }
    Integer hopsPerSteps = desiredHopDistancesMap.get(sourceType);

    if (hopsPerSteps != null) {
      return hopsPerSteps;
    } else {
      return 1;
    }
  }

  public void setDistancePerStep(int sourceType, int hops_per_step) {
    desiredHopDistancesMap.put(sourceType, hops_per_step);
  }

  public boolean isAccelerationEnabled(int sourceType) {
    return desiredHopDistancesMap.containsKey(sourceType);
  }

}
