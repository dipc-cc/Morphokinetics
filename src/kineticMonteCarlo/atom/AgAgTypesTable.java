package kineticMonteCarlo.atom;

public class AgAgTypesTable {

  private final byte[][] table;

  public byte getType(int immobile, int mobile) {
    return table[immobile][mobile];
  }

  public AgAgTypesTable() {

    table = new byte[7][7];

    // [Number of immobile neighbours][Number of mobile neighbours] = atom type
    //we don't differenciate here between A edge and B edge, this table is just for obtaining the atom type without orientation differences.
    table[0][0] = AbstractAtom.TERRACE;
    table[0][1] = AbstractAtom.TERRACE; //nucleación!!, intocable, algoritmos óptimos de difustion pensados para que no cambie el tipo de los vecinos al difundirse
    table[0][2] = AbstractAtom.TERRACE;
    table[0][3] = AbstractAtom.TERRACE; //casos enfermizos
    table[0][4] = AbstractAtom.BULK;
    table[0][5] = AbstractAtom.BULK;
    table[0][6] = AbstractAtom.BULK;

    table[1][0] = AbstractAtom.CORNER;
    table[1][1] = AbstractAtom.CORNER;
    table[1][2] = AbstractAtom.KINK;  //es un corner con dos edges, realmente es considerado como un kink
    table[1][3] = AbstractAtom.BULK;
    table[1][4] = AbstractAtom.BULK;
    table[1][5] = AbstractAtom.BULK;
    table[1][6] = AbstractAtom.BULK;

    table[2][0] = AbstractAtom.EDGE;
    table[2][1] = AbstractAtom.KINK;
    table[2][2] = AbstractAtom.BULK;
    table[2][3] = AbstractAtom.BULK;
    table[2][4] = AbstractAtom.BULK;
    table[2][5] = AbstractAtom.BULK;
    table[2][6] = AbstractAtom.BULK;

    table[3][0] = AbstractAtom.KINK;
    table[3][1] = AbstractAtom.KINK;
    table[3][2] = AbstractAtom.KINK;
    table[3][3] = AbstractAtom.BULK;
    table[3][4] = AbstractAtom.BULK;
    table[3][5] = AbstractAtom.BULK;
    table[3][6] = AbstractAtom.BULK;

    table[4][0] = AbstractAtom.BULK;
    table[4][1] = AbstractAtom.BULK;
    table[4][2] = AbstractAtom.BULK;
    table[4][3] = AbstractAtom.BULK;
    table[4][4] = AbstractAtom.BULK;
    table[4][5] = AbstractAtom.BULK;
    table[4][6] = AbstractAtom.BULK;

    table[5][0] = AbstractAtom.BULK;
    table[5][1] = AbstractAtom.BULK;
    table[5][2] = AbstractAtom.BULK;
    table[5][3] = AbstractAtom.BULK;
    table[5][4] = AbstractAtom.BULK;
    table[5][5] = AbstractAtom.BULK;
    table[5][6] = AbstractAtom.BULK;

    table[6][0] = AbstractAtom.BULK;
    table[6][1] = AbstractAtom.BULK;
    table[6][2] = AbstractAtom.BULK;
    table[6][3] = AbstractAtom.BULK;
    table[6][4] = AbstractAtom.BULK;
    table[6][5] = AbstractAtom.BULK;
    table[6][6] = AbstractAtom.BULK;
  }
}
