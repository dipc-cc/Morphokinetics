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

import kineticMonteCarlo.site.CatalysisCoHoffmannSite;
import kineticMonteCarlo.site.CatalysisSite;

/**
 * Algorithm from "kmos: A lattice kinetic Monte Carlo framework". M.J.
 * Hoffmann, S. Matera, K. Reuter. Computer Physics Communications 185(2014)
 * 2138 - 2150
 * 
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisCoHoffmannLattice extends CatalysisCoLattice {
  
  static public final int N_REACT = 20;
  private final float hexaArea;
  
  private final int[][] availableSitesA;
  private final int[][] inverseAvailableSitesI;
  private final int[] numberOfSites; // Nr. of sites N^avail_a
  
  public CatalysisCoHoffmannLattice(int hexaSizeI, int hexaSizeJ, String ratesLibrary) {
    super(hexaSizeI, hexaSizeJ, ratesLibrary);
    hexaArea = (float) hexaSizeI * hexaSizeJ;
    availableSitesA = new int[N_REACT][(int) hexaArea];
    inverseAvailableSitesI = new int[N_REACT][(int) hexaArea];
    for (int i = 0; i < availableSitesA.length; i++) {
      for (int j = 0; j < availableSitesA[0].length; j++) {
        availableSitesA[i][j] = -1;
        inverseAvailableSitesI[i][j] = -1;
      }
    }
    numberOfSites = new int[N_REACT];
  }
  
  public void insert(int id, int proc) {
    availableSitesA[proc][numberOfSites[proc]] = id;
    inverseAvailableSitesI[proc][id] = numberOfSites[proc];
    numberOfSites[proc]++;
  }
  
  public void remove(int id, int proc) {
    numberOfSites[proc]--;
    int posA = inverseAvailableSitesI[proc][id];
    int idI = availableSitesA[proc][numberOfSites[proc]];
    availableSitesA[proc][posA] = idI;
    availableSitesA[proc][numberOfSites[proc]] = -1; // disable last position
    inverseAvailableSitesI[proc][id] = -1; // disable it
    inverseAvailableSitesI[proc][idI] = posA;
  }
  
  public CatalysisSite getAvailableSite(int proc, int random) {
    int id = availableSitesA[proc][random];
    return getUc(id).getSite(0);
  }
  
  @Override
  CatalysisCoHoffmannSite newAtom(int i, int j) {
    return new CatalysisCoHoffmannSite(createId(i, j), (short) i, (short) j);
  }
}
