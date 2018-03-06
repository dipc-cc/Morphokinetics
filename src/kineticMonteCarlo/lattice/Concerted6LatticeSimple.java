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
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractGrowthSite;
import kineticMonteCarlo.atom.ConcertedSite;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Concerted6LatticeSimple extends AgUcLatticeSimple {
  
  public Concerted6LatticeSimple(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {
    super(hexaSizeI, hexaSizeJ, modified, distancePerStep, 2);
  }
  
  @Override 
  public void deposit(AbstractGrowthSite atom, boolean forceNucleation) {
    atom.setOccupied(true);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      addNeighbour(atom.getNeighbour(i));
    }
    addOccupied();
    atom.resetProbability();
  }
  
  @Override
  public double extract(AbstractGrowthSite atom) {
    atom.setOccupied(false);
    double probabilityChange = atom.getProbability();
    
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      removeNeighbour(atom.getNeighbour(i));
    }

    atom.resetProbability();
    atom.setList(false);
    subtractOccupied();
    return probabilityChange;
  }
  
  /**
   * Éste lo ejecutan los primeros vecinos
   *
   * @param neighbourAtom neighbour atom of the original atom
   * @param originType type of the original atom
   * @param forceNucleation
   */
  private void addNeighbour(AbstractGrowthSite neighbourAtom) {
    neighbourAtom.addOccupiedNeighbour(1);
    byte newType = (byte) (neighbourAtom.getType() + 1);
    if (newType > 6) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is >6, which is in practice impossible");
    }
    ((ConcertedSite) neighbourAtom).addNMobile(1);

    // Always changes the type of neighbour
    neighbourAtom.setType(newType);
  }
  
  /**
   * Computes the removal of one mobile atom.
   * 
   * @param neighbourAtom neighbour atom of the original atom
   */
  private void removeNeighbour(AbstractGrowthSite neighbourAtom) {
    neighbourAtom.addOccupiedNeighbour(-1);
    byte newType = (byte) (neighbourAtom.getType() - 1);
    if (newType < 0) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is <0, which is in practice impossible " + neighbourAtom + neighbourAtom.getType());
    }
    ((ConcertedSite) neighbourAtom).addNMobile(-1); // remove one mobile atom (original atom has been extracted)

    neighbourAtom.setType(newType);
  }
}
