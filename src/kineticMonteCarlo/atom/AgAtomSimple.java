/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgAtomSimple extends AgAtom{
  
  public AgAtomSimple(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa);
  }

  public AgAtomSimple(int id, int i) {
    super(id, i);
  }
  
  @Override
  public boolean isPartOfImmobilSubstrate() {
    return false;
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
   * Returns the real type of the current atom. This means nothing, 
   * in the current atom class is the same as the type.
   *
   * @return real type of the current atom.
   */
  @Override
  public byte getRealType() {
    return getType();
  }
  
  /**
   * Simpler implementation than {@link AgAtom#chooseRandomHop()}. This one, does not consider
   * corner rounding.
   *
   * @return
   */
  @Override
  public AbstractGrowthAtom chooseRandomHop() {

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
    return getNeighbour(cont);
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
}
