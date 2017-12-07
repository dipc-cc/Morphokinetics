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
    double[][] energies = new double[12][16];
    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 16; j++) {
        energies[i][j] = eImpossible;
      }
    }    
    // From  type 0
    energies[0][0] = 0.059; // to type 0
    energies[0][3] = 0.317; // to type 2, subtype 1
    energies[0][5] = 0.744; // to type 3, subtype 0
    // From  type 1
    energies[1][0] = 0.388; // to type 0
    energies[1][1] = 0.065; // to type 1
    energies[1][2] = 0.269; // to type 2, subtype 0
    energies[1][13] = 0.317; // to type 2, subtype 0, detach
    energies[1][3] = 0.295; // to type 2, subtype 1
    energies[1][14] = 0.748; // to type 2, subtype 1, detach
    energies[1][15] = 0.483; // to type 3, subtype 0, detach
    energies[1][6] = 0.474; // to type 3, subtype 1
    energies[1][8] = 0.692; // to type 4, subtype 0
    // From  type 2, subtype 0
    energies[2][0] = 0.633; // to type 0
    energies[2][1] = 0.338; // to type 1
    energies[2][2] = 0.289; // to type 2, subtype 0
    energies[2][3] = 0.302; // to type 2, subtype 1
    energies[2][14] = 0.323; // to type 2, subtype 1, detach
    energies[2][4] = 0.256; // to type 2, subtype 2
    energies[2][5] = 0.274; // to type 3, subtype 0
    energies[2][15] = 0.497; // to type 3, subtype 0, detach
    energies[2][6] = 0.253; // to type 3, subtype 1
    energies[2][8] = 0.24; // to type 4, subtype 0
    // From  type 2, subtype 1
    energies[3][0] = 0.668; // to type 0
    energies[3][1] = 0.363; // to type 1
    energies[3][2] = 0.322; // to type 2, subtype 0
    energies[3][13] = 0.321; // to type 2, subtype 0, detach
    energies[3][3] = 0.329; // to type 2, subtype 1
    energies[3][14] = 0.308; // to type 2, subtype 1, detach
    energies[3][4] = 0.29; // to type 2, subtype 2
    energies[3][5] = 0.309; // to type 3, subtype 0
    energies[3][15] = 0.281; // to type 3, subtype 0, detach
    energies[3][7] = 0.466; // to type 3, subtype 2
    energies[3][8] = 0.279; // to type 4, subtype 0
    energies[3][9] = 0.72; // to type 4, subtype 1
    energies[3][10] = 0.444; // to type 4, subtype 2
    energies[3][11] = 0.444; // to type 5
    // From  type 2, subtype 2
    // From  type 3, subtype 0
    energies[5][0] = 0.851; // to type 0
    energies[5][2] = 0.514; // to type 2, subtype 0
    energies[5][3] = 0.533; // to type 2, subtype 1
    energies[5][14] = 0.493; // to type 2, subtype 1, detach
    energies[5][4] = 0.465; // to type 2, subtype 2
    energies[5][5] = 0.493; // to type 3, subtype 0
    energies[5][15] = 0.504; // to type 3, subtype 0, detach
    energies[5][6] = 0.463; // to type 3, subtype 1
    // From  type 3, subtype 1
    energies[6][1] = 0.591; // to type 1
    energies[6][2] = 0.511; // to type 2, subtype 0
    energies[6][3] = 0.508; // to type 2, subtype 1
    energies[6][4] = 0.376; // to type 2, subtype 2
    // From  type 3, subtype 2
    // From  type 4, subtype 0
    // From  type 4, subtype 1
    // From  type 4, subtype 2
    // From  type 5
    energies[11][6] = 1.05; // to type 3, subtype 1
    energies[11][7] = 0.999; // to type 3, subtype 2
    energies[11][9] = 1.02; // to type 4, subtype 1
    energies[11][10] = 0.993; // to type 4, subtype 2
    energies[11][11] = 0.873; // to type 5
    setEnergies(energies);

    double[] concertedEnergies = new double[9]; // up to 8 atom islands
    concertedEnergies[0] = eImpossible; 
    concertedEnergies[1] = eImpossible; 
    concertedEnergies[2] = 0.060; // dimer
    concertedEnergies[3] = 0.160; 
    concertedEnergies[4] = 0.180; 
    concertedEnergies[5] = 0.223; 
    concertedEnergies[6] = 0.300; 
    concertedEnergies[7] = 0.400; 
    concertedEnergies[8] = 0.370; 
    setConcertedEnergies(concertedEnergies);
  }
}