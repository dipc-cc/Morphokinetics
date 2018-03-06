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

import kineticMonteCarlo.atom.SiSite;
import kineticMonteCarlo.unitCell.SiUnitCell;
import kineticMonteCarlo.unitCell.Simple3dUc;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class SiLattice extends AbstractLattice {

  private SiUnitCell unitCell;
  private SiSite[] atoms;
  private Simple3dUc[] ucList;

  public SiLattice(int millerX, int millerY, int millerZ, int sizeX, int sizeY, int sizeZ) {
    if (millerX == 0 && millerY == 0 && millerZ == 0) {
      System.exit(-1);
    }

    setHexaSizeI(sizeX);
    setHexaSizeJ(sizeY);
    setHexaSizeK(sizeZ);

    unitCell = new SiUnitCell();
    setUnitCellSize(unitCell.createUnitCell(millerX, millerY, millerZ));
    if (getUnitCellSize() < 4 || getUnitCellSize() > 127) {
      System.out.println("UC size inappropiate: " + getUnitCellSize());
      System.exit(-1);
    }
    short[] ucNeighbours = unitCell.getNeighs();
    byte[] block = unitCell.getNBlock();

    float[] coords = new float[getUnitCellSize() * 3 + 3];
    for (int i = 0; i < getUnitCellSize(); i++) {
      coords[i * 3] = (float) unitCell.getCellsP()[i].getX();
      coords[i * 3 + 1] = (float) unitCell.getCellsP()[i].getY();
      coords[i * 3 + 2] = (float) unitCell.getCellsP()[i].getZ();
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

    atoms = new SiSite[getHexaSizeI() * getHexaSizeJ() * getHexaSizeK() * getUnitCellSize()];
    ucList = new Simple3dUc[getHexaSizeI() * getHexaSizeJ() * getHexaSizeK() * getUnitCellSize()];

    createAtoms(coords, unitCell);
    interconnectAtoms(ucNeighbours, block);
  }

  @Override
  public void setProbabilities(double[] probabilities) {
    for (int i = 0; i < atoms.length; i++) {
      atoms[i].setProbabilities(probabilities);
    }
  }

  public SiUnitCell getUnitCell() {
    return unitCell;
  }

  @Override
  public SiSite getSite(int unitCellX, int unitCellY, int unitCellZ, int unitCellPos) {
    return atoms[((unitCellZ * getHexaSizeJ() + unitCellY) * getHexaSizeI() + unitCellX) * getUnitCellSize() + unitCellPos];
  }

  @Override
  public Simple3dUc getUc(int pos) {
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
    //Unremove atoms and set to bulk mode (4,12)
    for (int i = 0; i < getHexaSizeJ(); i++) {
      for (int j = 0; j < getHexaSizeI(); j++) {
        for (int a = 0; a < getUnitCellSize(); a++) {
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
        for (int a = 0; a < getUnitCellSize(); a++) {
          atoms[(((getHexaSizeK() - 1) * getHexaSizeJ() + i) * getHexaSizeI() + j) * getUnitCellSize() + a].remove();
        }
      }
    }
  }

  /**
   * Atoms creation.
   * 
   * @param coords
   * @param uc 
   */
  private void createAtoms(float[] coords, SiUnitCell uc) {
    int cont = 0;
    for (int a = 0; a < getHexaSizeK(); a++) {
      for (int b = 0; b < getHexaSizeJ(); b++) {
        for (int c = 0; c < getHexaSizeI(); c++) {
          for (int j = 0; j < getUnitCellSize(); j++) {
            float x = coords[j * 3] + c * (float) uc.getLimitX();
            float y = coords[j * 3 + 1] + b * (float) uc.getLimitY();
            float z = -coords[j * 3 + 2] + (a + 1) * (float) uc.getLimitZ();

            atoms[cont] = new SiSite(x, y, z);
            ucList[cont] = new Simple3dUc(a, b, c, atoms[cont]);
            ucList[cont].setPosX(x);
            ucList[cont].setPosY(y);
            ucList[cont].setPosZ(z);
            cont++;
          }
        }
      }
    }
  }

  /**
   * Atoms inter-connection.
   * 
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
                        setNeighbour((SiSite)atoms[((zNeighbour * getHexaSizeJ() + yNeighbour) * getHexaSizeI() + xNeighbour) * getUnitCellSize() + posNeighbour], i);
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
