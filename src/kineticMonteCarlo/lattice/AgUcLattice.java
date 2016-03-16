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
import kineticMonteCarlo.atom.AgAtom;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;

/**
 * Ag lattice with unit cell
 * @author J. Alberdi-Rodriguez
 */
public class AgUcLattice extends AgLattice {
  
  private final Point2D centralCartesianLocation;
  /**
   * How many unit cells are in X axis
   */
  private int sizeI;
  /**
   * How many unit cells are in Y axis
   */
  private int sizeJ;
  /**
   * Unit cell list
   */
  private List<AgUc> ucList;
  /**
   * Unit cell array
   */
  private AgUc[][] ucArray;
  
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
}
