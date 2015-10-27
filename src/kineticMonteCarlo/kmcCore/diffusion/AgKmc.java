/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.diffusion;

import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;
import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.AgAgLattice;
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
          short perimeterType) {
    super(config, justCentralFlake, coverage, useMaxPerimeter, perimeterType);

    HopsPerStep distancePerStep = new HopsPerStep();
    this.lattice = new AgAgLattice(hexaSizeI, hexaSizeJ, modifiedBuffer, distancePerStep);
    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
      this.perimeter = new RoundPerimeter("Ag");
    }
  }

  public AgKmc(ListConfiguration config, 
          int hexaSizeI, 
          int hexaSizeJ, 
          double depositionRate, 
          double islandDensity) {

    this(config, hexaSizeI, hexaSizeJ, true, (float) -1, false, RoundPerimeter.CIRCLE);
    
    this.setIslandDensityAndDepositionRate(depositionRate, islandDensity);
  }

  @Override
  public void depositSeed() {
    lattice.resetOccupied();
    if (justCentralFlake) {
      if (this.useMaxPerimeter){
        this.perimeter.setMaxPerimeter();
      }
      perimeter.setMinRadius();
      if (this.perimeterType == RoundPerimeter.CIRCLE) {
        this.perimeter.setAtomPerimeter(lattice.setInsideCircle(perimeter.getCurrentRadius()));
      } else {
        this.perimeter.setAtomPerimeter(lattice.setInsideSquare(perimeter.getCurrentRadius()));
      }

      int jCentre = (lattice.getHexaSizeJ() / 2);
      int iCentre = (lattice.getHexaSizeI() / 2) - (lattice.getHexaSizeJ() / 4);

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
        int X = (int) (StaticRandom.raw() * lattice.getHexaSizeI());
        int Y = (int) (StaticRandom.raw() * lattice.getHexaSizeJ());
        depositAtom(X, Y);
      }
    }
  }

  private void configureDevitaAccelerator(HopsPerStep distancePerStep) {
    this.accelerator = new DevitaAccelerator(this.lattice, distancePerStep);

    if (accelerator != null) {
      this.accelerator.tryToSpeedUp(TERRACE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(100)
              .setMaxAccumulatedSteps(200)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(8));

      this.accelerator.tryToSpeedUp(EDGE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(30)
              .setMaxAccumulatedSteps(100)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(5));
    }
  }

}
