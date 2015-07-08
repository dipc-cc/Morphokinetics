/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore.worker;

/**
 *
 * @author Nestor
 */
public interface IIntervalListener {
    
            public void handleSimulationIntervalFinish(int workerID,int workID);
    
}
