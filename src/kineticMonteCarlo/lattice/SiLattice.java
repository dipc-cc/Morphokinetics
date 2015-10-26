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

  private SiAtom[] lattice;
  private UnitCell unitCell;

  public SiLattice(int millerX, int millerY, int millerZ, int sizeX, int sizeY, int sizeZ) {

    if (millerX == 0 && millerY == 0 && millerZ == 0) {
      System.exit(-1);
    }

    this.hexaSizeI = sizeX;
    this.hexaSizeJ = sizeY;
    this.hexaSizeK = sizeZ;

    unitCell = new UnitCell();
    unitCellSize = unitCell.createUnitCell(millerX, millerY, millerZ);
    if (unitCellSize < 4 || unitCellSize > 127) {
      System.out.println("UC size inappropiate: " + unitCellSize);
      System.exit(-1);
    }
    short[] ucNeighbours = unitCell.getNeighs();
    byte[] block = unitCell.getNBlock();

    float[] coords = new float[unitCellSize * 3 + 3];
    for (int i = 0; i < unitCellSize; i++) {
      coords[i * 3] = unitCell.getCellsP()[i].getPosX(0, 0, 0);
      coords[i * 3 + 1] = unitCell.getCellsP()[i].getPosY(0, 0, 0);
      coords[i * 3 + 2] = unitCell.getCellsP()[i].getPosZ(0, 0, 0);
    }

    coords[unitCellSize * 3] = (float) unitCell.getLimitX();
    coords[unitCellSize * 3 + 1] = (float) unitCell.getLimitY();
    coords[unitCellSize * 3 + 2] = (float) unitCell.getLimitZ();

    double tamX = unitCell.getCellsP()[0].getLimitX() * sizeX;
    double tamY = unitCell.getCellsP()[0].getLimitY() * sizeY;

    if (tamX > tamY) {
      this.hexaSizeI = (int) Math.round((int) sizeX * tamY / tamX);
    } else {
      this.hexaSizeJ = (int) Math.round((int) sizeY * tamX / tamY);
    }

    lattice = new SiAtom[this.hexaSizeI * this.hexaSizeJ * this.hexaSizeK * unitCellSize];

    this.createAtoms(coords, unitCell);
    this.interconnectAtoms(ucNeighbours, block);
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < lattice.length; i++) {
      lattice[i].initialize(probabilities);
    }
  }

  public UnitCell getUnitCell() {
    return unitCell;
  }

  @Override
  public SiAtom getAtom(int unitCellX, int unitCellY, int unitCellZ, int unitCellPos) {
    return lattice[((unitCellZ * hexaSizeJ + unitCellY) * hexaSizeI + unitCellX) * unitCellSize + unitCellPos];
  }

  @Override
  public void reset() {

    //Unremove atoms and set to bulk mode (4,12)
    for (int i = 0; i < this.hexaSizeJ; i++) {
      for (int j = 0; j < this.hexaSizeI; j++) {
        for (int a = 0; a < this.unitCellSize; a++) {
          lattice[(((hexaSizeK - 1) * hexaSizeJ + i) * hexaSizeI + j) * unitCellSize + a].setOnList(null);
          lattice[(((hexaSizeK - 1) * hexaSizeJ + i) * hexaSizeI + j) * unitCellSize + a].unRemove();
        }
      }
    }

    for (int k = 0; k < (hexaSizeK - 1) * hexaSizeJ * hexaSizeI * unitCellSize; k++) {
      lattice[k].setOnList(null);
      lattice[k].unRemove();
      lattice[k].setAsBulk();
    }

    // Update neighbourhood of top atoms
    for (int k = (hexaSizeK - 1) * hexaSizeJ * hexaSizeI * unitCellSize; k < hexaSizeK * hexaSizeJ * hexaSizeI * unitCellSize; k++) {
      lattice[k].updateN1FromScratch();
    }

    for (int k = (hexaSizeK - 1) * hexaSizeJ * hexaSizeI * unitCellSize; k < hexaSizeK * hexaSizeJ * hexaSizeI * unitCellSize; k++) {
      lattice[k].updateN2FromScratch();
    }

    // Remove top layer atoms
    for (int i = 0; i < this.hexaSizeJ; i++) {
      for (int j = 0; j < this.hexaSizeI; j++) {
        for (int a = 0; a < this.unitCellSize; a++) {
          lattice[(((this.hexaSizeK - 1) * this.hexaSizeJ + i) * this.hexaSizeI + j) * this.unitCellSize + a].remove();
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
    for (int a = 0; a < this.hexaSizeK; a++) {
      for (int b = 0; b < this.hexaSizeJ; b++) {
        for (int c = 0; c < this.hexaSizeI; c++) {
          for (int j = 0; j < unitCellSize; j++) {
            float x = coords[j * 3] + c * (float) UC.getLimitX();
            float y = coords[j * 3 + 1] + b * (float) UC.getLimitY();
            float z = -coords[j * 3 + 2] + (a + 1) * (float) UC.getLimitZ();

            lattice[cont] = new SiAtom(x, y, z);
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
    for (int z = 0; z < this.hexaSizeK; z++) {
      for (int y = 0; y < this.hexaSizeJ; y++) {
        for (int x = 0; x < this.hexaSizeI; x++) {
          for (int j = 0; j < unitCellSize; j++) {
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
                yNeighbour = hexaSizeJ - 1;
              }
              if (yNeighbour > hexaSizeJ - 1) {
                yNeighbour = 0;
              }

              if (((block[j * 4 + i] >> 2) & 3) == 1) {
                xNeighbour++;
              }
              if (((block[j * 4 + i] >> 2) & 3) == 3) {
                xNeighbour--;
              }
              if (xNeighbour < 0) {
                xNeighbour = hexaSizeI - 1;
              }
              if (xNeighbour > hexaSizeI - 1) {
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
              if (zNeighbour < hexaSizeK) {

                lattice[((z * hexaSizeJ + y) * hexaSizeI + x) * unitCellSize + j].setNeighbour(lattice[((zNeighbour * hexaSizeJ + yNeighbour) * hexaSizeI + xNeighbour) * unitCellSize + posNeighbour], i);

              } else {
                lattice[((z * hexaSizeJ + y) * hexaSizeI + x) * unitCellSize + j].setNeighbour(null, i);
              }
            }
          }
        }
      }
    }
  }
}
