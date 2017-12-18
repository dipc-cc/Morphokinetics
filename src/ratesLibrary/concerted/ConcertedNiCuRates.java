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
    double[][] energies = new double[12][16];
    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 16; j++) {
        energies[i][j] = eImpossible;
      }
    }
    // From  type 0
    energies[0][0] = 0.031; // to type 0
    energies[0][1] = 0.03; // to type 1
    energies[0][2] = 0.024; // to type 2, subtype 0
    energies[0][3] = 0.014; // to type 2, subtype 1
    energies[0][5] = 0.0087; // to type 3, subtype 0
    // From  type 1
    energies[1][0] = 0.568; // to type 0
    energies[1][1] = 0.016; // to type 1
    energies[1][12] = 0.3; // to type 1, detach
    energies[1][2] = 0.014; // to type 2, subtype 0
    energies[1][13] = 0.159; // to type 2, subtype 0, detach
    energies[1][3] = 0.006; // to type 2, subtype 1
    energies[1][14] = 1.054; // to type 2, subtype 1, detach
    energies[1][4] = 0.001; // to type 2, subtype 2
    energies[1][5] = 0.001; // to type 3, subtype 0
    energies[1][15] = 0.4; // to type 3, subtype 0, detach
    energies[1][6] = 0.001; // to type 3, subtype 1
    energies[1][8] = 0.001; // to type 4, subtype 0
    // From  type 2, subtype 0
    energies[2][0] = 0.938; // to type 0
    energies[2][1] = 0.439; // to type 1
    energies[2][12] = 0.746; // to type 1, detach
    energies[2][2] = 0.364; // to type 2, subtype 0
    energies[2][13] = 0.743; // to type 2, subtype 0, detach
    energies[2][3] = 0.389; // to type 2, subtype 1
    energies[2][14] = 0.541; // to type 2, subtype 1, detach
    energies[2][4] = 0.305; // to type 2, subtype 2
    energies[2][5] = 0.5; // to type 3, subtype 0
    energies[2][15] = 0.588; // to type 3, subtype 0, detach
    energies[2][6] = 0.319; // to type 3, subtype 1
    energies[2][8] = 0.264; // to type 4, subtype 0
    // From  type 2, subtype 1
    energies[3][0] = 0.8; // to type 0
    energies[3][1] = 0.489; // to type 1
    energies[3][12] = 0.55; // to type 1, detach
    energies[3][2] = 0.45; // to type 2, subtype 0
    energies[3][13] = 0.467; // to type 2, subtype 0, detach
    energies[3][3] = 0.448; // to type 2, subtype 1
    energies[3][14] = 0.356; // to type 2, subtype 1, detach
    energies[3][4] = 0.366; // to type 2, subtype 2
    energies[3][5] = 0.404; // to type 3, subtype 0
    energies[3][15] = 0.371; // to type 3, subtype 0, detach
    energies[3][6] = 0.367; // to type 3, subtype 1
    energies[3][7] = 0.51; // to type 3, subtype 2
    energies[3][8] = 0.355; // to type 4, subtype 0
    energies[3][9] = 0.93; // to type 4, subtype 1
    energies[3][10] = 0.423; // to type 4, subtype 2
    energies[3][11] = 0.288; // to type 5
    // From  type 2, subtype 2
    energies[4][1] = 0.678; // to type 1
    energies[4][2] = 0.34; // to type 2, subtype 0
    energies[4][3] = 0.4; // to type 2, subtype 1
    energies[4][4] = 0.886; // to type 2, subtype 2
    energies[4][5] = 0.638; // to type 3, subtype 0
    energies[4][6] = 0.8; // to type 3, subtype 1
    energies[4][8] = 0.421; // to type 4, subtype 0
    // From  type 3, subtype 0
    energies[5][0] = 1.29; // to type 0
    energies[5][1] = 0.804; // to type 1
    energies[5][12] = 0.749; // to type 1, detach
    energies[5][2] = 0.704; // to type 2, subtype 0
    energies[5][13] = 0.5; // to type 2, subtype 0, detach
    energies[5][3] = 0.742; // to type 2, subtype 1
    energies[5][14] = 0.662; // to type 2, subtype 1, detach
    energies[5][4] = 0.629; // to type 2, subtype 2
    energies[5][5] = 0.644; // to type 3, subtype 0
    energies[5][15] = 0.714; // to type 3, subtype 0, detach
    energies[5][6] = 0.633; // to type 3, subtype 1
    energies[5][8] = 0.578; // to type 4, subtype 0
    // From  type 3, subtype 1
    energies[6][1] = 0.875; // to type 1
    energies[6][2] = 0.69; // to type 2, subtype 0
    energies[6][3] = 0.693; // to type 2, subtype 1
    energies[6][4] = 0.482; // to type 2, subtype 2
    energies[6][5] = 0.57; // to type 3, subtype 0
    energies[6][6] = 0.489; // to type 3, subtype 1
    energies[6][7] = 0.912; // to type 3, subtype 2
    energies[6][8] = 0.421; // to type 4, subtype 0
    energies[6][9] = 0.919; // to type 4, subtype 1
    energies[6][10] = 0.812; // to type 4, subtype 2
    energies[6][11] = 0.967; // to type 5
    // From  type 3, subtype 2
    energies[7][3] = 1.162; // to type 2, subtype 1
    energies[7][6] = 1.144; // to type 3, subtype 1
    energies[7][7] = 1.191; // to type 3, subtype 2
    energies[7][9] = 1.237; // to type 4, subtype 1
    energies[7][10] = 0.994; // to type 4, subtype 2
    energies[7][11] = 0.992; // to type 5
    // From  type 4, subtype 0
    energies[8][1] = 1.145; // to type 1
    energies[8][2] = 0.959; // to type 2, subtype 0
    energies[8][5] = 0.858; // to type 3, subtype 0
    energies[8][6] = 0.811; // to type 3, subtype 1
    energies[8][8] = 0.733; // to type 4, subtype 0
    // From  type 4, subtype 1
    energies[9][3] = 1.33; // to type 2, subtype 1
    energies[9][6] = 1.384; // to type 3, subtype 1
    energies[9][8] = 1.225; // to type 4, subtype 0
    energies[9][9] = 1.385; // to type 4, subtype 1
    energies[9][10] = 1.111; // to type 4, subtype 2
    energies[9][11] = 1.0; // to type 5
    // From  type 4, subtype 2
    energies[10][3] = 1.31; // to type 2, subtype 1
    energies[10][6] = 1.12; // to type 3, subtype 1
    energies[10][7] = 1.291; // to type 3, subtype 2
    energies[10][9] = 1.09; // to type 4, subtype 1
    energies[10][10] = 1.175; // to type 4, subtype 2
    energies[10][11] = 1.1; // to type 5
    // From  type 5
    energies[11][3] = 1.507; // to type 2, subtype 1
    energies[11][6] = 1.382; // to type 3, subtype 1
    energies[11][7] = 1.482; // to type 3, subtype 2
    energies[11][9] = 1.326; // to type 4, subtype 1
    energies[11][10] = 1.405; // to type 4, subtype 2
    energies[11][11] = 1.317; // to type 5
    setEnergies(energies);
    
    double[] concertedEnergies = new double[9]; // up to 8 atom islands
    concertedEnergies[0] = eImpossible; 
    concertedEnergies[1] = eImpossible; 
    concertedEnergies[2] = 0.008; // dimer
    concertedEnergies[3] = 0.046; 
    concertedEnergies[4] = 0.115; 
    concertedEnergies[5] = 0.172; 
    concertedEnergies[6] = 0.167; 
    concertedEnergies[7] = 0.250; 
    concertedEnergies[8] = 0.330; 
    setConcertedEnergies(concertedEnergies);    
  }
}
