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
package kineticMonteCarlo.activationEnergy;

import basic.Parser;
import java.io.PrintWriter;
import kineticMonteCarlo.lattice.AbstractGrowthLattice;


/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class BdaActivationEnergy extends ActivationEnergy {
  private double histogramPossibles;

  public BdaActivationEnergy(Parser parser) {
    super(parser);
  }
    
  /**
   * Computes possibles for catalysis for all kind of processes
   *
   * @param lattice
   * @param elapsedTime
   * @param stationary
   */
  //@Override
  public void updatePossibles(AbstractGrowthLattice lattice, double elapsedTime, boolean stationary) {
    int monomers = lattice.getMonomerCount();
    histogramPossibles += monomers * elapsedTime;
  }
  
  @Override
  public void reset() {
    super.reset();
    histogramPossibles = 0;
  }
  
  @Override
  void printPossibles(PrintWriter print, double time) {
    print.print(time + "\t");
    print.print(histogramPossibles + "\t");
  }
  
  @Override
  public void printAe(PrintWriter print[], double time) {
    print[0].print(time + "\t");
    print[0].print(histogramPossibles);
    print[0].println();
    print[0].flush();
  }
}
