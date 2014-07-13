/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.KMC_core;

import Kinetic_Monte_Carlo.lattice.Abstract_lattice;
import Kinetic_Monte_Carlo.list.Abstract_list;

/**
 *
 * @author Nestor
 */
public interface IKMC {
    
    
    public void initializeRates(double[] rates);
    

    public Abstract_lattice getLattice();
       
    public void simulate();

    public void simulate(double endtime) ;

    public void simulate(int iterations) ;
    
    public Abstract_list getSurfaceList();
    
    public double getTime();
    
    public int getIterations();
    
    /**
     * Returns a sampled topological measurement of the KMC surface
     * @param surface destination array.
     */
    public void getSampledSurface(float[][] surface);
    
    
    
}
