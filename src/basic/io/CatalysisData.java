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
  private float coverage;
  private double time;
  private double coverageCO;
  private double coverageO;
  private int iHexa;
  private int jHexa;
  
  public CatalysisData(int iHexa, int jHexa, double time){
    this.iHexa = iHexa;
    this.jHexa = jHexa;
    this.time = time;
  }
  
  public CatalysisData(float coverage, double time, float coverageCO, float coverageO) {
    this.coverage = coverage;
    this.time = time;
    this.coverageCO = coverageCO;
    this.coverageO = coverageO;
  }
  
  public double[] getAdsorptionData(){
    double[] data = new double[3];
    data[0] = (double) iHexa;
    data[1] = (double) jHexa;
    data[2] = time;
    return data;
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
