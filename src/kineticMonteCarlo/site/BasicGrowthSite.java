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
package kineticMonteCarlo.site;

import java.util.Arrays;
import java.util.List;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthSite extends AbstractGrowthSite {

  public static final byte TERRACE = 0;
  public static final byte EDGE = 1;
  public static final byte KINK = 2;
  public static final byte ISLAND = 3;

  private final static BasicGrowthTypesTable TYPE_TABLE = new BasicGrowthTypesTable();

  private final BasicGrowthSite[] neighbours = new BasicGrowthSite[4];

  public BasicGrowthSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa, 4, 2);
  }
  
  /**
   * For the orientation they are only available two position. Orientation is either | or _. It is
   * assumed that current atom is of type EDGE.
   *
   * @return horizontal (0) or vertical (1).
   */
  @Override
  public int getOrientation() {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      if (neighbours[i].isOccupied()) {
        return i % 2;
      }
    }
    return -1;
  }

  @Override
  public void setNeighbour(AbstractSurfaceSite a, int pos) {
    neighbours[pos] = (BasicGrowthSite) a;
  }

  @Override
  public BasicGrowthSite getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  @Override
  public List getAllNeighbours() {
    return Arrays.asList(neighbours);
  }
  
  /**
   * 
   * @param pos position of the neighbour
   * @return change in the probability
   */
  @Override
  public double updateOneBound(int pos) {
    // Store previous probability
    double probabilityChange = -getBondsProbability(pos);
    // Update to the new probability and save
    setBondsProbability(probJumpToNeighbour(1, pos), pos);
    probabilityChange += getBondsProbability(pos);
    addProbability(probabilityChange);

    return probabilityChange;
  }

  @Override
  public boolean isEligible() {
    return isOccupied() && getType() < ISLAND;
  }
  
  @Override
  public boolean isPartOfImmobilSubstrate() {
    return isOccupied() && getType() == ISLAND;
  }
  
  /**
   * Calculates the new atom type when adding or removing a neighbour.
   *
   * @param addToNeighbour variation of the number of the number of neighbours. Must be -1, 0 or 1
   * @return new type
   */
  public byte getNewType(int addToNeighbour) {
    return TYPE_TABLE.getCurrentType(getOccupiedNeighbours() + addToNeighbour);
  }
  
  /**
   * Returns the type of the neighbour atom if current one would not exist.
   *
   * @param position position is the original one; has to be inverted.
   * @return the type.
   */
  @Override
  public byte getTypeWithoutNeighbour(int position) {
    return TYPE_TABLE.getCurrentType(getOccupiedNeighbours() - 1);
  }

  @Override
  public boolean areTwoTerracesTogether() {
    if (getOccupiedNeighbours() != 2) {
      return false;
    }
    int cont = 0;
    int i = 0;
    while (cont < 2 && i < getNumberOfNeighbours()) {
      if (neighbours[i].isOccupied()) {
        if (neighbours[i].getType() != TERRACE) {
          return false;
        }
        cont++;
      }
      i++;
    }
    return true;
  }

  @Override
  public AbstractGrowthSite chooseRandomHop() {
    double linearSearch = StaticRandom.raw() * getProbability();

    double sum = 0;
    int cont = 0;
    while (true) {
      sum += getBondsProbability(cont++);
      if (sum >= linearSearch) {
        break;
      }
      if (cont == getNumberOfNeighbours()) {
        break;
      }
    }
    cont--;

    return neighbours[cont];
  }

  @Override
  public void obtainRateFromNeighbours() {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(probJumpToNeighbour(1, i), i);
      addProbability(getBondsProbability(i));
    }
    // check if it is really a kink
    if (getType() == KINK) {
      int neighbourPositions = 0;
      for (int i = 0; i < neighbours.length; i++) {
        if (neighbours[i].isOccupied()) {
          neighbourPositions += i;
        }
      }
      // make immobile, if two neighbours of the kink are not consecutive
      if (neighbourPositions % 2 == 0) { 
        for (int i = 0; i < neighbours.length; i++) {
          neighbourPositions += i;
          addProbability(-getBondsProbability(i)); 
          setBondsProbability(0, i);
        }
        setType(ISLAND);
      }
    }
  }
  
  @Override
  public double probJumpToNeighbour(int ignored, int position) {
    if (neighbours[position].isOccupied()) {
      return 0;
    }

    byte originType = getType();
    byte destination = neighbours[position].getTypeWithoutNeighbour(position);

    return getProbability(originType, destination);
  }

  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  @Override
  public void clear() {
    
    super.clear();
    setType(TERRACE);
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(0, i);
    }
  }
 
}
