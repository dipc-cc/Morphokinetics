/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.unitCell.AgUc;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point3D;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.ConcertedSite;
import kineticMonteCarlo.site.AgSite;
import static kineticMonteCarlo.site.AgSite.EDGE;
import static kineticMonteCarlo.site.AgSite.TERRACE;
import kineticMonteCarlo.site.AgSiteSimple;
import kineticMonteCarlo.site.ModifiedBuffer;
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
  private final int[] shift = {2,3,4,5,0,1};
  
  /**
   * Creates a lattice to work with hexagonal Ag simulation, based on unit cells (UC).
   * 
   * @param hexaSizeI size in I direction. How many points horizontally.
   * @param hexaSizeJ size in J direction. How many points vertically.
   * @param modified temporary buffer.
   * @param distancePerStep auxiliary class for Devita.
   * @param type whether to use Ag simple, normal or concerted
   */
  public AgUcLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep, int type) {
    super(hexaSizeI, hexaSizeJ, modified, distancePerStep);
    setUnitCellSize(2);
    ucList = new ArrayList<>();
    createAtoms(type);
    
    // We assume that central unit cell, position 0 is the centre
    centralCartesianLocation = new Point2D.Float(getHexaSizeI() / 2.0f, (float) (getHexaSizeJ() / 2.0f) * (Y_RATIO * 2));
    setAngles();
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
  public AgUc getUc(int pos) {
    return ucList.get(pos);
  }
  
  @Override
  public AgUc getUc(int i, int j) {
    return ucArray[i][j];
  }
  
  /**
   * The area is the number of cells * 2.
   * @return the coverage of the lattice.
   */
  @Override
  public float getCoverage() {
    return (float) getOccupied() / (float) (sizeI * sizeJ * 2);
  }
  
  
  @Override
  public int size() {
    return ucList.size();
  }
  
  /**
   * Returns an atom of given unit cell and lattice position.
   *
   * @param iHexa
   * @param jHexa
   * @param pos
   * @return an atom.
   */
  @Override
  public AgSite getSite(int iHexa, int jHexa, int pos) {
    return ucArray[iHexa][jHexa].getSite(pos);
  }
  
  @Override
  public AgSite getCentralAtom() {
    int jCentre = (getHexaSizeJ() / 2);
    int iCentre = (getHexaSizeI() / 2);
    return getSite(iCentre, jCentre, 0);
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
  public int getAvailableDistance(AbstractGrowthSite atom, int thresholdDistance) {
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
  
  @Override
  public AbstractGrowthSite getFarSite(AbstractGrowthSite atom, int distance) {
    switch (atom.getType()) {
      case TERRACE:
        return chooseClearAreaTerrace(atom, distance);
      case EDGE:
        return chooseClearAreaStep(atom, distance);
      default:
        return null;
    }
  }
    
  /**
   * Changes the occupation of the clicked atom from unoccupied to occupied, or vice versa. It is
   * experimental and only works with AgUc simulation mode. If fails, the execution continues
   * normally.
   *
   * @param xMouse absolute X location of the pressed point
   * @param yMouse absolute Y location of the pressed point
   * @param scale zoom level
   */
  @Override
  public void changeOccupationByHand(double xMouse, double yMouse, int scale) {
    int iLattice;
    int jLattice;
    // scale the position with respect to the current scale.
    double xCanvas = xMouse / scale;
    double yCanvas = yMouse / scale;
    // choose the correct lattice
    iLattice = (int) Math.floor(xCanvas);
    jLattice = (int) Math.floor(yCanvas / (2.0 * Y_RATIO));
    double j = yCanvas / (2.0 * Y_RATIO);
    double i;
    int pos = 0;
    // choose the atom within the lattice
    if (j - jLattice > 0.5) {
      pos = 1;
      i = (double) xCanvas;
      if (i - iLattice < 0.5) { // correct the lattice number in pos 1
        iLattice--;
      }
    }

    AbstractGrowthSite atom = getUc(iLattice, jLattice).getSite(pos);

    if (atom.isOccupied()) {
      extract(atom);
    } else {
      deposit(atom, false);
    }
  }
  
  /**
   * Creates all atoms and assigns it neighbours. How are computed the neighbours is documented here:
   * https://bitbucket.org/Nesferjo/ekmc-project/wiki/Relationship%20between%20Cartesian%20and%20hexagonal%20representations
   *  
   * @param type 0 = AgAtomSimple, type 1 = AgAtom, type 2 = ConcertedAtom.
   */
  private void createAtoms(int type) {
    sizeI = Math.round(getCartSizeX() / AgUc.getSizeX());
    sizeJ = Math.round(getCartSizeY() / AgUc.getSizeY());
    // Initialise unit cells (with atoms)
    ucArray = new AgUc[sizeI][sizeJ];
    int id = 0;
    for (int i = 0; i < sizeI; i++) {
      for (int j = 0; j < sizeJ; j++) {
        List<AgSite> atomsList = new ArrayList<>(2);
        AgSite atom0;
        AgSite atom1;
        switch (type) {
          case 0:
            atom0 = new AgSiteSimple(id++, 0);
            atom1 = new AgSiteSimple(id++, 1);
            break;
          case 1:
            atom0 = new AgSite(id++, 0);
            atom1 = new AgSite(id++, 1);
            break;
          case 2:
            atom0 = new ConcertedSite(id++, 0);
            atom1 = new ConcertedSite(id++, 1);
            break;
          default:
            atom0 = null;
            atom1 = null; // it should not happen!
        }
        atomsList.add(atom0);
        atomsList.add(atom1);
        AgUc uc = new AgUc(i, j, atomsList);
        ucList.add(uc);
        ucArray[i][j] = uc;
        // set Cartesian position
        Point3D ucPos = uc.getPos();
        Point3D atom0Pos = atom0.getPos();
        Point3D atom1Pos = atom1.getPos();
        atom0.setCartesianPosition(ucPos.add(atom0Pos));
        atom1.setCartesianPosition(ucPos.add(atom1Pos));
      }
    }
    
    //Interconect atoms (go through all unit cells)
    for (int k = 0; k < size(); k++) {
      AgUc uc = ucList.get(k);

      // First atom of unit cell
      AgSite atom = uc.getSite(0);

      int i = uc.getPosI() - 1;
      int j = uc.getPosJ() - 1;
      if (i < 0) i = sizeI - 1;
      if (j < 0) j = sizeJ - 1;
      atom.setNeighbour(ucArray[i][j].getSite(1), 0);

      i = uc.getPosI();
      j = uc.getPosJ() - 1;
      if (j < 0) j = sizeJ - 1;
      atom.setNeighbour(ucArray[i][j].getSite(1), 1);

      i = uc.getPosI() + 1;
      j = uc.getPosJ();
      if (i == sizeI) i = 0;
      atom.setNeighbour(ucArray[i][j].getSite(0), 2);

      i = uc.getPosI();
      j = uc.getPosJ();
      atom.setNeighbour(ucArray[i][j].getSite(1), 3);

      i = uc.getPosI() - 1;
      j = uc.getPosJ();
      if (i < 0) i = sizeI - 1;
      atom.setNeighbour(ucArray[i][j].getSite(1), 4);

      i = uc.getPosI() - 1;
      j = uc.getPosJ();
      if (i < 0) i = sizeI - 1;
      atom.setNeighbour(ucArray[i][j].getSite(0), 5);

      // Second atom of unit cell
      atom = uc.getSite(1);

      i = uc.getPosI();
      j = uc.getPosJ();
      atom.setNeighbour(ucArray[i][j].getSite(0), 0);

      i = uc.getPosI() + 1;
      j = uc.getPosJ();
      if (i == sizeI) i = 0;
      atom.setNeighbour(ucArray[i][j].getSite(0), 1);
      atom.setNeighbour(ucArray[i][j].getSite(1), 2);

      i = uc.getPosI() + 1;
      j = uc.getPosJ() + 1;
      if (i == sizeI) i = 0;
      if (j == sizeJ) j = 0;
      atom.setNeighbour(ucArray[i][j].getSite(0), 3);

      i = uc.getPosI();
      j = uc.getPosJ() + 1;
      if (j == sizeJ) j = 0;
      atom.setNeighbour(ucArray[i][j].getSite(0), 4);

      i = uc.getPosI() - 1;
      j = uc.getPosJ();
      if (i < 0) i = sizeI - 1;
      atom.setNeighbour(ucArray[i][j].getSite(1), 5);
    }

  }
  
  /**
   * We only care about the largest possible distance atoms.
   * 
   * @param atom
   * @param thresholdDistance
   * @return clear distance.
   */
  private int getClearAreaTerrace(AbstractGrowthSite atom, int thresholdDistance) {
    byte errorCode = 0;
    int possibleDistance = 0;
    while (true) {
      atom = atom.getNeighbour(4); // get the first neighbour
      for (int direction = 0; direction < 6; direction++) {
        for (int j = 0; j <= possibleDistance; j++) {
          atom = atom.getNeighbour(direction);
          if (atom.isOutside()) {
            errorCode |= 1;
          }
          if (atom.isOccupied()) { // we have touched an occupied atom, exit
            errorCode |= 2;
            return possibleDistance;
          }
        }
      }
      possibleDistance++;
      if ((errorCode & 1) != 0) { // if some of the atoms are outside, return
        return possibleDistance;
      }
      if (possibleDistance > thresholdDistance) {
        return thresholdDistance;
      }
    }
  }
  
  private int getClearAreaTerrace(AbstractGrowthSite atom, int thresholdDistance, boolean changeLevel, int currentLevel, int position, int turnDirection, byte errorCode) {
    int possibleDistance = 1; // = currentLevel
    AbstractGrowthSite currentAtom;
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
      //clearAreaList.add(currentAtom);
    }
    if (currentLevel >= thresholdDistance) {
      return possibleDistance;
    }

    return getClearAreaTerrace(currentAtom, thresholdDistance, changeLevel, currentLevel, position, turnDirection, errorCode);
  }
  
    
  /**
   * 
   * @param atom origin atom.
   * @param distance how far we have to move.
   * @return destination atom.
   */
  private AbstractGrowthSite chooseClearAreaTerrace(AbstractGrowthSite atom, int distance) {
    int sizeOfPerimeter = distance * 6;
    int randomNumber = StaticRandom.rawInteger(sizeOfPerimeter);
    int quotient = randomNumber / distance; // far direction
    int mod = randomNumber % distance; // perimeter direction
    
    for (int i = 0; i < distance; i++) {
      atom = atom.getNeighbour(quotient);
    }
    
    for (int i = 0; i < mod; i++) {
      atom = atom.getNeighbour(shift[quotient]);
    }
    
    return atom;
  }
  
  private int getClearAreaStep(AbstractGrowthSite atom, int thresholdDistance) {
    int distance = 1;
    AbstractGrowthSite currentAtom;
    AbstractGrowthSite lastRight = atom;
    AbstractGrowthSite lastLeft = atom;
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
      currentAtom = lastRight.getNeighbour(right);
      if (currentAtom.isOccupied() || currentAtom.getType() < 2) {
        return distance - 1;
      }
      lastRight = currentAtom;

      currentAtom = lastLeft.getNeighbour(left);
      if (currentAtom.isOccupied() || currentAtom.getType() < 2) {
        return distance - 1;
      }
      lastLeft = currentAtom;

      if (distance == thresholdDistance) {
        return distance;
      }
      distance++;
    }
  }
  
  private AbstractGrowthSite chooseClearAreaStep(AbstractGrowthSite atom, int distance) {
    double randomNumber = StaticRandom.raw();
    int neighbour = 0;
    switch (atom.getOrientation()) {
      case 0:
      case 3:
        if (randomNumber > 0.5) {
          neighbour = 5;
          break;
        } else {
          neighbour = 2;
          break;
        }
      case 1:
      case 4:
        if (randomNumber > 0.5) {
          neighbour = 0;
          break;
        } else {
          neighbour = 3;
          break;
        }
      case 2:
      case 5:
        if (randomNumber > 0.5) {
          neighbour = 1;
          break;
        } else {
          neighbour = 4;
          break;
        }
    }
    for (int i = 0; i < distance; i++) {
      atom = atom.getNeighbour(neighbour);
    }
    return atom;
  }
}
