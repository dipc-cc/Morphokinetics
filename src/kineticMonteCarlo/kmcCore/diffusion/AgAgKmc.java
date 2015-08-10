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
          int hexaSizeI, 
          int hexaSizeJ, 
          boolean justCentralFlake,
          boolean randomise, 
          boolean useMaxPerimeter,
          short perimeterType) {
    super(config, justCentralFlake, randomise, useMaxPerimeter, perimeterType);

    HopsPerStep distancePerStep = new HopsPerStep();
    this.lattice = new AgAgLattice(hexaSizeI, hexaSizeJ, modifiedBuffer, distancePerStep);
    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
    }
  }

  public AgAgKmc(ListConfiguration config, 
          int hexaSizeI, 
          int hexaSizeJ, 
          double depositionRate, 
          double islandDensity) {

    this(config, hexaSizeI, hexaSizeJ, false, true, false, RoundPerimeter.CIRCLE);
    
    this.setIslandDensityAndDepositionRate(depositionRate, islandDensity);
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

      int Ycenter = (lattice.getHexaSizeJ() / 2);
      int Xcenter = (lattice.getHexaSizeI() / 2) - (lattice.getHexaSizeJ() / 4);

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
        int X = (int) (StaticRandom.raw() * lattice.getHexaSizeI());
        int Y = (int) (StaticRandom.raw() * lattice.getHexaSizeJ());
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
