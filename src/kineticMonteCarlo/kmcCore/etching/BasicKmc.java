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

/**
 *
 * @author Nestor
 */
public class BasicKmc extends AbstractEtchingKmc {

  private double minHeight;

  public BasicKmc(ListConfiguration listCconfig, int sizeX, int sizeY, boolean randomise) {
    super(listCconfig, randomise);
    lattice = new BasicLattice(sizeX, sizeY);

  }

  /**
   * This model ignores the deposition rate
   * @param rates
   */ 
  @Override
  public void initializeRates(double[] rates) {

    lattice.setProbabilities(rates);
    list.reset();
    lattice.reset();
    minHeight = 4;

    for (int i = 0; i < lattice.getHexaSizeI(); i++) {
      for (int j = 0; j < lattice.getHexaSizeJ(); j++) {
        for (int k = 0; k < lattice.getHexaSizeK(); k++) {
          for (int l = 0; l < lattice.getSizeUC(); l++) {

            BasicAtom atom = (BasicAtom) lattice.getAtom(i, j, k, l);
            if (atom.getType() < 4 && atom.getType() > 0 && !atom.isRemoved()) {
              list.addAtom(atom);
            }

          }
        }
      }
    }
  }

  @Override
  protected boolean performSimulationStep() {

    BasicAtom atom = (BasicAtom) list.nextEvent(RNG);
    if (atom.getY() > lattice.getHexaSizeJ() - minHeight) {

      return true;
    }

    atom.remove();
    atom.setOnList(null);
    for (int k = 0; k < 4; k++) {
      if (atom.getHeighbor(k).getType() == 3) {
        list.addAtom(atom.getHeighbor(k));
      }
    }
    return false;

  }

  @Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];

    double scaleX = binX / (float) lattice.getHexaSizeI();
    ListIterator<AbstractAtom> iterator = list.getIterator();

    while (iterator.hasNext()) {
      BasicAtom atom = (BasicAtom) iterator.next();
      int sampledPosX = (int) (atom.getX() * scaleX);
      if (surface[0][sampledPosX] < atom.getY()) {
        surface[0][sampledPosX] = atom.getY();
      }
    }
    return surface;
  }
}
