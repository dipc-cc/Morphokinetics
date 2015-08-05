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

  public BasicLattice(int axonSizeI, int axonSizeJ) {

    this.axonSizeI = axonSizeI;
    this.axonSizeJ = axonSizeJ;
    this.unitCellSize = 1;
    lattice = new BasicAtom[this.axonSizeI * this.axonSizeJ];
    createAtoms(axonSizeI, axonSizeJ);
    interconnectAtoms();
  }

  @Override
  public AbstractAtom getAtom(int iAxon, int jAxon, int kAxon, int unitCellPos) {
    return lattice[((jAxon) * axonSizeI + iAxon) * unitCellSize + unitCellPos];
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < lattice.length; i++) {
      lattice[i].initialize(probabilities);
    }
  }

  @Override
  public void reset() {
    for (int i = 0; i < axonSizeJ; i++) {
      for (int j = 0; j < axonSizeI; j++) {
        lattice[i * axonSizeI + j].setOnList(null);
        lattice[i * axonSizeI + j].unRemove();
      }
    }

    for (int i = 0; i < axonSizeJ; i++) {
      for (int j = 0; j < axonSizeI; j++) {
        lattice[i * axonSizeI + j].updateTypeFromScratch();
      }
    }

    for (int i = 0; i < axonSizeJ; i++) {
      for (int j = 0; j < axonSizeI; j++) {
        if (i < 4) {
          lattice[i * axonSizeI + j].remove();
        }
      }
    }
  }

  @Override
  public int getAxonSizeI() {
    return super.getAxonSizeI();
  }

  @Override
  public int getAxonSizeJ() {
    return super.getAxonSizeJ();
  }

  @Override
  public int getAxonSizeK() {
    return 1;
  }

  @Override
  public int getSizeUC() {
    return 1;
  }

  private void createAtoms(int axonSizeI, int axonSizeJ) {
    for (short i = 0; i < axonSizeJ; i++) {
      for (short j = 0; j < axonSizeI; j++) {
        lattice[i * axonSizeI + j] = new BasicAtom(j, i);
      }
    }
  }

  private void interconnectAtoms() {
    for (int i = 0; i < axonSizeJ; i++) {
      for (int j = 0; j < axonSizeI; j++) {
        if (i - 1 >= 0) {
          lattice[i * axonSizeI + j].setNeighbor(lattice[(i - 1) * axonSizeI + j], 0);                    //up        
        }
        lattice[i * axonSizeI + j].setNeighbor(lattice[Math.min(i + 1, axonSizeJ - 1) * axonSizeI + j], 1);      //down        
        int izq = j - 1;
        if (izq < 0) {
          izq = axonSizeI - 1;
        }
        lattice[i * axonSizeI + j].setNeighbor(lattice[i * axonSizeI + izq], 2);                        //left        
        lattice[i * axonSizeI + j].setNeighbor(lattice[i * axonSizeI + ((j + 1) % axonSizeI)], 3);              //right       
      }
    }
  }
}
