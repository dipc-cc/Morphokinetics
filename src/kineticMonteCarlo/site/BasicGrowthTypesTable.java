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
package kineticMonteCarlo.site;

import static kineticMonteCarlo.site.BasicGrowthSite.EDGE;
import static kineticMonteCarlo.site.BasicGrowthSite.ISLAND;
import static kineticMonteCarlo.site.BasicGrowthSite.KINK;
import static kineticMonteCarlo.site.BasicGrowthSite.TERRACE;

public class BasicGrowthTypesTable {

  private final byte[] tablePresent;
  private final byte[] tableFuture;

  /**
   * All predefined type of atom (current if the atom is occupied and future if the atom is not
   * occupied).
   */
  public BasicGrowthTypesTable() {

    tablePresent = new byte[5];
   
    tablePresent[0] = TERRACE;
    tablePresent[1] = EDGE;
    tablePresent[2] = KINK;
    tablePresent[3] = ISLAND; 
    tablePresent[4] = ISLAND; 
    
    tableFuture = tablePresent;

  }
  
  /**
   * Returns the type of the atom between TERRACE, EDGE, KINK and ISLAND. 
   *
   * @param numberOfNeighbourAtoms number of occupied neighbours
   * @return type of the atom (byte)
   */
  public byte getCurrentType(int numberOfNeighbourAtoms) {
    if (numberOfNeighbourAtoms > 4) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is >4, which is in practice impossible");
    }
    return tablePresent[numberOfNeighbourAtoms];
  }
  
  public byte getFutureType(int numberOfNeighbourAtoms) {
    byte type;
    try {
      type = tableFuture[numberOfNeighbourAtoms];
    } catch (ArrayIndexOutOfBoundsException exception) {
      System.err.println("Catched error getting future type of Ag atom " + exception);
      System.err.println("Trying to access " + numberOfNeighbourAtoms);
      type = TERRACE;
    }
    return type;
  }
}
