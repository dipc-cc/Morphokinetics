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
public class ReentrancesPerAngleAg10million extends AbstractStatistics {

  public ReentrancesPerAngleAg10million() {
    readAndSetStatistics("reentrancesPerAngleHexagonal10million.txt");
  }
}
