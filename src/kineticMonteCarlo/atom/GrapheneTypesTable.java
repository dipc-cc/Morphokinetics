package kineticMonteCarlo.atom;

public class GrapheneTypesTable {
//oooooooooooooooooooooooooooo
/*  

   [1º neighbors amount][2º neighbors amount][ 3º neighbors amount]=atom type;
  
   //Types: 0 terrace, 1 corner, 2 zigzag edge,3 armchair edge, 4 zigzag__with_extra, 5 sick, 6 kinks, 7 bulk
   //[1º neighbors amount][2º neighbors amount][ 3º neighbors amount]: local atom configuration
   */
//oooooooooooooooooooooooooooo

  private final byte[][][] table;

  public byte getType(int neigh1st, int neigh2nd, int neigh3rd) {

    return table[neigh1st][neigh2nd][neigh3rd];
  }

  public GrapheneTypesTable() {

    table = new byte[4][7][4];

    table[0][0][0] = AbstractAtom.TERRACE;
    table[0][0][1] = AbstractAtom.TERRACE;
    table[0][0][2] = AbstractAtom.TERRACE;
    table[0][0][3] = AbstractAtom.TERRACE;

    table[0][1][0] = AbstractAtom.TERRACE;
    table[0][1][1] = AbstractAtom.TERRACE;
    table[0][1][2] = AbstractAtom.TERRACE;
    table[0][1][3] = AbstractAtom.TERRACE;

    table[0][2][0] = AbstractAtom.TERRACE;
    table[0][2][1] = AbstractAtom.TERRACE;
    table[0][2][2] = AbstractAtom.TERRACE;
    table[0][2][3] = AbstractAtom.TERRACE;

    table[0][3][0] = AbstractAtom.TERRACE;
    table[0][3][1] = AbstractAtom.TERRACE;
    table[0][3][2] = AbstractAtom.TERRACE;
    table[0][3][3] = AbstractAtom.TERRACE;

    table[0][4][0] = AbstractAtom.TERRACE;
    table[0][4][1] = AbstractAtom.TERRACE;
    table[0][4][2] = AbstractAtom.TERRACE;
    table[0][4][3] = AbstractAtom.TERRACE;

    table[0][5][0] = AbstractAtom.TERRACE;
    table[0][5][1] = AbstractAtom.TERRACE;
    table[0][5][2] = AbstractAtom.TERRACE;
    table[0][5][3] = AbstractAtom.TERRACE;

    table[0][6][0] = AbstractAtom.TERRACE;
    table[0][6][1] = AbstractAtom.TERRACE;
    table[0][6][2] = AbstractAtom.TERRACE;
    table[0][6][3] = AbstractAtom.TERRACE;

    table[1][0][0] = AbstractAtom.CORNER;
    table[1][0][1] = AbstractAtom.CORNER;
    table[1][0][2] = AbstractAtom.CORNER;
    table[1][0][3] = AbstractAtom.CORNER;

    table[1][1][0] = AbstractAtom.CORNER;
    table[1][1][1] = AbstractAtom.CORNER;
    table[1][1][2] = AbstractAtom.CORNER;
    table[1][1][3] = AbstractAtom.CORNER;

    table[1][2][0] = AbstractAtom.CORNER;
    table[1][2][1] = AbstractAtom.CORNER;
    table[1][2][2] = AbstractAtom.ZIGZAG_EDGE;  // zigzag adatom
    table[1][2][3] = AbstractAtom.SICK;

    table[1][3][0] = AbstractAtom.SICK;
    table[1][3][1] = AbstractAtom.ARMCHAIR_EDGE;  // armchair adatom
    table[1][3][2] = AbstractAtom.ZIGZAG_WITH_EXTRA;
    table[1][3][3] = AbstractAtom.SICK;

    table[1][4][0] = AbstractAtom.SICK;
    table[1][4][1] = AbstractAtom.SICK;
    table[1][4][2] = AbstractAtom.SICK;
    table[1][4][3] = AbstractAtom.SICK;

    table[1][5][0] = AbstractAtom.SICK;
    table[1][5][1] = AbstractAtom.SICK;
    table[1][5][2] = AbstractAtom.SICK;
    table[1][5][3] = AbstractAtom.SICK;

    table[1][6][0] = AbstractAtom.SICK;
    table[1][6][1] = AbstractAtom.SICK;
    table[1][6][2] = AbstractAtom.SICK;
    table[1][6][3] = AbstractAtom.SICK;

    table[2][0][0] = AbstractAtom.KINK;
    table[2][0][1] = AbstractAtom.KINK;
    table[2][0][2] = AbstractAtom.KINK;
    table[2][0][3] = AbstractAtom.KINK;

    table[2][1][0] = AbstractAtom.KINK;
    table[2][1][1] = AbstractAtom.KINK;
    table[2][1][2] = AbstractAtom.KINK;
    table[2][1][3] = AbstractAtom.KINK;

    table[2][2][0] = AbstractAtom.KINK;
    table[2][2][1] = AbstractAtom.KINK;
    table[2][2][2] = AbstractAtom.KINK;
    table[2][2][3] = AbstractAtom.KINK;

    table[2][3][0] = AbstractAtom.KINK;
    table[2][3][1] = AbstractAtom.KINK;
    table[2][3][2] = AbstractAtom.KINK;
    table[2][3][3] = AbstractAtom.KINK;

    table[2][4][0] = AbstractAtom.KINK;
    table[2][4][1] = AbstractAtom.KINK;
    table[2][4][2] = AbstractAtom.KINK;
    table[2][4][3] = AbstractAtom.KINK;

    table[2][5][0] = AbstractAtom.KINK;
    table[2][5][1] = AbstractAtom.KINK;
    table[2][5][2] = AbstractAtom.KINK;
    table[2][5][3] = AbstractAtom.KINK;

    table[2][6][0] = AbstractAtom.KINK;
    table[2][6][1] = AbstractAtom.KINK;
    table[2][6][2] = AbstractAtom.KINK;
    table[2][6][3] = AbstractAtom.KINK;

    table[3][0][0] = AbstractAtom.BULK;
    table[3][0][1] = AbstractAtom.BULK;
    table[3][0][2] = AbstractAtom.BULK;
    table[3][0][3] = AbstractAtom.BULK;

    table[3][1][0] = AbstractAtom.BULK;
    table[3][1][1] = AbstractAtom.BULK;
    table[3][1][2] = AbstractAtom.BULK;
    table[3][1][3] = AbstractAtom.BULK;

    table[3][2][0] = AbstractAtom.BULK;
    table[3][2][1] = AbstractAtom.BULK;
    table[3][2][2] = AbstractAtom.BULK;
    table[3][2][3] = AbstractAtom.BULK;

    table[3][3][0] = AbstractAtom.BULK;
    table[3][3][1] = AbstractAtom.BULK;
    table[3][3][2] = AbstractAtom.BULK;
    table[3][3][3] = AbstractAtom.BULK;

    table[3][4][0] = AbstractAtom.BULK;
    table[3][4][1] = AbstractAtom.BULK;
    table[3][4][2] = AbstractAtom.BULK;
    table[3][4][3] = AbstractAtom.BULK;

    table[3][5][0] = AbstractAtom.BULK;
    table[3][5][1] = AbstractAtom.BULK;
    table[3][5][2] = AbstractAtom.BULK;
    table[3][5][3] = AbstractAtom.BULK;

    table[3][6][0] = AbstractAtom.BULK;
    table[3][6][1] = AbstractAtom.BULK;
    table[3][6][2] = AbstractAtom.BULK;
    table[3][6][3] = AbstractAtom.BULK;
  }

}
