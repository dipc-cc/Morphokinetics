/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import static kineticMonteCarlo.atom.CatalysisAtom.EDGE;
import static kineticMonteCarlo.atom.CatalysisAtom.TERRACE;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaAccelerator;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.DevitaHopsConfig;
import kineticMonteCarlo.kmcCore.growth.devitaAccelerator.HopsPerStep;
import kineticMonteCarlo.lattice.CatalysisLattice;

/**
 *
 * @author Karmele Valencia
 */
public abstract class CatalysisKmc extends AbstractGrowthKmc {
  
  public CatalysisKmc(Parser parser) {
    super(parser);
    
    HopsPerStep distancePerStep = new HopsPerStep();
    CatalysisLattice basicLattice = new CatalysisLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    basicLattice.init();
    setLattice(basicLattice); 
    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("Ag"));
    }
    if (parser.useDevita()) {
      configureDevitaAccelerator(distancePerStep);
    }
  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
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
