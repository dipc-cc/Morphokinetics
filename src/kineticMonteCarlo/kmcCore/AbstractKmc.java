/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.kmcCore;

import kineticMonteCarlo.list.AbstractList;
import kineticMonteCarlo.list.ListConfiguration;
import utils.edu.cornell.lassp.houle.rngPack.Ranecu;

/**
 *
 * @author Nestor
 */
public abstract class AbstractKmc implements IKmc {

    
    protected AbstractList list;
    protected static Ranecu RNG;
    protected int iterations_for_last_simulation;
    

    public AbstractKmc(ListConfiguration config) {
        RNG = new Ranecu(System.nanoTime());
        list=config.create_list();
    }

    
    @Override
    public abstract void initializeRates(double[] rates);
    
    
    //returns true if a stop condition happened (all atom etched, all surface covered)
    protected abstract boolean perform_simulation_step();
    
    @Override
    public int getIterations() {
       return iterations_for_last_simulation;
    } 
    
    @Override
    public void simulate() {
        iterations_for_last_simulation=0;
        while (!perform_simulation_step()) iterations_for_last_simulation++;
            
        
    }

    @Override
    public void simulate(double endtime) {
        iterations_for_last_simulation=0;
        while (list.getTime() < endtime) {
            if (perform_simulation_step()) break;
            iterations_for_last_simulation++;
        }
    }

 
    public void simulate(int iterations) {
      
        iterations_for_last_simulation=0;
        for (int i = 0; i < iterations; i++) {
            if (perform_simulation_step()) break;
            iterations_for_last_simulation++;      
        } 
        
        list.cleanup();
    }

    
    @Override
    public AbstractList getSurfaceList() {
        return list;
    }

    @Override
    public double getTime() {
        return list.getTime();
    }
    
}
