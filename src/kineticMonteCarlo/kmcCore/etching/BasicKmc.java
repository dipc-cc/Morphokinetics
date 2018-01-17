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
package kineticMonteCarlo.kmcCore.etching;

import basic.Parser;
import kineticMonteCarlo.atom.AbstractAtom;
import kineticMonteCarlo.atom.BasicAtom;
import kineticMonteCarlo.lattice.BasicLattice;
import java.util.ListIterator;
import kineticMonteCarlo.kmcCore.AbstractKmc;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class BasicKmc extends AbstractKmc {

  private double minHeight;

  public BasicKmc(Parser parser) {
    super(parser);
    setLattice(new BasicLattice(parser.getCartSizeX(), parser.getCartSizeY()));

  }

  @Override
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
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
   * This model ignores the deposition rate.
   * 
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
              getList().addDiffusionProbability(atom.getProbability());
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
        getList().addDiffusionProbability(atom.getProbability());
      }
    }
    return false;

  }
}
