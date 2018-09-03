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

import kineticMonteCarlo.process.CatalysisProcess;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisCoHoffmannSite extends CatalysisSite {
  
  private final CatalysisProcess[] processes;
  
  public CatalysisCoHoffmannSite(int id, short iHexa, short jHexa) {
    super(id, iHexa, jHexa);
    
    processes = new CatalysisProcess[20];
    for (int i = 0; i < processes.length; i++) {
      processes[i] = new CatalysisProcess();
    }
    
    setProcceses(processes);
  }
}
