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

import kineticMonteCarlo.site.AbstractGrowthSite;
import static kineticMonteCarlo.process.ConcertedProcess.MULTI;
import kineticMonteCarlo.process.MultiAtomProcess;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class MultiAtom extends Island {
  
  private final MultiAtomProcess process;
  /** MultiAtom has a certain direction that goes from atom 0 to atom 1. */
  private int direction;

  public MultiAtom(int islandNumber) {
    super(islandNumber);
    process = new MultiAtomProcess();
  }

  public MultiAtom(Island another) {
    super(another);
    process = new MultiAtomProcess();
  }

  public int getDirection() {
    return direction;
  }

  /**
   * Set the direction of the MultiAtom, from atom at position 0 to atom 1.
   * 
   * @param direction [0-5]
   */
  public void setDirection(int direction) {
    this.direction = direction;
  }

  /**
   * Selects a random direction for the multi atom to move.
   *
   * @return random direction out of 2 possible directions.
   */
  public int getRandomMultiAtomDirection() {
     double randomNumber = StaticRandom.raw() * getRate(MULTI);
    if (randomNumber < getEdgeRate(0)) {
      return (direction + 3) % 6;
    }
    return direction;
  }
  
  public double getEdgeRate(int pos) {
    return process.getEdgeRate(pos);
  }
  
  @Override
  public void addRate(byte ignored, double rate, int pos) {
    process.addRate(rate, pos);
  }
  
  /**
   * Edge 0 is the neighbour of atom 0, and
   * edge 1 is the neighbour of atom 1.
   * 
   * @param pos edge 0 or 1
   * @return type of the possible diffusion: 0 or 1.
   */
  public int getEdgeType(int pos) {
    AbstractGrowthSite atom;
    if (pos == 0) { // get neighbour of atom 0
      atom = getAtomAt(0).getNeighbour((direction + 3) % 6);
    } else { // get neighbour of atom 1
      atom = getAtomAt(1).getNeighbour(direction);
    }
    if (atom.getType() == 2) 
      return 0;
    if (atom.getType() == 3)
      return 1;
    if (atom.getType() == 4)
      return 2;
    if (atom.getType() == 5)
      return 3;
    System.out.println("Error");
    return -1;
  }
}
