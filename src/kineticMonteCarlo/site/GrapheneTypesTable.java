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
package kineticMonteCarlo.site;

public class GrapheneTypesTable {
  /*  
   [1st neighbors amount][2nd neighbors amount][3rd neighbors amount] = atom type
   */
  
  private final byte[][][] table;

  public GrapheneTypesTable() {
    table = new byte[4][7][4];

    table[0][0][0] = AbstractSite.TERRACE;
    table[0][0][1] = AbstractSite.TERRACE;
    table[0][0][2] = AbstractSite.TERRACE;
    table[0][0][3] = AbstractSite.TERRACE;

    table[0][1][0] = AbstractSite.TERRACE;
    table[0][1][1] = AbstractSite.TERRACE;
    table[0][1][2] = AbstractSite.TERRACE;
    table[0][1][3] = AbstractSite.TERRACE;

    table[0][2][0] = AbstractSite.TERRACE;
    table[0][2][1] = AbstractSite.TERRACE;
    table[0][2][2] = AbstractSite.TERRACE;
    table[0][2][3] = AbstractSite.TERRACE;

    table[0][3][0] = AbstractSite.TERRACE;
    table[0][3][1] = AbstractSite.TERRACE;
    table[0][3][2] = AbstractSite.TERRACE;
    table[0][3][3] = AbstractSite.TERRACE;

    table[0][4][0] = AbstractSite.TERRACE;
    table[0][4][1] = AbstractSite.TERRACE;
    table[0][4][2] = AbstractSite.TERRACE;
    table[0][4][3] = AbstractSite.TERRACE;

    table[0][5][0] = AbstractSite.TERRACE;
    table[0][5][1] = AbstractSite.TERRACE;
    table[0][5][2] = AbstractSite.TERRACE;
    table[0][5][3] = AbstractSite.TERRACE;

    table[0][6][0] = AbstractSite.TERRACE;
    table[0][6][1] = AbstractSite.TERRACE;
    table[0][6][2] = AbstractSite.TERRACE;
    table[0][6][3] = AbstractSite.TERRACE;

    table[1][0][0] = AbstractSite.CORNER;
    table[1][0][1] = AbstractSite.CORNER;
    table[1][0][2] = AbstractSite.CORNER;
    table[1][0][3] = AbstractSite.CORNER;

    table[1][1][0] = AbstractSite.CORNER;
    table[1][1][1] = AbstractSite.CORNER;
    table[1][1][2] = AbstractSite.CORNER;
    table[1][1][3] = AbstractSite.CORNER;

    table[1][2][0] = AbstractSite.CORNER;
    table[1][2][1] = AbstractSite.CORNER;
    table[1][2][2] = AbstractSite.ZIGZAG_EDGE;  // zigzag adatom
    table[1][2][3] = AbstractSite.SICK;

    table[1][3][0] = AbstractSite.SICK;
    table[1][3][1] = AbstractSite.ARMCHAIR_EDGE;  // armchair adatom
    table[1][3][2] = AbstractSite.ZIGZAG_WITH_EXTRA;
    table[1][3][3] = AbstractSite.SICK;

    table[1][4][0] = AbstractSite.SICK;
    table[1][4][1] = AbstractSite.SICK;
    table[1][4][2] = AbstractSite.SICK;
    table[1][4][3] = AbstractSite.SICK;

    table[1][5][0] = AbstractSite.SICK;
    table[1][5][1] = AbstractSite.SICK;
    table[1][5][2] = AbstractSite.SICK;
    table[1][5][3] = AbstractSite.SICK;

    table[1][6][0] = AbstractSite.SICK;
    table[1][6][1] = AbstractSite.SICK;
    table[1][6][2] = AbstractSite.SICK;
    table[1][6][3] = AbstractSite.SICK;

    table[2][0][0] = AbstractSite.KINK;
    table[2][0][1] = AbstractSite.KINK;
    table[2][0][2] = AbstractSite.KINK;
    table[2][0][3] = AbstractSite.KINK;

    table[2][1][0] = AbstractSite.KINK;
    table[2][1][1] = AbstractSite.KINK;
    table[2][1][2] = AbstractSite.KINK;
    table[2][1][3] = AbstractSite.KINK;

    table[2][2][0] = AbstractSite.KINK;
    table[2][2][1] = AbstractSite.KINK;
    table[2][2][2] = AbstractSite.KINK;
    table[2][2][3] = AbstractSite.KINK;

    table[2][3][0] = AbstractSite.KINK;
    table[2][3][1] = AbstractSite.KINK;
    table[2][3][2] = AbstractSite.KINK;
    table[2][3][3] = AbstractSite.KINK;

    table[2][4][0] = AbstractSite.KINK;
    table[2][4][1] = AbstractSite.KINK;
    table[2][4][2] = AbstractSite.KINK;
    table[2][4][3] = AbstractSite.KINK;

    table[2][5][0] = AbstractSite.KINK;
    table[2][5][1] = AbstractSite.KINK;
    table[2][5][2] = AbstractSite.KINK;
    table[2][5][3] = AbstractSite.KINK;

    table[2][6][0] = AbstractSite.KINK;
    table[2][6][1] = AbstractSite.KINK;
    table[2][6][2] = AbstractSite.KINK;
    table[2][6][3] = AbstractSite.KINK;

    table[3][0][0] = AbstractSite.BULK;
    table[3][0][1] = AbstractSite.BULK;
    table[3][0][2] = AbstractSite.BULK;
    table[3][0][3] = AbstractSite.BULK;

    table[3][1][0] = AbstractSite.BULK;
    table[3][1][1] = AbstractSite.BULK;
    table[3][1][2] = AbstractSite.BULK;
    table[3][1][3] = AbstractSite.BULK;

    table[3][2][0] = AbstractSite.BULK;
    table[3][2][1] = AbstractSite.BULK;
    table[3][2][2] = AbstractSite.BULK;
    table[3][2][3] = AbstractSite.BULK;

    table[3][3][0] = AbstractSite.BULK;
    table[3][3][1] = AbstractSite.BULK;
    table[3][3][2] = AbstractSite.BULK;
    table[3][3][3] = AbstractSite.BULK;

    table[3][4][0] = AbstractSite.BULK;
    table[3][4][1] = AbstractSite.BULK;
    table[3][4][2] = AbstractSite.BULK;
    table[3][4][3] = AbstractSite.BULK;

    table[3][5][0] = AbstractSite.BULK;
    table[3][5][1] = AbstractSite.BULK;
    table[3][5][2] = AbstractSite.BULK;
    table[3][5][3] = AbstractSite.BULK;

    table[3][6][0] = AbstractSite.BULK;
    table[3][6][1] = AbstractSite.BULK;
    table[3][6][2] = AbstractSite.BULK;
    table[3][6][3] = AbstractSite.BULK;
  }
  
  public byte getType(int neigh1st, int neigh2nd, int neigh3rd) {
    return table[neigh1st][neigh2nd][neigh3rd];
  }
}
