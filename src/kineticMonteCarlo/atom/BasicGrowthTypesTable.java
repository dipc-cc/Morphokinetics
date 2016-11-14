package kineticMonteCarlo.atom;

import static kineticMonteCarlo.atom.BasicGrowthAtom.EDGE;
import static kineticMonteCarlo.atom.BasicGrowthAtom.ISLAND;
import static kineticMonteCarlo.atom.BasicGrowthAtom.KINK;
import static kineticMonteCarlo.atom.BasicGrowthAtom.TERRACE;

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
    byte type;

    if (numberOfNeighbourAtoms > 4) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is >4, which is in practice impossible");
    }
    try {
      type = tablePresent[numberOfNeighbourAtoms];
    } catch (ArrayIndexOutOfBoundsException exception) {
      System.err.println("Catched error getting current type of Ag atom " + exception);
      System.err.println("Trying to access " + numberOfNeighbourAtoms);
      type = TERRACE;
    }
    return type;
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
