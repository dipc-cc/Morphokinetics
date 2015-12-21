package kineticMonteCarlo.atom;

import static kineticMonteCarlo.atom.AgAtom.CORNER;
import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.ISLAND;
import static kineticMonteCarlo.atom.AgAtom.KINK;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;

public class AgTypesTable {

  private final byte[][] table;

  /**
   * Returns the type of the atom between TERRACE, CORNER, EDGE, KINK and ISLAND.
   *
   * @param immobile Number of immobile neighbours
   * @param mobile Number of mobile neighbours
   * @return type of the atom (byte)
   */
  public byte getType(int immobile, int mobile) {
    return table[immobile][mobile];
  }

  public AgTypesTable() {

    table = new byte[7][7];

    // [Number of immobile neighbours][Number of mobile neighbours] = atom type
    //we don't differenciate here between A edge and B edge, this table is just for obtaining the atom type without orientation differences.
    table[0][0] = TERRACE;
    table[0][1] = TERRACE; //nucleación!!, intocable, algoritmos óptimos de difustion pensados para que no cambie el tipo de los vecinos al difundirse
    table[0][2] = TERRACE;
    table[0][3] = TERRACE; //casos enfermizos
    table[0][4] = ISLAND;
    table[0][5] = ISLAND;
    table[0][6] = ISLAND;

    table[1][0] = CORNER;
    table[1][1] = CORNER;
    table[1][2] = KINK;  //es un corner con dos edges, realmente es considerado como un kink
    table[1][3] = ISLAND;
    table[1][4] = ISLAND;
    table[1][5] = ISLAND;
    table[1][6] = ISLAND;

    table[2][0] = EDGE;
    table[2][1] = KINK;
    table[2][2] = ISLAND;
    table[2][3] = ISLAND;
    table[2][4] = ISLAND;
    table[2][5] = ISLAND;
    table[2][6] = ISLAND;

    table[3][0] = KINK;
    table[3][1] = KINK;
    table[3][2] = KINK;
    table[3][3] = ISLAND;
    table[3][4] = ISLAND;
    table[3][5] = ISLAND;
    table[3][6] = ISLAND;

    table[4][0] = ISLAND;
    table[4][1] = ISLAND;
    table[4][2] = ISLAND;
    table[4][3] = ISLAND;
    table[4][4] = ISLAND;
    table[4][5] = ISLAND;
    table[4][6] = ISLAND;

    table[5][0] = ISLAND;
    table[5][1] = ISLAND;
    table[5][2] = ISLAND;
    table[5][3] = ISLAND;
    table[5][4] = ISLAND;
    table[5][5] = ISLAND;
    table[5][6] = ISLAND;

    table[6][0] = ISLAND;
    table[6][1] = ISLAND;
    table[6][2] = ISLAND;
    table[6][3] = ISLAND;
    table[6][4] = ISLAND;
    table[6][5] = ISLAND;
    table[6][6] = ISLAND;
  }
}
