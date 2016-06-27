/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class GrapheneAtomSchoenhalz extends GrapheneAtom {

  private GrapheneSchoenhalzRates rates;

  private GrapheneAtomSchoenhalz[] neighbours = new GrapheneAtomSchoenhalz[12];
  
  public GrapheneAtomSchoenhalz(int id, short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(id, iHexa, jHexa, distancePerStep);
    setNumberOfNeighbours(9);
    rates = new GrapheneSchoenhalzRates();
  }
  
  @Override
  public void setNeighbours(GrapheneAtom[] neighbours) {
    super.setNeighbours(neighbours);
    for (int i = 0; i < neighbours.length; i++) {
      this.neighbours[i] = (GrapheneAtomSchoenhalz) neighbours[i];
    }
  }  
  
  @Override
  public GrapheneAtomSchoenhalz getNeighbour(int pos) {
    return neighbours[pos];
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
    GrapheneAtomSchoenhalz atom = neighbours[pos];
    if (atom.isOccupied()) {
      return 0;
    }

    int originN1 = getN1();
    int originN2 = getN2();
    int destinationN1 = atom.getN1();
    int destinationN2 = atom.getN2();
    int n3 = getN3(); // in this model we ignore 3rd neighbours
    // Remove neighbour atom
    if (pos < 3) {
      destinationN1--;
    } else if (pos < 9) {
      if (originN1 == 1 && originN2 == 2 && destinationN1 == 1 && destinationN2 == 2) { // zigzag edge diffusion
        destinationN2--;
      } else {
        return 0;
      }
    }

    return rates.getRate(originN1, originN2, destinationN1, destinationN2, 1273);
  
  /**
   * Only BULK atom types are considered immobile atoms.
   * 
   * @return 
   */
  @Override
  public boolean isEligible() {
    return isOccupied() && (getType() <= KINK);
  }
}
