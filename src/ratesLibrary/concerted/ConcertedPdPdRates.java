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
    energies[0][2] = 0.027; // type 2, subtype 0
    energies[0][3] = 0.598; // type 2, subtype 1
    energies[0][5] = 1.280; // type 3, subtype 0
    // From type 1
    energies[1][0]  = 0.539;
    energies[1][1]  = 0.296;
    energies[1][12] = 0.352; // type 1, detach
    energies[1][2]  = 0.222; // type 2, subtype 0
    energies[1][13] = 0.416; // type 2, subtype 0, detach
    energies[1][3]  = 0.273; // type 2, subtype 1
    energies[1][14] = 0.907; // type 2, subtype 1, detach
    energies[1][4]  = 0.222; // type 2, subtype 2
    energies[1][5]  = 0.215; // type 3, subtype 0
    energies[1][15] = 0.179; // type 3, subtype 0, detach
    energies[1][6]  = 0.189; // type 3, subtype 1
    energies[1][8]  = 0.122; // type 4, subtype 0 
    // From type 2, subtype 0
    energies[2][0]  = 0.964;
    energies[2][1]  = 0.674; 
    energies[2][12] = 1.2; // type 1, detach
    energies[2][2]  = 0.582; // type 2, subtype 0
    energies[2][13] = 0.640; // type 2, subtype 0, detach
    energies[2][3]  = 0.620; // type 2, subtype 1
    energies[2][14] = 0.274; // type 2, subtype 1, detach
    energies[2][4]  = 0.531; // type 2, subtype 2
    energies[2][5]  = 0.566; // type 3, subtype 0
    energies[2][6]  = 0.316; // type 3, subtype 1
    energies[2][8]  = 0.463; // type 4, subtype 0 
    // From type 2, subtype 1
    energies[3][0]  = 1.030;
    energies[3][1]  = 0.771;
    energies[3][12] = 1.4; // type 1, detach
    energies[3][2]  = 0.771; // type 2, subtype 0
    energies[3][13] = 0.679; // type 2, subtype 0, detach
    energies[3][3]  = 0.744; // type 2, subtype 1
    energies[3][14] = 0.325; // type 2, subtype 1, detach
    energies[3][4]  = 0.630; // type 2, subtype 2
    energies[3][5]  = 0.709; // type 3, subtype 0
    energies[3][15] = 0.331; // type 3, subtype 0, detach
    energies[3][6]  = 0.636; // type 3, subtype 1
    energies[3][7]  = 0.595; // type 3, subtype 2 
    energies[3][8]  = 0.595; // type 4, subtype 0
    energies[3][9]  = 1.1; // type 4, subtype 1
    energies[3][10] = 0.293; // type 4, subtype 2
    energies[3][11] = 0.293; // type 5
    // From type 2, subtype 2
    energies[4][1] = 0.720;
    energies[4][2] = 0.627; // type 2, subtype 0
    energies[4][3] = 0.667; // type 2, subtype 1
    energies[4][4] = 1.137; // type 2, subtype 2
    energies[4][5] = 0.604; // type 3, subtype 0
    energies[4][6] = 0.540; // type 3, subtype 1
    energies[4][8] = 0.5; // type 4, subtype 0
    // From type 3, subtype 0
    energies[5][0]  = 1.372;
    energies[5][1]  = 1.069;
    energies[5][12] = 1.193; // type 1, detach
    energies[5][2]  = 0.971; // type 2, subtype 0
    energies[5][13] = 1.030; // type 2, subtype 0, detach
    energies[5][3]  = 0.629; // type 2, subtype 1
    energies[5][14] = 0.915; // type 2, subtype 1, detach
    energies[5][4]  = 0.943; // type 2, subtype 2
    energies[5][5]  = 0.725; // type 3, subtype 0
    energies[5][15] = 0.904; // type 3, subtype 0, detach
    energies[5][6]  = 0.834; // type 3, subtype 1
    energies[5][8]  = 0.9; // type 4, subtype 0
     // From type 3, subtype 1   
    energies[6][1]  = 0.986;
    energies[6][2]  = 1.020; // type 2, subtype 0
    energies[6][3]  = 0.908; // type 2, subtype 1
    energies[6][4]  = 0.957; // type 2, subtype 2
    energies[6][5]  = 0.898; // type 3, subtype 0
    energies[6][6]  = 1.162; // type 3, subtype 1
    energies[6][7]  = 0.818; // type 3, subtype 2
    energies[6][8]  = 0.967; // type 4, subtype 0
    energies[6][9]  = 0.618; // type 4, subtype 1
    energies[6][10] = 0.922; // type 4, subtype 2
    energies[6][11] = 0.9; // type 5
    // From type 3, subtype 2
    energies[7][3]  = 1.158; // type 2, subtype 1
    energies[7][6]  = 1.228; // type 3, subtype 1
    energies[7][7]  = 1.310; // type 3, subtype 2
    energies[7][9]  = 1.3; // type 4, subtype 1
    energies[7][10] = 0.801; // type 4, subtype 2
    energies[7][11] = 1.235; // type 5
    // From type 4, subtype 0
    energies[8][1] = 1.390;
    energies[8][2] = 1.261; // type 2, subtype 0
    energies[8][5] = 1.213; // type 3, subtype 0
    energies[8][6] = 1.178; // type 3, subtype 1
    energies[8][8] = 1.082; // type 4, subtype 0
    // From type 4, subtype 1
    energies[9][3]  = 1.285; // type 2, subtype 1
    energies[9][6]  = 1.228; // type 3, subtype 1
    energies[9][8]  = 1.210; // type 4, subtype 0
    energies[9][9]  = 1.471; // type 4, subtype 1
    energies[9][10] = 1.039; // type 4, subtype 2
    energies[9][11] = 1.1; // type 5
    // From type 4, subtype 2    
    energies[10][3]  = 1.226; // type 2, subtype 1
    energies[10][6]  = 1.264; // type 3, subtype 1
    energies[10][7]  = 1.261; // type 3, subtype 2
    energies[10][9]  = 1.297; // type 4, subtype 1
    energies[10][10] = 0.965; // type 4, subtype 2
    energies[10][11] = 1; // type 5
    // From type 5
    energies[11][3]  = 1.47; // type 2, subtype 1
    /*energies[11][6]  = 0; // type 3, subtype 1
    energies[11][7]  = 0; // type 3, subtype 2
    energies[11][9]  = 0; // type 4, subtype 1
    energies[11][10] = 0; // type 4, subtype 2
    energies[11][11] = 0; // type 5//*/
    
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