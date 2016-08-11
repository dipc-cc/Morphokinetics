package kineticMonteCarlo.atom;

import java.util.Arrays;
import static kineticMonteCarlo.atom.AgAtom.CORNER;
import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.ISLAND;
import static kineticMonteCarlo.atom.AgAtom.KINK;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;

public class AgTypesTable {

  private final byte[][] tablePresent;
  private final byte[][] tableFuture;
    
  /**
   * All predefined type of atom (current if the atom is occupied and future if the atom is not
   * occupied). A proper image of these types is in
   * https://bitbucket.org/Nesferjo/ekmc-project/wiki/Process%20types
   */
  public AgTypesTable() {

    tablePresent = new byte[7][7];

    // [Number of immobile neighbours][Number of mobile neighbours] = atom type
    //we don't differenciate here between A edge and B edge, this table is just for obtaining the atom type without orientation differences.
    tablePresent[0][0] = TERRACE;
    tablePresent[0][1] = TERRACE; //nucleación!!, intocable, algoritmos óptimos de difustion pensados para que no cambie el tipo de los vecinos al difundirse
    tablePresent[0][2] = TERRACE;
    tablePresent[0][3] = TERRACE; //casos enfermizos
    tablePresent[0][4] = ISLAND;
    tablePresent[0][5] = ISLAND;
    tablePresent[0][6] = ISLAND;

    tablePresent[1][0] = CORNER;
    tablePresent[1][1] = CORNER;
    tablePresent[1][2] = KINK;  //es un corner con dos edges, realmente es considerado como un kink
    tablePresent[1][3] = ISLAND;
    tablePresent[1][4] = ISLAND;
    tablePresent[1][5] = ISLAND;
    tablePresent[1][6] = ISLAND;

    tablePresent[2][0] = EDGE;
    tablePresent[2][1] = KINK;
    tablePresent[2][2] = ISLAND;
    tablePresent[2][3] = ISLAND;
    tablePresent[2][4] = ISLAND;
    tablePresent[2][5] = ISLAND;
    tablePresent[2][6] = ISLAND;

    tablePresent[3][0] = KINK;
    tablePresent[3][1] = KINK;
    tablePresent[3][2] = KINK;
    tablePresent[3][3] = ISLAND;
    tablePresent[3][4] = ISLAND;
    tablePresent[3][5] = ISLAND;
    tablePresent[3][6] = ISLAND;

    tablePresent[4][0] = ISLAND;
    tablePresent[4][1] = ISLAND;
    tablePresent[4][2] = ISLAND;
    tablePresent[4][3] = ISLAND;
    tablePresent[4][4] = ISLAND;
    tablePresent[4][5] = ISLAND;
    tablePresent[4][6] = ISLAND;

    tablePresent[5][0] = ISLAND;
    tablePresent[5][1] = ISLAND;
    tablePresent[5][2] = ISLAND;
    tablePresent[5][3] = ISLAND;
    tablePresent[5][4] = ISLAND;
    tablePresent[5][5] = ISLAND;
    tablePresent[5][6] = ISLAND;

    tablePresent[6][0] = ISLAND;
    tablePresent[6][1] = ISLAND;
    tablePresent[6][2] = ISLAND;
    tablePresent[6][3] = ISLAND;
    tablePresent[6][4] = ISLAND;
    tablePresent[6][5] = ISLAND;
    tablePresent[6][6] = ISLAND;

    tableFuture = new byte[7][7];

    // [Number of immobile neighbours][Number of mobile neighbours] = atom type
    //we don't differenciate here between A edge and B edge, this table is just for obtaining the atom type without orientation differences.
    tableFuture[0][0] = TERRACE;
    tableFuture[0][1] = TERRACE; 
    tableFuture[0][2] = CORNER;
    tableFuture[0][3] = EDGE;
    tableFuture[0][4] = KINK;
    tableFuture[0][5] = ISLAND;
    tableFuture[0][6] = ISLAND;

    tableFuture[1][0] = CORNER;
    tableFuture[1][1] = CORNER;
    tableFuture[1][2] = CORNER;
    tableFuture[1][3] = KINK;
    tableFuture[1][4] = ISLAND;
    tableFuture[1][5] = ISLAND;
    tableFuture[1][6] = ISLAND;

    tableFuture[2][0] = EDGE;
    tableFuture[2][1] = EDGE;
    tableFuture[2][2] = KINK;
    tableFuture[2][3] = ISLAND;
    tableFuture[2][4] = ISLAND;
    tableFuture[2][5] = ISLAND;
    tableFuture[2][6] = ISLAND;

    tableFuture[3][0] = KINK;
    tableFuture[3][1] = KINK;
    tableFuture[3][2] = KINK;
    tableFuture[3][3] = ISLAND;
    tableFuture[3][4] = ISLAND;
    tableFuture[3][5] = ISLAND;
    tableFuture[3][6] = ISLAND;

    tableFuture[4][0] = ISLAND;
    tableFuture[4][1] = ISLAND;
    tableFuture[4][2] = ISLAND;
    tableFuture[4][3] = ISLAND;
    tableFuture[4][4] = ISLAND;
    tableFuture[4][5] = ISLAND;
    tableFuture[4][6] = ISLAND;

    tableFuture[5][0] = ISLAND;
    tableFuture[5][1] = ISLAND;
    tableFuture[5][2] = ISLAND;
    tableFuture[5][3] = ISLAND;
    tableFuture[5][4] = ISLAND;
    tableFuture[5][5] = ISLAND;
    tableFuture[5][6] = ISLAND;

    tableFuture[6][0] = ISLAND;
    tableFuture[6][1] = ISLAND;
    tableFuture[6][2] = ISLAND;
    tableFuture[6][3] = ISLAND;
    tableFuture[6][4] = ISLAND;
    tableFuture[6][5] = ISLAND;
    tableFuture[6][6] = ISLAND;
  }
  /**
   * Returns the type of the atom between TERRACE, CORNER, EDGE, KINK and ISLAND. An image can be
   * found in https://bitbucket.org/Nesferjo/ekmc-project/wiki/Process%20types
   *
   * @param immobile Number of immobile neighbours
   * @param mobile Number of mobile neighbours
   * @return type of the atom (byte)
   */
  public byte getCurrentType(int immobile, int mobile) {
    byte type;

    if (immobile + mobile > 6) {
      throw new ArrayIndexOutOfBoundsException("The sum of mobile and immobile neighbours is >6, which is in practice impossible");
    }
    try {
      type = tablePresent[immobile][mobile];
    } catch (ArrayIndexOutOfBoundsException exception) {
      System.err.println("Catched error getting type of Ag atom " + exception);
      System.err.println("Trying to access " + immobile + " " + mobile);
      if (mobile < 0) {
        type = tablePresent[immobile][0];
      } else {
        type = TERRACE;
      }
    }
    return type;
  }
  
  public byte getFutureType(int immobile, int mobile) {
    byte type;
    if (mobile == 0) {
      //throw new ArrayIndexOutOfBoundsException("No mobile neighbours (mobile == 0), which is in practice impossible");
    }
    if (immobile + mobile > 6) {
      throw new ArrayIndexOutOfBoundsException("The sum of mobile and immobile neighbours is >6, which is in practice impossible");
    }
    try {
      type = tableFuture[immobile][mobile];
    } catch (ArrayIndexOutOfBoundsException exception) {
      System.err.println("Catched error getting future type of Ag atom " + exception);
      System.err.println("" + Arrays.toString(exception.getStackTrace()));
      System.err.println("Trying to access " + immobile + " " + mobile);
      type = TERRACE;
    }
    return type;
  }
}
