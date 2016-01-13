/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.AgAtom;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.ModifiedBuffer;
import java.awt.geom.Point2D;
import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.ISLAND;
import static kineticMonteCarlo.atom.AgAtom.KINK_A;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgLattice extends AbstractGrowthLattice {

  public AgLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {

    super(hexaSizeI, hexaSizeJ, modified);

    setAtoms(createAtoms());
    setAngles();
  }

  private AgAtom[][] createAtoms() {
    //Instantiate atoms
    AgAtom[][] atoms = new AgAtom[getHexaSizeI()][getHexaSizeJ()];
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        atoms[i][j] = new AgAtom((short) i, (short) j);
      }
    }
    
    //Interconect atoms
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        AgAtom atom = (AgAtom) atoms[iHexa][jHexa];
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;

        atom.setNeighbour((AgAtom) atoms[i][j], 0);
        i = iHexa + 1;
        j = jHexa - 1;
        if (i == getHexaSizeI()) i = 0;
        if (j < 0) j = getHexaSizeJ() - 1;

        atom.setNeighbour((AgAtom) atoms[i][j], 1);
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;

        atom.setNeighbour((AgAtom) atoms[i][j], 2);
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;

        atom.setNeighbour((AgAtom) atoms[i][j], 3);
        i = iHexa - 1;
        j = jHexa + 1;
        if (i < 0) i = getHexaSizeI() - 1;
        if (j == getHexaSizeJ()) j = 0;

        atom.setNeighbour((AgAtom) atoms[i][j], 4);
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        atom.setNeighbour((AgAtom) atoms[i][j], 5);
      }
    }
    return atoms;
  }

  @Override
  public AbstractGrowthAtom getNeighbour(int iHexa, int jHexa, int neighbour) {
    return ((AgAtom) getAtom(iHexa, jHexa)).getNeighbour(neighbour);
  }

  @Override
  public int getAvailableDistance(int atomType, short iHexa, short jHexa, int thresholdDistance) {
    switch (atomType) {
      case TERRACE:
        return getClearAreaTerrace(iHexa, jHexa, thresholdDistance);
      case EDGE:
        return getClearAreaStep(iHexa, jHexa, thresholdDistance);
      default:
        return 0;
    }
  }

  @Override
  public AbstractGrowthAtom getFarSite(int originType, short iHexa, short jHexa, int distance) {
    switch (originType) {
      case TERRACE:
        return chooseClearAreaTerrace(iHexa, jHexa, distance, StaticRandom.raw());
      case EDGE:
        return chooseClearAreaStep(iHexa, jHexa, distance, StaticRandom.raw());
      default:
        return null;
    }
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return new Point2D.Float(getHexaSizeI() / 2.0f, (float) (getHexaSizeJ() * Y_RATIO / 2.0f));
  }

  @Override
  public float getCartSizeX() {
    return getHexaSizeI();
  }

  @Override
  public float getCartSizeY() {
    return getHexaSizeJ() * Y_RATIO;
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {

    float xCart = iHexa + jHexa * 0.5f;
    if (xCart >= getHexaSizeI()) {
      xCart -= getHexaSizeI();
    }
    float yCart = jHexa * Y_RATIO;
    return new Point2D.Double(xCart, yCart);
  }

  /**
   * The Cartesian X is the location I, plus the half of J.
   * We have to do the module to ensure that fits in a rectangular Cartesian mesh
   * @param iHexa
   * @param jHexa
   * @return
   */
  @Override
  public double getCartX(int iHexa, int jHexa) {
    float xCart = (iHexa + jHexa * 0.5f) % getHexaSizeI();
    return xCart;
  }

  /**
   * Simple relation between Y (Cartesian) and J (hexagonal),
   * with Y_RATIO (=sin 60º)
   * @param jHexa
   * @return
   */
  @Override
  public double getCartY(int jHexa) {
    return jHexa * Y_RATIO;
  }

  public int getClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int thresholdDistance) {

    int possibleDistance = 1;

    int i = iHexaOrigin;
    int j = jHexaOrigin - 1;
    byte errorCode = 0;
    if (j < 0) {
      j = getHexaSizeJ() - 1;
    }
    
    // This while follows this iteration pattern:
    // go right, up, left up, left, down, right down (-1), jump down and increment
    //
    // This implementation is clearly not efficient (simple profiling is enough to demonstrate)
    out:
    while (true) {
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        i++;
        if (i == getHexaSizeI()) i = 0;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        j++;
        if (j == getHexaSizeJ()) j = 0;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        j++;
        i--;
        if (j == getHexaSizeJ()) j = 0;
        if (i < 0) i = getHexaSizeI() - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        i--;
        if (i < 0) i = getHexaSizeI() - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        j--;
        if (j < 0) j = getHexaSizeJ() - 1;
      }
      for (int iter = 0; iter < possibleDistance; iter++) {
        if (getAtom(i, j).isOutside()) errorCode |= 1;
        if (getAtom(i, j).isOccupied()) {errorCode |= 2; break out;}
        j--;
        i++;
        if (j < 0) j = getHexaSizeJ() - 1;
        if (i == getHexaSizeI()) i = 0;
      }

      if (errorCode != 0) break;
      if (possibleDistance >= thresholdDistance) return possibleDistance;
      possibleDistance++;
      j--;
      if (j < 0) j = getHexaSizeJ() - 1;
      
    }
    if ((errorCode & 2) != 0) return possibleDistance - 1;
    if ((errorCode & 1) != 0) return possibleDistance;
    return -1;
  }

  public AbstractGrowthAtom chooseClearAreaTerrace(short iHexaOrigin, short jHexaOrigin, int distance, double raw) {

    int tmp = (int) (raw * (distance * 6));

    int i = iHexaOrigin;
    int j = jHexaOrigin - distance;
    if (j < 0) j = getHexaSizeJ() - 1;

    int counter = 0;

    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      i++;
      if (i == getHexaSizeI()) i = 0;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      j++;
      if (j == getHexaSizeJ()) j = 0;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      j++;
      i--;
      if (j == getHexaSizeJ()) j = 0;
      if (i < 0) i = getHexaSizeI() - 1;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      i--;
      if (i < 0) i = getHexaSizeI() - 1;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      j--;
      if (j < 0) j = getHexaSizeJ() - 1;
    }
    for (int iter = 0; iter < distance; iter++) {
      counter++;
      if (counter > tmp) return getAtom(i, j);
      j--;
      i++;
      if (j < 0) j = getHexaSizeJ() - 1;
      if (i == getHexaSizeI()) i = 0;
    }

    return null;
  }

  public int getClearAreaStep(short iHexaOrigin, short jHexaOrigin, int thresholdDistance) {

    int distance = 1;
    int i;
    int j;

    switch (getAtom(iHexaOrigin, jHexaOrigin).getOrientation()) {
      case 0:
      case 3:
        while (true) {
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          if (getAtom(i, jHexaOrigin).isOccupied() || getAtom(i, jHexaOrigin).getType() < 2) {
            return distance - 1;
          }
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          if (getAtom(i, jHexaOrigin).isOccupied() || getAtom(i, jHexaOrigin).getType() < 2) {
            return distance - 1;
          }
          if (distance == thresholdDistance) {
            return distance;
          }
          distance++;
        }

      case 1:
      case 4:
        while (true) {
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          if (getAtom(iHexaOrigin, j).isOccupied() || getAtom(iHexaOrigin, j).getType() < 2) {
            return distance - 1;
          }
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          if (getAtom(iHexaOrigin, j).isOccupied() || getAtom(iHexaOrigin, j).getType() < 2) {
            return distance - 1;
          }
          if (distance == thresholdDistance) {
            return distance;
          }
          distance++;
        }

      case 2:
      case 5:
        while (true) {
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          if (getAtom(i, j).isOccupied() || getAtom(i, j).getType() < 2) {
            return distance - 1;
          }
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          if (getAtom(i, j).isOccupied() || getAtom(i, j).getType() < 2) {
            return distance - 1;
          }
          if (distance == thresholdDistance) {
            return distance;
          }
          distance++;
        }

      default:
        return -1;
    }
  }

  public AbstractGrowthAtom chooseClearAreaStep(short iHexaOrigin, short jHexaOrigin, int distance, double raw) {

    int i;
    int j;

    switch (getAtom(iHexaOrigin, jHexaOrigin).getOrientation()) {
      case 0:
      case 3:
        if (raw > 0.5) {
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          return getAtom(i, jHexaOrigin);
        } else {
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          return getAtom(i, jHexaOrigin);
        }
      case 1:
      case 4:
        if (raw > 0.5) {
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          return getAtom(iHexaOrigin, j);
        } else {
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          return getAtom(iHexaOrigin, j);
        }
      case 2:
      case 5:
        if (raw > 0.5) {
          i = iHexaOrigin - distance;
          if (i < 0) i = getHexaSizeI() - 1;
          j = jHexaOrigin + distance;
          if (j >= getHexaSizeJ()) j = 0;
          return getAtom(i, j);
        } else {
          i = iHexaOrigin + distance;
          if (i >= getHexaSizeI()) i = 0;
          j = jHexaOrigin - distance;
          if (j < 0) j = getHexaSizeJ() - 1;
          return getAtom(i, j);
        }
    }
    return null;
  }
  
  @Override
  public void deposit(AbstractGrowthAtom a, boolean forceNucleation) {
    AgAtom atom = (AgAtom) a;
    atom.setOccupied(true);
    if (forceNucleation) {
      atom.setType(ISLAND);
    }

    byte originalType = atom.getType();
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        addOccupiedNeighbour(atom.getNeighbour(i), originalType, forceNucleation);
      }
    }

    addAtom(atom);
    if (atom.getNMobile() > 0) {
      addBondAtom(atom);
    }
    atom.resetProbability();
    //atom.setList(true);
  }

  @Override
  public void extract(AbstractGrowthAtom a) {
    AgAtom atom = (AgAtom) a;
    atom.setOccupied(false);
    
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        removeMobileOccupied(atom.getNeighbour(i));
      }
    }

    if (atom.getNMobile() > 0) {
      addBondAtom(atom);
    }

    atom.resetProbability();
    atom.setList(false);
  }

  private void removeImmobilAddMobile(AgAtom atom) {
    if (atom.getNImmobile() == 0) {  //estado de transición
      atom.addNMobile(1); // nMobile++;
      atom.addNImmobile(-1); // nImmobile--;
      return;
    }

    byte newType = atom.getNewType(-1, +1); // --nImmobile, ++nMobile
    atom.addNImmobile(-1);
    atom.addNMobile(1);

    if (atom.getType() != newType) { // ha cambiado el tipo, hay que actualizar ligaduras
      boolean immobileToMobile = (atom.getType() >= KINK_A && newType < KINK_A);
      atom.setType(newType);
      addAtom(atom);
      if (atom.getNMobile() > 0 && !atom.isOccupied()) {
        addBondAtom(atom);
      }

      if (immobileToMobile && atom.isOccupied()) {
        for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
          if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
            removeImmobilAddMobile(atom.getNeighbour(i));
          }
        }
      }
    }
  }
  
  private void removeMobileAddImmobile(AgAtom atom, boolean forceNucleation) {
    if (atom.getNMobile() == 0) {
      atom.addNMobile(-1); // nMobile--
      atom.addNImmobile(1); // nImmobile++
      return;
    }

    byte newType = atom.getNewType(1, -1); //++nImmobile, --nMobile
    atom.addNMobile(-1);
    atom.addNImmobile(1);

    if (forceNucleation && atom.isOccupied()) {
      newType = ISLAND;
    }

    if (atom.getType() != newType) { // ha cambiado el tipo, hay que actualizar ligaduras
      boolean mobileToImmobile = (atom.getType() < KINK_A && newType >= KINK_A);
      atom.setType(newType);
      addAtom(atom);
      if (atom.getNMobile() > 0 && !atom.isOccupied()) {
        addBondAtom(atom);
      }
      if (mobileToImmobile && atom.isOccupied()) {
        for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
          if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
            removeMobileAddImmobile(atom.getNeighbour(i), forceNucleation);
          }
        }
      }
    }
  }
   
  /**
   * Éste lo ejecutan los primeros vecinos
   * @param atom neighbour atom of the original atom
   * @param originType type of the original atom
   * @param forceNucleation
   */
  private void addOccupiedNeighbour(AgAtom atom, byte originType, boolean forceNucleation) {
    byte newType;

    if (originType < KINK_A) {
      newType = atom.getNewType(0, 1); //nImmobile, ++nMobile
      atom.addNMobile(1);
    } else {
      newType = atom.getNewType(1, 0); //++nImmobile, nMobile
      atom.addNImmobile(1);
    }

    if (forceNucleation) {
      newType = ISLAND;
    }

    if (atom.getType() != newType) {
      boolean mobileToImmobile = (atom.getType() < KINK_A && newType >= KINK_A);
      atom.setType(newType);
      addAtom(atom);
      if (atom.getNMobile() > 0 && !atom.isOccupied()) {
        addBondAtom(atom);
      }
      if (mobileToImmobile && atom.isOccupied()) {
        for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
          if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
            removeMobileAddImmobile(atom.getNeighbour(i), forceNucleation);
          }
        }
      }
    }
  }
   
  /**
   * 
   * @param atom neighbour atom of the original atom
   */
  private void removeMobileOccupied(AgAtom atom) {

    byte newType = atom.getNewType(0, -1); //nImmobile, --nMobile
    atom.addNMobile(-1); // remove one mobile atom (original atom has been extracted)

    if (atom.getType() != newType) {
      boolean immobileToMobile = (atom.getType() >= KINK_A && newType < KINK_A);
      atom.setType(newType);
      addAtom(atom);
      if (atom.getNMobile() > 0 && !atom.isOccupied()) {
        addBondAtom(atom);
      }
      if (immobileToMobile && atom.isOccupied()) {
        for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
          if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
            removeImmobilAddMobile(atom.getNeighbour(i));
          }
        }
      }
    }
  }
}
