/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.worker;

import Kinetic_Monte_Carlo.KMC_core.Abstract_KMC;
import Kinetic_Monte_Carlo.lattice.Abstract_lattice;
import Kinetic_Monte_Carlo.list.Abstract_list;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Nestor
 */
public class KMC_worker extends Thread {

    private Abstract_KMC KMC;
    private int workerID;
    private int workID;
    private boolean active = true;
    private Semaphore receiveCommandsLock = new Semaphore(0);
    private Semaphore performSimulationLock = new Semaphore(0);
    private IFinish_listener finishListener;
    private IInterval_listener intervalListener;
    private int interval_steps;
    private int iterations;
    private double endTime;
    private String simulation_type = "";

    public KMC_worker(Abstract_KMC KMC, int workerID) {
        this.KMC = KMC;
        this.workerID = workerID;
        this.active = true;
    }

    public void destroy() {
        active = false;
        performSimulationLock.release();
        KMC = null;
    }

    public Abstract_KMC getKMC() {
        return KMC;
    }

    @Override
    public void run() {

        while (active) {

            switch (simulation_type) {

                case "by_time":
                    KMC.simulate(endTime);
                    break;
                case "by_steps":
                    KMC.simulate(iterations);
                    break;
                case "until_finish":
                    KMC.simulate();
                    break;
                case "by_intervals":
                    do {
                        KMC.simulate(interval_steps);
                        intervalListener.handleSimulationIntervalFinish(workerID,workID);
                    } while (KMC.getIterations() == interval_steps);
                    break;
                default:
                    break;
            }

            receiveCommandsLock.release();
            
            if (!"".equals(simulation_type)) {
                finishListener.handleSimulationFinish(workerID,workID);
            }
            
            
            try {
                performSimulationLock.acquire();
            } catch (InterruptedException e) {
            }
        }
    }

    public int getID() {
        return workerID;
    }

    public void initialize(double[] rates) {
        try {
            this.receiveCommandsLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        this.KMC.initializeRates(rates);
        this.receiveCommandsLock.release();
    }

    public Abstract_lattice getLattice() {
        return this.KMC.getLattice();
    }

    public void simulate(IFinish_listener toAdd,int workID) {
        try {
            this.receiveCommandsLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        this.simulation_type = "until_finish";
        this.finishListener = toAdd;
        this.workID=workID;
        this.performSimulationLock.release();
    }

    public void simulate(double endtime, IFinish_listener toAdd,int workID) {
        try {
            this.receiveCommandsLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        this.simulation_type = "by_time";
        this.finishListener = toAdd;
        this.workID=workID;
        this.performSimulationLock.release();
    }

    public void simulate(int iterations, IFinish_listener toAdd,int workID) {
        try {
            this.receiveCommandsLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        this.simulation_type = "by_steps";
        this.finishListener = toAdd;
        this.workID=workID;
        this.iterations=iterations;
        this.performSimulationLock.release();
    }

    public void simulate(IInterval_listener toAdd,IFinish_listener toFinish, int interval_steps,int workID) {
        try {
            this.receiveCommandsLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        this.simulation_type = "by_intervals";
        this.intervalListener = toAdd;
        this.finishListener = toFinish;
        this.workID=workID;
        this.interval_steps = interval_steps;
        performSimulationLock.release();
    }

    public Abstract_list getSurfaceList() {
        return this.KMC.getSurfaceList();
    }

    public double getTime() {
        return this.KMC.getTime();
    }

    public int getIterations() {
        return this.KMC.getIterations();
    }

    public void getSampledSurface(float[][] surface) {
        this.KMC.getSampledSurface(surface);
    }
}
