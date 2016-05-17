/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.BasicAtom;
import utils.list.ListConfiguration;
import java.util.ListIterator;
import kineticMonteCarlo.atom.BasicGrowthAtom;
import kineticMonteCarlo.lattice.BasicGrowthLattice;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class BasicGrowthKmc extends AbstractGrowthKmc {

  private double minHeight;

  public BasicGrowthKmc(ListConfiguration config, 
          int hexaSizeI, 
          int hexaSizeJ, 
          boolean justCentralFlake,
          boolean periodicSingleFlake,
          float coverage,
          boolean useMaxPerimeter,
          short perimeterType,
          boolean extraOutput) {
    super(config, justCentralFlake, periodicSingleFlake, coverage, useMaxPerimeter, perimeterType, extraOutput);
     
    BasicGrowthLattice basicLattice = new BasicGrowthLattice(hexaSizeI, hexaSizeJ, getModifiedBuffer());
    basicLattice.init();
    setLattice(basicLattice); 
    if (justCentralFlake) {
      setPerimeter(new RoundPerimeter("Ag"));
    }
  }
    
  @Override
  public void depositSeed() {
    getLattice().resetOccupied();
    if (isJustCentralFlake()) {
      setAtomPerimeter();
      setCurrentOccupiedArea(8); // Seed will have 8 atoms
      
      int jCentre = (getLattice().getHexaSizeJ() / 2);
      int iCentre = (getLattice().getHexaSizeI() / 2);

      depositAtom(iCentre, jCentre);
      depositAtom(iCentre + 1, jCentre);

      depositAtom(iCentre - 1, jCentre + 1);
      depositAtom(iCentre, jCentre + 1);
      depositAtom(iCentre + 1, jCentre + 1);

      depositAtom(iCentre, jCentre + 2);
      depositAtom(iCentre - 1, jCentre + 2);
      depositAtom(iCentre - 1, jCentre + 3);

    } else {

      for (int i = 0; i < 3; i++) {
        int I = (int) (StaticRandom.raw() * getLattice().getHexaSizeI());
        int J = (int) (StaticRandom.raw() * getLattice().getHexaSizeJ());
        depositAtom(I, J);
      }
    }
  }

  
  // Kentzeko
  /*
  @Override
  protected boolean performSimulationStep() {
    BasicGrowthAtom atom = (BasicGrowthAtom) getList().nextEvent();
    if (atom == null) return false;
    if (atom.getY() > getLattice().getHexaSizeJ() - minHeight) {
      return true;
    }

    atom.remove();
    atom.setList(null);
    for (int k = 0; k < 4; k++) {
      if (atom.getNeighbour(k).getType() == 3) {
        getList().addAtom(atom.getNeighbour(k));
        getList().addTotalProbability(atom.getProbability());
      }
    }
    return false;

  }*/

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
  }
  
  /*@Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];

    double scaleX = binX / (float) getLattice().getHexaSizeI();
    ListIterator<AbstractAtom> iterator = getList().getIterator();

    while (iterator.hasNext()) {
      BasicAtom atom = (BasicAtom) iterator.next();
      int sampledPosX = (int) (atom.getX() * scaleX);
      if (surface[0][sampledPosX] < atom.getY()) {
        surface[0][sampledPosX] = atom.getY();
      }
    }
    return surface;
  }*/

  /**
   * Number of islands has no sense in etching.
   * @return -1 always
   */
  @Override
  public int getIslandCount() {
    return -1;
  }
  
  /**
   * Does nothing
   *
   * @param inputArea
   * @param scale
   * @return just in case, the input area
   */
  @Override
  public float[][] increaseEmptyArea(float[][] inputArea, double scale) {
    return inputArea;
  }
}
