/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator;

/**
 *
 * @author Nestor
 */
public class DevitaHopsConfig {
    
    private int max_accumulated_steps = 100;
    private int min_accumulated_steps = 30;
    private int max_distance_hops = 5;
    private int min_distance_hops = 1;

    public int getMax_accumulated_steps() {
        return max_accumulated_steps;
    }

    public DevitaHopsConfig setMax_accumulated_steps(int max_accumulated_steps) {
        this.max_accumulated_steps = max_accumulated_steps;
        return this;
    }

    public int getMin_accumulated_steps() {
        return min_accumulated_steps;
    }

    public DevitaHopsConfig setMin_accumulated_steps(int min_accumulated_steps) {
        this.min_accumulated_steps = min_accumulated_steps;
        return this;
    }

    public int getMax_distance_hops() {
        return max_distance_hops;
    }

    public DevitaHopsConfig setMax_distance_hops(int max_distance_hops) {
        this.max_distance_hops = max_distance_hops;
        return this;
    }

    public int getMin_distance_hops() {
        return min_distance_hops;
    }

    public DevitaHopsConfig setMin_distance_hops(int min_distance_hops) {
        this.min_distance_hops = min_distance_hops;
        return this;
    }
    
    
    
}
