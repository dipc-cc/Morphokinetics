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

  public GrapheneKmc(ListConfiguration config, 
          int hexaSizeI, 
          int hexaSizeJ, 
          boolean justCentralFlake, 
          boolean randomise, 
          boolean useMaxPerimeter,
          short perimeterType) {
    super(config, justCentralFlake, randomise, useMaxPerimeter, perimeterType);

    HopsPerStep distancePerStep = new HopsPerStep();

    this.lattice = new GrapheneLattice(hexaSizeI, hexaSizeJ, modifiedBuffer, distancePerStep);

    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
    }
  }

  @Override
  protected void depositSeed() {
    if (justCentralFlake) {
      this.perimeter = new RoundPerimeter("Graphene_CVD_growth");
      if (this.useMaxPerimeter){
        this.perimeter.setMaxPerimeter();
      }
      if (this.perimeterType == RoundPerimeter.CIRCLE) {
        this.perimeter.setAtomPerimeter(lattice.setInsideCircle(perimeter.getCurrentRadius())); 
      } else {
        this.perimeter.setAtomPerimeter(lattice.setInsideSquare(perimeter.getCurrentRadius()));
      }

      int iCentre = lattice.getHexaSizeJ() / 2;
      int jCentre = lattice.getHexaSizeI() / 2;
      
      for (int j = -1; j < 2; j++) {
        for (int i = -1; i < 1; i++) {
          this.depositAtom(jCentre + i, iCentre + j);
        }
      }
    } else {
      for (int i = 0; i < 3; i++) {
        int iHexa = (int) (StaticRandom.raw() * lattice.getHexaSizeI());
        int jHexa = (int) (StaticRandom.raw() * lattice.getHexaSizeJ());
        depositAtom(iHexa, jHexa);
      }
    }
  }

  private void configureDevitaAccelerator(HopsPerStep distancePerStep) {
    this.accelerator = new DevitaAccelerator(this.lattice, distancePerStep);

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
