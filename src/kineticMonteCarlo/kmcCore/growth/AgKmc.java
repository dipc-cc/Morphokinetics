/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.AgLattice;
import utils.list.ListConfiguration;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgKmc extends AbstractGrowthKmc {

  public AgKmc(ListConfiguration config, 
          int hexaSizeI, 
          int hexaSizeJ, 
          boolean justCentralFlake,
          float coverage,
          boolean useMaxPerimeter,
          short perimeterType,
          boolean depositInAllArea) {
    super(config, justCentralFlake, coverage, useMaxPerimeter, perimeterType, depositInAllArea);

    HopsPerStep distancePerStep = new HopsPerStep();
    setLattice(new AgLattice(hexaSizeI, hexaSizeJ, getModifiedBuffer(), distancePerStep));
    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
      setPerimeter(new RoundPerimeter("Ag"));
    }
  }

  public AgKmc(ListConfiguration config, 
          int hexaSizeI, 
          int hexaSizeJ, 
          double depositionRate, 
          double islandDensity) {

    this(config, hexaSizeI, hexaSizeJ, true, (float) -1, false, RoundPerimeter.CIRCLE, false);
    
    setDepositionRate(depositionRate, islandDensity);
  }

  @Override
  public void depositSeed() {
    getLattice().resetOccupied();
    if (isJustCentralFlake()) {
      setAtomPerimeter();
      setCurrentOccupiedArea(8); // Seed will have 8 atoms
      
      int jCentre = (getLattice().getHexaSizeJ() / 2);
      int iCentre = (getLattice().getHexaSizeI() / 2) - (getLattice().getHexaSizeJ() / 4);

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
        int X = (int) (StaticRandom.raw() * getLattice().getHexaSizeI());
        int Y = (int) (StaticRandom.raw() * getLattice().getHexaSizeJ());
        depositAtom(X, Y);
      }
    }
  }

  private void configureDevitaAccelerator(HopsPerStep distancePerStep) {
    setAccelerator(new DevitaAccelerator(getLattice(), distancePerStep));

    if (getAccelerator() != null) {
      getAccelerator().tryToSpeedUp(TERRACE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(100)
              .setMaxAccumulatedSteps(200)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(8));

      getAccelerator().tryToSpeedUp(EDGE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(30)
              .setMaxAccumulatedSteps(100)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(5));
    }
  }

}
