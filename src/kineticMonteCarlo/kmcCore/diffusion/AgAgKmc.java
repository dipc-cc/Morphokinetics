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

  public AgAgKmc(ListConfiguration config, int sizeX, int sizeY, boolean justCentralFlake, boolean randomise) {
    super(config, justCentralFlake, randomise);

    HopsPerStep distancePerStep = new HopsPerStep();
    this.lattice = new AgAgLattice(sizeX, sizeY, modifiedBuffer, distancePerStep);
    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
    }
  }

  public AgAgKmc(AgAgKmcConfig config, boolean justCentralFlake, boolean randomise) {

    super(config.getListConfig(), justCentralFlake, randomise);

    HopsPerStep distancePerStep = new HopsPerStep();
    this.lattice = new AgAgLattice(config.getSizeX(), config.getSizeY(), modifiedBuffer, distancePerStep);
    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
    }
    this.setIslandDensityAndDepositionRate(config.getDepositionRate(), config.getIslandDensity());
  }

  @Override
  protected void depositSeed() {

    if (justCentralFlake) {
      this.perimeter = new RoundPerimeter("Ag_Ag_growth");

      this.perimeter.setAtomPerimeter(lattice.setInside(perimeter.getCurrentRadius()));

      int Ycenter = (lattice.getSizeY() / 2);
      int Xcenter = (lattice.getSizeX() / 2) - (lattice.getSizeY() / 4);

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
        int X = (int) (StaticRandom.raw() * lattice.getSizeX());
        int Y = (int) (StaticRandom.raw() * lattice.getSizeY());
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
