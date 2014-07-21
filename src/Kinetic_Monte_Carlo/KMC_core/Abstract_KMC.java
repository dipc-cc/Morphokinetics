/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core;

import Kinetic_Monte_Carlo.list.Abstract_list;
import Kinetic_Monte_Carlo.list.List_configuration;
import utils.edu.cornell.lassp.houle.RngPack.Ranecu;

/**
 *
 * @author Nestor
 */
public abstract class Abstract_KMC implements IKMC {

    
    protected Abstract_list list;
    protected static Ranecu RNG;
    protected int iterations_last_simulation;
    

    public Abstract_KMC(List_configuration config) {
        RNG = new Ranecu(System.nanoTime());
        list=config.create_list();
    }

    
    @Override
    public abstract void initializeRates(double[] rates);
    
    
    //returns true if a stop condition happened (all atom etched, all surface covered)
    protected abstract boolean perform_simulation_step();
    
    @Override
    public int getIterations() {
       return iterations_last_simulation;
    } 
    
    @Override
    public void simulate() {
        iterations_last_simulation=0;
        while (!perform_simulation_step()) iterations_last_simulation++;
            
        
    }

    @Override
    public void simulate(double endtime) {
        iterations_last_simulation=0;
        while (list.getTime() < endtime) {
            if (perform_simulation_step()) break;
            iterations_last_simulation++;
        }
    }

    @Override
    public void simulate(int iterations) {
      
        iterations_last_simulation=0;
        for (int i = 0; i < iterations; i++) {
            if (perform_simulation_step()) break;
            iterations_last_simulation++;      
        } 
        
        list.cleanup();
    }

    
    @Override
    public Abstract_list getSurfaceList() {
        return list;
    }

    @Override
    public double getTime() {
        return list.getTime();
    }
    
}
