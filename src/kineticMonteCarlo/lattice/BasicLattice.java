/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
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

import kineticMonteCarlo.atom.BasicAtom;
import kineticMonteCarlo.unitCell.IUc;
import kineticMonteCarlo.unitCell.Simple3dUc;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class BasicLattice extends AbstractLattice {

  private BasicAtom[] atoms;
  private Simple3dUc[] ucList;
  
  public BasicLattice(int hexaSizeI, int hexaSizeJ) {
    setHexaSizeI(hexaSizeI);
    setHexaSizeJ(hexaSizeJ);
    setHexaSizeK(1);
    setUnitCellSize(1);
    
    atoms = new BasicAtom[hexaSizeI * hexaSizeJ];
    ucList = new Simple3dUc[hexaSizeI * hexaSizeJ];
    createAtoms(hexaSizeI, hexaSizeJ);
    interconnectAtoms();
  }

  @Override
  public BasicAtom getAtom(int iHexa, int jHexa, int kHexa, int unitCellPos) {
    return atoms[((jHexa) * getHexaSizeI() + iHexa) * getUnitCellSize() + unitCellPos];
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < atoms.length; i++) {
      atoms[i].setProbabilities(probabilities);
    }
  }

  @Override
  public IUc getUc(int pos) {
    return ucList[pos];
  }

  /**
   * Number of islands has no sense in etching.
   *
   * @return -1 always.
   */
  @Override
  public int getIslandCount() {
    return -1;
  }
  
  /**
   * Gyradius is not implemented in etching.
   *
   * @return -1.0 always.
   */
  @Override
  public float getAverageGyradius() {
    return -1.0f;
  }
  
  @Override
  public void reset() {
    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        atoms[i * getHexaSizeI() + j].setList(null);
        atoms[i * getHexaSizeI() + j].unRemove();
      }
    }

    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        atoms[i * getHexaSizeI() + j].updateN1FromScratch();
      }
    }

    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        if (i < 4) {
          atoms[i * getHexaSizeI() + j].remove();
        }
      }
    }
  }

  private void createAtoms(int hexaSizeI, int hexaSizeJ) {
    for (short i = 0; i < hexaSizeJ; i++) {
      for (short j = 0; j < hexaSizeI; j++) {
        atoms[i * hexaSizeI + j] = new BasicAtom(j, i);
        ucList[i * hexaSizeI + j] = new Simple3dUc(j, i, atoms[i * hexaSizeI + j]);
      }
    }
  }

  private void interconnectAtoms() {
    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        if (i - 1 >= 0) {
          atoms[i * getHexaSizeI() + j].setNeighbour(atoms[(i - 1) * getHexaSizeI() + j], 0);                      //up        
        }
        atoms[i * getHexaSizeI() + j].setNeighbour(atoms[Math.min(i + 1, getHexaSizeJ() - 1) * getHexaSizeI() + j], 1); //down        
        int izq = j - 1;
        if (izq < 0) {
          izq = getHexaSizeI() - 1;
        }
        atoms[i * getHexaSizeI() + j].setNeighbour(atoms[i * getHexaSizeI() + izq], 2);                   //left        
        atoms[i * getHexaSizeI() + j].setNeighbour(atoms[i * getHexaSizeI() + ((j + 1) % getHexaSizeI())], 3); //right       
      }
    }
  }

}
