# Copyright (C) 2018 J. Alberdi-Rodriguez
#
# This file is part of Morphokinetics.
#
# Morphokinetics is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Morphokinetics is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.

import numpy as np

def getConcertedEnergies(self):
    libSwitcher = {
        "AgAg": getConcertedEnergiesAgAg,
        "PdPd": getConcertedEnergiesPdPd,
        "NiCu": getConcertedEnergiesNiCu,
        "CuNi": getConcertedEnergiesCuNi,
    }
    func = libSwitcher.get(self.rLib)
    single, concerted = func()

    single = single.reshape(192)
    allEnergies = np.concatenate([single, concerted])
    
    return allEnergies

def getConcertedEnergiesAgAg():
    energies = 99999999*np.ones(dtype=float, shape=(12,16))
    eImpossible = 99999999

    # From  type 0
    energies[0][0] = 0.059; # to type 0
    energies[0][1] = 0.05; # to type 1
    energies[0][2] = 0.055; # to type 2, subtype 0
    energies[0][3] = 0.317; # to type 2, subtype 1
    energies[0][5] = 0.043; # to type 3, subtype 0
    # From  type 1
    energies[1][0] = 0.388; # to type 0
    energies[1][1] = 0.065; # to type 1
    energies[1][12] = 0.3; # to type 1, detach
    energies[1][2] = 0.06; # to type 2, subtype 0
    energies[1][13] = 0.317; # to type 2, subtype 0, detach
    energies[1][3] = 0.06; # to type 2, subtype 1
    energies[1][14] = 0.748; # to type 2, subtype 1, detach
    energies[1][4] = 0.046; # to type 2, subtype 2
    energies[1][5] = 0.04; # to type 3, subtype 0
    energies[1][15] = 0.483; # to type 3, subtype 0, detach
    energies[1][6] = 0.045; # to type 3, subtype 1
    energies[1][8] = 0.04; # to type 4, subtype 0
    # From  type 2, subtype 0
    energies[2][0] = 0.633; # to type 0
    energies[2][1] = 0.338; # to type 1
    energies[2][12] = 0.584; # to type 1, detach
    energies[2][2] = 0.289; # to type 2, subtype 0
    energies[2][13] = 0.584; # to type 2, subtype 0, detach
    energies[2][3] = 0.302; # to type 2, subtype 1
    energies[2][14] = 0.323; # to type 2, subtype 1, detach
    energies[2][4] = 0.256; # to type 2, subtype 2
    energies[2][5] = 0.274; # to type 3, subtype 0
    energies[2][15] = 0.497; # to type 3, subtype 0, detach
    energies[2][6] = 0.253; # to type 3, subtype 1
    energies[2][8] = 0.24; # to type 4, subtype 0
    # From  type 2, subtype 1
    energies[3][0] = 0.668; # to type 0
    energies[3][1] = 0.363; # to type 1
    energies[3][12] = 0.5; # to type 1, detach
    energies[3][2] = 0.322; # to type 2, subtype 0
    energies[3][13] = 0.321; # to type 2, subtype 0, detach
    energies[3][3] = 0.329; # to type 2, subtype 1
    energies[3][14] = 0.308; # to type 2, subtype 1, detach
    energies[3][4] = 0.29; # to type 2, subtype 2
    energies[3][5] = 0.309; # to type 3, subtype 0
    energies[3][15] = 0.281; # to type 3, subtype 0, detach
    energies[3][6] = 0.466; # to type 3, subtype 1
    energies[3][7] = 0.279; # to type 3, subtype 2
    energies[3][8] = 0.72; # to type 4, subtype 0
    energies[3][9] = 0.444; # to type 4, subtype 1
    energies[3][10] = 0.44; # to type 4, subtype 2
    energies[3][11] = 0.455; # to type 5
    # From  type 2, subtype 2
    energies[4][1] = 0.336; # to type 1
    energies[4][2] = 0.281; # to type 2, subtype 0
    energies[4][3] = 0.28; # to type 2, subtype 1
    energies[4][4] = 0.725; # to type 2, subtype 2
    energies[4][5] = 0.219; # to type 3, subtype 0
    energies[4][6] = 0.143; # to type 3, subtype 1
    energies[4][8] = 0.105; # to type 4, subtype 0
    # From  type 3, subtype 0
    energies[5][0] = 0.851; # to type 0
    energies[5][1] = 0.571; # to type 1
    energies[5][12] = 0.778; # to type 1, detach
    energies[5][2] = 0.514; # to type 2, subtype 0
    energies[5][13] = 0.52; # to type 2, subtype 0, detach
    energies[5][3] = 0.533; # to type 2, subtype 1
    energies[5][14] = 0.493; # to type 2, subtype 1, detach
    energies[5][4] = 0.465; # to type 2, subtype 2
    energies[5][5] = 0.493; # to type 3, subtype 0
    energies[5][15] = 0.504; # to type 3, subtype 0, detach
    energies[5][6] = 0.463; # to type 3, subtype 1
    energies[5][8] = 0.445; # to type 4, subtype 0
    # From  type 3, subtype 1
    energies[6][1] = 0.591; # to type 1
    energies[6][2] = 0.511; # to type 2, subtype 0
    energies[6][3] = 0.508; # to type 2, subtype 1
    energies[6][4] = 0.376; # to type 2, subtype 2
    energies[6][5] = 0.447; # to type 3, subtype 0
    energies[6][6] = 0.378; # to type 3, subtype 1
    energies[6][7] = 0.72; # to type 3, subtype 2
    energies[6][8] = 0.34; # to type 4, subtype 0
    energies[6][9] = 0.703; # to type 4, subtype 1
    energies[6][10] = 0.649; # to type 4, subtype 2
    energies[6][11] = 0.709; # to type 5
    # From  type 3, subtype 2
    energies[7][3] = 0.819; # to type 2, subtype 1
    energies[7][6] = 0.803; # to type 3, subtype 1
    energies[7][7] = 0.815; # to type 3, subtype 2
    energies[7][9] = 0.82; # to type 4, subtype 1
    energies[7][10] = 0.744; # to type 4, subtype 2
    energies[7][11] = 0.812; # to type 5
    # From  type 4, subtype 0
    energies[8][1] = 0.771; # to type 1
    energies[8][2] = 0.69; # to type 2, subtype 0
    energies[8][5] = 0.65; # to type 3, subtype 0
    energies[8][6] = 0.582; # to type 3, subtype 1
    energies[8][8] = 0.546; # to type 4, subtype 0
    # From  type 4, subtype 1
    energies[9][3] = 0.952; # to type 2, subtype 1
    energies[9][6] = 0.954; # to type 3, subtype 1
    energies[9][8] = 0.9; # to type 4, subtype 0
    energies[9][9] = 0.951; # to type 4, subtype 1
    energies[9][10] = 0.855; # to type 4, subtype 2
    energies[9][11] = 1.0; # to type 5
    # From  type 4, subtype 2
    energies[10][3] = 0.937; # to type 2, subtype 1
    energies[10][6] = 0.877; # to type 3, subtype 1
    energies[10][7] = 0.924; # to type 3, subtype 2
    energies[10][9] = 0.871; # to type 4, subtype 1
    energies[10][10] = 0.879; # to type 4, subtype 2
    energies[10][11] = 1.1; # to type 5
    # From  type 5
    energies[11][3] = 1.06; # to type 2, subtype 1
    energies[11][6] = 1.01; # to type 3, subtype 1
    energies[11][7] = 1.05; # to type 3, subtype 2
    energies[11][9] = 0.999; # to type 4, subtype 1
    energies[11][10] = 1.02; # to type 4, subtype 2
    energies[11][11] = 0.993; # to type 5

    concertedEnergies = 99999999*np.ones(9,dtype=float)
    concertedEnergies[2] = 0.097; # dimer
    concertedEnergies[3] = 0.152; 
    concertedEnergies[4] = 0.188; 
    concertedEnergies[5] = 0.278; 
    concertedEnergies[6] = 0.239; 
    concertedEnergies[7] = 0.376; 
    concertedEnergies[8] = 0.401; 

    return energies, concertedEnergies
    
def getConcertedEnergiesPdPd():
    energies = 99999999*np.ones(dtype=float, shape=(12,16))
    eImpossible = 99999999

    # From  type 0
    energies[0][0] = 0.034; # to type 0
    energies[0][1] = 0.03; # to type 1
    energies[0][2] = 0.027; # to type 2, subtype 0
    energies[0][3] = 0.0012; # to type 2, subtype 1
    energies[0][5] = 0.0015; # to type 3, subtype 0
    # From  type 1
    energies[1][0] = 0.539; # to type 0
    energies[1][1] = 0.296; # to type 1
    energies[1][12] = 0.352; # to type 1, detach
    energies[1][2] = 0.029; # to type 2, subtype 0
    energies[1][13] = 0.416; # to type 2, subtype 0, detach
    energies[1][3] = 0.021; # to type 2, subtype 1
    energies[1][14] = 0.907; # to type 2, subtype 1, detach
    energies[1][4] = 0.222; # to type 2, subtype 2
    energies[1][5] = 0.014; # to type 3, subtype 0
    energies[1][15] = 0.179; # to type 3, subtype 0, detach
    energies[1][6] = 0.001; # to type 3, subtype 1
    energies[1][8] = 0.122; # to type 4, subtype 0
    # From  type 2, subtype 0
    energies[2][0] = 0.964; # to type 0
    energies[2][1] = 0.674; # to type 1
    energies[2][12] = 0.811; # to type 1, detach
    energies[2][2] = 0.38; # to type 2, subtype 0
    energies[2][13] = 0.64; # to type 2, subtype 0, detach
    energies[2][3] = 0.406; # to type 2, subtype 1
    energies[2][14] = 0.274; # to type 2, subtype 1, detach
    energies[2][4] = 0.309; # to type 2, subtype 2
    energies[2][5] = 0.321; # to type 3, subtype 0
    energies[2][15] = 0.316; # to type 3, subtype 0, detach
    energies[2][6] = 0.29; # to type 3, subtype 1
    energies[2][8] = 0.244; # to type 4, subtype 0
    # From  type 2, subtype 1
    energies[3][0] = 1.03; # to type 0
    energies[3][1] = 0.494; # to type 1
    energies[3][12] = 0.841; # to type 1, detach
    energies[3][2] = 0.451; # to type 2, subtype 0
    energies[3][13] = 0.679; # to type 2, subtype 0, detach
    energies[3][3] = 0.454; # to type 2, subtype 1
    energies[3][14] = 0.325; # to type 2, subtype 1, detach
    energies[3][4] = 0.352; # to type 2, subtype 2
    energies[3][5] = 0.396; # to type 3, subtype 0
    energies[3][15] = 0.331; # to type 3, subtype 0, detach
    energies[3][6] = 0.636; # to type 3, subtype 1
    energies[3][7] = 0.473; # to type 3, subtype 2
    energies[3][8] = 0.318; # to type 4, subtype 0
    energies[3][9] = 0.844; # to type 4, subtype 1
    energies[3][10] = 0.293; # to type 4, subtype 2
    energies[3][11] = 0.3; # to type 5
    # From  type 2, subtype 2
    energies[4][1] = 0.72; # to type 1
    energies[4][2] = 0.627; # to type 2, subtype 0
    energies[4][3] = 0.317; # to type 2, subtype 1
    energies[4][4] = 0.858; # to type 2, subtype 2
    energies[4][5] = 0.177; # to type 3, subtype 0
    energies[4][6] = 0.54; # to type 3, subtype 1
    energies[4][8] = 0.465; # to type 4, subtype 0
    # From  type 3, subtype 0
    energies[5][0] = 1.372; # to type 0
    energies[5][1] = 1.069; # to type 1
    energies[5][12] = 1.193; # to type 1, detach
    energies[5][2] = 0.755; # to type 2, subtype 0
    energies[5][13] = 1.03; # to type 2, subtype 0, detach
    energies[5][3] = 0.8; # to type 2, subtype 1
    energies[5][14] = 0.681; # to type 2, subtype 1, detach
    energies[5][4] = 0.664; # to type 2, subtype 2
    energies[5][5] = 0.679; # to type 3, subtype 0
    energies[5][15] = 0.725; # to type 3, subtype 0, detach
    energies[5][6] = 0.643; # to type 3, subtype 1
    energies[5][8] = 0.834; # to type 4, subtype 0
    # From  type 3, subtype 1
    energies[6][1] = 0.7; # to type 1
    energies[6][2] = 0.729; # to type 2, subtype 0
    energies[6][3] = 0.507; # to type 2, subtype 1
    energies[6][4] = 0.577; # to type 2, subtype 2
    energies[6][5] = 0.486; # to type 3, subtype 0
    energies[6][6] = 0.876; # to type 3, subtype 1
    energies[6][7] = 0.405; # to type 3, subtype 2
    energies[6][8] = 0.918; # to type 4, subtype 0
    energies[6][9] = 0.716; # to type 4, subtype 1
    energies[6][10] = 0.618; # to type 4, subtype 2
    energies[6][11] = 0.922; # to type 5
    # From  type 3, subtype 2
    energies[7][3] = 1.143; # to type 2, subtype 1
    energies[7][6] = 1.08; # to type 3, subtype 1
    energies[7][7] = 1.231; # to type 3, subtype 2
    energies[7][9] = 1.287; # to type 4, subtype 1
    energies[7][10] = 0.828; # to type 4, subtype 2
    energies[7][11] = 1.273; # to type 5
    # From  type 4, subtype 0
    energies[8][1] = 1.39; # to type 1
    energies[8][2] = 1.261; # to type 2, subtype 0
    energies[8][5] = 0.903; # to type 3, subtype 0
    energies[8][6] = 1.178; # to type 3, subtype 1
    energies[8][8] = 0.748; # to type 4, subtype 0
    # From  type 4, subtype 1
    energies[9][3] = 1.352; # to type 2, subtype 1
    energies[9][6] = 1.383; # to type 3, subtype 1
    energies[9][8] = 1.174; # to type 4, subtype 0
    energies[9][9] = 1.377; # to type 4, subtype 1
    energies[9][10] = 1.019; # to type 4, subtype 2
    energies[9][11] = 1.0; # to type 5
    # From  type 4, subtype 2
    energies[10][3] = 1.266; # to type 2, subtype 1
    energies[10][6] = 1.264; # to type 3, subtype 1
    energies[10][7] = 1.233; # to type 3, subtype 2
    energies[10][9] = 1.297; # to type 4, subtype 1
    energies[10][10] = 0.971; # to type 4, subtype 2
    energies[10][11] = 1.05; # to type 5
    # From  type 5
    energies[11][3] = 1.47; # to type 2, subtype 1
    energies[11][15] = 1.3; # to type 3, subtype 0, detach
    energies[11][6] = 1.413; # to type 3, subtype 1
    energies[11][7] = 1.236; # to type 3, subtype 2
    energies[11][9] = 1.293; # to type 4, subtype 1
    energies[11][10] = 1.205; # to type 4, subtype 2
    energies[11][11] = 1.076; # to type 5 

    concertedEnergies = 99999999*np.ones(9,dtype=float)
    concertedEnergies[2] = 0.099; # dimer
    concertedEnergies[3] = 0.168; 
    concertedEnergies[4] = 0.179; 
    concertedEnergies[5] = 0.242; 
    concertedEnergies[6] = 0.253; 
    concertedEnergies[7] = 0.325; 
    concertedEnergies[8] = 0.438; 

    return energies, concertedEnergies

def getConcertedEnergiesNiCu():
    energies = 99999999*np.ones(dtype=float, shape=(12,16))
    eImpossible = 99999999

def getConcertedEnergiesCuNi():
    energies = 99999999*np.ones(dtype=float, shape=(12,16))
    eImpossible = 99999999
