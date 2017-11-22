/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ratesLibrary.concerted;

/**
 *
 * @author J. Alberdi-Rodriguez, Shree Ram Acharya
 */
public class ConcertedNiCuRates extends AbstractConcertedRates {
    
  public ConcertedNiCuRates(float temperature) {
    super(temperature);
    double eImpossible = 9999999;
    double[][] energies = new double[7][7];

    energies[0][0] = 0.024;
    energies[0][1] = 0.022;
    energies[0][2] = 0.007;
    energies[0][3] = 0.031;
    energies[0][4] = eImpossible;
    energies[0][5] = eImpossible;
    energies[0][6] = eImpossible;
    
    energies[1][0] = 0.563;
    energies[1][1] = 0.004;
    energies[1][2] = 0.005;
    energies[1][3] = 0.003;
    energies[1][4] = 0.360;
    energies[1][5] = eImpossible;
    energies[1][6] = eImpossible;
    
    energies[2][0] = 0.132;
    energies[2][1] = 0.427;
    energies[2][2] = 0.060;
    energies[2][3] = 0.001;
    energies[2][4] = 0.551;
    energies[2][5] = 0.003;
    energies[2][6] = eImpossible;
    
    energies[3][0] = 0.455;
    energies[3][1] = 0.799;
    energies[3][2] = 0.398;
    energies[3][3] = 0.002;
    energies[3][4] = 0.893;
    energies[3][5] = 0.005;
    energies[3][6] = eImpossible;
    
    energies[4][0] = eImpossible;
    energies[4][1] = 0.378;
    energies[4][2] = 0.047;
    energies[4][3] = 0.033;
    energies[4][4] = 0.361;
    energies[4][5] = 0.002;
    energies[4][6] = eImpossible;
    
    energies[5][0] = eImpossible;
    energies[5][1] = eImpossible;
    energies[5][2] = 1.050;
    energies[5][3] = 0.651;
    energies[5][4] = 1.357;
    energies[5][5] = 0.100;
    energies[5][6] = eImpossible;
    
    energies[6][0] = eImpossible;
    energies[6][1] = eImpossible;
    energies[6][2] = eImpossible;
    energies[6][3] = eImpossible;
    energies[6][4] = eImpossible;
    energies[6][5] = eImpossible;
    energies[6][6] = eImpossible;
    
    setEnergies(energies);
  }
}
