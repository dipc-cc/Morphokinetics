/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.perimeterStatistics;

/**
 *
 * @author Nestor
 */
public abstract class AbstractPerimeterStatistics {

  protected int[] totalCount;
  private int[][] hopsCount;
  private int[][] reentranceCount;
  private final int minRadius;
  private final int maxRadius;
  
  /**
   * This constructor limits the size of the perimeter. Arbitrarily starts at radius 20 
   * and finishes at radius of the data, we choose to increase 5 by 5.
   * @param statisticAtom
   * @param statisticsHops 
   */
  public AbstractPerimeterStatistics(Statistics statisticAtom, 
          Statistics statisticsHops){
 
    this.totalCount = new int[statisticAtom.getRows()];
    minRadius = 10;
    maxRadius = statisticAtom.getRows() - 10;
    
    reentranceCount = new int[statisticAtom.getRows()][statisticAtom.getColumns()];
    hopsCount = new int[statisticsHops.getRows()][statisticsHops.getColumns()];
    
    for (int i = 0; i < statisticAtom.getRows(); i++) {
      this.totalCount[i] = statisticAtom.getTotalCount(i);
    }
    this.reentranceCount = statisticAtom.getWholeData();
    this.hopsCount = statisticsHops.getWholeData();
  }
  
  public int getTotalCount(int radius) {
    return totalCount[radius-10];
  }

  public int getReentranceCount(int radius, int offsetDegree) {
    return reentranceCount[radius-10][offsetDegree];
  }

  public int getHopsCount(int radius, int offsetDegree) {
    return hopsCount[radius-10][offsetDegree];
  }

  /**
   * We increase the radius in 5 positions, 
   * because statistics previously were done with this criteria
   * @param radiusSize current radius size
   * @return next radius size (current+5)
   */
  public int getNextRadiusInSize(int radiusSize) {
    radiusSize += 5; //increase in 5
    if (radiusSize >= maxRadius) 
      return -1;
    return radiusSize;
  }

  /**
   * Arbitrarily the minimum radius is 10
   * @return 10
   */
  public int getMinRadiusInSize() {
    return minRadius;
  }

}
