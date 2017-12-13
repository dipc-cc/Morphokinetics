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
    
    return single

def getConcertedEnergiesAgAg():
    energies = 99999999*np.ones(dtype=float, shape=(12,16))
    eImpossible = 99999999

    
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
