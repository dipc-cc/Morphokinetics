/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.diffusion.grapheneCvdGrowth;

import kineticMonteCarlo.kmcCore.diffusion.Abstract2DDiffusionKmc;
import kineticMonteCarlo.kmcCore.diffusion.RoundPerimeter;
import kineticMonteCarlo.atom.diffusion.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.atom.diffusion.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.atom.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.diffusion.grapheneCvdGrowth.GrapheneCvdGrowthLattice;
import kineticMonteCarlo.list.ListConfiguration;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class GrapheneKmc extends Abstract2DDiffusionKmc {

  public GrapheneKmc(ListConfiguration config, int sizeX, int sizeY, boolean justCentralFlake) {
    super(config, justCentralFlake);

    HopsPerStep distance_per_step = new HopsPerStep();

    this.lattice = new GrapheneCvdGrowthLattice(sizeX, sizeY, modified_buffer, distance_per_step);

    if (justCentralFlake) {
      configureDevitaAccelerator(distance_per_step);
    }
  }

  @Override
  protected void depositSeed() {
    if (justCentralFlake) {
      this.perimeter = new RoundPerimeter("Graphene_CVD_growth");
      this.perimeter.setAtomPerimeter(lattice.setInside(perimeter.getCurrentRadius()));

      int Ycenter = lattice.getSizeY() / 2;
      int Xcenter = (lattice.getSizeX() / 2);
      for (int j = -1; j < 2; j++) {
        for (int i = -1; i < 1; i++) {
          this.depositAtom(Xcenter + i, Ycenter + j);
        }
      }
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

    this.accelerator.tryToSpeedUp(0,
            new DevitaHopsConfig()
            .setMin_accumulated_steps(100)
            .setMax_accumulated_steps(200)
            .setMin_distance_hops(1)
            .setMax_distance_hops(10));

        //accelerating types 2 and 3 does not improve performance and introduce some morphology differences
    this.accelerator.tryToSpeedUp(2,
            new DevitaHopsConfig()
            .setMin_accumulated_steps(30)
            .setMax_accumulated_steps(100)
            .setMin_distance_hops(1)
            .setMax_distance_hops(5));

    this.accelerator.tryToSpeedUp(3,
            new DevitaHopsConfig()
            .setMin_accumulated_steps(30)
            .setMax_accumulated_steps(100)
            .setMin_distance_hops(1)
            .setMax_distance_hops(5));
  }
}
