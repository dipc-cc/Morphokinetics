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
  private float coverageLagos;
  private float coverageGaps;
  
  public CatalysisData(int iHexa, int jHexa, double time){
    this.iHexa = iHexa;
    this.jHexa = jHexa;
    this.time = time;
  }
  
  public CatalysisData(float coverage, double time, float coverageCO, float coverageO, float coverageLagos) {
    this.coverage = coverage;
    this.time = time;
    this.coverageCO = coverageCO;
    this.coverageO = coverageO;
    this.coverageLagos = coverageLagos;
  }
  
  public CatalysisData(float coverage, double time, float coverageCO, float coverageO, float coverageLagos, float coverageGaps) {
    this.coverage = coverage;
    this.time = time;
    this.coverageCO = coverageCO;
    this.coverageO = coverageO;
    this.coverageLagos = coverageLagos;
    this.coverageGaps = coverageGaps;
  }
  
  public double[] getAdsorptionData(){
    double[] data = new double[3];
    data[0] = (double) iHexa;
    data[1] = (double) jHexa;
    data[2] = time;
    return data;
  }
  
  public double[] getCatalysisData(){
    double[] data = new double[5];
    data[0] = (double) coverage;
    data[1] = time;
    data[2] = (double) coverageCO;
    data[3] = (double) coverageO;
    data[4] = (double) coverageLagos;
    return data;
  }
  
  public double[] getCatalysisCompleteData(){
    double[] data = new double[6];
    data[0] = (double) coverage;
    data[1] = time;
    data[2] = (double) coverageCO;
    data[3] = (double) coverageO;
    data[4] = (double) coverageLagos;
    data[5] = (double) coverageGaps;
    return data;
  }
}
