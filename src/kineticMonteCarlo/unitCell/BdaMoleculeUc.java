/*
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
package kineticMonteCarlo.unitCell;

import javafx.geometry.Point3D;
import kineticMonteCarlo.site.AbstractGrowthSite;
import kineticMonteCarlo.site.BdaMoleculeSite;
import static kineticMonteCarlo.site.BdaMoleculeSite.ALPHA;
import static kineticMonteCarlo.site.BdaMoleculeSite.BETA;

/**
 * BDA molecules on top of Ag (BdaSurfaceUc). There are 12 possible neighbours. It can have 8 neighbours at the
 * same time: north-west, north-east, east-north, east-south, south-east,
 * south-west, east-south, east-north. North, east, south and west also exists.
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaMoleculeUc extends AbstractGrowthUc implements IUc {
  
  
  /** Central position is in this AgUc. */
  //private BdaSurfaceUc agUc;
  private final BdaMoleculeUc[] neighbours;
  private final BdaMoleculeSite bdaMolecule;
  /** Base energy of the molecule. It will be updated with the neighbourhood.*/
  private double energy;
  private final int numberOfNeighbours;
  private final int id;
  
  public BdaMoleculeUc(int id ) {
    super(-1, -1, null);
    this.id = id;
    bdaMolecule = new BdaMoleculeSite(id, false);
   
    numberOfNeighbours = 12;
    neighbours = new BdaMoleculeUc[numberOfNeighbours];
    energy = 0;
  }
   
  public void setNeighbour(BdaMoleculeUc uc, int pos) {
    neighbours[pos] = uc;
    if (uc != null) {
      if (bdaMolecule.getType() == ALPHA) {
        if (uc.isRotated()) {
          pos = (pos + 3) % 12;
        }
        switch (pos) {
          case 0:
          case 2:
          case 6:
          case 8:
            energy -= 0.05;
            break;
          case 1:
          case 7:
            energy += 0.2;
            break;
          case 3:
          case 5:
          case 9:
          case 11:
            energy += 0.1;
            break;
          case 4:
          case 10:
            energy -= 0.1;
            break;
        }
      }
      if (bdaMolecule.getType() == BETA) {
        switch (pos) {
          case 0:
          case 2:
          case 6:
          case 8:
            energy -= 0.00;
            break;
          case 1:
          case 7:
            energy -= 0.2;
            break;
          case 3:
          case 9:
            energy -= 0.2;
            break;
          case 5:
          case 11:
            energy += 0.0;
            break;
          case 4:
          case 10:
            energy -= 0.0;
            break;
        }
      }
    }
  }
  
  public void resetNeighbourhood() {
    for (int i = 0; i < neighbours.length; i++) {
      neighbours[i] = null;
    }
    energy = 0.0; // reset the energy too
  }

  public BdaMoleculeUc getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  public int getNumberOfNeighbours() {
    return numberOfNeighbours;
  }
  /**
   * Base energy of the molecule.
   * 
   * @return energy in eV.
   */
  public double getEnergy() {
    return energy;
  }

  /**
   * Always returns the current atom.
   *
   * @param pos ignored.
   * @return current atom.
   */
  @Override
  public AbstractGrowthSite getSite(int pos) {
    return bdaMolecule;
  }
  
  /**
   * Number of elements.
   *
   * @return quantity of unit cells.
   */
  @Override
  public int size() {
    return 1;
  }
  
  @Override
  public Point3D getPos() {
    Point3D pos = null;
    switch (bdaMolecule.getType()) {
      case ALPHA:
        // bridge
        if (bdaMolecule.isRotated()) {
          pos = new Point3D(0.5, 0, 0);
        } else {
          pos = new Point3D(0, 0.5, 0);
        }
        break;
      case BETA:
        if (bdaMolecule.isRotated()) { // top
          pos = new Point3D(0, 0, 0);
        } else { // bridge
          pos = new Point3D(0, 0.5, 0);
        }
        break;
    }

    return pos;
  }
  
  public boolean isRotated() {
    return bdaMolecule.isRotated();
  }

  public void setRotated(boolean rotated) {
    bdaMolecule.setRotated(rotated);
  }
  
  @Override
  public String toString() {
    String returnString = "Molecule Id " + id;
    return returnString;
  }
}
