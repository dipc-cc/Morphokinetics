/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kineticMonteCarlo.lattice.perimeterStatistics;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public abstract class AbstractStatistics {
  
  private int[][] data;
  
  /**
   * Returns the total value of analysed atoms used to do the statistics, which is stores in the last position (for every radius).
   * @param index
   * @return the value of data[0][180]
   */
  public int getTotalCount(int index){
    return data[index][data[0].length-1];
  }
  
  @Deprecated
  public int getTotalCount(){
    return data[0][data[0].length-1];
  }
  
  /**
   * 
   * @return the length of the matrix
   */
  public int getLenght(){
    return data.length;
  }
  
  public int getData(int i, int j){
    return data[i][j];
  }
  
  /**
   * To properly initialise in the not abstract constructor
   * @param rawData 
   */
  protected void setData(int[][] rawData){
    data = rawData;
  }
  
  /**
   * Returns the entire data matrix that is working on. 
   * This is the whole information about the atom re-entrance that the code has.
   * @return 
   */
  protected int[][] getWholeData() {
    return data;
  }
          
}
