/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.process.CatalysisProcess;
import static kineticMonteCarlo.process.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DESORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DIFFUSION;
import static kineticMonteCarlo.process.CatalysisProcess.REACTION;
import utils.StaticRandom;

/**
 *
 * @author Karmele Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisAtom extends AbstractGrowthAtom {

  public static final byte CO = 0;
  public static final byte O = 1;
  public static final byte C = 0;
  public static final byte O2 = 1;
  public static final byte BR = 0;
  public static final byte CUS = 1;
  
  private final static BasicGrowthTypesTable TYPE_TABLE = new BasicGrowthTypesTable();

  private int occupiedNeighbours;
  private final CatalysisAtom[] neighbours = new CatalysisAtom[4];
  /**
   * Bridge or CUS.
   */
  private final byte latticeSite;
  private boolean eligible;
  private final CatalysisProcess[] processes;
  
  /**
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   */
  private double[][][] probabilities;
  
  private CatalysisAtomAttributes attributes;
  
  /** Current atom is CO^CUS and it has as many CO^CUS neighbours. */
  private int coCusWithCoCus;
  
  public CatalysisAtom(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa, 4, 4);
    occupiedNeighbours = 0; 
    if (iHexa % 2 == 0) {
      latticeSite = BR;
    } else {
      latticeSite = CUS;
    }
    attributes = new CatalysisAtomAttributes();
    eligible = true;
    processes = new CatalysisProcess[4];
    processes[ADSORPTION] = new CatalysisProcess();
    processes[DESORPTION] = new CatalysisProcess();
    processes[REACTION] = new CatalysisProcess();
    processes[DIFFUSION] = new CatalysisProcess();
    setProcceses(processes);
    coCusWithCoCus = 0;
  }
  
  @Override
  public AbstractGrowthAtomAttributes getAttributes() {
    return attributes;
  }
  
  @Override
  public void setAttributes(AbstractGrowthAtomAttributes attributes) {
    this.attributes = (CatalysisAtomAttributes) attributes;
  }
  
  public byte getLatticeSite() {
    return latticeSite;
  }
  
  /**
   * Tells when current atom is completely surrounded.
   * 
   * @return true if it has 4 occupied neighbours.
   */
  @Override
  public boolean isIsolated() {
    return getOccupiedNeighbours() == 4;
  }
  
  /**
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   *
   * @param probabilities Default rates.
   */
  public void initialiseRates(double[][][] probabilities) {
    this.probabilities = probabilities;
  }
    
  /**
   * Dummy method. Just to run.
   * @param originType
   * @param targetType
   * @return 
   */
  @Override
  public double getProbability(int originType, int targetType) {
    return 0;
  }
  
  /**
   * For the orientation they are only available two position. Orientation is either | or _. It is
   * assumed that current atom is of type EDGE.
   *
   * @return horizontal (0) or vertical (1).
   */
  @Override
  public int getOrientation() {
    for (int i = 0; i < getNumberOfNeighbours(); i++) {
      if (neighbours[i].isOccupied()) {
        return i % 2;
      }
    }
    return -1;
  }

  @Override
  public void setNeighbour(AbstractGrowthAtom a, int pos) {
    neighbours[pos] = (CatalysisAtom) a;
  }

  @Override
  public CatalysisAtom getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  public CatalysisAtom getRandomNeighbour(byte process) {
    CatalysisAtom neighbour;
    double randomNumber = StaticRandom.raw() * getRate(process);
    double sum = 0.0;
    for (int j = 0; j < getNumberOfNeighbours(); j++) {
      sum += processes[process].getEdgeRate(j);
      if (sum > randomNumber) {
        neighbour = getNeighbour(j);
        return neighbour;
      }
    }
    // raise an error
    return null;
  }

  public void addCoCusNeighbours(int value) {
    coCusWithCoCus += value;
  }

  public void cleanCoCusNeighbours() {
    coCusWithCoCus = 0;
  }

  public int getCoCusNeighbours() {
    return coCusWithCoCus;
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
    return isOccupied() && eligible;
  }
  
  @Override
  public boolean isPartOfImmobilSubstrate() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  /**
   * How many neighbours are occupied.
   *
   * @return occupied neighbours.
   */
  public int getOccupiedNeighbours() {
    return occupiedNeighbours;
  }
  
  /**
   * An atom is added to current neighbourhood (can be negative).
   * 
   * @param value to be added or removed.
   */
  public void addOccupiedNeighbour(int value) {
    occupiedNeighbours += value;
  }
  
  /**
   * Returns the type of the neighbour atom if current one would not exist.
   *
   * @param position position is the original one; has to be inverted.
   * @return the type.
   */
  @Override
  public byte getTypeWithoutNeighbour(int position) {
    return TYPE_TABLE.getCurrentType(occupiedNeighbours - 1);
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
  
  /**
   * Makes this atom "immobile" if it is surrounded by 4 neighbours. Thus, it will not be eligible
   * for diffusion.
   */
  public void checkImmobile() {
    eligible = true;
    if (getOccupiedNeighbours() == 4) {
      eligible = false;
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

    return neighbours[cont];
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

    byte originSite = latticeSite; // cus or bridge
    byte originType = attributes.getType(); // O or CO
    byte destinationSite = originSite; // cus or bridge
    if (position % 2 != 0) {
      destinationSite = (byte) (originSite ^ 1); // the opposite of cus or bridge
    }
    return probabilities[originSite][originType][destinationSite];
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
    for (int i = 0; i < 4; i++) {
      processes[i].clear();
    }
  }
  
  @Override
  public void swapAttributes(AbstractGrowthAtom a) {
    CatalysisAtom atom = (CatalysisAtom) a;
    CatalysisAtomAttributes tmpAttributes = this.attributes;
    this.attributes = (CatalysisAtomAttributes) atom.getAttributes();
    this.attributes.addOneHop();
    atom.setAttributes(tmpAttributes);
  }
  
  public void equalRate(byte process) {
    processes[process].equalRate();
  }
  
  @Override
  public String toString() {
    String returnString = "Atom Id " + getId() + " desorptionRate " + processes[DESORPTION].getRate() + " " + processes[DESORPTION].getSumRate();
    return returnString;
  }
}
