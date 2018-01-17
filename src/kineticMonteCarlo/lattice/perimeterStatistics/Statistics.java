/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
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

import basic.io.Restart;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class Statistics {
  
  private int[][] data;
  private final Restart restart;
  
  public Statistics(String statisticsFile) {
    restart = new Restart(Restart.getJarBaseDir() + "/perimeterData");
    readAndSetStatistics(statisticsFile);
  }
  
  /**
   * Returns the total value of analysed atoms used to do the statistics, which is stores in the last position (for every radius).
   * @param index
   * @return the value of data[0][180]
   */
  public int getTotalCount(int index){
    return data[index][data[0].length-1];
  }

  /**
   * 
   * @return number of rows of the matrix
   */
  public int getRows(){
    return data.length;
  } 
  
  /**
   * 
   * @return number of columns of the matrix
   */
  public int getColumns(){
    return data[0].length;
  }
  
  public int getData(int i, int j){
    return data[i][j];
  }
  
  /**
   * To properly initialise in the not abstract constructor
   * @param rawData 
   */
  void setData(int[][] rawData){
    data = rawData;
  }
  
  /**
   * Returns the entire data matrix that is working on. 
   * This is the whole information about the atom re-entrance that the code has.
   * 
   * @return all the data
   */
  int[][] getWholeData() {
    return data;
  }

  private void readAndSetStatistics(String fileName) {
    
    float[][] tmp;
    int[][] result;
    try {
      tmp = restart.readSurfaceText2D(fileName);
      result = new int[tmp.length][tmp[1].length];
      for (int i = 0; i < tmp.length; i++) {
        for (int j = 0; j < tmp[1].length; j++) {
          result[i][j] = Math.round(tmp[i][j]);
        }
      }
      setData(result);
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
      System.err.println("Could not be read the statistic of the perimeter re-entrance");
      System.err.println("Exiting...");
      System.exit(-99);
    }
  }
}
