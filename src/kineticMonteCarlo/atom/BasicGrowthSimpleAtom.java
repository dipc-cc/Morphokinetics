/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthSimpleAtom extends BasicGrowthAtom {
  
  public BasicGrowthSimpleAtom(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa);
  }
  
  @Override
  public boolean isPartOfImmobilSubstrate() {
    return isOccupied() && getType() == ISLAND && getOccupiedNeighbours() == 4;
  }
}
