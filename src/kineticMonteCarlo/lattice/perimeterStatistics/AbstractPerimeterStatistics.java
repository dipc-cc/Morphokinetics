/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.diffusion.perimeterStatistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import kineticMonteCarlo.lattice.diffusion.perimeterStatistics.agAg.AgAgRawStatisticDataHopsCount1Million;

/**
 *
 * @author Nestor
 */
public abstract class AbstractPerimeterStatistics {

  protected int totalCount;
  protected Map<Integer, Map<Integer, Integer>> hopsCountMap;
  protected Map<Integer, Map<Integer, Integer>> atomsCountMap;

  public AbstractPerimeterStatistics(AbstractStatisticAtom statisticAtom, 
          AbstractStatisticsHops statisticsHops){
 
    this.totalCount = statisticAtom.getTotalCount();
    this.atomsCountMap = new HashMap();
    this.hopsCountMap = new HashMap();
    int radius = 20;

    for (int i = 0; i < statisticAtom.getLenght(); i++) {
      Map<Integer, Integer> currentRadiusCountMap = new HashMap();
      Map<Integer, Integer> currentRadiusHopMap = new HashMap();

      this.atomsCountMap.put(radius, currentRadiusCountMap);
      this.hopsCountMap.put(radius, currentRadiusHopMap);

      for (int j = 0; j < 180; j++) {
        currentRadiusCountMap.put(j, statisticAtom.getData(i, j));
        currentRadiusHopMap.put(j, statisticsHops.getData(i, j));
      }
      radius += 5;
    }
  }
          
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
