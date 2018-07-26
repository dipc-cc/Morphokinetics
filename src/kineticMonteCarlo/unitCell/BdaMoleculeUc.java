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
  private int occupiedNeighbours;
  private final int id;
  
  public BdaMoleculeUc(int id, Byte type) {
    super(-1, -1, null);
    this.id = id;
    bdaMolecule = new BdaMoleculeSite(id, false, type);
   
    numberOfNeighbours = 12;
    neighbours = new BdaMoleculeUc[numberOfNeighbours];
    energy = 0;
    occupiedNeighbours = 0;
  }
   
  public void setNeighbour(BdaMoleculeUc uc, int neighbourCode) {
    neighbours[neighbourCode] = uc;
    if (uc != null) {
      occupiedNeighbours++;
      if (bdaMolecule.getType() == ALPHA && uc.getSite(0).getType() == ALPHA) {
        if (uc.isRotated()) {
          neighbourCode = (neighbourCode + 3) % 12;
        }
        switch (neighbourCode) {
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
      if (bdaMolecule.getType() == BETA && uc.getSite(0).getType() == BETA) {
        if (neighbourCode == 1) {
          energy -= 0.2;
        }
      }
    }
  }
  
  public void resetNeighbourhood() {
    for (int i = 0; i < neighbours.length; i++) {
      neighbours[i] = null;
    }
    energy = 0.0; // reset the energy too
    occupiedNeighbours = 0;
  }

  public BdaMoleculeUc getNeighbour(int pos) {
    return neighbours[pos];
  }
  
  /**
   * All possible neighbours.
   * 
   * @return 
   */
  public int getNumberOfNeighbours() {
    return numberOfNeighbours;
  }
  
  /**
   * Number of occupied neighbours.
   * 
   * @return 
   */
  public int getOccupiedNeighbours() {
    return occupiedNeighbours;
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
    Point3D pos;
    
    if (isShifted()) {
      pos = new Point3D(0, 0, 0);
    } else {
      if (isRotated()) {
        pos = new Point3D(0.5, 0, 0);
      } else {
        pos = new Point3D(0, 0.5, 0);
      }
    }
    return pos;
  }
  
  public boolean isRotated() {
    return bdaMolecule.isRotated();
  }

  public void setRotated(boolean rotated) {
    bdaMolecule.setRotated(rotated);
  }
  
  public boolean isShifted() {
    return bdaMolecule.isShifted();
  }

  public void setShifted(boolean shifted) {
    bdaMolecule.setShifted(shifted);
  }
  
  @Override
  public String toString() {
    String returnString = "Molecule Id " + id;
    return returnString;
  }
}
