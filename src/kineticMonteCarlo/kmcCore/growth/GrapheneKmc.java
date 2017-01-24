/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import static kineticMonteCarlo.atom.AbstractAtom.ARMCHAIR_EDGE;
import static kineticMonteCarlo.atom.AbstractAtom.TERRACE;
import static kineticMonteCarlo.atom.AbstractAtom.ZIGZAG_EDGE;
import kineticMonteCarlo.atom.GrapheneAtom;
import kineticMonteCarlo.atom.GrapheneAtomGaillard;
import kineticMonteCarlo.atom.GrapheneAtomGaillard1Neighbour;
import kineticMonteCarlo.atom.GrapheneAtomSchoenhalz;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.GrapheneLattice;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class GrapheneKmc extends AbstractGrowthKmc {

  public GrapheneKmc(Parser parser) {
    super(parser);

    HopsPerStep distancePerStep = new HopsPerStep();

    GrapheneLattice lattice;
    switch (parser.getRatesLibrary()) {
      case "Gaillard1Neighbour":
        lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneAtomGaillard1Neighbour.class);
        break;
      case "Gaillard2Neighbours":
        lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneAtomGaillard.class);
        break;
      case "Schoenhalz":
        lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneAtomSchoenhalz.class);
        break;
      default:
        lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneAtom.class);
        break;
    }
    setLattice(lattice);

    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("graphene"));
    }
    if (parser.useDevita()) {
      configureDevitaAccelerator(distancePerStep);
    }
    super.initHistogramSucces(8);
  }

  private void configureDevitaAccelerator(HopsPerStep distancePerStep) {
    setAccelerator(new DevitaAccelerator(getLattice(), distancePerStep));

    getAccelerator().tryToSpeedUp(TERRACE,
            new DevitaHopsConfig()
            .setMinAccumulatedSteps(100)
            .setMaxAccumulatedSteps(200)
            .setMinDistanceHops(1)
            .setMaxDistanceHops(10));

    //accelerating types 2 (ZIGZAG_EDGE) and 3 (ARMCHAIR_EDGE) does not improve performance and introduce some morphology differences
    getAccelerator().tryToSpeedUp(ZIGZAG_EDGE,
            new DevitaHopsConfig()
            .setMinAccumulatedSteps(30)
            .setMaxAccumulatedSteps(100)
            .setMinDistanceHops(1)
            .setMaxDistanceHops(5));

    getAccelerator().tryToSpeedUp(ARMCHAIR_EDGE,
            new DevitaHopsConfig()
            .setMinAccumulatedSteps(30)
            .setMaxAccumulatedSteps(100)
            .setMinDistanceHops(1)
            .setMaxDistanceHops(5));
  }
}
