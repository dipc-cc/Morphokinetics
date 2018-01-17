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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
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

  public void setDistancePerStep(int sourceType, int hopsPerStep) {
    desiredHopDistancesMap.put(sourceType, hopsPerStep);
  }

  public boolean isAccelerationEnabled(int sourceType) {
    return desiredHopDistancesMap.containsKey(sourceType);
  }

}
