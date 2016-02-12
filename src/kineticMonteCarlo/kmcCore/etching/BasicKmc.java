/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.etching;

import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.BasicAtom;
import kineticMonteCarlo.lattice.BasicLattice;
import utils.list.ListConfiguration;
import java.util.ListIterator;
import kineticMonteCarlo.kmcCore.AbstractKmc;

/**
 *
 * @author Nestor
 */
public class BasicKmc extends AbstractKmc {

  private double minHeight;

  public BasicKmc(ListConfiguration listCconfig, int sizeX, int sizeY, boolean randomise) {
    super(listCconfig);
    setLattice(new BasicLattice(sizeX, sizeY));

  }

  /**
   * This model ignores the deposition rate.
   * @param rates
   */ 
  @Override
  public void initialiseRates(double[] rates) {
    getLattice().setProbabilities(rates);
    minHeight = 4;
  }
    
  @Override
  public void depositSeed() {
    for (int i = 0; i < getLattice().getHexaSizeI(); i++) {
      for (int j = 0; j < getLattice().getHexaSizeJ(); j++) {
        for (int k = 0; k < getLattice().getHexaSizeK(); k++) {
          for (int l = 0; l < getLattice().getUnitCellSize(); l++) {
            BasicAtom atom = (BasicAtom) getLattice().getAtom(i, j, k, l);
            if (atom.getType() < 4 && atom.getType() > 0 && !atom.isRemoved()) {
              getList().addAtom(atom);
              getList().addTotalProbability(atom.getProbability());
            }

          }
        }
      }
    }
  }

  @Override
  protected boolean performSimulationStep() {
    BasicAtom atom = (BasicAtom) getList().nextEvent();
    if (atom == null) return false;
    if (atom.getY() > getLattice().getHexaSizeJ() - minHeight) {
      return true;
    }

    atom.remove();
    atom.setList(null);
    for (int k = 0; k < 4; k++) {
      if (atom.getNeighbour(k).getType() == 3) {
        getList().addAtom(atom.getNeighbour(k));
        getList().addTotalProbability(atom.getProbability());
      }
    }
    return false;

  }

  @Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];

    double scaleX = binX / (float) getLattice().getHexaSizeI();
    ListIterator<AbstractAtom> iterator = getList().getIterator();

    while (iterator.hasNext()) {
      BasicAtom atom = (BasicAtom) iterator.next();
      int sampledPosX = (int) (atom.getX() * scaleX);
      if (surface[0][sampledPosX] < atom.getY()) {
        surface[0][sampledPosX] = atom.getY();
      }
    }
    return surface;
  }

  /**
   * Number of islands has no sense in etching.
   * @return -1 always
   */
  @Override
  public int getIslandCount() {
    return -1;
  }
  
  /**
   * Does nothing
   *
   * @param inputArea
   * @param scale
   * @return just in case, the input area
   */
  @Override
  public float[][] increaseEmptyArea(float[][] inputArea, double scale) {
    return inputArea;
  }
}
