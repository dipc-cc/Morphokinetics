/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kinetic_Monte_Carlo.atom.diffusion.DevitaAccelerator;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public class Hops_per_step {

    private final Map<Integer, Integer> desired_hop_distances_map = new HashMap();

    public int getDistancePerStep(int sourceType, int destinationType) {

        if (sourceType != destinationType) {
            return 1;
        }
        Integer hops_per_steps=desired_hop_distances_map.get(sourceType);
        
        if (hops_per_steps!=null) {
            return hops_per_steps;
        } else {
            return 1;
        }
    }

    public void setDistancePerStep(int sourceType, int hops_per_step) {

        desired_hop_distances_map.put(sourceType, hops_per_step);
    }
    
    public boolean isAccelerationEnabled(int sourceType){
        return desired_hop_distances_map.containsKey(sourceType);
    }

}
