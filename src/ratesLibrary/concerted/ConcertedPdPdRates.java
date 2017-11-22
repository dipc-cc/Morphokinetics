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
public class ConcertedPdPdRates extends AbstractConcertedRates {
  
  public ConcertedPdPdRates(float temperature) {
    super(temperature);
    double eImpossible = 9999999;
    double[][] energies = new double[7][7];
    
    energies[0][0] = 0.034;
    energies[0][1] = 0.030;
    energies[0][2] = 0.003;
    energies[0][3] = 0.323;
    energies[0][4] = eImpossible;
    energies[0][5] = eImpossible;
    energies[0][6] = eImpossible;
    
    energies[1][0] = 0.574;
    energies[1][1] = 0.023;
    energies[1][2] = 0.016;
    energies[1][3] = 0.020;
    energies[1][4] = 0.417;
    energies[1][5] = eImpossible;
    energies[1][6] = eImpossible;
    
    energies[2][0] = 0.500;
    energies[2][1] = 0.466;
    energies[2][2] = 0.091;
    energies[2][3] = 0.006;
    energies[2][4] = 0.665;
    energies[2][5] = 0.002;
    energies[2][6] = eImpossible;
    
    energies[3][0] = 0.318;
    energies[3][1] = 0.879;
    energies[3][2] = 0.439;
    energies[3][3] = 0.013;
    energies[3][4] = 1.093;
    energies[3][5] = 0.006;
    energies[3][6] = eImpossible;
    
    energies[4][0] = eImpossible;
    energies[4][1] = 0.434;
    energies[4][2] = 0.073;
    energies[4][3] = 0.054;
    energies[4][4] = 0.383;
    energies[4][5] = 0.008;
    energies[4][6] = eImpossible;
    
    energies[5][0] = eImpossible;
    energies[5][1] = eImpossible;
    energies[5][2] = 1.150;
    energies[5][3] = 0.734;
    energies[5][4] = 1.504;
    energies[5][5] = 0.084;
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