/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.atom.GrapheneAtomGaillard;
import kineticMonteCarlo.atom.ModifiedBuffer;

/**
 *
 * @author Nestor
 */
public class GrapheneLatticeGaillard extends GrapheneLattice {

  public GrapheneLatticeGaillard(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {
    super(hexaSizeI, hexaSizeJ, modified, distancePerStep);

    // j axis has to be multiple of two
    if ((hexaSizeJ % 2) != 0) {
      hexaSizeJ++;
    }
    
    createAtoms(hexaSizeI, hexaSizeJ, distancePerStep);
    setAngles();
  }

  private int createId(int i, int j) {
    return j * getHexaSizeI() + i;
  }
    
  /**
   * Creates the atom array for graphene. It only allows even parameters
   *
   * @param hexaSizeI even number of the size in hexagonal lattice points. Currently it is
   * represented vertically starting from the top
   * @param hexaSizeJ even number of the size in hexagonal lattice points. Currently it is
   * represented horizontally starting from the left
   * @param distancePerStep
   * @return
   */
  private GrapheneAtomGaillard[][] createAtoms(int hexaSizeI, int hexaSizeJ, HopsPerStep distancePerStep) {
    //Instantiate atoms
    GrapheneAtomGaillard[][] atoms = new GrapheneAtomGaillard[hexaSizeI][hexaSizeJ];
    for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa += 2) {
      for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa += 2) {
        //para cada unit cell

        //atomo 0 de la unit cell, tipo 0
        atoms[iHexa][jHexa] = new GrapheneAtomGaillard(createId(iHexa, jHexa), (short) iHexa, (short) jHexa, distancePerStep);

        iHexa++;
        //atomo 1 de la unit cell, tipo 1
        atoms[iHexa][jHexa] = new GrapheneAtomGaillard(createId(iHexa, jHexa), (short) iHexa, (short) jHexa, distancePerStep);

        iHexa--;
        jHexa++;
        //atomo 2 de la unit cell, tipo 1   
        atoms[iHexa][jHexa] = new GrapheneAtomGaillard(createId(iHexa, jHexa), (short) iHexa, (short) jHexa, distancePerStep);

        iHexa++;
        //atomo 3 de la unit cell, tipo 0
        atoms[iHexa][jHexa] = new GrapheneAtomGaillard(createId(iHexa, jHexa), (short) iHexa, (short) jHexa, distancePerStep);

        iHexa--;
        jHexa--;
      }
    }
    
    setAtoms(atoms);
    
    //Interconect atoms
    for (int iHexa = 0; iHexa < getHexaSizeI(); iHexa++) {
      for (int jHexa = 0; jHexa < getHexaSizeJ(); jHexa++) {
        // Get and all 12 neighbours of current graphene atom
        GrapheneAtomGaillard[] neighbours = new GrapheneAtomGaillard[12];
        for (int i = 0; i < 12; i++) {
          neighbours[i] = (GrapheneAtomGaillard) getNeighbour(iHexa, jHexa, i);
        }
        atoms[iHexa][jHexa].setNeighbours(neighbours);
      }
    }
    return atoms;
  }
}
