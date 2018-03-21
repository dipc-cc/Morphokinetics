/*
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
package kineticMonteCarlo.activationEnergy;

import basic.Parser;
import static kineticMonteCarlo.process.ConcertedProcess.SINGLE;
import kineticMonteCarlo.site.AbstractGrowthSite;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ConcertedActivationEnergy extends ActivationEnergy {
  
  private int[][] transitionsHistogram;
  private Double[][] histogramPossibleTmp;
  private Double[][] histogramPossibleCounterTmp;
  private double previousProbability;

  
  public ConcertedActivationEnergy(Parser parser) {
    super(parser);
    transitionsHistogram = new int[12][16];
    setLengthI(12);
    setLengthJ(16);
  }
  
  public void addTransitions(AbstractGrowthSite atom) {
    if (atom.isOccupied()) {
      updateTransitions(atom, 1);
    }
  }

  public void removeTransitions(AbstractGrowthSite atom) {
    updateTransitions(atom, -1);
  }
  
  private void updateTransitions(AbstractGrowthSite atom, int add) {
    byte type;
    if (add == -1) {
      type = atom.getOldType();
    } else {
      type = atom.getRealType();
    }
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      byte neighbourType = atom.getEdgeType(SINGLE, i);
      if (neighbourType > -1) {
        transitionsHistogram[type][neighbourType] += add;
      }
    }
  }
  
  public void updatePossibles(double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy()) {
      if (previousProbability != totalAndDepositionProbability) {
        histogramPossibleTmp = initDouble();
        histogramPossibleCounterTmp = initDouble();
        for (int i = 0; i < transitionsHistogram.length; i++) {
          for (int j = 0; j < transitionsHistogram[0].length; j++) {
            for (int k = 0; k < transitionsHistogram[i][j]; k++) {
              updatePossible(i, j, elapsedTime);
              histogramPossibleTmp[i][j] += elapsedTime;
              histogramPossibleCounterTmp[i][j]++;
              updateCounter(i, j);
            }
          }
          //previousProbability = totalAndDepositionProbability;
        }
      } else { // Total probability is the same as at the previous instant, so multiplicities are the same and we can use cached data
        for (int i = 0; i < 2; i++) {
          for (int j = 0; j < 2; j++) {
            updatePossible(histogramPossibleTmp);
            updateCounter(histogramPossibleCounterTmp);
          }
        }
      }
    }
  }
  
  @Override
  public void reset() {
    super.reset();
    transitionsHistogram = new int[12][16];
  }
}
