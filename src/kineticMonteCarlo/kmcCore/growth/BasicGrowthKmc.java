/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.growth;

import basic.Parser;
import kineticMonteCarlo.atom.BasicGrowthAtom;
import kineticMonteCarlo.lattice.BasicGrowthLattice;
import kineticMonteCarlo.unitCell.IUc;
import utils.StaticRandom;

/**
 *
 * @author Nestor
 */
public class BasicGrowthKmc extends AbstractGrowthKmc {

  public BasicGrowthKmc(Parser parser) {
    super(parser);
     
    BasicGrowthLattice basicLattice = new BasicGrowthLattice(parser.getHexaSizeI(), parser.getHexaSizeJ(), getModifiedBuffer());
    basicLattice.init();
    setLattice(basicLattice); 
    initList();
    if (parser.justCentralFlake()) {
      setPerimeter(new RoundPerimeter("Ag"));
    }
    super.initHistogramSucces(4);
  }
    
  @Override
  public void depositSeed() {
    getLattice().resetOccupied();
    if (isJustCentralFlake()) {
      setAtomPerimeter();
      setCurrentOccupiedArea(8); // Seed will have 8 atoms
      
      int jCentre = (getLattice().getHexaSizeJ() / 2);
      int iCentre = (getLattice().getHexaSizeI() / 2);

      depositAtom(iCentre, jCentre);
      depositAtom(iCentre + 1, jCentre);

      depositAtom(iCentre - 1, jCentre + 1);
      depositAtom(iCentre, jCentre + 1);
      depositAtom(iCentre + 1, jCentre + 1);

      depositAtom(iCentre, jCentre + 2);
      depositAtom(iCentre - 1, jCentre + 2);
      depositAtom(iCentre - 1, jCentre + 3);

    } else {

      for (int i = 0; i < 3; i++) {
        int I = (int) (StaticRandom.raw() * getLattice().getHexaSizeI());
        int J = (int) (StaticRandom.raw() * getLattice().getHexaSizeJ());
        depositAtom(I, J);
      }
    }
  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
  }
  
  /**
   * Performs a simulation step.
   * @return true if a stop condition happened (all atom etched, all surface covered)
   */
  @Override
  protected boolean performSimulationStep() {
    BasicGrowthAtom destinationAtom = (BasicGrowthAtom) getList().nextEvent();

    if (destinationAtom == null) {
      System.out.println("error");
    }
    depositAtom(destinationAtom);

    return false;
  }
  
  private void initList(){
    BasicGrowthLattice lattice = (BasicGrowthLattice) getLattice();
    for (int i = 0; i < lattice.size(); i++) {
      IUc uc = lattice.getUc(i);
      for (int j = 0; j < uc.size(); j++) {
        BasicGrowthAtom atom = (BasicGrowthAtom) uc.getAtom(j);
        atom.probability = this.depositionRatePerSite;
        getList().addAtom(atom);
      }
    }
    System.out.println("init finished");
  }
  
  @Override
  public void reset() {
    super.reset();
    initList();
  }
}
