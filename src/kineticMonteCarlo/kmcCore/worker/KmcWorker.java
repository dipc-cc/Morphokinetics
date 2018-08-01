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
package kineticMonteCarlo.kmcCore.worker;

import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.lattice.AbstractLattice;
import utils.list.AbstractList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public class KmcWorker extends Thread {

  private AbstractKmc kmc;
  private int workerId;
  private int workId;
  private boolean active = true;
  private Semaphore receiveCommandsLock = new Semaphore(0);
  private Semaphore performSimulationLock = new Semaphore(0);
  private IFinishListener finishListener;
  private IIntervalListener intervalListener;
  private int intervalSteps;
  private int iterations;
  private double endTime;
  private String simulationType = "";

  public KmcWorker(AbstractKmc kmc, int workerId) {
    this.kmc = kmc;
    this.workerId = workerId;
    active = true;
  }

  public AbstractKmc getKmc() {
    return kmc;
  }

  public int getWorkerId() {
    return workerId;
  }
  
  public double getTime() {
    return kmc.getTime();
  }

  public long getIterations() {
    return kmc.getSimulatedSteps();
  }

  public float[][] getSampledSurface(int binX, int binY) {
    return kmc.getSampledSurface(binX, binY);
  }
  
  public AbstractList getSurfaceList() {
    return kmc.getList();
  }

  public AbstractLattice getLattice() {
    return kmc.getLattice();
  }

  public void initialise(double[] rates) {
    try {
      receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    kmc.reset();
    kmc.initialiseRates(rates);
    kmc.depositSeed();
    receiveCommandsLock.release();
  }

  public void destroyWorker() {
    active = false;
    performSimulationLock.release();
    kmc = null;
  }
  
  @Override
  public void run() {

    while (active) {

      switch (simulationType) {

        case "by_time":
          kmc.simulate(endTime);
          break;
        case "by_steps":
          kmc.simulate(iterations);
          break;
        case "until_finish":
          kmc.simulate();
          break;
        case "by_intervals":
          do {
            kmc.reset();
            kmc.depositSeed();
            kmc.simulate(intervalSteps);
            intervalListener.handleSimulationIntervalFinish(workerId, workId);
          } while (kmc.getSimulatedSteps()== intervalSteps);
          break;
        default:
          break;
      }

      receiveCommandsLock.release();

      if (!"".equals(simulationType)) {
        finishListener.handleSimulationFinish(workerId, workId);
      }

      try {
        performSimulationLock.acquire();
      } catch (InterruptedException e) {
      }
    }
  }

  public void simulate(IFinishListener toAdd, int workID) {
    try {
      receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    simulationType = "until_finish";
    finishListener = toAdd;
    workId = workID;
    performSimulationLock.release();
  }

  public void simulate(double endtime, IFinishListener toAdd, int workID) {
    try {
      receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    simulationType = "by_time";
    finishListener = toAdd;
    workId = workID;
    performSimulationLock.release();
  }

  public void simulate(int iterations, IFinishListener toAdd, int workID) {
    try {
      receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    simulationType = "by_steps";
    finishListener = toAdd;
    workId = workID;
    this.iterations = iterations;
    performSimulationLock.release();
  }

  public void simulate(IIntervalListener toAdd, IFinishListener toFinish, int intervalSteps, int workId) {
    try {
      receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    simulationType = "by_intervals";
    intervalListener = toAdd;
    finishListener = toFinish;
    this.workId = workId;
    this.intervalSteps = intervalSteps;
    performSimulationLock.release();
  }
}
