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
    unitCellSize = unitCell.create_Unit_Cell(millerX, millerY, millerZ);
    if (unitCellSize < 4 || unitCellSize > 127) {
      System.out.println("UC size inappropiate: " + unitCellSize);
      System.exit(-1);
    }
    short[] UC_neig = unitCell.getNeighs();
    byte[] block = unitCell.getN_block();

    float[] coords = new float[unitCellSize * 3 + 3];
    for (int i = 0; i < unitCellSize; i++) {
      coords[i * 3] = unitCell.getCellsP()[i].getPos_x(0, 0, 0);
      coords[i * 3 + 1] = unitCell.getCellsP()[i].getPos_y(0, 0, 0);
      coords[i * 3 + 2] = unitCell.getCellsP()[i].getPos_z(0, 0, 0);
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

    this.create_atoms(coords, unitCell);
    this.interconnect_atoms(UC_neig, block);
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < lattice.length; i++) {
      lattice[i].initialize(probabilities);
    }
  }

  public UnitCell getUnit_Cell() {

    return unitCell;
  }

  public SiAtom getAtom(int Unit_cell_X, int Unit_cell_Y, int Unit_cell_Z, int Unit_cell_pos) {

    return lattice[((Unit_cell_Z * hexaSizeJ + Unit_cell_Y) * hexaSizeI + Unit_cell_X) * unitCellSize + Unit_cell_pos];
  }

  @Override
  public void reset() {

        //Unremove atoms and set to bulk mode (4,12)
    //----------------------------------------------------------
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

        // Update neighborhood of top atoms
    //----------------------------------------------------------  
    for (int k = (hexaSizeK - 1) * hexaSizeJ * hexaSizeI * unitCellSize; k < hexaSizeK * hexaSizeJ * hexaSizeI * unitCellSize; k++) {
      lattice[k].updateN1FromScratch();
    }

    for (int k = (hexaSizeK - 1) * hexaSizeJ * hexaSizeI * unitCellSize; k < hexaSizeK * hexaSizeJ * hexaSizeI * unitCellSize; k++) {
      lattice[k].updateN2FromScratch();
    }

        // Remove top layer atoms
    //----------------------------------------------------------
    for (int i = 0; i < this.hexaSizeJ; i++) {
      for (int j = 0; j < this.hexaSizeI; j++) {
        for (int a = 0; a < this.unitCellSize; a++) {

          lattice[(((this.hexaSizeK - 1) * this.hexaSizeJ + i) * this.hexaSizeI + j) * this.unitCellSize + a].remove();
        }
      }
    }
  }

  private void create_atoms(float[] coords, UnitCell UC) {
    //atoms creation
    int cont = 0;
    for (int a = 0; a < this.hexaSizeK; a++) {
      for (int b = 0; b < this.hexaSizeJ; b++) {
        for (int c = 0; c < this.hexaSizeI; c++) {
          for (int j = 0; j < unitCellSize; j++) {

            float X = coords[j * 3] + c * (float) UC.getLimitX();
            float Y = coords[j * 3 + 1] + b * (float) UC.getLimitY();
            float Z = -coords[j * 3 + 2] + (a + 1) * (float) UC.getLimitZ();

            lattice[cont] = new SiAtom(X, Y, Z);
            cont++;

          }
        }
      }
    }
  }

  private void interconnect_atoms(short[] UC_neig, byte[] block) {
    //atoms inter-connection

    for (int Z = 0; Z < this.hexaSizeK; Z++) {
      for (int Y = 0; Y < this.hexaSizeJ; Y++) {
        for (int X = 0; X < this.hexaSizeI; X++) {
          for (int j = 0; j < unitCellSize; j++) {

            for (int i = 0; i < 4; i++) {

              int vecino_X = X;
              int vecino_Y = Y;
              int vecino_Z = Z;
              int pos_vecino = UC_neig[j * 4 + i];
              if ((block[j * 4 + i] & 3) == 1) {
                vecino_Y++;
              }
              if ((block[j * 4 + i] & 3) == 3) {
                vecino_Y--;
              }
              if (vecino_Y < 0) {
                vecino_Y = hexaSizeJ - 1;
              }
              if (vecino_Y > hexaSizeJ - 1) {
                vecino_Y = 0;
              }

              if (((block[j * 4 + i] >> 2) & 3) == 1) {
                vecino_X++;
              }
              if (((block[j * 4 + i] >> 2) & 3) == 3) {
                vecino_X--;
              }
              if (vecino_X < 0) {
                vecino_X = hexaSizeI - 1;
              }
              if (vecino_X > hexaSizeI - 1) {
                vecino_X = 0;
              }

              if (((block[j * 4 + i] >> 4) & 3) == 1) {
                vecino_Z++;
              }
              if (((block[j * 4 + i] >> 4) & 3) == 3) {
                vecino_Z--;
              }

              if (vecino_Z < 0) {
                vecino_X = 0;
                vecino_Y = 0;
                vecino_Z = 0;
                pos_vecino = j;
              }
              if (vecino_Z < hexaSizeK) {

                lattice[((Z * hexaSizeJ + Y) * hexaSizeI + X) * unitCellSize + j].setNeighbour(lattice[((vecino_Z * hexaSizeJ + vecino_Y) * hexaSizeI + vecino_X) * unitCellSize + pos_vecino], i);

              } else {
                lattice[((Z * hexaSizeJ + Y) * hexaSizeI + X) * unitCellSize + j].setNeighbour(null, i);
              }
            }
          }
        }
      }
    }
  }
}
