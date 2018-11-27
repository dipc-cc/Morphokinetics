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
public class ConcertedCuNiRates extends AbstractConcertedRates {
  
  public ConcertedCuNiRates(float temperature) {
    super(temperature);
    double eImpossible = 9999999;
    double[][] energies = new double[12][16];
    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 16; j++) {
        energies[i][j] = eImpossible;
      }
    }
    // From  type 0
    energies[0][0] = 0.052; // to type 0
    energies[0][1] = 0.035; // to type 1
    energies[0][2] = 0.031; // to type 2, subtype 0
    energies[0][3] = 0.029; // to type 2, subtype 1
    energies[0][5] = 0.007; // to type 3, subtype 0
    // From  type 1
    energies[1][0] = 0.428; // to type 0
    energies[1][1] = 0.038; // to type 1
    energies[1][12] = 0.333; // to type 1, detach
    energies[1][2] = 0.026; // to type 2, subtype 0
    energies[1][13] = 0.258; // to type 2, subtype 0, detach
    energies[1][3] = 0.043; // to type 2, subtype 1
    energies[1][14] = 0.194; // to type 2, subtype 1, detach
    energies[1][4] = 0.01; // to type 2, subtype 2
    energies[1][5] = 0.01; // to type 3, subtype 0
    energies[1][15] = 0.187; // to type 3, subtype 0, detach
    energies[1][6] = 0.007; // to type 3, subtype 1
    energies[1][8] = 0.0; // to type 4, subtype 0
    // From  type 2, subtype 0
    energies[2][0] = 0.736; // to type 0
    energies[2][1] = 0.36; // to type 1
    energies[2][12] = 0.608; // to type 1, detach
    energies[2][2] = 0.268; // to type 2, subtype 0
    energies[2][13] = 0.625; // to type 2, subtype 0, detach
    energies[2][3] = 0.261; // to type 2, subtype 1
    energies[2][14] = 0.383; // to type 2, subtype 1, detach
    energies[2][4] = 0.167; // to type 2, subtype 2
    energies[2][5] = 0.22; // to type 3, subtype 0
    energies[2][15] = 0.412; // to type 3, subtype 0, detach
    energies[2][6] = 0.164; // to type 3, subtype 1
    energies[2][8] = 0.141; // to type 4, subtype 0
    // From  type 2, subtype 1
    energies[3][0] = 0.75; // to type 0
    energies[3][1] = 0.417; // to type 1
    energies[3][12] = 0.565; // to type 1, detach
    energies[3][2] = 0.308; // to type 2, subtype 0
    energies[3][13] = 0.206; // to type 2, subtype 0, detach
    energies[3][3] = 0.293; // to type 2, subtype 1
    energies[3][14] = 0.167; // to type 2, subtype 1, detach
    energies[3][4] = 0.197; // to type 2, subtype 2
    energies[3][5] = 0.258; // to type 3, subtype 0
    energies[3][15] = 0.228; // to type 3, subtype 0, detach
    energies[3][6] = 0.208; // to type 3, subtype 1
    energies[3][7] = 0.509; // to type 3, subtype 2
    energies[3][8] = 0.187; // to type 4, subtype 0
    energies[3][9] = 0.418; // to type 4, subtype 1
    energies[3][10] = 0.411; // to type 4, subtype 2
    energies[3][11] = 0.395; // to type 5
    // From  type 2, subtype 2
    energies[4][1] = 0.403; // to type 1
    energies[4][2] = 0.198; // to type 2, subtype 0
    energies[4][3] = 0.223; // to type 2, subtype 1
    energies[4][4] = 0.807; // to type 2, subtype 2
    energies[4][5] = 0.13; // to type 3, subtype 0
    energies[4][6] = 0.022; // to type 3, subtype 1
    energies[4][8] = 0.0063; // to type 4, subtype 0
    // From  type 3, subtype 0
    energies[5][0] = 1.01; // to type 0
    energies[5][1] = 0.663; // to type 1
    energies[5][12] = 0.828; // to type 1, detach
    energies[5][2] = 0.546; // to type 2, subtype 0
    energies[5][13] = 0.399; // to type 2, subtype 0, detach
    energies[5][3] = 0.539; // to type 2, subtype 1
    energies[5][14] = 0.413; // to type 2, subtype 1, detach
    energies[5][4] = 0.39; // to type 2, subtype 2
    energies[5][5] = 0.473; // to type 3, subtype 0
    energies[5][15] = 0.376; // to type 3, subtype 0, detach
    energies[5][6] = 0.388; // to type 3, subtype 1
    energies[5][8] = 0.353; // to type 4, subtype 0
    // From  type 3, subtype 1
    energies[6][1] = 0.697; // to type 1
    energies[6][2] = 0.507; // to type 2, subtype 0
    energies[6][3] = 0.479; // to type 2, subtype 1
    energies[6][4] = 0.4; // to type 2, subtype 2
    energies[6][5] = 0.41; // to type 3, subtype 0
    energies[6][6] = 0.252; // to type 3, subtype 1
    energies[6][7] = 0.755; // to type 3, subtype 2
    energies[6][8] = 0.215; // to type 4, subtype 0
    energies[6][9] = 0.692; // to type 4, subtype 1
    energies[6][10] = 0.669; // to type 4, subtype 2
    energies[6][11] = 0.67; // to type 5
    // From  type 3, subtype 2
    energies[7][3] = 0.861; // to type 2, subtype 1
    energies[7][6] = 0.778; // to type 3, subtype 1
    energies[7][7] = 0.814; // to type 3, subtype 2
    energies[7][9] = 0.751; // to type 4, subtype 1
    energies[7][10] = 0.687; // to type 4, subtype 2
    energies[7][11] = 0.679; // to type 5
    // From  type 4, subtype 0
    energies[8][1] = 0.947; // to type 1
    energies[8][2] = 0.748; // to type 2, subtype 0
    energies[8][3] = 0.957; // to type 2, subtype 1
    energies[8][4] = 0.851; // to type 2, subtype 2
    energies[8][5] = 0.627; // to type 3, subtype 0
    energies[8][6] = 0.468; // to type 3, subtype 1
    energies[8][8] = 0.397; // to type 4, subtype 0
    // From  type 4, subtype 1
    energies[9][3] = 1.01; // to type 2, subtype 1
    energies[9][6] = 0.895; // to type 3, subtype 1
    energies[9][7] = 1.107; // to type 3, subtype 2
    energies[9][8] = 0.727; // to type 4, subtype 0
    energies[9][9] = 0.938; // to type 4, subtype 1
    energies[9][10] = 0.956; // to type 4, subtype 2
    energies[9][11] = 0.84; // to type 5
    // From  type 4, subtype 2
    energies[10][3] = 1.02; // to type 2, subtype 1
    energies[10][6] = 1.02; // to type 3, subtype 1
    energies[10][7] = 0.961; // to type 3, subtype 2
    energies[10][9] = 0.992; // to type 4, subtype 1
    energies[10][10] = 1.043; // to type 4, subtype 2
    energies[10][11] = 0.716; // to type 5
    // From  type 5
    energies[11][3] = 1.144; // to type 2, subtype 1
    energies[11][15] = 1.01; // to type 3, subtype 0, detach
    energies[11][6] = 1.13; // to type 3, subtype 1
    energies[11][7] = 1.008; // to type 3, subtype 2
    energies[11][9] = 1.088; // to type 4, subtype 1
    energies[11][10] = 1.067; // to type 4, subtype 2
    energies[11][11] = 0.908; // to type 5
    setEnergies(energies);
    
    double[] concertedEnergies = new double[9]; // up to 8 atom islands
    concertedEnergies[0] = eImpossible; 
    concertedEnergies[1] = eImpossible; 
    concertedEnergies[2] = 0.062; // dimer
    concertedEnergies[3] = 0.161; 
    concertedEnergies[4] = 0.182; 
    concertedEnergies[5] = 0.222; 
    concertedEnergies[6] = 0.201; 
    concertedEnergies[7] = 0.403; 
    concertedEnergies[8] = 0.372; 
    setConcertedEnergies(concertedEnergies);
    
    double[] multiAtomEnergies = new double[4]; // 2 atoms can move in an edge
    multiAtomEnergies[0] = 0.481; // type 1, one of the atoms goes from 2 to 1 neighbour
    multiAtomEnergies[1] = 0.437; // type 2, both atom go from 2 to 2 neighbours.
    multiAtomEnergies[2] = 0.397; // type 3, one of the atoms goes from 2 to 3 neighbours.
    multiAtomEnergies[3] = 0.228; // type 4, one of the atoms goes from 2 to 4 neighbours.
    setMultiAtomEnergies(multiAtomEnergies);
  }
}
