/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import java.awt.geom.Point2D;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;
import utils.StaticRandom;

/**
 *
 * @author DONOSTIA INTERN, J. Alberdi-Rodriguez
 */
public class AgAtom extends AbstractGrowthAtom {

  private static AgTypesTable typesTable;
  private final AgAtom[] neighbours = new AgAtom[6];
  /** Number of immobile neighbours. */
  private int nImmobile;
  /** Number of mobile neighbours. */
  private int nMobile;
  /**
   * Position within unit cell.
   */
  private final int pos;
  
  public static final byte TERRACE = 0;
  public static final byte CORNER = 1;
  public static final byte EDGE_A = 2;
  public static final byte KINK_A = 3;
  public static final byte ISLAND = 4;
  public static final byte EDGE_B = 5;
  public static final byte KINK_B = 6;
  
  // Before we actually know the value of those, we simply use A type
  public static final byte EDGE = EDGE_A;
  public static final byte KINK = KINK_A;
  
  public AgAtom(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa, 6);
    if (typesTable == null) {
      typesTable = new AgTypesTable();
    }
    nImmobile = 0;
    nMobile   = 0;
    pos = 0;
  }

  /**
   * Constructor for unit cell
   * @param pos position within the unit cell
   */
  public AgAtom(int id, int pos) {
    super(id, 6);
    this.pos = pos;
    nImmobile = 0;
    nMobile   = 0;
  }

  /**
   * Returns the position within the unit cell
   * @return coordinates in unit cell
   */
  @Override
  public Point2D getPos() {
    switch (pos) {
      case 0:
        return new Point2D.Double(0, 0);
      case 1:
        return new Point2D.Double(0.5, AbstractGrowthLattice.Y_RATIO);
      default:
        throw new UnsupportedOperationException("Trying to acces to an atom within the unit cell that doesn't exists");
    }
  }

  @Override
  public void setNeighbour(AbstractGrowthAtom a, int pos) {
    neighbours[pos] = (AgAtom) a;
  }

  @Override
  public AgAtom getNeighbour(int pos) {
    return neighbours[pos];
  }

  @Override
  public boolean isEligible() {
    return isOccupied() && getType() < KINK_A;
  }

  public boolean isPartOfImmobilSubstrate() {
    return isOccupied() && getType() == ISLAND;
  }

  /**
   * 
   * @return the number of immobile neighbours.
   */
  public int getNImmobile() {
    return nImmobile;
  }

  /**
   * 
   * @return the number of mobile neighbours.
   */
  public int getNMobile() {
    return nMobile;
  }

  public void setNImmobile(int nImmobile) {
    this.nImmobile = (byte) nImmobile;
  }

  public void setNMobile(int nMobile) {
    this.nMobile = (byte) nMobile;
  }
  
  /**
   * Adds the given number to the number of mobile neighbours
   * @param quantity 
   */
  public void addNMobile(int quantity) {
    nMobile = nMobile + quantity;
  }
  
  /**
   * Adds the given number to the number of immobile neighbours
   * @param quantity 
   */
  public void addNImmobile(int quantity) {
    nImmobile = nImmobile + quantity;
  }

  /**
   * Calculates the new atom type when adding or removing a neighbour.
   *
   * @param addToImmobile variation of the number of immobile neighbours. Must be -1, 0 or 1
   * @param addToMobile variation of the number of mobile neighbours. Must be -1, 0 or 1
   * @return new type
   */
  public byte getNewType(int addToImmobile, int addToMobile) {
    return typesTable.getType(nImmobile + addToImmobile, nMobile + addToMobile);
  }
  
  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability
   */
  @Override
  public void clear() {
    
    super.clear();
    setType(TERRACE);
    nImmobile = nMobile = 0; // current atom has no neighbour
    
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(0, i);
    }
  }

  @Override
  public int getOrientation() {
    // Create the occupation code shifting the number of positions with the neighbours of the current atom
    int occupationCode = 0;
    for (int i = 0; i < 6; i++) {
      if (neighbours[i].isOccupied()) {
        occupationCode |= (1 << i);
      }
    }

    if (getType() == EDGE_A) {
      return calculateEdgeType(occupationCode);
    }
    if (getType() == KINK_A) {
      return calculateKinkType(occupationCode);
    }
    return -1;
  }

  /**
   * This atom is an edge (it has two neighbours). There are 6 possible positions for the edge,
   * depending on its neighbours. In the next "figure" the current atom is [] and the numbers are
   * its neighbours:                 
   *    0  1
   *   5 [] 2
   *    4  3
   * A proper image of the positions is documented here:
   * https://bitbucket.org/Nesferjo/ekmc-project/wiki/Relationship%20between%20Cartesian%20and%20hexagonal%20representations
   * @param code binary code with the occupied neighbours.
   * @return orientation (a number between 0 and 5 inclusive).
   */
  private int calculateEdgeType(int code) {
    switch (code) {
      case 3: //1+2 positions    (0+1 neighbours)
        return 3;
      case 6: //2+4 positions    (1+2 neighbours)
        return 4;
      case 12: //4+8positions    (2+3 neighbours)
        return 5;
      case 24: //8+16 positions  (3+4 neighbours)
        return 0;
      case 48: //16+32 positions (4+5 neighbours)
        return 1;
      case 33: //1+32 positions  (5+0 neighbours)
        return 2;
      default:
        return -1;
    }
  }

  /**
   * This atom is a kink (it has three neighbours). There are 6 possible positions for the kink,
   * depending on its neighbours. In the next "figure" the current atom is [] and the numbers are
   * its neighbours:                 
   *    0  1
   *   5 [] 2
   *    4  3
   * A proper image of the positions is documented here:
   * https://bitbucket.org/Nesferjo/ekmc-project/wiki/Relationship%20between%20Cartesian%20and%20hexagonal%20representations
   * @param code binary code with the occupied neighbours.
   * @return  orientation (a number between 0 and 5 inclusive).
   */
  private int calculateKinkType(int code) {

    switch (code) {
      case 7:  //1 + 2 + 4   (0+1+2 neighbours)
        return 0;
      case 14: //2 + 4 + 8   (1+2+3 neighbours)
        return 1;
      case 28: //4 + 8 + 16  (2+3+4 neighbours)
        return 2;
      case 56: //8 + 16 + 32 (3+4+5 neighbours)
        return 3;
      case 49: //16 + 32 + 1 (4+5+0 neighbours)
        return 4;
      case 35: //32 + 1 + 2  (5+0+1 neighbours)
        return 5;
      default:
        return -1;
    }
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

    if (getType() == EDGE_A && neighbours[cont].getType() == CORNER) {
      return aheadCornerAtom(cont);
    }

    return neighbours[cont];
  }

  /**
   * When the current atom's destination is a corner, it jumps over it to the different type edge.
   * 
   * @bug Destination can be other corner
   * @param cornerPosition position of the neighbour, from current atom
   * @return other type edge
   */
  private AgAtom aheadCornerAtom(int cornerPosition) {
    if ((getOrientation() & 1) != 0) { // B type edge

      switch (cornerPosition) {
        case 0:
          return neighbours[5].getNeighbour(0);
        case 1:
          return neighbours[2].getNeighbour(1);
        case 2:
          return neighbours[1].getNeighbour(2);
        case 3:
          return neighbours[4].getNeighbour(3);
        case 4:
          return neighbours[3].getNeighbour(4);
        case 5:
          return neighbours[0].getNeighbour(5);
      }
    } else { // A type edge

      switch (cornerPosition) { 
        case 0:
          return neighbours[1].getNeighbour(0);
        case 1:
          return neighbours[0].getNeighbour(1);
        case 2:
          return neighbours[3].getNeighbour(2);
        case 3:
          return neighbours[2].getNeighbour(3);
        case 4:
          return neighbours[5].getNeighbour(4);
        case 5:
          return neighbours[4].getNeighbour(5);
      }
    }
    return null;
  }

  @Override
  public boolean areTwoTerracesTogether() {

    if (nMobile != 2 || nImmobile != 0) {
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
  public void obtainRateFromNeighbours() {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      setBondsProbability(probJumpToNeighbour(i), i);
      addProbability(getBondsProbability(i));
    }
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
    setBondsProbability(probJumpToNeighbour(pos), pos);
    probabilityChange += getBondsProbability(pos);
    addProbability(probabilityChange);

    return probabilityChange;
  }
 
  /**
   * Returns the real type of the current atom. This means, that is able to determine whether edge
   * or kink is of type A or B
   *
   * @return real type of the current atom.
   */
  @Override
  public byte getRealType() {
    if (getType() == EDGE_A && (getOrientation() & 1) == 0) {
      return EDGE_B;
    }
    if (getType() == KINK_A && (getOrientation() & 1) == 0) {
      return KINK_B;
    }
    return getType();
  }
    
  private double probJumpToNeighbour(int position) {

    if (neighbours[position].isOccupied()) {
      return 0;
    }

    byte originType = getType();
    if (getType() == EDGE_A && (getOrientation() & 1) == 0) originType = EDGE_B;
    if (getType() == KINK_A && (getOrientation() & 1) == 0) originType = KINK_B;
    int myPositionForNeighbour = (position + 3) % getNumberOfNeighbours();
    byte destination = neighbours[position].getTypeWithoutNeighbour(myPositionForNeighbour);

    if (getType() == EDGE && destination == CORNER) { //soy un edge y el vecino es un corner, eso significa que podemos girar, a ver a donde
      if (originType == EDGE_A) destination = EDGE_B;
      if (originType == EDGE_B) destination = EDGE_A;
    } else {
      destination = (byte) Math.min(destination, 2);
      if (destination == EDGE_A && (neighbours[position].getOrientation() & 1) == 0) {
        destination = EDGE_B;
      }
    }
    return getProbability(originType, destination);
  }

  @Override
  public byte getTypeWithoutNeighbour(int posNeighbour) {

    if (!neighbours[posNeighbour].isOccupied()) return getType();

    if (neighbours[posNeighbour].getType() < KINK_A) {
      return typesTable.getType(nImmobile, nMobile - 1);
    } else {
      return typesTable.getType(nImmobile - 1, nMobile);
    }
  }
}
