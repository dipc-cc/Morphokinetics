/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.perimeterStatistics;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nestor
 */
public abstract class AbstractPerimeterStatistics {

  protected int[] totalCount;
  private int totalCountFirst;
  protected Map<Integer, int[]> hopsCountMap;
  protected Map<Integer, int[]> atomsCountMap;
  private final int minRadius;
  
  /**
   * This constructor limits the size of the perimeter. Arbitrarily starts at radius 20 
   * and finishes at radius 125, increasing 5 by 5.
   * @param statisticAtom
   * @param statisticsHops 
   */
  public AbstractPerimeterStatistics(AbstractStatistics statisticAtom, 
          AbstractStatistics statisticsHops){
 
    this.totalCount = new int[statisticAtom.getLenght()];
            
    this.atomsCountMap = new HashMap();
    this.hopsCountMap = new HashMap();
    minRadius = 20;
    int radius = 20;
    
    totalCountFirst = statisticAtom.getTotalCount();
    for (int i = 0; i < statisticAtom.getLenght(); i++) {
      this.totalCount[i] = statisticAtom.getTotalCount(i);
      int currentRadiusAtomsCountMatrix[] = new int[181];
      int currentRadiusHopsCountMatrix[] = new int[181];

      this.atomsCountMap.put(radius, currentRadiusAtomsCountMatrix);
      this.hopsCountMap.put(radius, currentRadiusHopsCountMatrix);

      for (int j = 0; j < 180; j++) {
        currentRadiusAtomsCountMatrix[j] = statisticAtom.getData(i, j);
        currentRadiusHopsCountMatrix[j] = statisticsHops.getData(i, j);
      }
      radius += 5;
    }
  }

  @Deprecated
  public int getTotalCount() {
    return totalCountFirst;
  }
  
  public int getTotalCount(int radius) {
    return totalCount[radius - 10];
  }

  public int getAtomsCount(int radius, int offsetDegree) {
    return atomsCountMap.get(radius)[offsetDegree];
  }

  public int getHopsCount(int radius, int offsetDegree) {
    return hopsCountMap.get(radius)[offsetDegree];
  }

  /**
   * We increase the radius in 5 positions, 
   * because statistics are done with this criteria
   * @param radiusSize current radius size
   * @return next radius size (current+5)
   */
  public int getNextRadiusInSize(int radiusSize) {
    radiusSize += 5; //increase in 5
    if (radiusSize >= 125) 
      return -1;
    return radiusSize;
  }

  /**
   * Arbitrarily the minimum radius is 20
   * @return 20
   */
  public int getMinRadiusInSize() {
    return minRadius;
  }

}
