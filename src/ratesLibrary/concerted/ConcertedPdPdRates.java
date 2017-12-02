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
    double[][] energies = new double[12][16];
    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 16; j++) {
        energies[i][j] = eImpossible;
      }
    }
    // From type 0
    energies[0][0] = 0.034;
    energies[0][1] = 0.030;
    energies[0][2] = 0.030; // type 2, subtype 0
    energies[0][3] = 0.060; // type 2, subtype 1
    energies[0][5] = 0.323; // type 3, subtype 0
    // From type 1
    energies[1][0]  = 0.538;
    energies[1][1]  = 0.296;
    energies[1][12] = 1;     // type 1, detach
    energies[1][2]  = 0.220; // type 2, subtype 0
    energies[1][13] = 0.660; // type 2, subtype 0, detach
    energies[1][3]  = 0.270; // type 2, subtype 1
    energies[1][14] = 0.91;  // type 2, subtype 1, detach
    energies[1][4]  = 0.022; // type 2, subtype 2
    energies[1][5]  = 0.212; // type 3, subtype 0
    energies[1][6]  = 0.212; // type 3, subtype 1
    energies[1][8]  = 0.417; // type 4, subtype 0 
    // From type 2, subtype 0
    energies[2][0]  = 0;
    energies[2][1]  = 0; 
    energies[2][2]  = 0; // type 2, subtype 0
    energies[2][3]  = 0; // type 2, subtype 1
    energies[2][4]  = 0; // type 2, subtype 2
    energies[2][5]  = 0; // type 3, subtype 0
    energies[2][6]  = 0; // type 3, subtype 1
    energies[2][8]  = 0; // type 4, subtype 0 
    energies[2][12] = 0; // type 1, detach
    energies[2][13] = 0; // type 2, subtype 0, detach
    energies[2][14] = 0; // type 2, subtype 1, detach
    // From type 2, subtype 1
    energies[3][0]  = 0;
    energies[3][1]  = 0;
    energies[3][2]  = 0; // type 2, subtype 0
    energies[3][3]  = 0; // type 2, subtype 1
    energies[3][4]  = 0; // type 2, subtype 2
    energies[3][5]  = 0; // type 3, subtype 0
    energies[3][6]  = 0; // type 3, subtype 1
    energies[3][7]  = 0; // type 3, subtype 2 
    energies[3][8]  = 0; // type 4, subtype 0
    energies[3][9]  = 0; // type 4, subtype 1
    energies[3][10] = 0; // type 4, subtype 2
    energies[3][11] = 0; // type 5
    energies[3][12] = 0; // type 1, detach
    energies[3][13] = 0; // type 2, subtype 0, detach
    energies[3][14] = 0; // type 2, subtype 1, detach
    energies[3][15] = 0; // type 3, subtype 0, detach
    // From type 2, subtype 2
    energies[4][1] = 0;
    energies[4][2] = 0; // type 2, subtype 0
    energies[4][3] = 0; // type 2, subtype 1
    energies[4][4] = 0; // type 2, subtype 2
    energies[4][5] = 0; // type 3, subtype 0
    energies[4][6] = 0; // type 3, subtype 1
    energies[4][8] = 0; // type 4, subtype 0
    // From type 3, subtype 0
    energies[5][0]  = 0;
    energies[5][1]  = 0;
    energies[5][2]  = 0; // type 2, subtype 0
    energies[5][3]  = 0; // type 2, subtype 1
    energies[5][4]  = 0; // type 2, subtype 2
    energies[5][5]  = 0; // type 3, subtype 0
    energies[5][6]  = 0; // type 3, subtype 1
    energies[5][8]  = 0; // type 4, subtype 0
    energies[5][12] = 0; // type 1, detach
    energies[5][13] = 0; // type 2, subtype 0, detach
    energies[5][14] = 0; // type 2, subtype 1, detach
    energies[5][15] = 0; // type 3, subtype 0, detach
     // From type 3, subtype 1   
    energies[6][1]  = 0;
    energies[6][2]  = 0; // type 2, subtype 0
    energies[6][3]  = 0; // type 2, subtype 1
    energies[6][4]  = 0; // type 2, subtype 2
    energies[6][5]  = 0; // type 3, subtype 0
    energies[6][6]  = 0; // type 3, subtype 1
    energies[6][7]  = 0; // type 3, subtype 2
    energies[6][8]  = 0; // type 4, subtype 0
    energies[6][9]  = 0; // type 4, subtype 1
    energies[6][10] = 0; // type 4, subtype 2
    energies[6][11] = 0; // type 5
    // From type 3, subtype 2
    energies[7][3]  = 0; // type 2, subtype 1
    energies[7][6]  = 0; // type 3, subtype 1
    energies[7][7]  = 0; // type 3, subtype 2
    energies[7][9]  = 0; // type 4, subtype 1
    energies[7][10] = 0; // type 4, subtype 2
    energies[7][11] = 0; // type 5
    // From type 4, subtype 0
    energies[8][1] = 0;
    energies[8][2] = 0; // type 2, subtype 0
    energies[8][5] = 0; // type 3, subtype 0
    energies[8][6] = 0; // type 3, subtype 1
    energies[8][8] = 0; // type 4, subtype 0
    // From type 4, subtype 1
    energies[9][3]  = 0; // type 2, subtype 1
    energies[9][6]  = 0; // type 3, subtype 1
    energies[9][8]  = 0; // type 4, subtype 0
    energies[9][9]  = 0; // type 4, subtype 1
    energies[9][10] = 0; // type 4, subtype 2
    energies[9][11] = 0; // type 5
    // From type 4, subtype 2    
    energies[10][3]  = 0; // type 2, subtype 1
    energies[10][6]  = 0; // type 3, subtype 1
    energies[10][7]  = 0; // type 3, subtype 2
    energies[10][9]  = 0; // type 4, subtype 1
    energies[10][10] = 0; // type 4, subtype 2
    energies[10][11] = 0; // type 5
    // From type 5
    energies[11][3]  = 0; // type 2, subtype 1
    energies[11][6]  = 0; // type 3, subtype 1
    energies[11][7]  = 0; // type 3, subtype 2
    energies[11][9]  = 0; // type 4, subtype 1
    energies[11][10] = 0; // type 4, subtype 2
    energies[11][11] = 0; // type 5
    
    setEnergies(energies);
  }
}