/* 
 * Copyright (C) 2018 K. Valencia, J. Alberdi-Rodriguez
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

import kineticMonteCarlo.process.CatalysisProcess;
import utils.StaticRandom;

/**
 *
 * @author J. Alberdi-Rodriguez
 */


public class CatalysisAmmoniaSite extends CatalysisSite  {

  public static final byte NH3 = 0;
  public static final byte NH2 = 1;
  public static final byte NH  = 2;
  public static final byte NO  = 3;
  public static final byte N   = 4;
  public static final byte O   = 5;
  public static final byte OH  = 6;
  
  public static final byte H2O = 7;
  public static final byte O2  = 8;
  public static final byte N2  = 9;
  public static final byte VAC  = 10;
  private final CatalysisProcess[] processes;
  
  public CatalysisAmmoniaSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa);
    processes = new CatalysisProcess[13];
    for (int i = 0; i < processes.length; i++) {
      processes[i] = new CatalysisProcess();
      
    }
    setProcceses(processes);
  }
  
  /**
   * CUS positions are only allowed. Tells when current atom is completely surrounded (north and south).
   * 
   * @return true if it has north and south occupied neighbours.
   */
  @Override
  public boolean isIsolated() {
    if (getLatticeSite() == CUS) {
      boolean result = getNeighbour(0).isOccupied() && getNeighbour(2).isOccupied();
      return result;
    } else {
      return false;
    }
  }
  @Override
  public CatalysisAmmoniaSite getNeighbour(int pos) {
    return (CatalysisAmmoniaSite) super.getNeighbour(pos);
  }
  
  @Override
  public CatalysisSite getRandomNeighbour(byte process) {
    CatalysisSite neighbour;
    double randomNumber = StaticRandom.raw() * getRate(process);
    double sum = 0.0;
    for (int j = 0; j < getNumberOfNeighbours(); j++) {
      sum += processes[process].getEdgeRate(j);
      if (sum > randomNumber) {
        neighbour = getNeighbour(j);
        return neighbour;
      }
    }
    // raise an error
    return null;
  }
}
