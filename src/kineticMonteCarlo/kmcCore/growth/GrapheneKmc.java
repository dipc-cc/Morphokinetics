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
    if (parser.getRatesLibrary().equals("Gaillard2Neighbours")) {
      lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneAtomGaillard.class);
    } else if (parser.getRatesLibrary().equals("Schoenhalz")) {
      lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneAtomSchoenhalz.class);
    } else {
      lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneAtom.class);
    }
    setLattice(lattice);

    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("graphene"));
      configureDevitaAccelerator(distancePerStep);
    }
    super.initHistogramSucces(8);
  }

  @Override
  public void depositSeed() {
    getLattice().resetOccupied();
    if (isJustCentralFlake()) {
      setAtomPerimeter();
      setCurrentOccupiedArea(7); // Seed will have 7 atoms
      
      int iCentre = getLattice().getHexaSizeJ() / 2;
      int jCentre = getLattice().getHexaSizeI() / 2;
      
      for (int j = -1; j < 2; j++) {
        for (int i = -1; i < 1; i++) {
          depositAtom(jCentre + i, iCentre + j);
        }
      }
      depositAtom(jCentre + 2, iCentre + 1);
    } else {
      for (int i = 0; i < 3; i++) {
        int iHexa = (int) (StaticRandom.raw() * getLattice().getHexaSizeI());
        int jHexa = (int) (StaticRandom.raw() * getLattice().getHexaSizeJ());
        depositAtom(iHexa, jHexa);
      }
    }
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
