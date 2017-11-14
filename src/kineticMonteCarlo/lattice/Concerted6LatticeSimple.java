package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.ConcertedAtom;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Concerted6LatticeSimple extends AgUcLatticeSimple {
  
  public Concerted6LatticeSimple(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {
    super(hexaSizeI, hexaSizeJ, modified, distancePerStep, 2);
  }
  
  @Override 
  public void deposit(AbstractGrowthAtom atom, boolean forceNucleation) {
    atom.setOccupied(true);
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      addNeighbour(atom.getNeighbour(i));
    }
    atom.resetProbability();
  }
  
  @Override
  public double extract(AbstractGrowthAtom atom) {
    atom.setOccupied(false);
    double probabilityChange = atom.getProbability();
    
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      removeNeighbour(atom.getNeighbour(i));
    }

    atom.resetProbability();
    atom.setList(false);
    return probabilityChange;
  }
  
  /**
   * Éste lo ejecutan los primeros vecinos
   *
   * @param neighbourAtom neighbour atom of the original atom
   * @param originType type of the original atom
   * @param forceNucleation
   */
  private void addNeighbour(AbstractGrowthAtom neighbourAtom) {
    neighbourAtom.addOccupiedNeighbour(1);
    byte newType = (byte) (neighbourAtom.getType() + 1);
    if (newType > 6) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is >6, which is in practice impossible");
    }
    ((ConcertedAtom) neighbourAtom).addNMobile(1);

    // Always changes the type of neighbour
    neighbourAtom.setType(newType);
  }
  
  /**
   * Computes the removal of one mobile atom.
   * 
   * @param neighbourAtom neighbour atom of the original atom
   */
  private void removeNeighbour(AbstractGrowthAtom neighbourAtom) {
    neighbourAtom.addOccupiedNeighbour(-1);
    byte newType = (byte) (neighbourAtom.getType() - 1);
    if (newType < 0) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is <0, which is in practice impossible");
    }
    ((ConcertedAtom) neighbourAtom).addNMobile(-1); // remove one mobile atom (original atom has been extracted)

    neighbourAtom.setType(newType);
  }
}
