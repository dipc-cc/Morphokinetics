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
public class BasicGrowthAtom extends AbstractGrowthAtom {

  public static final byte TERRACE = 0;
  public static final byte EDGE = 1;
  public static final byte KINK = 2;
  public static final byte ISLAND = 3;

  private static BasicGrowthTypesTable typesTable;

  private int occupiedNeighbours;
  private final BasicGrowthAtom[] neighbours = new BasicGrowthAtom[4];

  public BasicGrowthAtom(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa, 4);
    
    if (typesTable == null) {
      typesTable = new BasicGrowthTypesTable();
    }
    occupiedNeighbours = 0;
  }
  
  @Override
  public byte getTypeWithoutNeighbour(int neighPos) {
    if (!neighbours[neighPos].isOccupied()) return getType(); // impossible to happen

    return typesTable.getCurrentType(occupiedNeighbours - 1);
  }

  @Override
  public boolean areTwoTerracesTogether() {
    if (occupiedNeighbours != 2) {
      return false;
    }
    int cont = 0;
    int i = 0;
    while (cont < 2 && i < getNumberOfNeighbours()) {
      if (neighbours[i].isOccupied()) {
        if (neighbours[i].getType() != TERRACE) {
          return false;
        }
        cont++;
      }
      i++;
    }
    return true;
  }

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

    return neighbours[cont];
  }

  @Override
  public int getOrientation() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void obtainRateFromNeighbours() {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(probJumpToNeighbour(1, i), i);
      addProbability(getBondsProbability(i));
    }
  }
  
  @Override
  public double probJumpToNeighbour(int ignored, int position) {

    if (neighbours[position].isOccupied()) {
      return 0;
    }

    byte originType = getType();
    int myPositionForNeighbour = (position + 2) % getNumberOfNeighbours();
    byte destination = neighbours[position].getTypeWithoutNeighbour(myPositionForNeighbour);

    if (destination > 3) {
      System.out.println("error! ");
    }
    return getProbability(originType, destination);
  }

  @Override
  public void setNeighbour(AbstractGrowthAtom a, int pos) {
    neighbours[pos] = (BasicGrowthAtom) a;
  }

  @Override
  public BasicGrowthAtom getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  /**
   * 
   * @param pos position of the neighbour
   * @return change in the probability
   */
  @Override
  public double updateOneBound(int pos) {
    // Store previous probability
    double probabilityChange = -getBondsProbability(pos);
    // Update to the new probability and save
    setBondsProbability(probJumpToNeighbour(1, pos), pos);
    probabilityChange += getBondsProbability(pos);
    addProbability(probabilityChange);

    return probabilityChange;
  }

  @Override
  public boolean isEligible() {
    return isOccupied() && getType() < ISLAND;
  }
  
  public boolean isPartOfImmobilSubstrate() {
    return isOccupied() && getType() == ISLAND;
  }
  
  /**
   * Calculates the new atom type when adding or removing a neighbour.
   *
   * @param addToNeighbour variation of the number of the number of neighbours. Must be -1, 0 or 1
   * @return new type
   */
  public byte getNewType(int addToNeighbour) {
    return typesTable.getCurrentType(occupiedNeighbours+addToNeighbour);
  }
  
  public int getOccupiedNeighbours(){
    return occupiedNeighbours;
  }
  
  public void addOccupiedNeighbour(int value) {
    occupiedNeighbours += value;
  }
  
  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  @Override
  public void clear() {
    
    super.clear();
    setType(TERRACE);
    occupiedNeighbours = 0; // current atom has no neighbour
    
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(0, i);
    }
  }
 
}
