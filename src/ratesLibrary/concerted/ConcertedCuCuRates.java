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
public class ConcertedCuCuRates extends AbstractConcertedRates {
  
  public ConcertedCuCuRates(float temperature) {
    super(temperature);
    double eImpossible = 9999999;
    double[][] energies = new double[12][16];
    for (int i = 0; i < 12; i++) {
      for (int j = 0; j < 16; j++) {
        energies[i][j] = eImpossible;
      }
    }
    // From  type 0
    energies[0][0] = 0.029; // to type 0
    energies[0][1] = 0.409; // to type 1
    energies[0][2] = 0.72; // to type 2, subtype 0
    energies[0][5] = 1.0; // to type 3, subtype 0
    // From  type 1
    energies[1][0] = 0.451; // to type 0
    energies[1][1] = 0.021; // to type 1
    energies[1][2] = 0.348; // to type 2, subtype 0
    energies[1][14] = 0.843; // to type 2, subtype 1, detach
    // From  type 2, subtype 0
    energies[2][0] = 0.77; // to type 0
    energies[2][1] = 0.359; // to type 1
    energies[2][12] = 0.687; // to type 1, detach
    energies[2][2] = 0.273; // to type 2, subtype 0
    energies[2][3] = 0.285; // to type 2, subtype 1
    energies[2][4] = 0.189; // to type 2, subtype 2
    energies[2][5] = 0.213; // to type 3, subtype 0
    energies[2][15] = 0.488; // to type 3, subtype 0, detach
    energies[2][6] = 0.201; // to type 3, subtype 1
    energies[2][8] = 0.562; // to type 4, subtype 0
    // From  type 2, subtype 1
    energies[3][0] = 0.817; // to type 0
    energies[3][1] = 0.405; // to type 1
    energies[3][12] = 0.646; // to type 1, detach
    energies[3][2] = 0.338; // to type 2, subtype 0
    energies[3][13] = 0.299; // to type 2, subtype 0, detach
    energies[3][4] = 0.232; // to type 2, subtype 2
    energies[3][5] = 0.278; // to type 3, subtype 0
    energies[3][8] = 0.217; // to type 4, subtype 0
    energies[3][9] = 0.684; // to type 4, subtype 1
    energies[3][10] = 0.245; // to type 4, subtype 2
    energies[3][11] = 0.245; // to type 5
    // From  type 2, subtype 2
    energies[4][1] = 0.726; // to type 1
    energies[4][2] = 0.816; // to type 2, subtype 0
    energies[4][3] = 0.746; // to type 2, subtype 1
    // From  type 3, subtype 0
    energies[5][0] = 1.072; // to type 0
    energies[5][1] = 0.684; // to type 1
    energies[5][12] = 0.905; // to type 1, detach
    energies[5][2] = 0.56; // to type 2, subtype 0
    energies[5][13] = 0.564; // to type 2, subtype 0, detach
    energies[5][3] = 0.58; // to type 2, subtype 1
    energies[5][4] = 0.444; // to type 2, subtype 2
    energies[5][5] = 0.482; // to type 3, subtype 0
    energies[5][15] = 0.492; // to type 3, subtype 0, detach
    energies[5][6] = 0.434; // to type 3, subtype 1
    energies[5][8] = 0.398; // to type 4, subtype 0
    // From  type 3, subtype 1
    energies[6][1] = 0.731; // to type 1
    energies[6][2] = 0.52; // to type 2, subtype 0
    energies[6][4] = 0.327; // to type 2, subtype 2
    energies[6][5] = 0.396; // to type 3, subtype 0
    energies[6][6] = 0.244; // to type 3, subtype 1
    energies[6][7] = 0.699; // to type 3, subtype 2
    energies[6][8] = 0.586; // to type 4, subtype 0
    energies[6][9] = 0.72; // to type 4, subtype 1
    // From  type 3, subtype 2
    energies[7][3] = 0.919; // to type 2, subtype 1
    energies[7][6] = 0.777; // to type 3, subtype 1
    energies[7][9] = 0.946; // to type 4, subtype 1
    energies[7][10] = 0.668; // to type 4, subtype 2
    energies[7][11] = 0.988; // to type 5
    // From  type 4, subtype 0
    energies[8][1] = 0.986; // to type 1
    energies[8][2] = 0.777; // to type 2, subtype 0
    energies[8][5] = 0.648; // to type 3, subtype 0
    energies[8][6] = 0.559; // to type 3, subtype 1
    energies[8][8] = 0.506; // to type 4, subtype 0
    // From  type 4, subtype 1
    energies[9][3] = 0.946; // to type 2, subtype 1
    energies[9][6] = 1.0; // to type 3, subtype 1
    energies[9][8] = 0.824; // to type 4, subtype 0
    energies[9][9] = 1.02; // to type 4, subtype 1
    energies[9][10] = 0.757; // to type 4, subtype 2
    // From  type 4, subtype 2
    energies[10][3] = 0.978; // to type 2, subtype 1
    energies[10][6] = 0.774; // to type 3, subtype 1
    energies[10][7] = 0.988; // to type 3, subtype 2
    // From  type 5
    energies[11][3] = 1.127; // to type 2, subtype 1
    energies[11][15] = 0.983; // to type 3, subtype 0, detach
    energies[11][6] = 1.09; // to type 3, subtype 1
    energies[11][10] = 0.914; // to type 4, subtype 2
    energies[11][11] = 0.814; // to type 5
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
    
    double[] multiAtomEnergies = new double[4]; // 2 atoms can move in an edge
    multiAtomEnergies[0] = 0.448; // type 1, one of the atoms goes from 2 to 1 neighbour.(not real)
    multiAtomEnergies[1] = 0.357; // type 2, both atom go from 2 to 2 neighbours.(not real)
    multiAtomEnergies[2] = 0.172; // type 3, one of the atoms goes from 2 to 3 neighbours.
    multiAtomEnergies[3] = 0.147; // type 4, one of the atoms goes from 2 to 4 neighbours.
    setMultiAtomEnergies(multiAtomEnergies);
  }
}
