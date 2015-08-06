/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.diffusion;

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
public class AgAgKmc extends Abstract2DDiffusionKmc {

  public AgAgKmc(ListConfiguration config, 
          int axonSizeI, 
          int axonSizeJ, 
          boolean justCentralFlake,
          boolean randomise, 
          boolean useMaxPerimeter,
          short perimeterType) {
    super(config, justCentralFlake, randomise, useMaxPerimeter, perimeterType);

    HopsPerStep distancePerStep = new HopsPerStep();
    this.lattice = new AgAgLattice(axonSizeI, axonSizeJ, modifiedBuffer, distancePerStep);
    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
    }
  }

  public AgAgKmc(AgAgKmcConfig config, boolean justCentralFlake, boolean randomise) {

    super(config.getListConfig(), justCentralFlake, randomise, false, RoundPerimeter.CIRCLE);

    HopsPerStep distancePerStep = new HopsPerStep();
    this.lattice = new AgAgLattice(config.getAxonSizeI(), config.getAxonSizeJ(), modifiedBuffer, distancePerStep);
    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
    }
    this.setIslandDensityAndDepositionRate(config.getDepositionRate(), config.getIslandDensity());
  }

  @Override
  protected void depositSeed() {

    if (justCentralFlake) {
      this.perimeter = new RoundPerimeter("Ag_Ag_growth");
      if (this.useMaxPerimeter){
        this.perimeter.setMaxPerimeter();
      }
      if (this.perimeterType == RoundPerimeter.CIRCLE) {
        this.perimeter.setAtomPerimeter(lattice.setInsideCircle(perimeter.getCurrentRadius()));
      } else {
        this.perimeter.setAtomPerimeter(lattice.setInsideSquare(perimeter.getCurrentRadius()));
      }

      int Ycenter = (lattice.getAxonSizeJ() / 2);
      int Xcenter = (lattice.getAxonSizeI() / 2) - (lattice.getAxonSizeJ() / 4);

      this.depositAtom(Xcenter, Ycenter);
      this.depositAtom(Xcenter + 1, Ycenter);

      this.depositAtom(Xcenter - 1, Ycenter + 1);
      this.depositAtom(Xcenter, Ycenter + 1);
      this.depositAtom(Xcenter + 1, Ycenter + 1);

      this.depositAtom(Xcenter, Ycenter + 2);
      this.depositAtom(Xcenter - 1, Ycenter + 2);
      this.depositAtom(Xcenter - 1, Ycenter + 3);

    } else {

      for (int i = 0; i < 3; i++) {
        int X = (int) (StaticRandom.raw() * lattice.getAxonSizeI());
        int Y = (int) (StaticRandom.raw() * lattice.getAxonSizeJ());
        depositAtom(X, Y);
      }
    }
  }

  private void configureDevitaAccelerator(HopsPerStep distancePerStep) {
    this.accelerator = new DevitaAccelerator(this.lattice, distancePerStep);

    if (accelerator != null) {
      this.accelerator.tryToSpeedUp(0,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(100)
              .setMaxAccumulatedSteps(200)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(8));

      this.accelerator.tryToSpeedUp(2,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(30)
              .setMaxAccumulatedSteps(100)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(5));
    }
  }

}
