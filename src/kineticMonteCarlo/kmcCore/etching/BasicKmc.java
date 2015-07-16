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

  public BasicKmc(ListConfiguration listCconfig, int sizeX, int sizeY) {
    super(listCconfig);
    lattice = new BasicLattice(sizeX, sizeY);

  }

  //this models ignore the deposition rate
  @Override
  public void initializeRates(double[] rates) {

    lattice.setProbabilities(rates);
    list.reset();
    lattice.reset();
    minHeight = 4;

    for (int i = 0; i < lattice.getSizeX(); i++) {
      for (int j = 0; j < lattice.getSizeY(); j++) {
        for (int k = 0; k < lattice.getSizeZ(); k++) {
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
    if (atom.getY() > lattice.getSizeY() - minHeight) {

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
  public void getSampledSurface(float[][] surface) {
    int binY = surface.length;
    int binX = surface[0].length;

    double scaleX = binX / (float) lattice.getSizeX();
    ListIterator<AbstractAtom> iterator = list.getIterator();

    while (iterator.hasNext()) {
      BasicAtom atom = (BasicAtom) iterator.next();
      int sampledPosX = (int) (atom.getX() * scaleX);
      if (surface[0][sampledPosX] < atom.getY()) {
        surface[0][sampledPosX] = atom.getY();
      }
    }

  }
}
