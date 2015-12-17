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

  public BasicLattice(int hexaSizeI, int hexaSizeJ) {
    setHexaSizeI(hexaSizeI);
    setHexaSizeJ(hexaSizeJ);
    setHexaSizeK(1);
    setUnitCellSize(1);
    
    atoms = new BasicAtom[hexaSizeI * hexaSizeJ];
    createAtoms(hexaSizeI, hexaSizeJ);
    interconnectAtoms();
  }

  @Override
  public AbstractAtom getAtom(int iHexa, int jHexa, int kHexa, int unitCellPos) {
    return atoms[((jHexa) * getHexaSizeI() + iHexa) * getUnitCellSize() + unitCellPos];
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < atoms.length; i++) {
      atoms[i].setProbabilities(probabilities);
    }
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
