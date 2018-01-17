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
  private float coverageLakes;
  private float coverageGaps;
  
  public CatalysisData(int iHexa, int jHexa, double time){
    this.iHexa = iHexa;
    this.jHexa = jHexa;
    this.time = time;
  }
  
  public CatalysisData(float coverage, double time, float coverageCO, float coverageO, float coverageLakes) {
    this.coverage = coverage;
    this.time = time;
    this.coverageCO = coverageCO;
    this.coverageO = coverageO;
    this.coverageLakes = coverageLakes;
  }
  
  public CatalysisData(float coverage, double time, float coverageCO, float coverageO, float coverageLakes, float coverageGaps) {
    this.coverage = coverage;
    this.time = time;
    this.coverageCO = coverageCO;
    this.coverageO = coverageO;
    this.coverageLakes = coverageLakes;
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
    double[] data = new double[6];
    data[0] = (double) coverage;
    data[1] = time;
    data[2] = (double) coverageCO;
    data[3] = (double) coverageO;
    data[4] = (double) coverageLakes;
    data[5] = (double) coverageGaps;
    return data;
  }
  
}
