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
    
    rates = new GrapheneGaillardRates();
  }
  
  /**
   * Probability to jump to given neighbour position. 
   * 
   * @param originType
   * @param pos
   * @return probability
   */
  @Override
  public double probJumpToNeighbour(int originType, int pos) {
    AbstractGrowthAtom atom = getNeighbour(pos);
    if (atom.isOccupied()) {
      return 0;
    }

    int n1 = getN1();
    int n2 = getN2();
    int n3 = getN3(); // in this model we ignore 3rd neighbours
    // Remove neighbour atom
    if (pos < 3) {
      n1--;
    } else if (pos < 9) {
      n2--;
    }
    
    return rates.getRate(n1, n2, 1273);
  }
}
