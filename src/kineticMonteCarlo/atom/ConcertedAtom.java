/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

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
}
