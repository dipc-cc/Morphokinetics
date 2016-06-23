/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;

/**
 * Based on paper P. Gaillard, T. Chanier, L. Henrard, P. Moskovkin, S. Lucas. Surface Science,
 * Volumes 637–638, July–August 2015, Pages 11-18, http://dx.doi.org/10.1016/j.susc.2015.02.014.
 *
 * @author J. Alberdi-Rodriguez
 */
public class GrapheneAtomGaillard1Neighbour extends GrapheneAtom {
  
  public GrapheneAtomGaillard1Neighbour(int id, short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(id, iHexa, jHexa, distancePerStep);
    setNumberOfNeighbours(3);
  }
}
