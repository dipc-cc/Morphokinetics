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
import kineticMonteCarlo.atom.SiSite;
import kineticMonteCarlo.atom.AbstractSite;
import kineticMonteCarlo.lattice.SiLattice;
import java.util.ListIterator;
import kineticMonteCarlo.kmcCore.AbstractKmc;
import utils.MathUtils;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class SiKmc extends AbstractKmc {

  private final double minHeight;

  public SiKmc(Parser parser) {
    super(parser);
    setLattice(new SiLattice(parser.getMillerX(), parser.getMillerY(), parser.getMillerZ(), parser.getCartSizeX(), parser.getCartSizeY(), parser.getCartSizeZ()));
    
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
  public float[][] getHexagonalPeriodicSurface(int binX, int binY) {
    return getSampledSurface(binX, binY);
  }
  
  @Override
  public float[][] getSampledSurface(int binX, int binY) {
    float[][] surface = new float[binX][binY];
    
    double scaleX = binX / (getLattice().getHexaSizeI() * ((SiLattice) getLattice()).getUnitCell().getLimitX());
    double scaleY = binY / (getLattice().getHexaSizeJ() * ((SiLattice) getLattice()).getUnitCell().getLimitY());
    ListIterator<AbstractSite> iterator = getList().getIterator();

    for (int i = 0; i < binY; i++) {
      for (int j = 0; j < binX; j++) {
        surface[i][j] = 0;
      }
    }

    while (iterator.hasNext()) {
      SiSite atom = (SiSite) iterator.next();
      int sampledPosX = (int) MathUtils.truncate(atom.getX() * scaleX, 3);
      if (sampledPosX == binX) {
        sampledPosX--;
      }
      int sampledPosY = (int) MathUtils.truncate(atom.getY() * scaleY, 3);
      if (sampledPosY == binY) {
        sampledPosY--;
      }

      if (surface[sampledPosY][sampledPosX] == 0 || surface[sampledPosY][sampledPosX] < atom.getZ()) {
        surface[sampledPosY][sampledPosX] = (float) atom.getZ();
      }
    }

    MathUtils.fillSurfaceHoles(surface);
    return surface;
  }

  @Override
  public void depositSeed() {
    for (int i = 0; i < getLattice().getHexaSizeI(); i++) {
      for (int j = 0; j < getLattice().getHexaSizeJ(); j++) {
        for (int k = 0; k < getLattice().getHexaSizeK(); k++) {
          for (int l = 0; l < getLattice().getUnitCellSize(); l++) {
            SiSite atom = (SiSite) getLattice().getSite(i, j, k, l);
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
    SiSite atom = (SiSite) getList().nextEvent();
    // I am not sure that the next line is correct (Joseba). However,
    if (atom == null) return false; // next even can be null and we should be ready to handle this
    double probabilityChange = atom.remove();
    getList().addDiffusionProbability(probabilityChange);
    atom.setList(null);
    for (int k = 0; k < 4; k++) {
      SiSite neighbour = atom.getNeighbour(k);
      if (neighbour.getN1() == 3) {
        getList().addAtom(neighbour);
        //getList().addTotalProbability(atom.getProbability());
      }
    }
    return atom.getZ() < minHeight * 2;
  }
}
