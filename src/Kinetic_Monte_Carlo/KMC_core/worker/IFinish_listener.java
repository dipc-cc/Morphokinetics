/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core.worker;

/**
 *
 * @author Nestor
 */
public interface IFinish_listener {
    
            public void handleSimulationFinish(int workerID,int workID);
    
}
