/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez, E. Sanchez
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

import basic.Parser;
import java.util.logging.Level;
import java.util.logging.Logger;
import kineticMonteCarlo.kmcCore.growth.AbstractGrowthKmc;
import kineticMonteCarlo.lattice.AbstractLattice;
import utils.list.AbstractList;
import utils.list.BinnedList;
import utils.list.LinearList;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez, E. Sanchez
 */
public abstract class AbstractKmc implements IKmc {

  private final AbstractList list;
  private AbstractLattice lattice;
  private int iterationsForLastSimulation;
  
  public AbstractKmc(Parser parser) {
    switch (parser.getListType()) {
      case "linear":
        list = new LinearList(parser);
        break;
      case "binned":
        list = new BinnedList(parser, parser.getBinsLevels(), parser.getExtraLevels());
        break;
      default:
        System.err.println("listType is not properly set");
        System.err.println("listType currently is " + parser.getListType());
        System.err.println("Available options are \"linear\" and \"binned\" ");
        list = null;
    }
  }
  
  @Override
  public long getSimulatedSteps() {
    return iterationsForLastSimulation;
  }
  
  @Override
  public double getTime() {
    return list.getTime();
  }

  /**
   * @return the lattice
   */
  @Override
  public AbstractLattice getLattice() {
    return lattice;
  }

  /**
   * @param lattice the lattice to set
   */
  public final void setLattice(AbstractLattice lattice) {
    this.lattice = lattice;
  }

  /**
   * @return the list
   */
  @Override
  public final AbstractList getList() {
    return list;
  }
  
  /**
   * Does the actual simulation. It has to be called after reset() and depositSeed().
   *
   * @return number of iterations that simulation took
   */
  @Override
  public int simulate() {
    iterationsForLastSimulation = 0;
    while (!performSimulationStep()) {
      iterationsForLastSimulation++;
      if (getLattice().isPaused()) {
        try {
          Thread.sleep(250);
        } catch (InterruptedException ex) {
          Logger.getLogger(AbstractGrowthKmc.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    return iterationsForLastSimulation;
  }

  /**
   * Does the actual simulation. It has to be called after reset() and depositSeed().
   * @param endtime 
   */
  @Override
  public void simulate(double endtime) {
    iterationsForLastSimulation = 0;
    while (getTime() < endtime) {
      if (performSimulationStep()) {
        break;
      }
      iterationsForLastSimulation++;
    }
  }

  /**
   * Does the actual simulation. It has to be called after reset() and depositSeed().
   * @param iterations 
   * @return number of iterations that simulation took
   */
  @Override
  public int simulate(int iterations) {
    iterationsForLastSimulation = 0;
    for (int i = 0; i < iterations; i++) {
      if (performSimulationStep()) {
        break;
      }
      iterationsForLastSimulation++;
    }
    
    list.cleanup();

    if (iterationsForLastSimulation == iterations ) {
      System.out.println("Too many simulation steps. Simulation steps: "+iterationsForLastSimulation);
      return -1;
    }
    return iterationsForLastSimulation;
  }

  /**
   * Initialises the rates of the simulation. It has to be called once, and only once (not within a loop).
   * @param rates 
   */
  @Override
  public abstract void initialiseRates(double[] rates);

  /**
   * Resets the lattice and the list of atoms. This method has to be called just before
   * depositSeed() and simulate().
   */
  @Override
  public void reset() {
    lattice.reset();
    list.reset();
  }
  
  /**
   * Performs a simulation step.
   * @return true if a stop condition happened (all atom etched, all surface covered)
   */
  protected abstract boolean performSimulationStep();

}
