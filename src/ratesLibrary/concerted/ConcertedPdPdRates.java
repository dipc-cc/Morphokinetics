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
    // From  type 0
    energies[0][0] = 0.034; // to type 0
    energies[0][1] = 0.03; // to type 1
    energies[0][2] = 0.027; // to type 2, subtype 0
    energies[0][3] = 0.598; // to type 2, subtype 1
    energies[0][5] = 1.28; // to type 3, subtype 0
    // From  type 1
    energies[1][0] = 0.539; // to type 0
    energies[1][1] = 0.296; // to type 1
    energies[1][12] = 0.352; // to type 1, detach
    energies[1][2] = 0.222; // to type 2, subtype 0
    energies[1][13] = 0.416; // to type 2, subtype 0, detach
    energies[1][3] = 0.273; // to type 2, subtype 1
    energies[1][14] = 0.907; // to type 2, subtype 1, detach
    energies[1][4] = 0.222; // to type 2, subtype 2
    energies[1][5] = 0.215; // to type 3, subtype 0
    energies[1][15] = 0.179; // to type 3, subtype 0, detach
    energies[1][6] = 0.189; // to type 3, subtype 1
    energies[1][8] = 0.122; // to type 4, subtype 0
    // From  type 2, subtype 0
    energies[2][0] = 0.964; // to type 0
    energies[2][1] = 0.674; // to type 1
    energies[2][12] = 0.811; // to type 1, detach
    energies[2][2] = 0.582; // to type 2, subtype 0
    energies[2][13] = 0.64; // to type 2, subtype 0, detach
    energies[2][3] = 0.62; // to type 2, subtype 1
    energies[2][14] = 0.274; // to type 2, subtype 1, detach
    energies[2][4] = 0.531; // to type 2, subtype 2
    energies[2][5] = 0.566; // to type 3, subtype 0
    energies[2][15] = 0.316; // to type 3, subtype 0, detach
    energies[2][6] = 0.523; // to type 3, subtype 1
    energies[2][8] = 0.463; // to type 4, subtype 0
    // From  type 2, subtype 1
    energies[3][0] = 1.03; // to type 0
    energies[3][1] = 0.771; // to type 1
    energies[3][12] = 0.841; // to type 1, detach
    energies[3][2] = 0.711; // to type 2, subtype 0
    energies[3][13] = 0.679; // to type 2, subtype 0, detach
    energies[3][3] = 0.744; // to type 2, subtype 1
    energies[3][14] = 0.325; // to type 2, subtype 1, detach
    energies[3][4] = 0.63; // to type 2, subtype 2
    energies[3][5] = 0.709; // to type 3, subtype 0
    energies[3][15] = 0.331; // to type 3, subtype 0, detach
    energies[3][6] = 0.636; // to type 3, subtype 1
    energies[3][7] = 0.595; // to type 3, subtype 2
    energies[3][8] = 0.595; // to type 4, subtype 0
    energies[3][9] = 1.1; // to type 4, subtype 1
    energies[3][10] = 0.293; // to type 4, subtype 2
    energies[3][11] = 0.293; // to type 5
    // From  type 2, subtype 2
    energies[4][1] = 0.72; // to type 1
    energies[4][2] = 0.627; // to type 2, subtype 0
    energies[4][3] = 0.667; // to type 2, subtype 1
    energies[4][4] = 1.137; // to type 2, subtype 2
    energies[4][5] = 0.604; // to type 3, subtype 0
    energies[4][6] = 0.54; // to type 3, subtype 1
    energies[4][8] = 0.465; // to type 4, subtype 0
    // From  type 3, subtype 0
    energies[5][0] = 1.372; // to type 0
    energies[5][1] = 1.069; // to type 1
    energies[5][12] = 1.193; // to type 1, detach
    energies[5][2] = 0.971; // to type 2, subtype 0
    energies[5][13] = 1.03; // to type 2, subtype 0, detach
    energies[5][3] = 1.016; // to type 2, subtype 1
    energies[5][14] = 0.681; // to type 2, subtype 1, detach
    energies[5][4] = 0.915; // to type 2, subtype 2
    energies[5][5] = 0.943; // to type 3, subtype 0
    energies[5][15] = 0.725; // to type 3, subtype 0, detach
    energies[5][6] = 0.904; // to type 3, subtype 1
    energies[5][8] = 0.834; // to type 4, subtype 0
    // From  type 3, subtype 1
    energies[6][1] = 1.093; // to type 1
    energies[6][2] = 0.986; // to type 2, subtype 0
    energies[6][3] = 1.02; // to type 2, subtype 1
    energies[6][4] = 0.908; // to type 2, subtype 2
    energies[6][5] = 0.957; // to type 3, subtype 0
    energies[6][6] = 0.898; // to type 3, subtype 1
    energies[6][7] = 1.162; // to type 3, subtype 2
    energies[6][8] = 0.818; // to type 4, subtype 0
    energies[6][9] = 0.967; // to type 4, subtype 1
    energies[6][10] = 0.618; // to type 4, subtype 2
    energies[6][11] = 0.922; // to type 5
    // From  type 3, subtype 2
    energies[7][3] = 1.158; // to type 2, subtype 1
    energies[7][6] = 1.228; // to type 3, subtype 1
    energies[7][7] = 1.31; // to type 3, subtype 2
    energies[7][9] = 0.801; // to type 4, subtype 1
    energies[7][10] = 0.801; // to type 4, subtype 2
    energies[7][11] = 1.235; // to type 5
    // From  type 4, subtype 0
    energies[8][1] = 1.39; // to type 1
    energies[8][2] = 1.261; // to type 2, subtype 0
    energies[8][5] = 1.213; // to type 3, subtype 0
    energies[8][6] = 1.178; // to type 3, subtype 1
    energies[8][8] = 1.082; // to type 4, subtype 0
    // From  type 4, subtype 1
    energies[9][3] = 1.285; // to type 2, subtype 1
    energies[9][6] = 1.228; // to type 3, subtype 1
    energies[9][8] = 1.21; // to type 4, subtype 0
    energies[9][9] = 1.417; // to type 4, subtype 1
    energies[9][10] = 1.039; // to type 4, subtype 2
    // From  type 4, subtype 2
    energies[10][3] = 1.266; // to type 2, subtype 1
    energies[10][6] = 1.264; // to type 3, subtype 1
    energies[10][7] = 1.261; // to type 3, subtype 2
    energies[10][9] = 1.297; // to type 4, subtype 1
    energies[10][10] = 0.965; // to type 4, subtype 2
    // From  type 5
    energies[11][3] = 1.47; // to type 2, subtype 1
    energies[11][15] = 1.406; // to type 3, subtype 0, detach
    energies[11][6] = 1.435; // to type 3, subtype 1
    energies[11][7] = 1.399; // to type 3, subtype 2
    energies[11][9] = 0.954; // to type 4, subtype 1
    energies[11][10] = 1.205; // to type 4, subtype 2
    energies[11][11] = 1.055; // to type 5
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