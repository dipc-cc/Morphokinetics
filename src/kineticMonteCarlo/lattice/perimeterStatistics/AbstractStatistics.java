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
   * 
   * @return the value of data[0][180]
   */
  public int getTotalCount(){
    return data[0][180];
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
          
}
