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
    processes = new ConcertedProcess[4];
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
}
