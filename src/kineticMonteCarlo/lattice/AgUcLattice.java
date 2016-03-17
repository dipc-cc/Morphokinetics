/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.unitCell.AgUc;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.AgAtom;
import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import utils.StaticRandom;

/**
 * Ag lattice with unit cell
 * @author J. Alberdi-Rodriguez
 */
public class AgUcLattice extends AgLattice {
  
  private final Point2D centralCartesianLocation;
  /**
   * How many unit cells are in X axis.
   */
  private int sizeI;
  /**
   * How many unit cells are in Y axis.
   */
  private int sizeJ;
  /**
   * Unit cell list.
   */
  private List<AgUc> ucList;
  /**
   * Unit cell array.
   */
  private AgUc[][] ucArray;
  /**
   * List to store free area that current atom has.
   */
  private List<AbstractGrowthAtom> clearAreaList;
  
  public AgUcLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {
    super(hexaSizeI, hexaSizeJ, modified, distancePerStep);
    ucList = new ArrayList<>();
    createAtoms();
    
    // We assume that central unit cell, position 0 is the centre
    centralCartesianLocation = new Point2D.Float(getHexaSizeI() / 2.0f, (float) (getHexaSizeJ() / 2.0f) * (Y_RATIO * 2));
    setAngles();
  }

  /**
   * Creates all atoms and asings it neighbours. How are computed the neighbours is documented
   * here:
   * https://bitbucket.org/Nesferjo/ekmc-project/wiki/Relationship%20between%20Cartesian%20and%20hexagonal%20representations
   *
   * @return
   */
  private void createAtoms() {
    
    sizeI = Math.round(getCartSizeX() / AgUc.getSizeX());
    sizeJ = Math.round(getCartSizeY() / AgUc.getSizeY());
    // Initialise unit cells (with atoms)
    ucArray = new AgUc[sizeI][sizeJ];
    for (int i = 0; i < sizeI; i++) {
      for (int j = 0; j < sizeJ; j++) {
        List<AgAtom> atomsList = new ArrayList<>(2);
        int index = j * sizeI + i;
        AgAtom atom0 = new AgAtom(index, 0);
        atomsList.add(atom0);
        AgAtom atom1 = new AgAtom(index, 1);
        atomsList.add(atom1);
        AgUc uc = new AgUc(2, i, j, atomsList);
        ucList.add(uc);
        ucArray[i][j] = uc;
      }
    }

    //Interconect atoms (go through all unit cells)
    for (int k = 0; k < size(); k++) {
      AgUc uc = ucList.get(k);

      // First atom of unit cell
      AgAtom atom = uc.getAtom(0);

      int i = uc.getPosI() - 1;
      int j = uc.getPosJ() - 1;
      if (i < 0) i = sizeI - 1;
      if (j < 0) j = sizeJ - 1;
      atom.setNeighbour(ucArray[i][j].getAtom(1), 0);

      i = uc.getPosI();
      j = uc.getPosJ() - 1;
      if (j < 0) j = sizeJ - 1;
      atom.setNeighbour(ucArray[i][j].getAtom(1), 1);

      i = uc.getPosI() + 1;
      j = uc.getPosJ();
      if (i == sizeI) i = 0;
      atom.setNeighbour(ucArray[i][j].getAtom(0), 2);

      i = uc.getPosI();
      j = uc.getPosJ();
      atom.setNeighbour(ucArray[i][j].getAtom(1), 3);

      i = uc.getPosI() - 1;
      j = uc.getPosJ();
      if (i < 0) i = sizeI - 1;
      atom.setNeighbour(ucArray[i][j].getAtom(1), 4);

      i = uc.getPosI() - 1;
      j = uc.getPosJ();
      if (i < 0) i = sizeI - 1;
      atom.setNeighbour(ucArray[i][j].getAtom(0), 5);

      // Second atom of unit cell
      atom = uc.getAtom(1);

      i = uc.getPosI();
      j = uc.getPosJ();
      atom.setNeighbour(ucArray[i][j].getAtom(0), 0);

      i = uc.getPosI() + 1;
      j = uc.getPosJ();
      if (i == sizeI) i = 0;
      atom.setNeighbour(ucArray[i][j].getAtom(0), 1);
      atom.setNeighbour(ucArray[i][j].getAtom(1), 2);

      i = uc.getPosI() + 1;
      j = uc.getPosJ() + 1;
      if (i == sizeI) i = 0;
      if (j == sizeJ) j = 0;
      atom.setNeighbour(ucArray[i][j].getAtom(0), 3);

      i = uc.getPosI();
      j = uc.getPosJ() + 1;
      if (j == sizeJ) j = 0;
      atom.setNeighbour(ucArray[i][j].getAtom(0), 4);

      i = uc.getPosI() - 1;
      j = uc.getPosJ();
      if (i < 0) i = sizeI - 1;
      atom.setNeighbour(ucArray[i][j].getAtom(1), 5);
    }

  }
   
  @Override
  public float getCartSizeX() {
    return getHexaSizeI();
}

  @Override
  public float getCartSizeY() {
    return getHexaSizeJ() * Y_RATIO * 2;
  }
  
  @Override
  public int size() {
    return ucList.size();
  }
  
  @Override
  public AgUc getUc(int pos) {
    return ucList.get(pos);
  }
  
  @Override
  public AgAtom getAtom(int iHexa, int jHexa) {
    return null;
  }
  
  /**
   * Returns an atom of given unit cell and lattice position.
   * @param iHexa
   * @param jHexa
   * @param pos
   * @return 
   */
  public AgAtom getAtom(int iHexa, int jHexa, int pos) {
    return ucArray[iHexa][jHexa].getAtom(pos);
  }
  
  @Override
  public Point2D getCentralCartesianLocation() {
    if (centralCartesianLocation == null) {
      return new Point2D.Float(getHexaSizeI() / 2.0f, (float) (getHexaSizeJ() / 2.0f) * (Y_RATIO * 2));
    } else {
      return centralCartesianLocation;
    }
  }
  
  @Override
  public int getAvailableDistance(AbstractGrowthAtom atom, int thresholdDistance) {
    clearAreaList = new ArrayList<>();
    clearAreaList.clear();
    switch (atom.getType()) {
      case TERRACE:
        //return getClearAreaTerrace(atom, thresholdDistance, true, 0, 0, 0, (byte) 0);
        return getClearAreaTerrace(atom, thresholdDistance);
      case EDGE:
        return getClearAreaStep(atom, thresholdDistance);
      default:
        return 0;
    }
  }
  
  private int getClearAreaTerrace(AbstractGrowthAtom atom, int thresholdDistance, boolean changeLevel, int currentLevel, int position, int turnDirection, byte errorCode) {
    int possibleDistance = 1; // = currentLevel
    AbstractGrowthAtom currentAtom;
    if (changeLevel) {
      currentAtom = atom.getNeighbour(0).getNeighbour(2); // Skip the first possition, to avoid counting more than once
      if (currentLevel == 0) {
        turnDirection = 3; // in the first level there are no more neighbours in 2 direction
      } else {
        turnDirection = 2; // go to the 2 direction (right).
      }
      currentLevel++;
      position = 1;
      changeLevel = false;
    } else {

      // choose the next atom 
      currentAtom = atom.getNeighbour(turnDirection);
      position++;
      if (position >= currentLevel) { // time to turn
        position = 0;
        turnDirection++;
        if (turnDirection == 6) {
          turnDirection = 0;
        }

        if (turnDirection == 2) { // we arrived to the end of the current level
          if ((errorCode & 1) != 0) {
            return currentLevel;
          }
          changeLevel = true;
        } else {
          changeLevel = false;
        }
      }
    }

    if (currentAtom.isOutside()) {
      errorCode |= 1;
    }
    if (currentAtom.isOccupied()) {
      errorCode |= 2;
      return currentLevel - 1;
    } else {
      clearAreaList.add(currentAtom);
    }
    if (currentLevel >= thresholdDistance) {
      return possibleDistance;
    }

    return getClearAreaTerrace(currentAtom, thresholdDistance, changeLevel, currentLevel, position, turnDirection, errorCode);
  }
  
  /**
   * We only care about the largest possible distance atoms.
   * @param atom
   * @param thresholdDistance
   * @return clear distance
   */
  private int getClearAreaTerrace(AbstractGrowthAtom atom, int thresholdDistance) {
    boolean changeLevel = true;
    int currentLevel = 0;
    int position = 0;
    int turnDirection = 0;
    byte errorCode = 0;
    int possibleDistance = 1; 
    AbstractGrowthAtom currentAtom;
    List<AbstractGrowthAtom> listCurrentLevel = new ArrayList<>();
    List<AbstractGrowthAtom> listPreviousLevel = new ArrayList<>();
    
    listCurrentLevel.add(atom);
    
    while (true) {
      if (changeLevel) {
        listPreviousLevel = new ArrayList<>(listCurrentLevel);
        listCurrentLevel.clear();
        
        currentAtom = atom.getNeighbour(0).getNeighbour(2); // Skip the first possition, to avoid counting more than once
        if (currentLevel == 0) {
          turnDirection = 3; // in the first level there are no more neighbours in 2 direction
        } else {
          turnDirection = 2; // go to the 2 direction (right).
        }
        currentLevel++;
        position = 1;
        changeLevel = false;
        if (currentLevel > thresholdDistance) {
          clearAreaList = new ArrayList<>(listPreviousLevel);
          return possibleDistance;
        }
      } else {

        // choose the next atom 
        currentAtom = atom.getNeighbour(turnDirection);
        position++;
        if (position >= currentLevel) { // time to turn
          position = 0;
          turnDirection++;
          if (turnDirection == 6) {
            turnDirection = 0;
          }

          if (turnDirection == 2) { // we arrived to the end of the current level
            if ((errorCode & 1) != 0) { // if some of the atoms are outside, return
              // it is missing the current atom in the list
              listCurrentLevel.add(currentAtom);
              clearAreaList = new ArrayList<>(listCurrentLevel);
              return currentLevel;
            }
            changeLevel = true;
          } else {
            changeLevel = false;
          }
        }
      }

      if (currentAtom.isOutside()) {
        errorCode |= 1;
      }
      if (currentAtom.isOccupied()) { // we have touched an occupied atom, break
        errorCode |= 2;
        break;
      } else {
        listCurrentLevel.add(currentAtom);
      }
      atom = currentAtom; // go to the next atom
    }
    if ((errorCode & 2) != 0) { // we have touched an occupied atom, exit and store previous level atoms
      clearAreaList = new ArrayList<>(listPreviousLevel);
      return possibleDistance - 1;
    }
    return -1; // never should happen
  }
  
  @Override
  public AbstractGrowthAtom getFarSite(AbstractGrowthAtom atom, int distance) {
    int randomNumber = StaticRandom.rawInteger(clearAreaList.size());
    return clearAreaList.get(randomNumber);
  }
  
  private int getClearAreaStep(AbstractGrowthAtom atom, int thresholdDistance) {
    
    int distance = 1;
    clearAreaList.add(atom);
    clearAreaList.add(atom);
    AbstractGrowthAtom currentAtom;
    int right;
    int left;
    // select the neighbours depending on the orientation of the source atom
    switch (atom.getOrientation()) {
      case 0:
      case 3:
        right = 2;
        left = 5;
        break;
      case 1:
      case 4:
        right = 3;
        left = 0;
        break;
      case 2:
      case 5:
        right = 4;
        left = 1;
        break;
      default: // it is possible that the current atom does not have a proper orientation, skip the method
        return -1;
    }
    
    while (true) { // check if the last and firsts neighbours are occupied
      currentAtom = clearAreaList.get(clearAreaList.size() - 1).getNeighbour(right);
      if (currentAtom.isOccupied() || currentAtom.getType() < 2) {
        return distance - 1;
      }
      clearAreaList.set(1, currentAtom);

      currentAtom = clearAreaList.get(0).getNeighbour(left);
      if (currentAtom.isOccupied() || currentAtom.getType() < 2) {
        return distance - 1;
      }
      clearAreaList.set(0, currentAtom);

      if (distance == thresholdDistance) {
        return distance;
      }
      distance++;
    }
  }
}
