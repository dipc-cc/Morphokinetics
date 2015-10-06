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
public class ReentrancesPerAngleGraphene10million extends AbstractStatistics {

  public ReentrancesPerAngleGraphene10million() {
    readAndSetStatistics("reentrancesPerAngleHexagonalHoneycomb10million.txt");
  }
}
