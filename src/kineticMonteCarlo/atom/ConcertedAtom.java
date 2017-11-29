/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import java.util.BitSet;
import kineticMonteCarlo.process.ConcertedProcess;
import static kineticMonteCarlo.process.ConcertedProcess.ADSORB;
import static kineticMonteCarlo.process.ConcertedProcess.CONCERTED;
import static kineticMonteCarlo.process.ConcertedProcess.SINGLE;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class ConcertedAtom extends AgAtomSimple {
  
  private final ConcertedProcess[] processes;
  
  public ConcertedAtom(int id, int i) {
    super(id, i);
    processes = new ConcertedProcess[3];
    processes[ADSORB] = new ConcertedProcess();
    processes[SINGLE] = new ConcertedProcess();
    processes[CONCERTED] = new ConcertedProcess();
    setProcceses(processes);
  }
  
  public ConcertedAtom getRandomNeighbour(byte process) {
    ConcertedAtom neighbour;
    double randomNumber = StaticRandom.raw() * getRate(process);
    double sum = 0.0;
    for (int j = 0; j < getNumberOfNeighbours(); j++) {
      sum += processes[process].getEdgeRate(j);
      if (sum > randomNumber) {
        neighbour = (ConcertedAtom) getNeighbour(j);
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
    for (int i = 0; i < 3; i++) {
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
          AbstractGrowthAtom neighbour = getNeighbour(i);
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
  
  @Override
  public byte getRealType(){
    byte subtype = 0;
    int occupationCode;
    BitSet bits = getCode();
    switch (getType()) {
      case 0: // no subtype
        break;
      case 1: // no subtype
        break;
      case 2: // 3 subtypes
        subtype = getEdgeSubtype(bits);
        break;
      case 3: // 2 subtypes
        subtype = getKinkSubtype(bits);
        break;
      case 4: // 3 subtypes
        getType4Subtype(bits);
        break;
      case 5: // no subtype
        break;
    }
    return getType();
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
   * If the given number from bits is multiple of 7,
   * all 3 atoms are together.
   * 
   * @param bits
   * @return one subtype or the other.
   */
  private byte getKinkSubtype(BitSet bits) {
    int first = bits.nextSetBit(0);
    int second = bits.nextSetBit(first+1);
    int third = bits.nextSetBit(second+1);
    int number = (1 << first) | (1 << second) | (1 << third);
    if (number % 7 == 0) { // all three atoms are together (multiple of 7)
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
