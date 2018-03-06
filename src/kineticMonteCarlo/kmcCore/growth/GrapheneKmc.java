/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import static kineticMonteCarlo.atom.AbstractSite.ARMCHAIR_EDGE;
import static kineticMonteCarlo.atom.AbstractSite.TERRACE;
import static kineticMonteCarlo.atom.AbstractSite.ZIGZAG_EDGE;
import kineticMonteCarlo.atom.GrapheneSite;
import kineticMonteCarlo.atom.GrapheneSiteGaillard;
import kineticMonteCarlo.atom.GrapheneSiteGaillard1Neighbour;
import kineticMonteCarlo.atom.GrapheneSiteGaillardSimple;
import kineticMonteCarlo.atom.GrapheneSiteSchoenhalz;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.GrapheneLatticeSimple;
import kineticMonteCarlo.lattice.GrapheneLattice;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class GrapheneKmc extends AbstractGrowthKmc {

  public GrapheneKmc(Parser parser) {
    super(parser);

    HopsPerStep distancePerStep = new HopsPerStep();

    GrapheneLattice lattice;
    switch (parser.getRatesLibrary()) {
      case "GaillardSimple":
        lattice = new GrapheneLatticeSimple(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneSiteGaillardSimple.class);
        break;
      case "Gaillard1Neighbour":
        lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneSiteGaillard1Neighbour.class);
        break;
      case "Gaillard2Neighbours":
        lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneSiteGaillard.class);
        break;
      case "Schoenhalz":
        lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneSiteSchoenhalz.class);
        break;
      default:
        lattice = new GrapheneLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, GrapheneSite.class);
        break;
    }
    setLattice(lattice);

    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("graphene"));
    }
    if (parser.useDevita()) {
      configureDevitaAccelerator(distancePerStep);
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
