/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import kineticMonteCarlo.atom.AbstractGrowthAtom;
import static kineticMonteCarlo.atom.AgAtom.EDGE;
import static kineticMonteCarlo.atom.AgAtom.TERRACE;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.AgUcLattice;
import kineticMonteCarlo.unitCell.AgUc;
import utils.StaticRandom;
import utils.list.ListConfiguration;

/**
 * Ag unit cell kinetic Monte Carlo
 *
 * @author J. Alberdi-Rodriguez
 */
public class AgUcKmc extends AbstractGrowthKmc {

  public AgUcKmc(ListConfiguration config,
          int ucSizeI,
          int ucSizeJ,
          boolean justCentralFlake,
          boolean periodicSingleFlake,
          float coverage,
          boolean useMaxPerimeter,
          short perimeterType,
          boolean extraOutput) {
    super(config, justCentralFlake, periodicSingleFlake, coverage, useMaxPerimeter, perimeterType, extraOutput);

    HopsPerStep distancePerStep = new HopsPerStep();
    AgUcLattice agLattice = new AgUcLattice(ucSizeI, ucSizeJ, getModifiedBuffer(), distancePerStep);
    agLattice.init();
    setLattice(agLattice);
    if (justCentralFlake) {
      configureDevitaAccelerator(distancePerStep);
      setPerimeter(new RoundPerimeter("Ag"));
    }      
  }

  @Override
  public void depositSeed() {
    getLattice().resetOccupied();
    if (isJustCentralFlake()) {
      setAtomPerimeter();
      setCurrentOccupiedArea(8); // Seed will have 8 atoms

      int jCentre = (getLattice().getHexaSizeJ() / 2);
      int iCentre = (getLattice().getHexaSizeI() / 2);

      depositAtom(iCentre - 1, jCentre - 1, 1); 
      depositAtom(iCentre, jCentre - 1, 1);
      depositAtom(iCentre - 1, jCentre, 0);
      depositAtom(iCentre, jCentre, 0);
      depositAtom(iCentre + 1, jCentre, 0);
      depositAtom(iCentre, jCentre, 1);
      depositAtom(iCentre - 1, jCentre, 1);
      depositAtom(iCentre, jCentre + 1, 0);

    } else {

      for (int i = 0; i < 3; i++) {
        int I = (int) (StaticRandom.raw() * getLattice().getHexaSizeI());
        int J = (int) (StaticRandom.raw() * getLattice().getHexaSizeJ());
        depositAtom(I, J);
      }
    }
  }
 
  /**
   * 
   * @param iHexa unit cell coordinate
   * @param jHexa unit cell coordinate
   * @param pos position within the unit cell
   * @return 
   */
  protected boolean depositAtom(int iHexa, int jHexa, int pos) {
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
  
}
