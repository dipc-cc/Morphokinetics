/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.diffusion;

import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.kmcCore.diffusion.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.GrapheneLattice;
import utils.list.ListConfiguration;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class GrapheneKmc extends Abstract2DDiffusionKmc {

  public GrapheneKmc(ListConfiguration config, int sizeX, int sizeY, boolean justCentralFlake, boolean randomise) {
    super(config, justCentralFlake, randomise);

    HopsPerStep distancePerStep = new HopsPerStep();

    this.lattice = new GrapheneLattice(sizeX, sizeY, modifiedBuffer, distancePerStep);

    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
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
            .setMinAccumulatedSteps(100)
            .setMaxAccumulatedSteps(200)
            .setMinDistanceHops(1)
            .setMaxDistanceHops(10));

        //accelerating types 2 and 3 does not improve performance and introduce some morphology differences
    this.accelerator.tryToSpeedUp(2,
            new DevitaHopsConfig()
            .setMinAccumulatedSteps(30)
            .setMaxAccumulatedSteps(100)
            .setMinDistanceHops(1)
            .setMaxDistanceHops(5));

    this.accelerator.tryToSpeedUp(3,
            new DevitaHopsConfig()
            .setMinAccumulatedSteps(30)
            .setMaxAccumulatedSteps(100)
            .setMinDistanceHops(1)
            .setMaxDistanceHops(5));
  }
}
