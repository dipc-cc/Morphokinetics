/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion.perimeterStatistics;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public abstract class AbstractPerimeterStatistics {

    protected int totalCount;
    protected Map<Integer, Map<Integer, Integer>> hopsCountMap;
    protected Map<Integer, Map<Integer, Integer>> atomsCountMap;

    public int getTotalCount() {
        return totalCount;
    }

    public int getAtomsCount(int radius, int offsetDegree) {
        return atomsCountMap.get(radius).get(offsetDegree);
    }

    public int getHopsCount(int radius, int offsetDegree) {
        
        return hopsCountMap.get(radius).get(offsetDegree);
    }

    public int getNextRadiusInSize(int radiusSize) {

        Iterator<Integer> it = atomsCountMap.keySet().iterator();
        int radius = -1;
        while (it.hasNext()) {
            int value = it.next();
            if (value > radiusSize && (value < radius || radius == -1)) {
                radius = value;
            }
        }
        return radius;
    }
    
    public int getMinRadiusInSize() {

        Iterator<Integer> it = atomsCountMap.keySet().iterator();
        int radius = -1;
        while (it.hasNext()) {
            int value = it.next();
            if ((value < radius || radius == -1)) {
                radius = value;
            }
        }
        return radius;
    }
    
}
