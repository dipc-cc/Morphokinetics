/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import ratesLibrary.GrapheneGaillardRates;

/**
 * Based on paper P. Gaillard, T. Chanier, L. Henrard, P. Moskovkin, S. Lucas. Surface Science,
 * Volumes 637–638, July–August 2015, Pages 11-18, http://dx.doi.org/10.1016/j.susc.2015.02.014.
 *
 * @author J. Alberdi-Rodriguez
 */
public class GrapheneAtomGaillard extends GrapheneAtom {

  private GrapheneGaillardRates rates;

  public GrapheneAtomGaillard(int id, short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(id, iHexa, jHexa, distancePerStep);
    setNumberOfNeighbours(9);
    rates = new GrapheneGaillardRates();
  }
  
  /**
   * Probability to jump to given neighbour position. Only allowed to jump to first neighbours.
   *
   * @param originType
   * @param pos
   * @return probability
   */
  @Override
  public double probJumpToNeighbour(int originType, int pos) {
    GrapheneAtomGaillard atom = (GrapheneAtomGaillard) getNeighbour(pos);
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
      return 0;
    }
    
    return rates.getRate(originN1, originN2, destinationN1, destinationN2, 1273);
  }
  
  /**
   * Only BULK atom types are considered immobile atoms.
   * 
   * @return  true if current atom can be moved, false otherwise.
   */
  @Override
  public boolean isEligible() {
    return isOccupied() && (getType() <= KINK);
  }
}
