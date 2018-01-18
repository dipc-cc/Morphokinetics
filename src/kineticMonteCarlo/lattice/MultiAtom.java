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

import kineticMonteCarlo.process.ConcertedProcess;;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class MultiAtom extends Island {
  
  private final ConcertedProcess process;
  /** MultiAtom has a certain direction. */
  private int direction;

  public MultiAtom(int islandNumber) {
    super(islandNumber);
    process = new ConcertedProcess();
  }

  public MultiAtom(Island another) {
    super(another);
    process = new ConcertedProcess();
  }

  public int getDirection() {
    return direction;
  }

  public void setDirection(int direction) {
    this.direction = direction % 3;
  }

  /**
   * Selects a random direction for the multi atom to move.
   *
   * @return random direction out of 2 possible directions.
   */
  public int getRandomMultiAtomDirection() {
    double random = StaticRandom.raw();
    /*AbstractGrowthAtom atom1 = atoms.get(0);
    AbstractGrowthAtom atom2 = atoms.get(1);
    int direction = -1;
    for (int i = 0; i < atom1.getNumberOfNeighbours(); i++) {
      AbstractGrowthAtom neighbour = atom1.getNeighbour(i);
      if (neighbour.equals(atom2)) {
        direction = i;
        break;
      }
    }//*/
    if (random > 0.5) { // half of the times, the other direction
      return (direction + 3) % 6;
    }
    return direction;
  }
}
