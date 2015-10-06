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
public class HopsPerAngleAg10million extends AbstractStatistics {

  public HopsPerAngleAg10million() {
    readAndSetStatistics("hopsPerAngleHexagonal10million.txt");
  }
}
