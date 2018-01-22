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
    energies[0][1] = 0.05; // to type 1
    energies[0][2] = 0.055; // to type 2, subtype 0
    energies[0][3] = 0.317; // to type 2, subtype 1
    energies[0][5] = 0.043; // to type 3, subtype 0
    // From  type 1
    energies[1][0] = 0.388; // to type 0
    energies[1][1] = 0.065; // to type 1
    energies[1][12] = 0.3; // to type 1, detach
    energies[1][2] = 0.06; // to type 2, subtype 0
    energies[1][13] = 0.317; // to type 2, subtype 0, detach
    energies[1][3] = 0.06; // to type 2, subtype 1
    energies[1][14] = 0.748; // to type 2, subtype 1, detach
    energies[1][4] = 0.046; // to type 2, subtype 2
    energies[1][5] = 0.04; // to type 3, subtype 0
    energies[1][15] = 0.483; // to type 3, subtype 0, detach
    energies[1][6] = 0.045; // to type 3, subtype 1
    energies[1][8] = 0.04; // to type 4, subtype 0
    // From  type 2, subtype 0
    energies[2][0] = 0.633; // to type 0
    energies[2][1] = 0.338; // to type 1
    energies[2][12] = 0.584; // to type 1, detach
    energies[2][2] = 0.289; // to type 2, subtype 0
    energies[2][13] = 0.584; // to type 2, subtype 0, detach
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
    energies[3][12] = 0.5; // to type 1, detach
    energies[3][2] = 0.322; // to type 2, subtype 0
    energies[3][13] = 0.321; // to type 2, subtype 0, detach
    energies[3][3] = 0.329; // to type 2, subtype 1
    energies[3][14] = 0.308; // to type 2, subtype 1, detach
    energies[3][4] = 0.29; // to type 2, subtype 2
    energies[3][5] = 0.309; // to type 3, subtype 0
    energies[3][15] = 0.281; // to type 3, subtype 0, detach
    energies[3][6] = 0.466; // to type 3, subtype 1
    energies[3][7] = 0.279; // to type 3, subtype 2
    energies[3][8] = 0.72; // to type 4, subtype 0
    energies[3][9] = 0.444; // to type 4, subtype 1
    energies[3][10] = 0.44; // to type 4, subtype 2
    energies[3][11] = 0.455; // to type 5
    // From  type 2, subtype 2
    energies[4][1] = 0.336; // to type 1
    energies[4][2] = 0.281; // to type 2, subtype 0
    energies[4][3] = 0.28; // to type 2, subtype 1
    energies[4][4] = 0.725; // to type 2, subtype 2
    energies[4][5] = 0.219; // to type 3, subtype 0
    energies[4][6] = 0.143; // to type 3, subtype 1
    energies[4][8] = 0.105; // to type 4, subtype 0
    // From  type 3, subtype 0
    energies[5][0] = 0.851; // to type 0
    energies[5][1] = 0.571; // to type 1
    energies[5][12] = 0.778; // to type 1, detach
    energies[5][2] = 0.514; // to type 2, subtype 0
    energies[5][13] = 0.52; // to type 2, subtype 0, detach
    energies[5][3] = 0.533; // to type 2, subtype 1
    energies[5][14] = 0.493; // to type 2, subtype 1, detach
    energies[5][4] = 0.465; // to type 2, subtype 2
    energies[5][5] = 0.493; // to type 3, subtype 0
    energies[5][15] = 0.504; // to type 3, subtype 0, detach
    energies[5][6] = 0.463; // to type 3, subtype 1
    energies[5][8] = 0.445; // to type 4, subtype 0
    // From  type 3, subtype 1
    energies[6][1] = 0.591; // to type 1
    energies[6][2] = 0.511; // to type 2, subtype 0
    energies[6][3] = 0.508; // to type 2, subtype 1
    energies[6][4] = 0.376; // to type 2, subtype 2
    energies[6][5] = 0.447; // to type 3, subtype 0
    energies[6][6] = 0.378; // to type 3, subtype 1
    energies[6][7] = 0.72; // to type 3, subtype 2
    energies[6][8] = 0.34; // to type 4, subtype 0
    energies[6][9] = 0.703; // to type 4, subtype 1
    energies[6][10] = 0.649; // to type 4, subtype 2
    energies[6][11] = 0.709; // to type 5
    // From  type 3, subtype 2
    energies[7][3] = 0.819; // to type 2, subtype 1
    energies[7][6] = 0.803; // to type 3, subtype 1
    energies[7][7] = 0.815; // to type 3, subtype 2
    energies[7][9] = 0.82; // to type 4, subtype 1
    energies[7][10] = 0.744; // to type 4, subtype 2
    energies[7][11] = 0.812; // to type 5
    // From  type 4, subtype 0
    energies[8][1] = 0.771; // to type 1
    energies[8][2] = 0.69; // to type 2, subtype 0
    energies[8][5] = 0.65; // to type 3, subtype 0
    energies[8][6] = 0.582; // to type 3, subtype 1
    energies[8][8] = 0.546; // to type 4, subtype 0
    // From  type 4, subtype 1
    energies[9][3] = 0.952; // to type 2, subtype 1
    energies[9][6] = 0.954; // to type 3, subtype 1
    energies[9][8] = 0.9; // to type 4, subtype 0
    energies[9][9] = 0.951; // to type 4, subtype 1
    energies[9][10] = 0.855; // to type 4, subtype 2
    energies[9][11] = 1.0; // to type 5
    // From  type 4, subtype 2
    energies[10][3] = 0.937; // to type 2, subtype 1
    energies[10][6] = 0.877; // to type 3, subtype 1
    energies[10][7] = 0.924; // to type 3, subtype 2
    energies[10][9] = 0.871; // to type 4, subtype 1
    energies[10][10] = 0.879; // to type 4, subtype 2
    energies[10][11] = 1.1; // to type 5
    // From  type 5
    energies[11][3] = 1.06; // to type 2, subtype 1
    energies[11][6] = 1.01; // to type 3, subtype 1
    energies[11][7] = 1.05; // to type 3, subtype 2
    energies[11][9] = 0.999; // to type 4, subtype 1
    energies[11][10] = 1.02; // to type 4, subtype 2
    energies[11][11] = 0.993; // to type 5
    setEnergies(energies);

    double[] concertedEnergies = new double[9]; // up to 8 atom islands
    concertedEnergies[0] = eImpossible; 
    concertedEnergies[1] = eImpossible; 
    concertedEnergies[2] = 0.097; // dimer
    concertedEnergies[3] = 0.152; 
    concertedEnergies[4] = 0.188; 
    concertedEnergies[5] = 0.278; 
    concertedEnergies[6] = 0.239; 
    concertedEnergies[7] = 0.376; 
    concertedEnergies[8] = 0.401; 
    setConcertedEnergies(concertedEnergies);
    
    double[] multiAtomEnergies = new double[2]; // 2 atoms can move in an edge
    multiAtomEnergies[0] = 0.472; // type 1, one of the atoms goes from 2 to 1 neighbour
    multiAtomEnergies[1] = 0.195; // type 2, both atom go from 2 to 2 neighbours.
    multiAtomEnergies[2] = 0.257; // type 3, one of the atoms goes from 2 to 3 neighbours. (not real)
    multiAtomEnergies[3] = 0.157; // type 4, one of the atoms goes from 2 to 4 neighbours. (not real)
    setMultiAtomEnergies(multiAtomEnergies);
  }
}
