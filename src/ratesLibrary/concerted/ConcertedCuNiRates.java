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
public class ConcertedCuNiRates extends AbstractConcertedRates {
  
  public ConcertedCuNiRates(float temperature) {
    super(temperature);
    double eImpossible = 9999999;
    double[][] energies = new double[7][7];

    energies[0][0] = 0.052;
    energies[0][1] = 0.040;
    energies[0][2] = 0.007;
    energies[0][3] = 0.205;
    energies[0][4] = eImpossible;
    energies[0][5] = eImpossible;
    energies[0][6] = eImpossible;
    
    energies[1][0] = 0.428;
    energies[1][1] = 0.038;
    energies[1][2] = 0.016;
    energies[1][3] = 0.062;
    energies[1][4] = 0.417;
    energies[1][5] = eImpossible;
    energies[1][6] = eImpossible;
    
    energies[2][0] = 0.716;
    energies[2][1] = 0.361;
    energies[2][2] = 0.448;
    energies[2][3] = 0.351;
    energies[2][4] = 0.507;
    energies[2][5] = 0.032;
    energies[2][6] = eImpossible;
    
    energies[3][0] = 1.000;
    energies[3][1] = 0.845;
    energies[3][2] = 0.014;
    energies[3][3] = 0.008;
    energies[3][4] = 0.863;
    energies[3][5] = 0.034;
    energies[3][6] = eImpossible;
    
    energies[4][0] = eImpossible;
    energies[4][1] = 0.413;
    energies[4][2] = 0.110;
    energies[4][3] = 0.083;
    energies[4][4] = 0.319;
    energies[4][5] = 0.080;
    energies[4][6] = eImpossible;
    
    energies[5][0] = eImpossible;
    energies[5][1] = eImpossible;
    energies[5][2] = 0.872;
    energies[5][3] = 0.561;
    energies[5][4] = 1.182;
    energies[5][5] = 0.029;
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
