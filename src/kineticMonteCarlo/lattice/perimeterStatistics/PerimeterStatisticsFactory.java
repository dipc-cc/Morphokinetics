/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.perimeterStatistics;

import android.content.Context;

/**
 *
 * @author Nestor
 */
public class PerimeterStatisticsFactory {

  private final AbstractPerimeterStatistics perimeterStatistics;
  
  public PerimeterStatisticsFactory(String statisticsName, Context context) {

    switch (statisticsName) {
      case "grapheneOld": {
        perimeterStatistics = new PerimeterStatistics(
                new Statistics("reentrancesPerAngleHexagonalHoneycomb1million.txt", context),
                new Statistics("hopsPerAngleHexagonalHoneycomb1million.txt", context));
        break;
      }      
      case "graphene": {
        perimeterStatistics = new PerimeterStatistics(
                new Statistics("reentrancesPerAngleHexagonalHoneycomb10million.txt", context),
                new Statistics("hopsPerAngleHexagonalHoneycomb10million.txt", context));
        break;
      }
      case "AgOld": {
        perimeterStatistics = new PerimeterStatistics(
                new Statistics("reentrancesPerAngleHexagonal1million.txt", context),
                new Statistics("hopsPerAngleHexagonal1million.txt", context));
        break;
      }
      case "Ag": {
        perimeterStatistics = new PerimeterStatistics(
                new Statistics("reentrancesPerAngleHexagonal10million.txt", context),
                new Statistics("hopsPerAngleHexagonal10million.txt", context));
        break;
      }
      default: {
        perimeterStatistics = null;
        System.err.println("Trying to get statistics for "+statisticsName);
        throw new UnsupportedOperationException("This execution mode is not supported");
      }
    }
  }

  public AbstractPerimeterStatistics getStatistics() {
    return perimeterStatistics;
  }
}
