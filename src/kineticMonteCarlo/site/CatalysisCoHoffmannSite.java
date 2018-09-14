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
package kineticMonteCarlo.site;

import static kineticMonteCarlo.lattice.CatalysisCoHoffmannLattice.N_REACT;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisCoHoffmannSite extends AbstractCatalysisSite {
  
  //private final CatalysisProcess[] processes;
  private final boolean[] activeProcesses;
  
  public CatalysisCoHoffmannSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa);
    
    activeProcesses = new boolean[N_REACT];
    for (int i = 0; i < activeProcesses.length; i++) {
      activeProcesses[i] = false;
    }
  }

  /**
   * They do nothing.
   */
  @Override
  public void cleanCoCusNeighbours() {
  }

  @Override
  public void addCoCusNeighbours(int value) {
  }
  
  @Override
  public void setOnList(byte process, boolean onList) {
    activeProcesses[process] = onList;
  }
  
  @Override
  public boolean isOnList(byte process) {
    return activeProcesses[process];
  }
}
