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
    this.setLattice(new AgLattice(hexaSizeI, hexaSizeJ, getModifiedBuffer(), distancePerStep));
    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
      this.setPerimeter(new RoundPerimeter("Ag"));
    }
  }

  public AgKmc(ListConfiguration config, 
          int hexaSizeI, 
          int hexaSizeJ, 
          double depositionRatePerSite) {

    this(config, hexaSizeI, hexaSizeJ, true, (float) -1, false, RoundPerimeter.CIRCLE, false);
    
    setDepositionRate(depositionRatePerSite);
  }

  @Override
  public void depositSeed() {
    getLattice().resetOccupied();
    if (isJustCentralFlake()) {
      setAtomPerimeter();

      int jCentre = (getLattice().getHexaSizeJ() / 2);
      int iCentre = (getLattice().getHexaSizeI() / 2) - (getLattice().getHexaSizeJ() / 4);

      this.depositAtom(iCentre, jCentre);
      this.depositAtom(iCentre + 1, jCentre);

      this.depositAtom(iCentre - 1, jCentre + 1);
      this.depositAtom(iCentre, jCentre + 1);
      this.depositAtom(iCentre + 1, jCentre + 1);

      this.depositAtom(iCentre, jCentre + 2);
      this.depositAtom(iCentre - 1, jCentre + 2);
      this.depositAtom(iCentre - 1, jCentre + 3);

    } else {

      for (int i = 0; i < 3; i++) {
        int X = (int) (StaticRandom.raw() * getLattice().getHexaSizeI());
        int Y = (int) (StaticRandom.raw() * getLattice().getHexaSizeJ());
        depositAtom(X, Y);
      }
    }
  }

  private void configureDevitaAccelerator(HopsPerStep distancePerStep) {
    this.setAccelerator(new DevitaAccelerator(this.getLattice(), distancePerStep));

    if (getAccelerator() != null) {
      this.getAccelerator().tryToSpeedUp(TERRACE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(100)
              .setMaxAccumulatedSteps(200)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(8));

      this.getAccelerator().tryToSpeedUp(EDGE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(30)
              .setMaxAccumulatedSteps(100)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(5));
    }
  }

}
