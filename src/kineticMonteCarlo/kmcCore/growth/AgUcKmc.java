/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
import static kineticMonteCarlo.site.AgSite.EDGE;
import static kineticMonteCarlo.site.AgSite.TERRACE;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.AgUcLattice;
import kineticMonteCarlo.lattice.AgUcLatticeSimple;
import kineticMonteCarlo.unitCell.AgUc;

/**
 * Ag unit cell kinetic Monte Carlo
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgUcKmc extends AbstractGrowthKmc {

  public AgUcKmc(Parser parser) {
    super(parser);

    HopsPerStep distancePerStep = new HopsPerStep();
    AgUcLattice agLattice;
    if (parser.getRatesLibrary().equals("simple")) {// || parser.getRatesLibrary().equals("bruneAgPt")) {
      agLattice = new AgUcLatticeSimple(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, 0);
    } else {
      agLattice = new AgUcLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, 1);
    }
    setLattice(agLattice);
    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("Ag"));
    }
    if (parser.useDevita()) {
      configureDevitaAccelerator(distancePerStep);
    }
  }
 
  public void printLattice() {
    AgUcLattice lattice = (AgUcLattice) getLattice();
    for (int i = 0; i < lattice.size(); i++) {
      AgUc uc = lattice.getUc(i);
      double ucX = uc.getPos().getX();
      double ucY = uc.getPos().getY();
      System.out.println(i + ") UC " + ucX + " " + ucY);
      for (int j = 0; j < uc.size(); j++) {
        double atomX = uc.getSite(j).getPos().getX();
        double atomY = uc.getSite(j).getPos().getY();
        System.out.println("Atom " + i + " position " + atomX + " " + atomY);
        System.out.println("uc position " + (ucX + atomX) + " " + (ucY + atomY));
      }

    }
  }
  
  private void configureDevitaAccelerator(HopsPerStep distancePerStep) {
    setAccelerator(new DevitaAccelerator(getLattice(), distancePerStep));

    if (getAccelerator() != null) {
      getAccelerator().tryToSpeedUp(TERRACE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(100)
              .setMaxAccumulatedSteps(200)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(8));

      getAccelerator().tryToSpeedUp(EDGE,
              new DevitaHopsConfig()
              .setMinAccumulatedSteps(30)
              .setMaxAccumulatedSteps(100)
              .setMinDistanceHops(1)
              .setMaxDistanceHops(5));
    }
  }
}
