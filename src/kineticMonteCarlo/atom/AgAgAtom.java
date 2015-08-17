/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.atom;

import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.Abstract2DDiffusionLattice;
import utils.StaticRandom;

/**
 *
 * @author DONOSTIA INTERN
 */
public class AgAgAtom extends Abstract2DDiffusionAtom {

  private static AgAgTypesTable typesTable;
  private final AgAgAtom[] neighbours = new AgAgAtom[6];
  private byte nImmobile;
  private byte nMobile;

  public AgAgAtom(short iHexa, short jHexa, HopsPerStep distancePerStep) {
    super(iHexa, jHexa, distancePerStep);
    if (typesTable == null) {
      typesTable = new AgAgTypesTable();
    }
    
    numberOfNeighbours = 12;
    bondsProbability = new double[numberOfNeighbours];
  }

  public void setNeighbour(AgAgAtom a, int pos) {
    neighbours[pos] = a;
  }

  public AgAgAtom getNeighbour(int pos) {
    return neighbours[pos];
  }

  @Override
  public void initialize(Abstract2DDiffusionLattice lattice, double[][] probabilities, ModifiedBuffer modified) {
    super.initialize(probabilities, modified);
  }

  @Override
  public boolean isEligible() {
    return occupied && (type < KINK);
  }

  private boolean isPartOfImmobilSubstrate() {
    return occupied && (type == BULK);
  }

  public byte getNImmobile() {
    return nImmobile;
  }

  public byte getNMobile() {
    return nMobile;
  }

  @Override
  public void clear() {

    nImmobile = nMobile = type = TERRACE;
    occupied = false;
    outside = false;

    totalProbability = 0;
    for (int i = 0; i < bondsProbability.length; i++) {
      bondsProbability[i] = 0;
    }

    setOnList(null);
  }

  @Override
  public int getOrientation() {

    int occupationCode = 0;
    for (int i = 0; i < 6; i++) {
      if (neighbours[i].isOccupied()) {
        occupationCode |= (1 << i);
      }
    }

    if (type == EDGE) {
      return aggrCalculateEdgeType(occupationCode);
    }
    if (type == KINK) {
      return aggrCalculateKinkType(occupationCode);
    }
    return -1;
  }

  private int aggrCalculateEdgeType(int code) {

    switch (code) {
      case 12:
        return 5;
      case 48:
        return 1;
      case 3:
        return 3;
      case 6:
        return 4;
      case 24:
        return 0;
      case 33:
        return 2;
      default:
        return -1;
    }
  }

  private int aggrCalculateKinkType(int code) {

    switch (code) {
      case (1 + 2 + 4):
        return 0;
      case (2 + 4 + 8):
        return 1;
      case (4 + 8 + 16):
        return 2;
      case (8 + 16 + 32):
        return 3;
      case (16 + 32 + 1):
        return 4;
      case (32 + 1 + 2):
        return 5;
      default:
        return -1;
    }
  }

  @Override
  public Abstract2DDiffusionAtom chooseRandomHop() {

    double linearSearch = StaticRandom.raw() * totalProbability;

    double sum = 0;
    int cont = 0;
    while (true) {
      sum += bondsProbability[cont++];
      if (sum >= linearSearch) {
        break;
      }
      if (cont == bondsProbability.length) {
        break;
      }
    }
    cont--;

    if (type == EDGE && neighbours[cont].getType() == CORNER) {
      return aheadCornerAtom(cont);
    }

    return neighbours[cont];
  }

  public AgAgAtom aheadCornerAtom(int cornerPosition) {
    if ((getOrientation() & 1) != 0) {

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
    } else {

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
    while (cont < 2 && i < 6) {
      if (neighbours[i].isOccupied()) {
        if (neighbours[i].getType() != 0) {
          return false;
        }
        cont++;
      }
      i++;
    }
    return true;
  }

  @Override
  public int getNeighbourCount() {
    return 6;
  }

  public void removeImmobilAddMobile() {

    if (nImmobile == 0) {  //estado de transición
      nMobile++;
      nImmobile--;
      return;
    }

    byte newType = typesTable.getType(--nImmobile, ++nMobile);

    if (type != newType) { // ha cambiado el tipo, hay que actualizar ligaduras

      boolean immobileToMobile = (type >= KINK && newType < KINK);

      type = newType;

      modified.addOwnAtom(this);
      if (nMobile > 0 && !occupied) {
        modified.addBondAtom(this);
      }

      if (immobileToMobile && occupied) {
        for (int i = 0; i < 6; i++) {
          if (!neighbours[i].isPartOfImmobilSubstrate()) {
            neighbours[i].removeImmobilAddMobile();
          }
        }
      }
    }
  }

  public void removeMobileAddImmobileProcess(boolean forceNucleation) {

    if (nMobile == 0) {
      nMobile--;
      nImmobile++;
      return;
    }

    byte newType = typesTable.getType(++nImmobile, --nMobile);

    if (forceNucleation && occupied) {
      newType = 4;
    }

    if (type != newType) { // ha cambiado el tipo, hay que actualizar ligaduras
      boolean mobileToImmobile = (type < KINK && newType >= KINK);
      type = newType;
      modified.addOwnAtom(this);
      if (nMobile > 0 && !occupied) {
        modified.addBondAtom(this);
      }
      if (mobileToImmobile && occupied) {

        for (int i = 0; i < 6; i++) {
          if (!neighbours[i].isPartOfImmobilSubstrate()) {
            neighbours[i].removeMobileAddImmobileProcess(forceNucleation);
          }
        }
      }
    }
  }
  /**
   * Éste lo ejecutan los primeros vecinos
   * @param originType
   * @param forceNucleation 
   */
  public void addOccupiedNeighbourProcess(byte originType, boolean forceNucleation) { 

    byte newType;

    if (originType < KINK) {
      newType = typesTable.getType(nImmobile, ++nMobile);
    } else {
      newType = typesTable.getType(++nImmobile, nMobile);
    }

    if (forceNucleation) {
      newType = BULK;
    }

    if (type != newType) {

      boolean mobileToImmobile = (type < KINK && newType >= KINK);

      type = newType;

      modified.addOwnAtom(this);
      if (nMobile > 0 && !occupied) {
        modified.addBondAtom(this);
      }

      if (mobileToImmobile && occupied) {
        for (int i = 0; i < 6; i++) {
          if (!neighbours[i].isPartOfImmobilSubstrate()) {
            neighbours[i].removeMobileAddImmobileProcess(forceNucleation);
          }
        }
      }
    }
  }

  public void removeMobileOccupied() {

    byte newType = typesTable.getType(nImmobile, --nMobile);

    if (type != newType) {

      boolean immobileToMobile = (type >= KINK && newType < KINK);

      type = newType;

      modified.addOwnAtom(this);
      if (nMobile > 0 && !occupied) {
        modified.addBondAtom(this);
      }

      if (immobileToMobile && occupied) {
        for (int i = 0; i < 6; i++) {
          if (!neighbours[i].isPartOfImmobilSubstrate()) {
            neighbours[i].removeImmobilAddMobile();
          }
        }
      }
    }
  }

  @Override
  public void deposit(boolean forceNucleation) {

    occupied = true;
    if (forceNucleation) {
      type = BULK;
    }

    byte originalType = type;
    for (int i = 0; i < 6; i++) {
      if (!neighbours[i].isPartOfImmobilSubstrate()) {
        neighbours[i].addOccupiedNeighbourProcess(originalType, forceNucleation);
      }
    }

    modified.addOwnAtom(this);
    if (nMobile > 0) {
      modified.addBondAtom(this);
    }
    totalProbability = 0;
  }

  @Override
  public void extract() {
    occupied = false;

    for (int i = 0; i < 6; i++) {
      if (!neighbours[i].isPartOfImmobilSubstrate()) {
        neighbours[i].removeMobileOccupied();
      }
    }

    if (nMobile > 0) {
      modified.addBondAtom(this);
    }

    list.addTotalProbability(-totalProbability);
    this.setOnList(null);
  }

  @Override
  public void updateAllRates() {
    double temp = -totalProbability;
    totalProbability = 0;

    if (this.isEligible()) {
      obtainRatesFromNeighbours();
      temp += totalProbability;
    }
    if (this.isOnList()) {
      list.addTotalProbability(temp);
    }
  }

  private void obtainRatesFromNeighbours() {
    for (int i = 0; i < 6; i++) {
      bondsProbability[i] = probJumpToNeighbour(i);
      totalProbability += bondsProbability[i];
    }
  }

  @Override
  public void updateOneBound(int neighborpos) {

    double temp = 0;
    totalProbability -= bondsProbability[neighborpos];
    temp -= bondsProbability[neighborpos];
    bondsProbability[neighborpos] = (float) probJumpToNeighbour(neighborpos);

    totalProbability += bondsProbability[neighborpos];
    temp += bondsProbability[neighborpos];

    list.addTotalProbability(temp);
  }

  private double probJumpToNeighbour(int position) {

    if (neighbours[position].isOccupied()) {
      return 0;
    }

    byte originType = type;
    if (type == EDGE && (getOrientation() & 1) == 0) {
      originType = 5;
    }
    if (type == KINK && (getOrientation() & 1) == 0) {
      originType = 6;
    }

    int myPositionForNeighbor = (position + 3) % 6;

    byte destination = neighbours[position].getTypeWithoutNeighbour(myPositionForNeighbor);

    if (type == 2 && destination == 1) { //soy un edge y el vecino es un corner, eso significa que podemos girar, a ver a donde

      int otherCorner = 0;
      if (originType == EDGE) {
        otherCorner = 5;
      }
      if (originType == 5) {
        otherCorner = 2;
      }
      return probabilities[originType][otherCorner];

    } else {

      destination = (byte) Math.min(destination, 2);

      if (destination == 2 && (neighbours[position].getOrientation() & 1) == 0) {
        destination = 5;
      }

      return probabilities[originType][destination];
    }
  }

  @Override
  public byte getTypeWithoutNeighbour(int posNeighbour) {

    if (!neighbours[posNeighbour].isOccupied()) {
      return type;
    }

    if (neighbours[posNeighbour].getType() < KINK) {
      return typesTable.getType(nImmobile, nMobile - 1);
    } else {
      return typesTable.getType(nImmobile - 1, nMobile);
    }
  }
}
