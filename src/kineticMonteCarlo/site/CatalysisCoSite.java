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
import static kineticMonteCarlo.process.CatalysisProcess.ADSORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DESORPTION;
import static kineticMonteCarlo.process.CatalysisProcess.DIFFUSION;
import static kineticMonteCarlo.process.CatalysisProcess.REACTION;

/**
 *
 * @author K. Valencia, J. Alberdi-Rodriguez
 */
public class CatalysisCoSite extends AbstractCatalysisSite {

  public static final byte CO = 0;
  public static final byte O = 1;
  public static final byte C = 0;
  public static final byte O2 = 1;

  /** Current atom is CO^CUS and it has as many CO^CUS neighbours. */
  private int coCusWithCoCus;
  
  public CatalysisCoSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa);
    CatalysisProcess[]processes = new CatalysisProcess[4];
    processes[ADSORPTION] = new CatalysisProcess();
    processes[DESORPTION] = new CatalysisProcess();
    processes[REACTION] = new CatalysisProcess();
    processes[DIFFUSION] = new CatalysisProcess();
    setProcceses(processes);
    coCusWithCoCus = 0;
  }
  
  /**
   * Tells when current atom is completely surrounded.
   * 
   * @return true if it has 4 occupied neighbours.
   */
  @Override
  public boolean isIsolated() {
    return getOccupiedNeighbours() == 4;
  }
  
  @Override
  public void addCoCusNeighbours(int value) {
    coCusWithCoCus += value;
  }

  @Override
  public void cleanCoCusNeighbours() {
    coCusWithCoCus = 0;
  }

  @Override
  public int getCoCusNeighbours() {
    return coCusWithCoCus;
  }

  @Override
  public boolean isEligible() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * Resets current atom; TERRACE type, no neighbours, no occupied, no outside and no probability.
   */
  @Override
  public void clear() {
    super.clear();
    setType(TERRACE);
  }
  
  
  @Override
  public String toString() {
    String returnString = "Atom Id " + getId();
    return returnString;
  }

  @Override
  public double getProbability() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
