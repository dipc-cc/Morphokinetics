/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import utils.list.ListConfiguration;
import kineticMonteCarlo.lattice.BasicGrowthLattice;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class BasicGrowthKmc extends AbstractGrowthKmc {

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
    super.initHistogramSucces(4);
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

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
  }
}
