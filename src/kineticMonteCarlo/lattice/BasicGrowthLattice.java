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

import java.awt.geom.Point2D;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.AbstractSurfaceSite;
import kineticMonteCarlo.site.BasicGrowthSite;
import static kineticMonteCarlo.site.BasicGrowthSite.ISLAND;
import static kineticMonteCarlo.site.BasicGrowthSite.TERRACE;
import static kineticMonteCarlo.site.BasicGrowthSite.EDGE;
import kineticMonteCarlo.site.BasicGrowthSimpleSite;
import kineticMonteCarlo.site.ModifiedBuffer;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BasicGrowthLattice extends AbstractGrowthLattice {
  private final boolean simple;

  public BasicGrowthLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, boolean simple) {
    super(hexaSizeI, hexaSizeJ, modified);
    this.simple = simple;
  }
  
  @Override
  public BasicGrowthSite getCentralAtom() {
    int jCentre = (getHexaSizeJ() / 2);
    int iCentre = (getHexaSizeI() / 2);
    return (BasicGrowthSite) getSite(iCentre, jCentre, 0);
  }
  
  @Override
  public AbstractGrowthSite getNeighbour(int iHexa, int jHexa, int neighbour) {
    int index = jHexa * getHexaSizeI() + iHexa;
    return ((BasicGrowthSite) getUc(index).getSite(0)).getNeighbour(neighbour);
  }

  @Override
  public float getCartSizeX() {
    return getHexaSizeI();
  }

  @Override
  public float getCartSizeY() {
    return getHexaSizeJ();
  }

  @Override
  public double getCartX(int iHexa, int jHexa) {
    return iHexa;
  }

  @Override
  public double getCartY(int jHexa) {
    return jHexa;
  }

  @Override
  public int getiHexa(double xCart, double yCart) {
    return (int) xCart;
  }

  @Override
  public int getjHexa(double yCart) {
    return (int) yCart;
  }

  @Override
  public Point2D getCartesianLocation(int iHexa, int jHexa) {
    return new Point2D.Double(iHexa, jHexa);
  }

  @Override
  public Point2D getCentralCartesianLocation() {
    return new Point2D.Float(getHexaSizeI() / 2, getHexaSizeJ() / 2);
  }

  @Override
  public int getAvailableDistance(AbstractGrowthSite atom, int thresholdDistance) {
    switch (atom.getType()) {
      case TERRACE:
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
  
  public void init() {
    setAtoms(createAtoms());
    setAngles();
  }    

  @Override
  public void deposit(AbstractSurfaceSite a, boolean forceNucleation) {
    BasicGrowthSite atom = (BasicGrowthSite) a;
    atom.setOccupied(true);
    if (forceNucleation) {
      atom.setType(ISLAND);
    }

    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        int originalPosition = (i + 2) % 4;
        addNeighbour(atom.getNeighbour(i), originalPosition, forceNucleation);
      }
    }

    addAtom(atom);
    if (atom.getOccupiedNeighbours() > 0) {
      addBondAtom(atom);
    }
    atom.resetProbability();
  }
  
  @Override
  public double extract(AbstractSurfaceSite a) {
    BasicGrowthSite atom = (BasicGrowthSite) a;
    atom.setOccupied(false);
    double probabilityChange = atom.getProbability();
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        int originalPosition = (i + 2) % 4;
        removeNeighbour(atom.getNeighbour(i), originalPosition);
      }
    }

    if (atom.getOccupiedNeighbours()> 0) {
      addBondAtom(atom);
    }

    atom.resetProbability();
    atom.setList(false);
    return probabilityChange;
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
    jLattice = (int) Math.floor(yCanvas);
    double j = yCanvas;
    int pos = 0;

    // for debugging
    System.out.println("scale " + scale + " " + (jLattice - j));
    System.out.println("x y " + xMouse + " " + yMouse + " | " + xCanvas + " " + yCanvas + " | " + iLattice + " " + jLattice + " | ");
    AbstractSurfaceSite atom = getUc(iLattice, jLattice).getSite(pos);

    if (atom.isOccupied()) {
      extract(atom);
    } else {
      deposit(atom, false);
    }
  }
  
  private int createId(int i, int j) {
    return j * getHexaSizeI() + i;
  }
  
  private BasicGrowthSite[][] createAtoms() {
    BasicGrowthSite[][] atoms;
    //Instantiate atoms
    if (simple) {
      atoms = new BasicGrowthSimpleSite[getHexaSizeI()][getHexaSizeJ()];
    } else {
      atoms = new BasicGrowthSite[getHexaSizeI()][getHexaSizeJ()];
    }
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        if (simple) {
          atoms[i][j] = new BasicGrowthSimpleSite(createId(i, j), (short) i, (short) j);
        } else {
          atoms[i][j] = new BasicGrowthSite(createId(i, j), (short) i, (short) j);
        }
      }
    }
    
    //Interconect atoms
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        // get current atom
        BasicGrowthSite atom = (BasicGrowthSite) atoms[iHexa][jHexa];
        
        // north neighbour
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;
        atom.setNeighbour((BasicGrowthSite) atoms[i][j], 0);

        // east neighbour
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;
        atom.setNeighbour((BasicGrowthSite) atoms[i][j], 1);

        // south neighbour
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;
        atom.setNeighbour((BasicGrowthSite) atoms[i][j], 2);
        
        // west neighbour
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        atom.setNeighbour((BasicGrowthSite) atoms[i][j], 3);
      }
    }
    return atoms;
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
    
    int quantity;
    while (true) {
      atom = atom.getNeighbour(2).getNeighbour(3); // get the first neighbour
      quantity = (possibleDistance * 2 + 2);
      for (int direction = 0; direction < 4; direction++) {
        for (int j = 0; j < quantity; j++) {
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
  
  /**
   * Chooses the randomly a far atom. Firstly, it goes to the given distance and secondly, it
   * navigates throw the perimeter.
   * 
   * @param atom origin atom.
   * @param distance how far we have to move.
   * @return destination atom.
   */
  private AbstractGrowthSite chooseClearAreaTerrace(AbstractGrowthSite atom, int distance) {
    int sizeOfPerimeter = distance * 2 * 4;
    int randomNumber = StaticRandom.rawInteger(sizeOfPerimeter);
    int quotient = randomNumber / (distance*2); // far direction
    int mod = randomNumber % (distance*2); // perimeter direction
    
    for (int i = 0; i < distance; i++) { // go far direction
      atom = atom.getNeighbour(quotient);
    }
    
    int direction;
    if (mod > distance) {
      direction = (quotient + 3) % 4;
      mod = mod - distance;
    }
    else {
      direction = (quotient + 1) % 4;
    }
    for (int i = 0; i < mod; i++) { // go throw perimeter (if required)
      atom = atom.getNeighbour(direction);
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
        right = 1;
        left = 3;
        break;
      case 1:
        right = 2;
        left = 0;
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
        if (randomNumber > 0.5) {
          neighbour = 1;
          break;
        } else {
          neighbour = 3;
          break;
        }
      case 1:
        if (randomNumber > 0.5) {
          neighbour = 0;
          break;
        } else {
          neighbour = 2;
          break;
        }
    }
    for (int i = 0; i < distance; i++) {
      atom = atom.getNeighbour(neighbour);
    }
    return atom;
  }
  
  /**
   * A new occupied atom was added before calling this method, here, updating the first and the
   * second neighbourhood.
   *
   * @param neighbourAtom current atom.
   * @param neighbourPosition the position of the neighbour.
   * @param forceNucleation
   */
  private void addNeighbour(BasicGrowthSite neighbourAtom, int neighbourPosition, boolean forceNucleation) {
    byte newType;

    newType = neighbourAtom.getNewType(1); 
    neighbourAtom.addOccupiedNeighbour(1);
    
    if (forceNucleation && neighbourAtom.isOccupied()) {
      newType = ISLAND;
    }

    if (neighbourAtom.getType() != newType) { // the type of neighbour has changed
      neighbourAtom.setType(newType);
      addAtom(neighbourAtom);
      if (neighbourAtom.getOccupiedNeighbours() > 0 && !neighbourAtom.isOccupied()) {
        addBondAtom(neighbourAtom);
      }
      // update second neighbours 
      for (int pos = 0; pos < neighbourAtom.getNumberOfNeighbours(); pos++) {
        // skip if the neighbour is the original atom or it is an island.
        if (neighbourPosition != pos && !neighbourAtom.getNeighbour(pos).isPartOfImmobilSubstrate()) {
          updateSecondNeighbour(neighbourAtom.getNeighbour(pos));
        }
      }
    }
  }

  private void updateSecondNeighbour(BasicGrowthSite secondNeighbourAtom) {
    if (secondNeighbourAtom.isOccupied()) {
      addAtom(secondNeighbourAtom);
    }
  }
  
  /**
   * Computes the removal of one atom.
   * 
   * @param neighbourAtom neighbour atom of the original atom.
   */
  private void removeNeighbour(BasicGrowthSite neighbourAtom, int neighbourPosition) {
    byte newType = neighbourAtom.getNewType(-1); // one less atom
    neighbourAtom.addOccupiedNeighbour(-1); // remove one atom (original atom has been extracted)

    if (neighbourAtom.getType() != newType) {
      neighbourAtom.setType(newType);
      addAtom(neighbourAtom);
      if (neighbourAtom.getOccupiedNeighbours() > 0 && !neighbourAtom.isOccupied()) {
        addBondAtom(neighbourAtom);
      }
      
      // update second neighbours 
      for (int pos = 0; pos < neighbourAtom.getNumberOfNeighbours(); pos++) {
        // skip if the neighbour is the original atom or is an island.
        if (neighbourPosition != pos && !neighbourAtom.getNeighbour(pos).isPartOfImmobilSubstrate()) {
          updateSecondNeighbour(neighbourAtom.getNeighbour(pos));
        }
      }
    }
  }
}
