/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.etching;

import kineticMonteCarlo.atom.SiAtom;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.lattice.SiLattice;
import java.util.ListIterator;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import utils.MathUtils;

/**
 *
 * @author Nestor
 */
public class SiKmc extends AbstractKmc {

  private final double minHeight;

  public SiKmc(SiKmcConfig config) {
    super(config.listConfig);
    setLattice(new SiLattice(config.millerX, config.millerY, config.millerZ, config.sizeX_UC, config.sizeY_UC, config.sizeZ_UC));

    minHeight = ((SiLattice) getLattice()).getUnitCell().getLimitZ();
  }

  /**
   * This model ignores the deposition rate.
   * @param rates
   */
  @Override
  public void initialiseRates(double[] rates) {
    getLattice().setProbabilities(rates);
  }
  
  @Override
  public void depositSeed() {
    for (int i = 0; i < getLattice().getHexaSizeI(); i++) {
      for (int j = 0; j < getLattice().getHexaSizeJ(); j++) {
        for (int k = 0; k < getLattice().getHexaSizeK(); k++) {
          for (int l = 0; l < getLattice().getUnitCellSize(); l++) {
            SiAtom atom = (SiAtom) getLattice().getAtom(i, j, k, l);
            if (atom.getN1() < 4 && atom.getN1() > 0 && !atom.isRemoved()) {
              getList().addAtom(atom);
              //getList().addTotalProbability(atom.getProbability());
            }
          }
        }
      }
    }
  }

  @Override
  protected boolean performSimulationStep() {
    SiAtom atom = (SiAtom) getList().nextEvent();
    // I am not sure that the next line is correct (Joseba). However,
    if (atom == null) return false; // next even can be null and we should be ready to handle this
    double probabilityChange = atom.remove();
    getList().addTotalProbability(probabilityChange);
    atom.setList(null);
    for (int k = 0; k < 4; k++) {
      SiAtom neighbour = atom.getNeighbour(k);
      if (neighbour.getN1() == 3) {
        getList().addAtom(neighbour);
        //getList().addTotalProbability(atom.getProbability());
      }
    }
    return atom.getZ() < minHeight * 2;
  }

  @Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];
    
    double scaleX = binX / (getLattice().getHexaSizeI() * ((SiLattice) getLattice()).getUnitCell().getLimitX());
    double scaleY = binY / (getLattice().getHexaSizeJ() * ((SiLattice) getLattice()).getUnitCell().getLimitY());
    ListIterator<AbstractAtom> iterator = getList().getIterator();

    for (int i = 0; i < binY; i++) {
      for (int j = 0; j < binX; j++) {
        surface[i][j] = 0;
      }
    }

    while (iterator.hasNext()) {
      SiAtom atom = (SiAtom) iterator.next();
      int sampledPosX = (int) MathUtils.truncate(atom.getX() * scaleX, 3);
      if (sampledPosX == binX) {
        sampledPosX--;
      }
      int sampledPosY = (int) MathUtils.truncate(atom.getY() * scaleY, 3);
      if (sampledPosY == binY) {
        sampledPosY--;
      }

      if (surface[sampledPosY][sampledPosX] == 0 || surface[sampledPosY][sampledPosX] < atom.getZ()) {
        surface[sampledPosY][sampledPosX] = atom.getZ();
      }
    }

    MathUtils.fillSurfaceHoles(surface);
    return surface;
  }
}
