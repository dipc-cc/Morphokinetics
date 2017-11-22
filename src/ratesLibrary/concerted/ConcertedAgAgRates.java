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
public class ConcertedAgAgRates extends AbstractConcertedRates {
  
  public ConcertedAgAgRates(float temperature) {
    super(temperature);
    double eImpossible = 9999999;
    double[][] energies = new double[7][7];
    
    energies[0][0] = 0.058;
    energies[0][1] = 0.056;
    energies[0][2] = 0.041;
    energies[0][3] = 0.038;
    energies[0][4] = eImpossible;
    energies[0][5] = eImpossible;
    energies[0][6] = eImpossible;
    
    energies[1][0] = 0.382;
    energies[1][1] = 0.055;
    energies[1][2] = 0.052;
    energies[1][3] = 0.080;
    energies[1][4] = 0.072;
    energies[1][5] = eImpossible;
    energies[1][6] = eImpossible;
    
    energies[2][0] = 0.625;
    energies[2][1] = 0.321;
    energies[2][2] = 0.119;
    energies[2][3] = 0.078;
    energies[2][4] = 0.132;
    energies[2][5] = 0.068;
    energies[2][6] = eImpossible;
    
    energies[3][0] = 0.840;
    energies[3][1] = 0.553;
    energies[3][2] = 0.331;
    energies[3][3] = 0.465;
    energies[3][4] = 0.124;
    energies[3][5] = 0.202;
    energies[3][6] = eImpossible;
    
    energies[4][0] = eImpossible;
    energies[4][1] = 0.754;
    energies[4][2] = 0.560;
    energies[4][3] = 0.267;
    energies[4][4] = 0.033;
    energies[4][5] = 0.193;
    energies[4][6] = eImpossible;
    
    energies[5][0] = eImpossible;
    energies[5][1] = eImpossible;
    energies[5][2] = 0.734;
    energies[5][3] = 0.621;
    energies[5][4] = 0.371;
    energies[5][5] = 0.021;
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