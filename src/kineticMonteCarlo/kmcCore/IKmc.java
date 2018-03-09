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
package kineticMonteCarlo.kmcCore;

import kineticMonteCarlo.lattice.AbstractLattice;
import utils.list.AbstractList;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public interface IKmc {

  public int getIterations();

  public double getTime();

  /**
   * Returns a sampled topological measurement of the KMC surface
   *
   * @param binX
   * @param binY
   * @return calculated surface
   */
  public float[][] getSampledSurface(int binX, int binY);

  public int simulate();

  public void simulate(double endtime);

  public int simulate(int iterations);
    
  public void initialiseRates(double[] rates);
  
  public void reset();
  
  public void depositSeed();

  public AbstractLattice getLattice();

  public AbstractList getList();
}
