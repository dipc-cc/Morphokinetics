/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.BasicAtom;

/**
 *
 * @author Nestor
 */
public class BasicLattice extends AbstractEtchingLattice {

  private BasicAtom[] lattice;

  public BasicLattice(int hexaSizeI, int hexaSizeJ) {

    this.hexaSizeI = hexaSizeI;
    this.hexaSizeJ = hexaSizeJ;
    this.unitCellSize = 1;
    lattice = new BasicAtom[this.hexaSizeI * this.hexaSizeJ];
    createAtoms(hexaSizeI, hexaSizeJ);
    interconnectAtoms();
  }

  @Override
  public AbstractAtom getAtom(int iHexa, int jHexa, int kHexa, int unitCellPos) {
    return lattice[((jHexa) * hexaSizeI + iHexa) * unitCellSize + unitCellPos];
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < lattice.length; i++) {
      lattice[i].initialize(probabilities);
    }
  }

  @Override
  public void reset() {
    for (int i = 0; i < hexaSizeJ; i++) {
      for (int j = 0; j < hexaSizeI; j++) {
        lattice[i * hexaSizeI + j].setOnList(null);
        lattice[i * hexaSizeI + j].unRemove();
      }
    }

    for (int i = 0; i < hexaSizeJ; i++) {
      for (int j = 0; j < hexaSizeI; j++) {
        lattice[i * hexaSizeI + j].updateTypeFromScratch();
      }
    }

    for (int i = 0; i < hexaSizeJ; i++) {
      for (int j = 0; j < hexaSizeI; j++) {
        if (i < 4) {
          lattice[i * hexaSizeI + j].remove();
        }
      }
    }
  }

  @Override
  public int getHexaSizeI() {
    return super.getHexaSizeI();
  }

  @Override
  public int getHexaSizeJ() {
    return super.getHexaSizeJ();
  }

  @Override
  public int getHexaSizeK() {
    return 1;
  }

  @Override
  public int getSizeUC() {
    return 1;
  }

  private void createAtoms(int hexaSizeI, int hexaSizeJ) {
    for (short i = 0; i < hexaSizeJ; i++) {
      for (short j = 0; j < hexaSizeI; j++) {
        lattice[i * hexaSizeI + j] = new BasicAtom(j, i);
      }
    }
  }

  private void interconnectAtoms() {
    for (int i = 0; i < hexaSizeJ; i++) {
      for (int j = 0; j < hexaSizeI; j++) {
        if (i - 1 >= 0) {
          lattice[i * hexaSizeI + j].setNeighbor(lattice[(i - 1) * hexaSizeI + j], 0);                    //up        
        }
        lattice[i * hexaSizeI + j].setNeighbor(lattice[Math.min(i + 1, hexaSizeJ - 1) * hexaSizeI + j], 1);      //down        
        int izq = j - 1;
        if (izq < 0) {
          izq = hexaSizeI - 1;
        }
        lattice[i * hexaSizeI + j].setNeighbor(lattice[i * hexaSizeI + izq], 2);                        //left        
        lattice[i * hexaSizeI + j].setNeighbor(lattice[i * hexaSizeI + ((j + 1) % hexaSizeI)], 3);              //right       
      }
    }
  }
}
