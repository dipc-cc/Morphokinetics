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
import java.io.PrintWriter;
import java.util.Iterator;
import kineticMonteCarlo.lattice.Island;
import kineticMonteCarlo.lattice.MultiAtom;
import static kineticMonteCarlo.process.ConcertedProcess.SINGLE;
import kineticMonteCarlo.site.AbstractGrowthSite;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ConcertedActivationEnergy extends ActivationEnergy {
  
  private int[][] transitionsHistogram;
  private Double[] histogramPossibleIsland;
  private Double[] histogramPossibleMultiAtom;

  
  public ConcertedActivationEnergy(Parser parser) {
    super(parser, true);
    transitionsHistogram = new int[12][16];
    setLengthI(12);
    setLengthJ(16);
    histogramPossibleIsland = new Double[9];
    histogramPossibleMultiAtom = new Double[4];
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
      for (int i = 0; i < transitionsHistogram.length; i++) {
        for (int j = 0; j < transitionsHistogram[0].length; j++) {
          if (transitionsHistogram[i][j] > 0) {
            updatePossible(i, j, elapsedTime, transitionsHistogram[i][j]);
            updateCounter(i, j, transitionsHistogram[i][j]);
          }
        }
      }
    }
  }
  
  public void updatePossiblesIslands(Iterator<Island> islands, double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy()) {
      while (islands.hasNext()) {
        Island island = (Island) islands.next();
        if (island.getNumberOfAtoms() < 9) {
          histogramPossibleIsland[island.getNumberOfAtoms()] += elapsedTime;
        }
      }
    }
  }

  public void updatePossiblesMultiAtoms(Iterator<MultiAtom> multiAtoms, double totalAndDepositionProbability, double elapsedTime) {
    if (doActivationEnergyStudy()) {
      // iterate over all MultiAtom of the surface to get all possible hops (only to compute multiplicity)
      while (multiAtoms.hasNext()) {
        MultiAtom multiAtom = (MultiAtom) multiAtoms.next();
        histogramPossibleMultiAtom[multiAtom.getEdgeType(0)] += elapsedTime; // for atom 0
        histogramPossibleMultiAtom[multiAtom.getEdgeType(1)] += elapsedTime; // for atom 1
      }
    }
  }
  
  @Override
  public void reset() {
    super.reset();
    transitionsHistogram = new int[12][16];
    histogramPossibleIsland = initDouble1(9);
    histogramPossibleMultiAtom = initDouble1(4);
  }
  
  @Override
  void printPossibles(PrintWriter print, double time) {
    super.printPossibles(print, time);
    for (int i = 0; i < 9; i++) {
      print.print(histogramPossibleIsland[i] + "\t");
    }
    for (int i = 0; i < histogramPossibleMultiAtom.length; i++) {
      print.print(histogramPossibleMultiAtom[i] + "\t");
    }
  }
}
