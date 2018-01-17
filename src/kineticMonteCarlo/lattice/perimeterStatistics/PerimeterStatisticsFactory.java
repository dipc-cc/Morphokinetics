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
public class PerimeterStatisticsFactory {

  private final AbstractPerimeterStatistics perimeterStatistics;
  
  public PerimeterStatisticsFactory(String statisticsName) {

    switch (statisticsName) {
      case "grapheneOld": {
        perimeterStatistics = new PerimeterStatistics(
                new Statistics("reentrancesPerAngleHexagonalHoneycomb1million.txt"),
                new Statistics("hopsPerAngleHexagonalHoneycomb1million.txt"));
        break;
      }      
      case "graphene": {
        perimeterStatistics = new PerimeterStatistics(
                new Statistics("reentrancesPerAngleHexagonalHoneycomb10million.txt"),
                new Statistics("hopsPerAngleHexagonalHoneycomb10million.txt"));
        break;
      }
      case "AgOld": {
        perimeterStatistics = new PerimeterStatistics(
                new Statistics("reentrancesPerAngleHexagonal1million.txt"),
                new Statistics("hopsPerAngleHexagonal1million.txt"));
        break;
      }
      case "Ag": {
        perimeterStatistics = new PerimeterStatistics(
                new Statistics("reentrancesPerAngleHexagonal10million.txt"),
                new Statistics("hopsPerAngleHexagonal10million.txt"));
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
