/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
