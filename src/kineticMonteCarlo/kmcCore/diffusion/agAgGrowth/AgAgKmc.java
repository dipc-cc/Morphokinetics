/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.diffusion.agAgGrowth;

import kineticMonteCarlo.kmcCore.diffusion.Abstract2DDiffusionKmc;
import kineticMonteCarlo.kmcCore.diffusion.RoundPerimeter;
import kineticMonteCarlo.atom.diffusion.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.atom.diffusion.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.atom.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.diffusion.agAgGrowth.AgAgGrowthLattice;
import kineticMonteCarlo.list.ListConfiguration;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class AgAgKmc extends Abstract2DDiffusionKmc {

  public AgAgKmc(ListConfiguration config, int sizeX, int sizeY, boolean justCentralFlake) {
    super(config, justCentralFlake);

    HopsPerStep distance_per_step = new HopsPerStep();
    this.lattice = new AgAgGrowthLattice(sizeX, sizeY, modified_buffer, distance_per_step);
    if (justCentralFlake) {
      configureDevitaAccelerator(distance_per_step);
    }
  }

  public AgAgKmc(AgAgKmcConfig config, boolean justCentralFlake) {

    super(config.getListConfig(), justCentralFlake);

    HopsPerStep distance_per_step = new HopsPerStep();
    this.lattice = new AgAgGrowthLattice(config.getSizeX(), config.getSizeY(), modified_buffer, distance_per_step);
    if (justCentralFlake) {
      configureDevitaAccelerator(distance_per_step);
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

  private void configureDevitaAccelerator(HopsPerStep distance_per_step) {
    this.accelerator = new DevitaAccelerator(this.lattice, distance_per_step);

    if (accelerator != null) {
      this.accelerator.tryToSpeedUp(0,
              new DevitaHopsConfig()
              .setMin_accumulated_steps(100)
              .setMax_accumulated_steps(200)
              .setMin_distance_hops(1)
              .setMax_distance_hops(8));

      this.accelerator.tryToSpeedUp(2,
              new DevitaHopsConfig()
              .setMin_accumulated_steps(30)
              .setMax_accumulated_steps(100)
              .setMin_distance_hops(1)
              .setMax_distance_hops(5));
    }
  }

}
