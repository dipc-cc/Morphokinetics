/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.SiAtom;
import basic.unitCell.UnitCell;

/**
 *
 * @author Nestor
 */
public class SiLattice extends AbstractEtchingLattice {

  private UnitCell unitCell;

  public SiLattice(int millerX, int millerY, int millerZ, int sizeX, int sizeY, int sizeZ) {

    if (millerX == 0 && millerY == 0 && millerZ == 0) {
      System.exit(-1);
    }

    setHexaSizeI(sizeX);
    setHexaSizeJ(sizeY);
    setHexaSizeK(sizeZ);

    unitCell = new UnitCell();
    setUnitCellSize(unitCell.createUnitCell(millerX, millerY, millerZ));
    if (getUnitCellSize() < 4 || getUnitCellSize() > 127) {
      System.out.println("UC size inappropiate: " + getUnitCellSize());
      System.exit(-1);
    }
    short[] ucNeighbours = unitCell.getNeighs();
    byte[] block = unitCell.getNBlock();

    float[] coords = new float[getUnitCellSize() * 3 + 3];
    for (int i = 0; i < getUnitCellSize(); i++) {
      coords[i * 3] = unitCell.getCellsP()[i].getPosX(0, 0, 0);
      coords[i * 3 + 1] = unitCell.getCellsP()[i].getPosY(0, 0, 0);
      coords[i * 3 + 2] = unitCell.getCellsP()[i].getPosZ(0, 0, 0);
    }

    coords[getUnitCellSize() * 3] = (float) unitCell.getLimitX();
    coords[getUnitCellSize() * 3 + 1] = (float) unitCell.getLimitY();
    coords[getUnitCellSize() * 3 + 2] = (float) unitCell.getLimitZ();

    double tamX = unitCell.getCellsP()[0].getLimitX() * sizeX;
    double tamY = unitCell.getCellsP()[0].getLimitY() * sizeY;

    if (tamX > tamY) {
      setHexaSizeI((int) Math.round((int) sizeX * tamY / tamX));
    } else {
      setHexaSizeJ((int) Math.round((int) sizeY * tamX / tamY));
    }

    atoms = new SiAtom[getHexaSizeI() * getHexaSizeJ() * getHexaSizeK() * getUnitCellSize()];

    this.createAtoms(coords, unitCell);
    this.interconnectAtoms(ucNeighbours, block);
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < atoms.length; i++) {
      atoms[i].setProbabilities(probabilities);
    }
  }

  public UnitCell getUnitCell() {
    return unitCell;
  }

  @Override
  public SiAtom getAtom(int unitCellX, int unitCellY, int unitCellZ, int unitCellPos) {
    return (SiAtom) atoms[((unitCellZ * getHexaSizeJ() + unitCellY) * getHexaSizeI() + unitCellX) * getUnitCellSize() + unitCellPos];
  }

  @Override
  public void reset() {

    //Unremove atoms and set to bulk mode (4,12)
    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        for (int a = 0; a < this.getUnitCellSize(); a++) {
          atoms[(((getHexaSizeK() - 1) * getHexaSizeJ() + i) * getHexaSizeI() + j) * getUnitCellSize() + a].setList(null);
          atoms[(((getHexaSizeK() - 1) * getHexaSizeJ() + i) * getHexaSizeI() + j) * getUnitCellSize() + a].unRemove();
        }
      }
    }

    for (int k = 0; k < (getHexaSizeK() - 1) * getHexaSizeJ() * getHexaSizeI() * getUnitCellSize(); k++) {
      atoms[k].setList(null);
      atoms[k].unRemove();
      atoms[k].setAsBulk();
    }

    // Update neighbourhood of top atoms
    for (int k = (getHexaSizeK() - 1) * getHexaSizeJ() * getHexaSizeI() * getUnitCellSize(); k < getHexaSizeK() * getHexaSizeJ() * getHexaSizeI() * getUnitCellSize(); k++) {
      atoms[k].updateN1FromScratch();
    }

    for (int k = (getHexaSizeK() - 1) * getHexaSizeJ() * getHexaSizeI() * getUnitCellSize(); k < getHexaSizeK() * getHexaSizeJ() * getHexaSizeI() * getUnitCellSize(); k++) {
      atoms[k].updateN2FromScratch();
    }

    // Remove top layer atoms
    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        for (int a = 0; a < this.getUnitCellSize(); a++) {
          atoms[(((getHexaSizeK() - 1) * getHexaSizeJ() + i) * getHexaSizeI() + j) * this.getUnitCellSize() + a].remove();
        }
      }
    }
  }

  /**
   * Atoms creation.
   * @param coords
   * @param UC 
   */
  private void createAtoms(float[] coords, UnitCell UC) {
    int cont = 0;
    for (int a = 0; a < getHexaSizeK(); a++) {
      for (int b = 0; b < getHexaSizeJ(); b++) {
        for (int c = 0; c < getHexaSizeI(); c++) {
          for (int j = 0; j < getUnitCellSize(); j++) {
            float x = coords[j * 3] + c * (float) UC.getLimitX();
            float y = coords[j * 3 + 1] + b * (float) UC.getLimitY();
            float z = -coords[j * 3 + 2] + (a + 1) * (float) UC.getLimitZ();

            atoms[cont] = new SiAtom(x, y, z);
            cont++;
          }
        }
      }
    }
  }

  /**
   * Atoms inter-connection.
   * @param neihbourUnitCell
   * @param block 
   */
  private void interconnectAtoms(short[] neihbourUnitCell, byte[] block) {
    for (int z = 0; z < getHexaSizeK(); z++) {
      for (int y = 0; y < getHexaSizeJ(); y++) {
        for (int x = 0; x < getHexaSizeI(); x++) {
          for (int j = 0; j < getUnitCellSize(); j++) {
            for (int i = 0; i < 4; i++) {
              int xNeighbour = x;
              int yNeighbour = y;
              int zNeighbour = z;
              int posNeighbour = neihbourUnitCell[j * 4 + i];
              if ((block[j * 4 + i] & 3) == 1) {
                yNeighbour++;
              }
              if ((block[j * 4 + i] & 3) == 3) {
                yNeighbour--;
              }
              if (yNeighbour < 0) {
                yNeighbour = getHexaSizeJ() - 1;
              }
              if (yNeighbour > getHexaSizeJ() - 1) {
                yNeighbour = 0;
              }

              if (((block[j * 4 + i] >> 2) & 3) == 1) {
                xNeighbour++;
              }
              if (((block[j * 4 + i] >> 2) & 3) == 3) {
                xNeighbour--;
              }
              if (xNeighbour < 0) {
                xNeighbour = getHexaSizeI() - 1;
              }
              if (xNeighbour > getHexaSizeI() - 1) {
                xNeighbour = 0;
              }

              if (((block[j * 4 + i] >> 4) & 3) == 1) {
                zNeighbour++;
              }
              if (((block[j * 4 + i] >> 4) & 3) == 3) {
                zNeighbour--;
              }

              if (zNeighbour < 0) {
                xNeighbour = 0;
                yNeighbour = 0;
                zNeighbour = 0;
                posNeighbour = j;
              }
              if (zNeighbour < getHexaSizeK()) {
                atoms[((z * getHexaSizeJ() + y) * getHexaSizeI() + x) * getUnitCellSize() + j].
                        setNeighbour((SiAtom)atoms[((zNeighbour * getHexaSizeJ() + yNeighbour) * getHexaSizeI() + xNeighbour) * getUnitCellSize() + posNeighbour], i);
              } else {
                atoms[((z * getHexaSizeJ() + y) * getHexaSizeI() + x) * getUnitCellSize() + j].setNeighbour(null, i);
              }
            }
          }
        }
      }
    }
  }
}
