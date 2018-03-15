/*
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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
package kineticMonteCarlo.lattice;

import kineticMonteCarlo.site.CatalysisSite;
import static kineticMonteCarlo.site.CatalysisSite.CO;
import static kineticMonteCarlo.site.CatalysisSite.CUS;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisCoFarkasLattice extends CatalysisCoLattice {
  
  public CatalysisCoFarkasLattice(int hexaSizeI, int hexaSizeJ, String ratesLibrary) {
    super(hexaSizeI, hexaSizeJ, ratesLibrary);
  }
  
  /**
   * Check whether two CO^CUS atoms are together. Only for Farkas
   * 
   * @param atom
   */
  @Override
  void updateCoCus(CatalysisSite atom) {
    if (atom.isOccupied() && atom.getLatticeSite() == CUS && atom.getType() == CO) {
      atom.cleanCoCusNeighbours();
      for (int i = 0; i < atom.getNumberOfNeighbours(); i += 2) { // Only up and down neighbours
        CatalysisSite neighbour = atom.getNeighbour(i);
        if (neighbour.isOccupied() && neighbour.getType() == CO) {
          atom.addCoCusNeighbours(1);
        }
      }
    }
  }
}
