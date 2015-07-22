package kineticMonteCarlo.atom;

public class AgAgTypesTable {

  private final byte[][] table;

  public byte getType(int immobile, int mobile) {
    return table[immobile][mobile];
  }

  public AgAgTypesTable() {

    table = new byte[7][7];

        //[inmovil][movil]
    //0: terrace, 1: corner, 2: edge, 3: kink, 4: bulk
    //we don't differenciate here between A edge and B edge, this table is just for obtaining the atom type without orientation differences.
    table[0][0] = 0;
    table[0][1] = 0; //nucleación!!, intocable, algoritmos óptimos de difustion pensados para que no cambie el tipo de los vecinos al difundirse
    table[0][2] = 0;
    table[0][3] = 0; //casos enfermizos
    table[0][4] = 4;
    table[0][5] = 4;
    table[0][6] = 4;

    table[1][0] = 1;
    table[1][1] = 1;
    table[1][2] = 3;  //es un corner con dos edges, realmente es considerado como un kink
    table[1][3] = 4;
    table[1][4] = 4;
    table[1][5] = 4;
    table[1][6] = 4;

    table[2][0] = 2;
    table[2][1] = 3;
    table[2][2] = 4;
    table[2][3] = 4;
    table[2][4] = 4;
    table[2][5] = 4;
    table[2][6] = 4;

    table[3][0] = 3;
    table[3][1] = 3;
    table[3][2] = 3;
    table[3][3] = 4;
    table[3][4] = 4;
    table[3][5] = 4;
    table[3][6] = 4;

    table[4][0] = 4;
    table[4][1] = 4;
    table[4][2] = 4;
    table[4][3] = 4;
    table[4][4] = 4;
    table[4][5] = 4;
    table[4][6] = 4;

    table[5][0] = 4;
    table[5][1] = 4;
    table[5][2] = 4;
    table[5][3] = 4;
    table[5][4] = 4;
    table[5][5] = 4;
    table[5][6] = 4;

    table[6][0] = 4;
    table[6][1] = 4;
    table[6][2] = 4;
    table[6][3] = 4;
    table[6][4] = 4;
    table[6][5] = 4;
    table[6][6] = 4;
  }
}
