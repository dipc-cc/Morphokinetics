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
public abstract class AbstractStatisticsHops {

  private int[][] data;

  public int getData(int i, int j) {
    return data[i][j];
  }

  /**
   * To properly initialise in the not abstract constructor
   *
   * @param rawData
   */
  protected void setData(int[][] rawData) {
    data = rawData;
  }

}
