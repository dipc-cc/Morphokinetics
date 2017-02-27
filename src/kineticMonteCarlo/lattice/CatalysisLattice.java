/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import java.awt.geom.Point2D;
import java.io.PrintWriter;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.CatalysisAtom;
import static kineticMonteCarlo.atom.CatalysisAtom.ISLAND;
import static kineticMonteCarlo.atom.CatalysisAtom.TERRACE;
import static kineticMonteCarlo.atom.CatalysisAtom.EDGE;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.unitCell.AbstractGrowthUc;
import utils.StaticRandom;

/**
 *
 * @author Karmele Valencia
 */
public class CatalysisLattice extends AbstractGrowthLattice {

  public CatalysisLattice(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified) {
    super(hexaSizeI, hexaSizeJ, modified);
  }
  
  @Override
  public CatalysisAtom getCentralAtom() {
    int jCentre = (getHexaSizeJ() / 2);
    int iCentre = (getHexaSizeI() / 2);
    return (CatalysisAtom) getAtom(iCentre, jCentre, 0);
  }
  
  @Override
  public AbstractGrowthAtom getNeighbour(int iHexa, int jHexa, int neighbour) {
    int index = jHexa * getHexaSizeI() + iHexa;
    return ((CatalysisAtom) getUc(index).getAtom(0)).getNeighbour(neighbour);
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
  public int getAvailableDistance(AbstractGrowthAtom atom, int thresholdDistance) {
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
  public AbstractGrowthAtom getFarSite(AbstractGrowthAtom atom, int distance) {
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
   * Default rates to jump from one type to the other. For example, this matrix stores the rates to
   * jump from terrace to edge.
   *
   * @param probabilities Default rates.
   */
  public void initialiseRates(double[][][] probabilities) {
    //atomTypesAmount = probabilities.length;
    //atomTypesCounter = new int[atomTypesAmount];
    for (int i = 0; i < size(); i++) {
      AbstractGrowthUc uc = getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        CatalysisAtom atom = (CatalysisAtom) uc.getAtom(j);
        atom.initialiseRates(probabilities);
      }
    }
  }
  
  public void init() {
    setAtoms(createAtoms());
    setAngles();
  }    

  @Override
  public void deposit(AbstractGrowthAtom a, boolean forceNucleation) {
    CatalysisAtom atom = (CatalysisAtom) a;
    atom.setOccupied(true);
    if (forceNucleation) {
      atom.setType(ISLAND);
    }

    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        int originalPosition = (i + 2) % 4;
        addOccupiedNeighbour(atom.getNeighbour(i), originalPosition, forceNucleation);
      }
    }

    addAtom(atom);
    if (atom.getOccupiedNeighbours()> 0) {
      addBondAtom(atom);
    }
    atom.resetProbability();
  }
  
  @Override
  public double extract(AbstractGrowthAtom a) {
    CatalysisAtom atom = (CatalysisAtom) a;
    atom.setOccupied(false);
    double probabilityChange = a.getProbability();
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      if (!atom.getNeighbour(i).isPartOfImmobilSubstrate()) {
        int originalPosition = (i + 2) % 4;
        removeOccupied(atom.getNeighbour(i), originalPosition);
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
    AbstractGrowthAtom atom = getUc(iLattice, jLattice).getAtom(pos);

    if (atom.isOccupied()) {
      extract(atom);
    } else {
      deposit(atom, false);
    }
  }
  
  /**
   * There are no islands in catalysis
   * @param print
   * @return -1
   */
  @Override
  public int countIslands(PrintWriter print) {
    return -1;
  }
  
  private int createId(int i, int j) {
    return j * getHexaSizeI() + i;
  }
  
  private CatalysisAtom[][] createAtoms() {
    //Instantiate atoms
    CatalysisAtom[][] atoms = new CatalysisAtom[getHexaSizeI()][getHexaSizeJ()];
    for (int i = 0; i < getHexaSizeI(); i++) {
      for (int j = 0; j < getHexaSizeJ(); j++) {
        atoms[i][j] = new CatalysisAtom(createId(i, j), (short) i, (short) j);
      }
    }
    
    //Interconect atoms
    for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
      for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
        // get current atom
        CatalysisAtom atom = (CatalysisAtom) atoms[iHexa][jHexa];
        
        // north neighbour
        int i = iHexa;
        int j = jHexa - 1;
        if (j < 0) j = getHexaSizeJ() - 1;
        atom.setNeighbour((CatalysisAtom) atoms[i][j], 0);

        // east neighbour
        i = iHexa + 1;
        j = jHexa;
        if (i == getHexaSizeI()) i = 0;
        atom.setNeighbour((CatalysisAtom) atoms[i][j], 1);

        // south neighbour
        i = iHexa;
        j = jHexa + 1;
        if (j == getHexaSizeJ()) j = 0;
        atom.setNeighbour((CatalysisAtom) atoms[i][j], 2);
        
        // west neighbour
        i = iHexa - 1;
        j = jHexa;
        if (i < 0) i = getHexaSizeI() - 1;
        atom.setNeighbour((CatalysisAtom) atoms[i][j], 3);
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
  private int getClearAreaTerrace(AbstractGrowthAtom atom, int thresholdDistance) {
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
  private AbstractGrowthAtom chooseClearAreaTerrace(AbstractGrowthAtom atom, int distance) {
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
  
  private int getClearAreaStep(AbstractGrowthAtom atom, int thresholdDistance) {
    int distance = 1;
    AbstractGrowthAtom currentAtom;
    AbstractGrowthAtom lastRight = atom;
    AbstractGrowthAtom lastLeft = atom;
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
  
  private AbstractGrowthAtom chooseClearAreaStep(AbstractGrowthAtom atom, int distance) {
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
  private void addOccupiedNeighbour(CatalysisAtom neighbourAtom, int neighbourPosition, boolean forceNucleation) {
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

  private void updateSecondNeighbour(CatalysisAtom secondNeighbourAtom) {
    if (secondNeighbourAtom.isOccupied()) {
      addAtom(secondNeighbourAtom);
    }
  }
  
  /**
   * Computes the removal of one atom.
   * 
   * @param neighbourAtom neighbour atom of the original atom.
   */
  private void removeOccupied(CatalysisAtom neighbourAtom, int neighbourPosition) {
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