package kineticMonteCarlo.lattice;

import kineticMonteCarlo.atom.AbstractGrowthAtom;
import kineticMonteCarlo.atom.AgAtom;
import kineticMonteCarlo.atom.ModifiedBuffer;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgUcLatticeSimple extends AgUcLattice{
  
  public AgUcLatticeSimple(int hexaSizeI, int hexaSizeJ, ModifiedBuffer modified, HopsPerStep distancePerStep) {
    super(hexaSizeI, hexaSizeJ, modified, distancePerStep, true);
  }
  
  @Override 
  public void deposit(AbstractGrowthAtom a, boolean forceNucleation) {
    AgAtom atom = (AgAtom) a;
    atom.setOccupied(true);
    byte originalType = atom.getType();
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      addOccupiedNeighbour(atom.getNeighbour(i));
    }

    addAtom(atom);
    if (atom.getNMobile() > 0) {
      addBondAtom(atom);
    }
    atom.resetProbability();
  }
  
  @Override
  public double extract(AbstractGrowthAtom a) {
    AgAtom atom = (AgAtom) a;
    atom.setOccupied(false);
    double probabilityChange = a.getProbability();
    
    for (int i = 0; i < atom.getNumberOfNeighbours(); i++) {
      removeMobileOccupied(atom.getNeighbour(i));
    }

    if (atom.getNMobile() > 0) {
      addBondAtom(atom);
    }

    atom.resetProbability();
    atom.setList(false);
    return probabilityChange;
  }
  
  /**
   * Éste lo ejecutan los primeros vecinos
   * @param neighbourAtom neighbour atom of the original atom
   * @param originType type of the original atom
   * @param forceNucleation
   */
  private void addOccupiedNeighbour(AgAtom neighbourAtom) {
    byte newType = (byte) (neighbourAtom.getType() + 1);
    if (newType > 6) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is >6, which is in practice impossible");
    }
    neighbourAtom.addNMobile(1);

    // Always changes the type of neighbour
    neighbourAtom.setType(newType);
    addAtom(neighbourAtom);
    if (neighbourAtom.getNMobile() > 0 && !neighbourAtom.isOccupied()) {
      addBondAtom(neighbourAtom);
    }
  }
  
  /**
   * Computes the removal of one mobile atom.
   * 
   * @param neighbourAtom neighbour atom of the original atom
   */
  private void removeMobileOccupied(AgAtom neighbourAtom) {

    byte newType = (byte) (neighbourAtom.getType() - 1);
    if (newType < 0) {
      throw new ArrayIndexOutOfBoundsException("The sum of neighbours is <0, which is in practice impossible");
    }
    neighbourAtom.addNMobile(-1); // remove one mobile atom (original atom has been extracted)

    neighbourAtom.setType(newType);
    addAtom(neighbourAtom);
    if (neighbourAtom.getNMobile() > 0 && !neighbourAtom.isOccupied()) {
      addBondAtom(neighbourAtom);
    }
  }
  
}
