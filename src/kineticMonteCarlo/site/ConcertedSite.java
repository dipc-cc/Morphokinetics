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

import java.util.BitSet;
import kineticMonteCarlo.process.ConcertedProcess;
import static kineticMonteCarlo.process.ConcertedProcess.ADSORB;
import static kineticMonteCarlo.process.ConcertedProcess.SINGLE;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ConcertedSite extends AgSiteSimple {
  
  private final ConcertedProcess[] processes;
  
  public ConcertedSite(int id, int i) {
    super(id, i);
    processes = new ConcertedProcess[2];
    processes[ADSORB] = new ConcertedProcess();
    processes[SINGLE] = new ConcertedProcess();
    setProcceses(processes);
  }
  
  public ConcertedSite getRandomNeighbour(byte process) {
    ConcertedSite neighbour;
    double randomNumber = StaticRandom.raw() * getRate(process);
    double sum = 0.0;
    for (int j = 0; j < getNumberOfNeighbours(); j++) {
      sum += processes[process].getEdgeRate(j);
      if (sum > randomNumber) {
        neighbour = (ConcertedSite) getNeighbour(j);
        return neighbour;
      }
    }
    // raise an error
    return null;
  }
  
  /**
   * Resets current atom; TERRACE type, no occupied, no outside and no probability.
   */
  @Override
  public void clear() {
    super.clear();
    setType(TERRACE);
    
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(0, i);
    }
    for (int i = 0; i < processes.length; i++) {
      processes[i].clear();
    }
  }

  public boolean isDimer() {
    if (isOccupied()) {
      if (getOccupiedNeighbours() > 0) {
        //System.out.println("neighbour " + getOccupiedNeighbours());
      }
      if (getOccupiedNeighbours() == 1) {
        for (int i = 0; i < getNumberOfNeighbours(); i++) {
          AbstractGrowthSite neighbour = getNeighbour(i);
          if (neighbour.isOccupied() && neighbour.getOccupiedNeighbours() > 1) {
            return false;
          }
        }
        return true; // only one neighbour atom with one occupied neighbour
      }
    }
    return false;
  }
  
  /**
   * Get probability in the given neighbour position.
   *
   * @param i neighbour position.
   * @return probability (rate).
   */
  @Override
  public double getBondsProbability(int i) {
    return processes[SINGLE].getEdgeRate(i);
  }
  
  /**
   * Defines 11 atom types,subtypes:
   *  0   -> 0
   *  1   -> 1
   *  2,0 -> 2
   *  2,1 -> 3
   *  2,2 -> 4
   *  3,0 -> 5
   *  3,1 -> 6
   *  3,2 -> 7
   *  4,0 -> 8
   *  4,1 -> 9
   *  4,2 -> 10
   *  5   -> 11
   *  6   -> 16
   * with some of them as detaching destination.
   * 
   * @return 0 <= type < 12.
   */
  @Override
  public byte getRealType() {
    byte type = getType();
    BitSet bits = getCode();
    return getTypeCode(type, bits);
  }
  
  private byte getTypeCode(byte type, BitSet bits) {
    byte subtype;
    switch (type) {
      case 0: // no subtype
        return type;
      case 1: // no subtype
        return type;
      case 2: // 3 subtypes
        subtype = getEdgeSubtype(bits);
        return (byte) (type + subtype);
      case 3: // 3 subtypes
        subtype = getKinkSubtype(bits);
        return (byte) (5 + subtype); 
      case 4: // 3 subtypes
        subtype = getType4Subtype(bits);
        return (byte) (8 + subtype);
      case 5: // no subtype
        return 11;
      case 6: // all neighbour atoms occupied
        return 16; // does not really matter
    }
    throw new ArrayIndexOutOfBoundsException("Origin type has to be 0 <= x <= 5");
  }
  
  /**
   * Returns the type of the neighbour atom if current one would not exist. It
   * has to also consider detaching atoms.
   *
   * @param position this position is the original one; has to be inverted.
   * @return the type.
   */
  @Override
  public byte getTypeWithoutNeighbour(int position) {
    int myPositionForNeighbour = (position + 3) % getNumberOfNeighbours();
    if (!getNeighbour(myPositionForNeighbour).isOccupied()) return getType(); // impossible to happen
    
    byte type = (byte) (getType() - 1);
    
    //byte subtype = getRealType();
    BitSet bits = new BitSet(6);
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      AbstractGrowthSite neighbour = getNeighbour(i);
      if (i != myPositionForNeighbour && neighbour.isOccupied()) { // exclude origin atom
        bits.set(i);
      }
    }
    type = getTypeCode(type, bits);
    if (type == 1 || type == 2 || type == 3 || type == 5) {
      AbstractGrowthSite origin = getNeighbour(myPositionForNeighbour);
      if (origin.getType() != 0)
        type = getDetachedType(type, myPositionForNeighbour);
    }
    return type;
  }
  
  /** 
   * When detaching from an island, the energy has to be different.
   * 
   * @param type
   * @param position
   * @return 
   */
  private byte getDetachedType(byte type, int position) {
    int pos2 = position - 1;
    if (pos2 == -1) {
      pos2 = 5;
    }
    AbstractGrowthSite neigh1 = getNeighbour((position + 1) % 6);
    AbstractGrowthSite neigh2 = getNeighbour(pos2);
    int detachedType = 11;
    if (type == 5)
      detachedType = 10;
    if (!neigh1.isOccupied() && !neigh2.isOccupied()) { // it is detaching, no common neighbours
      if (type == 1) {
        return (byte) (type + detachedType);
      }
    }
    return type;
  }
  /**
   * Gets a BitSet of the current occupancy.
   * 
   * @return BitSet
   */
  private BitSet getCode() {
    BitSet bits = new BitSet(6);
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      if (getNeighbour(i).isOccupied()) {// && !neighbours[i].equals(atom)) {
        bits.set(i);
      }
    }
    return bits;
  }
  
  /**
   * Two atoms are together, separated by one position or two positions.
   * 
   * @param bits
   * @return subtype
   */
  private byte getEdgeSubtype(BitSet bits) {
    int first = bits.nextSetBit(0);
    int second = bits.nextSetBit(first+1);
    int distance = getDistance(first, second);
    return (byte) (distance - 1);
  }
  
  /**
   * If the given number from bits is multiple of 21 all atoms are separated,
   * other multiples of 7, all 3 atoms are together.
   *
   * @param bits
   * @return one subtype or the other.
   */
  private byte getKinkSubtype(BitSet bits) {
    int first = bits.nextSetBit(0);
    int second = bits.nextSetBit(first + 1);
    int third = bits.nextSetBit(second + 1);
    int number = (1 << first) | (1 << second) | (1 << third);
    if (number % 21 == 0) { // all three atoms are separated (21 or 42) 
      return 2;
    } else if (number % 7 == 0) { // all three atoms are together (multiple of 7)
      return 0;
    } else { // one atom is separated 
      return 1;
    }
  }
  
  /**
   * It is just the complementary of the edge one.
   * 
   * @param bitSet
   * @return 
   */
  private byte getType4Subtype(BitSet bitSet) {
    bitSet.flip(0, 6);
    return getEdgeSubtype(bitSet);
  }
  /**
   * Takes into account that maximum distance can be 3. This is because is a
   * periodic 1D surface with 6 positions.
   *
   * @param first atom position [0,5]
   * @param second atom position [0,5]
   * @return
   */
  private int getDistance(int first, int second) {
    int distance = second - first;
    if (distance > 3) { // periodic distance
      distance = 6 - distance;
    }
    return distance;
  }
}
