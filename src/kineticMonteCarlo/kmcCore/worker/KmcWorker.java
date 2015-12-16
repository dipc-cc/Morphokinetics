/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.worker;

import kineticMonteCarlo.kmcCore.AbstractKmc;
import kineticMonteCarlo.lattice.AbstractLattice;
import utils.list.AbstractList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Nestor
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
    this.active = true;
  }

  public void destroyWorker() {
    active = false;
    performSimulationLock.release();
    kmc = null;
  }

  public AbstractKmc getKmc() {
    return kmc;
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
          } while (kmc.getIterations() == intervalSteps);
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

  public int getWorkerId() {
    return workerId;
  }

  public void initialize(double[] rates) {
    try {
      this.receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    this.kmc.reset();
    this.kmc.initialiseRates(rates);
    this.kmc.depositSeed();
    this.receiveCommandsLock.release();
  }

  public AbstractLattice getLattice() {
    return this.kmc.getLattice();
  }

  public void simulate(IFinishListener toAdd, int workID) {
    try {
      this.receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    this.simulationType = "until_finish";
    this.finishListener = toAdd;
    this.workId = workID;
    this.performSimulationLock.release();
  }

  public void simulate(double endtime, IFinishListener toAdd, int workID) {
    try {
      this.receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    this.simulationType = "by_time";
    this.finishListener = toAdd;
    this.workId = workID;
    this.performSimulationLock.release();
  }

  public void simulate(int iterations, IFinishListener toAdd, int workID) {
    try {
      this.receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    this.simulationType = "by_steps";
    this.finishListener = toAdd;
    this.workId = workID;
    this.iterations = iterations;
    this.performSimulationLock.release();
  }

  public void simulate(IIntervalListener toAdd, IFinishListener toFinish, int intervalSteps, int workID) {
    try {
      this.receiveCommandsLock.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }
    this.simulationType = "by_intervals";
    this.intervalListener = toAdd;
    this.finishListener = toFinish;
    this.workId = workID;
    this.intervalSteps = intervalSteps;
    performSimulationLock.release();
  }

  public AbstractList getSurfaceList() {
    return this.kmc.getList();
  }

  public double getTime() {
    return this.kmc.getTime();
  }

  public int getIterations() {
    return this.kmc.getIterations();
  }

  public float[][] getSampledSurface(int binX, int binY) {
    return this.kmc.getSampledSurface(binX, binY);
  }
}
