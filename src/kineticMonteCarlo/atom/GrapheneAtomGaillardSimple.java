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
public class GrapheneAtomGaillardSimple extends GrapheneAtom {
  
  public static final byte NEIGH0 = 0;
  public static final byte NEIGH1 = 1;
  public static final byte NEIGH2 = 2;
  public static final byte NEIGH3 = 3;
  
  public GrapheneAtomGaillardSimple(int id, short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(id, iHexa, jHexa, distancePerStep);
    setNumberOfNeighbours(3);
  }
  
  /**
   * Only BULK atom types are considered immobile atoms. From page 15 of the paper.
   * 
   * @return  true if current atom can be moved, false otherwise.
   */
  @Override
  public boolean isEligible() {
    return isOccupied() && (getType() <= NEIGH3);
  }
  
  @Override
  public double probJumpToNeighbour(int ignored, int position) {
    if (getNeighbour(position).isOccupied()) {
      return 0;
    }

    byte originType = getType();
    byte destination = getNeighbour(position).getTypeWithoutNeighbour(position);

    return getProbability(originType, destination);
  }  
  
  /**
   * Returns the type of the neighbour atom if current one would not exist. Essentially, 
   * it has one less neighbour.
   *
   * @param position ignored.
   * @return the type.
   */
  @Override
  public byte getTypeWithoutNeighbour(int position) {
    return (byte) (getType() - 1);
  }
  
  
  /**
   * Calculates the new atom type when adding or removing a neighbour.
   * 
   * @param neighbourPosition position of the neighbour. Must be always 1. Ignored
   * @param addOrRemove add or remove one neighbour. Must be -1 or 1
   * @return new atom type
   */
  @Override
  public byte getNewType(int neighbourPosition, int addOrRemove) {
    setN1(addOrRemove);
    return (byte) (getN1());
  }
}
