/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basic.io;

/**
 *
 * @author J. Alberdi-Rodriguez
 */
public class CatalysisData {
  private final float coverage;
  private final double time;
  private final double coverageCO;
  private final double coverageO;
  
  public CatalysisData(float coverage, double time, float coverageCO, float coverageO) {
    this.coverage = coverage;
    this.time = time;
    this.coverageCO = coverageCO;
    this.coverageO = coverageO;
  }
  
  public double[] getCatalysisData(){
    double[] data = new double[4];
    data[0] = (double) coverage;
    data[1] = time;
    data[2] = (double) coverageCO;
    data[3] = (double) coverageO;
    return data;
  }
}
