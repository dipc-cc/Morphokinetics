/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import kineticMonteCarlo.atom.AbstractGrowthAtom;
import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;
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
    if (parser.getRatesLibrary().equals("simple")) {
      agLattice = new AgUcLatticeSimple(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep);
    } else {
      agLattice = new AgUcLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer(), distancePerStep, false);
    }
    agLattice.init();
    setLattice(agLattice);
    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("Ag"));
    }
    if (parser.useDevita()) {
      configureDevitaAccelerator(distancePerStep);
    }
    super.initHistogramSucces(7);
  }
 
  public void printLattice() {
    AgUcLattice lattice = (AgUcLattice) getLattice();
    for (int i = 0; i < lattice.size(); i++) {
      AgUc uc = lattice.getUc(i);
      double ucX = uc.getPos().getX();
      double ucY = uc.getPos().getY();
      System.out.println(i + ") UC " + ucX + " " + ucY);
      for (int j = 0; j < uc.size(); j++) {
        double atomX = uc.getAtom(j).getPos().getX();
        double atomY = uc.getAtom(j).getPos().getY();
        System.out.println("Atom " + i + " position " + atomX + " " + atomY);
        System.out.println("uc position " + (ucX + atomX) + " " + (ucY + atomY));
      }

    }
  }
  
  /**
   * Tries to deposit an atom in the given coordinates.
   *
   * @param iHexa unit cell coordinate.
   * @param jHexa unit cell coordinate.
   * @param pos position within the unit cell.
   * @return true if deposited and false otherwise.
   */
  boolean depositAtom(int iHexa, int jHexa, int pos) {
    AbstractGrowthAtom atom = ((AgUcLattice) getLattice()).getAtom(iHexa, jHexa, pos);
    return depositAtom(atom);
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
