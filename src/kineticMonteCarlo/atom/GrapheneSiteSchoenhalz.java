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
package kineticMonteCarlo.atom;

import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import ratesLibrary.GrapheneSchoenhalzRates;

/**
 * Based on paper P. Gaillard, T. Chanier, L. Henrard, P. Moskovkin, S. Lucas. Surface Science,
 * Volumes 637–638, July–August 2015, Pages 11-18, http://dx.doi.org/10.1016/j.susc.2015.02.014.
 *
 * @author J. Alberdi-Rodriguez
 */
public class GrapheneSiteSchoenhalz extends GrapheneSite {

  private GrapheneSchoenhalzRates rates;

  private GrapheneSiteSchoenhalz[] neighbours = new GrapheneSiteSchoenhalz[12];
  
  public GrapheneSiteSchoenhalz(int id, short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(id, iHexa, jHexa, distancePerStep);
    setNumberOfNeighbours(9);
    rates = new GrapheneSchoenhalzRates();
  }
  
  @Override
  public void setNeighbours(GrapheneSite[] neighbours) {
    super.setNeighbours(neighbours);
    for (int i = 0; i < neighbours.length; i++) {
      this.neighbours[i] = (GrapheneSiteSchoenhalz) neighbours[i];
    }
  }  
  
  @Override
  public GrapheneSiteSchoenhalz getNeighbour(int pos) {
    return neighbours[pos];
  }
 
  /**
   * Only BULK atom types are considered immobile atoms.
   *
   * @return true if current atom can be moved, false otherwise.
   */
  @Override
  public boolean isEligible() {
    return isOccupied() && (getType() <= KINK);
  }
  
  /**
   * Probability to jump to given neighbour position. Only zigzag edge diffusion is a jump to second
   * neighbour.
   *
   * @param originType
   * @param pos
   * @return probability
   */
  @Override
  public double probJumpToNeighbour(int originType, int pos) {
    GrapheneSiteSchoenhalz atom = neighbours[pos];
    if (atom.isOccupied()) {
      return 0;
    }

    int originN1 = getN1();
    int originN2 = getN2();
    int destinationN1 = atom.getN1();
    int destinationN2 = atom.getN2();
    boolean secondNeighbour = false;
    int n3 = getN3(); // in this model we ignore 3rd neighbours
    // Remove neighbour atom
    if (pos < 3) {
      destinationN1--;
    } else if (pos < 9) {
      if (originN1 == 1 && originN2 == 2 && destinationN1 == 1 && destinationN2 == 3) { // probable zigzag edge diffusion
        boolean zigzag = false;
        for (int i = 0; i < 3; i++) {
          GrapheneSiteSchoenhalz tmpAtom = neighbours[i];
          if (tmpAtom.getN1() == 3) {
            zigzag = true;
            break;
          }
        }
        if (zigzag) { // is a zigzag
          secondNeighbour = true;
          destinationN2--;
        }
      } 
      //  dimmer diffusion
      else if (originN1 == 1 && destinationN1 == 1) {
        if (originN2 == 0 && destinationN2 == 1) {
          System.out.println("happened");
        }
      }
      
      else {
        return 0;
      }
    }

    return rates.getRate(originN1, originN2, destinationN1, destinationN2, secondNeighbour, 873);
  }  
 
}
