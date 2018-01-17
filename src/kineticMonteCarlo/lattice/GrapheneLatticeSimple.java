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

import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.GrapheneAtom;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class GrapheneLatticeSimple extends GrapheneLattice {
  
  public GrapheneLatticeSimple(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep, Class<?> inputClass) {
    super(hexaSizeI, hexaSizeJ, modified, distancePerStep, inputClass);
  }
  /**
   * 
   * @param a atom to deposit.
   * @param forceNucleation ignored.
   */
  @Override
  public void deposit(AbstractGrowthAtom a, boolean forceNucleation) {
    GrapheneAtom atom = (GrapheneAtom) a;
    atom.setOccupied(true);

    for (int i = 0; i < 3; i++) {
      add1stOccupiedNeighbour(atom.getNeighbour(i));
    }
    
    addAtom(atom);
    if (atom.getNeighbourCount() > 0) {
      addBondAtom(atom);
    }
    atom.resetProbability();
  }
  
  @Override
  public double extract(AbstractGrowthAtom a) {
    GrapheneAtom atom = (GrapheneAtom) a;
    atom.setOccupied(false);
    double probabilityChange = a.getProbability();
    
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      remove1stOccupiedNeighbour(atom.getNeighbour(i));
    }

    if (atom.getN1() > 0) {
      addBondAtom(atom);
    }

    atom.resetProbability();
    atom.setList(false);
    return probabilityChange;
  }
  
  /**
   * Éste lo ejecutan los primeros vecinos
   * @param neighbourAtom neighbour atom of the original atom
   * @param originType type of the original atom
   * @param forceNucleation
   */
  private void add1stOccupiedNeighbour(GrapheneAtom neighbourAtom) {
    byte newType = (byte) (neighbourAtom.getType() + 1);
    if (newType > 6) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is >6, which is in practice impossible");
    }
    neighbourAtom.setN1(1);

    // Always changes the type of neighbour
    neighbourAtom.setType(newType);
    addAtom(neighbourAtom);
    if (neighbourAtom.getN1() > 0 && !neighbourAtom.isOccupied()) {
      addBondAtom(neighbourAtom);
    }
  }
  
  /**
   * Computes the removal of one mobile atom.
   * 
   * @param neighbourAtom neighbour atom of the original atom
   */
  private void remove1stOccupiedNeighbour(GrapheneAtom neighbourAtom) {
    byte newType = (byte) (neighbourAtom.getType() - 1);
    if (newType < 0) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is <0, which is in practice impossible");
    }
    neighbourAtom.setN1(-1); // remove one mobile atom (original atom has been extracted)

    neighbourAtom.setType(newType);
    addAtom(neighbourAtom);
    if (neighbourAtom.getN1() > 0 && !neighbourAtom.isOccupied()) {
      addBondAtom(neighbourAtom);
    }
  }
}
