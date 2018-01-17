/* 
 * Copyright (C) 2018 J. Alberdi-Rodriguez
 *
 * This file is part of Morphokinetics.
 *
 * Morphokinetics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Morphokinetics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
 */
package ratesLibrary.concerted;

/**
 *
 * @author J. Alberdi-Rodriguez, S. R. Acharya
 */
public class ConcertedNiNiRates extends AbstractConcertedRates {
  
  public ConcertedNiNiRates(float temperature) {
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
    energies[0][1] = 0.448; // to type 1
    energies[0][2] = 0.803; // to type 2, subtype 0
    energies[0][3] = 0.824; // to type 2, subtype 1
    energies[0][5] = 1.125; // to type 3, subtype 0
    // From  type 1
    energies[1][0] = 0.54; // to type 0
    energies[1][1] = 0.044; // to type 1
    energies[1][12] = 0.355; // to type 1, detach
    energies[1][2] = 0.401; // to type 2, subtype 0
    energies[1][13] = 0.375; // to type 2, subtype 0, detach
    energies[1][3] = 0.443; // to type 2, subtype 1
    energies[1][14] = 1.11; // to type 2, subtype 1, detach
    energies[1][4] = 0.434; // to type 2, subtype 2
    energies[1][5] = 0.434; // to type 3, subtype 0
    energies[1][15] = 0.607; // to type 3, subtype 0, detach
    energies[1][6] = 0.723; // to type 3, subtype 1
    energies[1][8] = 1.03; // to type 4, subtype 0
    // From  type 2, subtype 0
    energies[2][0] = 0.904; // to type 0
    energies[2][1] = 0.441; // to type 1
    energies[2][2] = 0.362; // to type 2, subtype 0
    energies[2][3] = 0.37; // to type 2, subtype 1
    energies[2][14] = 0.616; // to type 2, subtype 1, detach
    energies[2][4] = 0.278; // to type 2, subtype 2
    energies[2][5] = 0.476; // to type 3, subtype 0
    energies[2][15] = 0.616; // to type 3, subtype 0, detach
    energies[2][6] = 0.278; // to type 3, subtype 1
    energies[2][8] = 0.642; // to type 4, subtype 0
    // From  type 2, subtype 1
    energies[3][0] = 0.952; // to type 0
    energies[3][1] = 0.486; // to type 1
    energies[3][12] = 0.794; // to type 1, detach
    energies[3][2] = 0.418; // to type 2, subtype 0
    energies[3][13] = 0.339; // to type 2, subtype 0, detach
    energies[3][3] = 0.415; // to type 2, subtype 1
    energies[3][14] = 0.314; // to type 2, subtype 1, detach
    energies[3][4] = 0.327; // to type 2, subtype 2
    energies[3][5] = 0.38; // to type 3, subtype 0
    energies[3][6] = 0.323; // to type 3, subtype 1
    energies[3][7] = 0.799; // to type 3, subtype 2
    energies[3][8] = 0.312; // to type 4, subtype 0
    energies[3][9] = 0.999; // to type 4, subtype 1
    energies[3][10] = 0.63; // to type 4, subtype 2
    energies[3][11] = 0.63; // to type 5
    // From  type 2, subtype 2
    energies[4][1] = 0.306; // to type 1
    energies[4][2] = 0.306; // to type 2, subtype 0
    energies[4][3] = 0.301; // to type 2, subtype 1
    energies[4][4] = 1.095; // to type 2, subtype 2
    energies[4][5] = 0.197; // to type 3, subtype 0
    // From  type 3, subtype 0
    energies[5][0] = 1.229; // to type 0
    energies[5][1] = 0.791; // to type 1
    energies[5][12] = 1.07; // to type 1, detach
    energies[5][2] = 0.69; // to type 2, subtype 0
    energies[5][13] = 0.659; // to type 2, subtype 0, detach
    energies[5][3] = 0.702; // to type 2, subtype 1
    energies[5][14] = 0.607; // to type 2, subtype 1, detach
    energies[5][4] = 0.565; // to type 2, subtype 2
    energies[5][5] = 0.633; // to type 3, subtype 0
    energies[5][15] = 0.612; // to type 3, subtype 0, detach
    energies[5][6] = 0.559; // to type 3, subtype 1
    energies[5][8] = 0.535; // to type 4, subtype 0
    // From  type 3, subtype 1
    energies[6][1] = 0.829; // to type 1
    energies[6][2] = 0.668; // to type 2, subtype 0
    energies[6][3] = 0.654; // to type 2, subtype 1
    energies[6][4] = 0.392; // to type 2, subtype 2
    energies[6][5] = 0.549; // to type 3, subtype 0
    energies[6][6] = 0.386; // to type 3, subtype 1
    energies[6][7] = 1.122; // to type 3, subtype 2
    // From  type 3, subtype 2
    energies[7][3] = 1.218; // to type 2, subtype 1
    energies[7][6] = 1.12; // to type 3, subtype 1
    energies[7][7] = 1.251; // to type 3, subtype 2
    energies[7][9] = 1.158; // to type 4, subtype 1
    energies[7][10] = 1.044; // to type 4, subtype 2
    energies[7][11] = 1.153; // to type 5
    // From  type 4, subtype 0
    energies[8][1] = 1.1; // to type 1
    energies[8][2] = 0.942; // to type 2, subtype 0
    energies[8][5] = 0.838; // to type 3, subtype 0
    energies[8][6] = 0.688; // to type 3, subtype 1
    energies[8][8] = 0.866; // to type 4, subtype 0
    // From  type 4, subtype 1
    energies[9][3] = 1.342; // to type 2, subtype 1
    energies[9][6] = 1.352; // to type 3, subtype 1
    energies[9][8] = 1.212; // to type 4, subtype 0
    energies[9][9] = 1.345; // to type 4, subtype 1
    energies[9][10] = 1.171; // to type 4, subtype 2
    // From  type 4, subtype 2
    energies[10][3] = 1.355; // to type 2, subtype 1
    energies[10][6] = 1.193; // to type 3, subtype 1
    energies[10][7] = 1.338; // to type 3, subtype 2
    energies[10][9] = 1.173; // to type 4, subtype 1
    energies[10][10] = 1.167; // to type 4, subtype 2
    // From  type 5
    energies[11][3] = 1.548; // to type 2, subtype 1
    energies[11][15] = 1.415; // to type 3, subtype 0, detach
    energies[11][6] = 1.522; // to type 3, subtype 1
    energies[11][7] = 1.379; // to type 3, subtype 2
    energies[11][9] = 1.397; // to type 4, subtype 1
    energies[11][10] = 1.351; // to type 4, subtype 2
    energies[11][11] = 1.171; // to type 5
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
