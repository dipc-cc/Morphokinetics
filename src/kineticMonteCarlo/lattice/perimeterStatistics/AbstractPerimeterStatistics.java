/* 
 * Copyright (C) 2018 N. Ferrando, J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package kineticMonteCarlo.lattice.perimeterStatistics;

/**
 *
 * @author N. Ferrando, J. Alberdi-Rodriguez
 */
public abstract class AbstractPerimeterStatistics {

  private final int[] totalCount;
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
 
    totalCount = new int[statisticAtom.getRows()];
    minRadius = 10;
    maxRadius = statisticAtom.getRows() - 10;
    
    reentranceCount = new int[statisticAtom.getRows()][statisticAtom.getColumns()];
    hopsCount = new int[statisticsHops.getRows()][statisticsHops.getColumns()];
    
    for (int i = 0; i < statisticAtom.getRows(); i++) {
      totalCount[i] = statisticAtom.getTotalCount(i);
    }
    reentranceCount = statisticAtom.getWholeData();
    hopsCount = statisticsHops.getWholeData();
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
   * We increase the radius in 1 position, because it should be the most precise and agrees the best
   * with theory. Previously, it was added 5 by 5 because statistics were done with this criteria
   *
   * @param radiusSize current radius size
   * @return next radius size (current+1)
   */
  public int getNextRadiusInSize(int radiusSize) {
    radiusSize += 1; //increase in 1
    if (radiusSize >= maxRadius) {
      return -1;
    }
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
