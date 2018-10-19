import numpy as np

class EnergyNiCu:

    def __init__(self):
        """ Ni on Cu """
        
        self.energies = 99999999*np.ones(dtype=float, shape=(12,16))
        eImpossible = 99999999
        
        # From  type 0
        self.energies[0][0] = 0.031; # to type 0
        self.energies[0][1] = 0.028; # to type 1
        self.energies[0][2] = 0.015; # to type 2, subtype 0
        self.energies[0][3] = 0.0091; # to type 2, subtype 1
        self.energies[0][5] = 0.0077; # to type 3, subtype 0
        # From  type 1
        self.energies[1][0] = 0.568; # to type 0
        self.energies[1][1] = 0.016; # to type 1
        self.energies[1][12] = 0.505; # to type 1, detach
        self.energies[1][2] = 0.014; # to type 2, subtype 0
        self.energies[1][13] = 0.159; # to type 2, subtype 0, detach
        self.energies[1][3] = 0.006; # to type 2, subtype 1
        self.energies[1][14] = 0.172; # to type 2, subtype 1, detach
        self.energies[1][4] = 0.0; # to type 2, subtype 2
        self.energies[1][5] = 0.001; # to type 3, subtype 0
        self.energies[1][15] = 0.18; # to type 3, subtype 0, detach
        self.energies[1][6] = 0.0; # to type 3, subtype 1
        self.energies[1][8] = 0.0229; # to type 4, subtype 0
        # From  type 2, subtype 0
        self.energies[2][0] = 0.938; # to type 0
        self.energies[2][1] = 0.439; # to type 1
        self.energies[2][12] = 0.746; # to type 1, detach
        self.energies[2][2] = 0.364; # to type 2, subtype 0
        self.energies[2][13] = 0.743; # to type 2, subtype 0, detach
        self.energies[2][3] = 0.389; # to type 2, subtype 1
        self.energies[2][14] = 0.541; # to type 2, subtype 1, detach
        self.energies[2][4] = 0.305; # to type 2, subtype 2
        self.energies[2][5] = 0.5; # to type 3, subtype 0
        self.energies[2][15] = 0.562; # to type 3, subtype 0, detach
        self.energies[2][6] = 0.319; # to type 3, subtype 1
        self.energies[2][8] = 0.263; # to type 4, subtype 0
        # From  type 2, subtype 1
        self.energies[3][0] = 0.8; # to type 0
        self.energies[3][1] = 0.489; # to type 1
        self.energies[3][12] = 0.55; # to type 1, detach
        self.energies[3][2] = 0.45; # to type 2, subtype 0
        self.energies[3][13] = 0.467; # to type 2, subtype 0, detach
        self.energies[3][3] = 0.448; # to type 2, subtype 1
        self.energies[3][14] = 0.356; # to type 2, subtype 1, detach
        self.energies[3][4] = 0.366; # to type 2, subtype 2
        self.energies[3][5] = 0.404; # to type 3, subtype 0
        self.energies[3][15] = 0.372; # to type 3, subtype 0, detach
        self.energies[3][6] = 0.382; # to type 3, subtype 1
        self.energies[3][7] = 0.659; # to type 3, subtype 2
        self.energies[3][8] = 0.353; # to type 4, subtype 0
        self.energies[3][9] = 0.531; # to type 4, subtype 1
        self.energies[3][10] = 0.423; # to type 4, subtype 2
        self.energies[3][11] = 0.643; # to type 5
        # From  type 2, subtype 2
        self.energies[4][1] = 0.678; # to type 1
        self.energies[4][2] = 0.34; # to type 2, subtype 0
        self.energies[4][3] = 0.334; # to type 2, subtype 1
        self.energies[4][4] = 0.596; # to type 2, subtype 2
        self.energies[4][5] = 0.283; # to type 3, subtype 0
        self.energies[4][6] = 0.115; # to type 3, subtype 1
        self.energies[4][8] = 0.17; # to type 4, subtype 0
        # From  type 3, subtype 0
        self.energies[5][0] = 1.29; # to type 0
        self.energies[5][1] = 0.804; # to type 1
        self.energies[5][12] = 2; # IT DOESN'T HAPPEN; IT KEEPS OSCILATING to type 1, detach
        self.energies[5][2] = 0.704; # to type 2, subtype 0
        self.energies[5][13] = 0.5; # to type 2, subtype 0, detach
        self.energies[5][3] = 0.742; # to type 2, subtype 1
        self.energies[5][14] = 0.662; # to type 2, subtype 1, detach
        self.energies[5][4] = 0.629; # to type 2, subtype 2
        self.energies[5][5] = 0.644; # to type 3, subtype 0
        self.energies[5][15] = 0.658; # to type 3, subtype 0, detach
        self.energies[5][6] = 0.645; # to type 3, subtype 1
        self.energies[5][8] = 0.571; # to type 4, subtype 0
        # From  type 3, subtype 1
        self.energies[6][1] = 0.875; # to type 1
        self.energies[6][2] = 0.69; # to type 2, subtype 0
        self.energies[6][3] = 0.693; # to type 2, subtype 1
        self.energies[6][4] = 0.482; # to type 2, subtype 2
        self.energies[6][5] = 0.57; # to type 3, subtype 0
        self.energies[6][6] = 0.518; # to type 3, subtype 1
        self.energies[6][7] = 0.902; # to type 3, subtype 2
        self.energies[6][8] = 0.429; # to type 4, subtype 0
        self.energies[6][9] = 0.892; # to type 4, subtype 1
        self.energies[6][10] = 0.931; # to type 4, subtype 2
        self.energies[6][11] = 0.945; # to type 5
        # From  type 3, subtype 2
        self.energies[7][3] = 1.162; # to type 2, subtype 1
        self.energies[7][6] = 1.144; # to type 3, subtype 1
        self.energies[7][7] = 1.191; # to type 3, subtype 2
        self.energies[7][9] = 1.129; # to type 4, subtype 1
        self.energies[7][10] = 1.259; # to type 4, subtype 2
        self.energies[7][11] = 0.992; # to type 5
        # From  type 4, subtype 0
        self.energies[8][1] = 1.145; # to type 1
        self.energies[8][2] = 0.959; # to type 2, subtype 0
        self.energies[8][5] = 0.858; # to type 3, subtype 0
        self.energies[8][6] = 0.839; # to type 3, subtype 1
        self.energies[8][8] = 0.726; # to type 4, subtype 0
        # From  type 4, subtype 1
        self.energies[9][3] = 1.33; # to type 2, subtype 1
        self.energies[9][6] = 1.384; # to type 3, subtype 1
        self.energies[9][8] = 1.225; # to type 4, subtype 0
        self.energies[9][9] = 1.4; # to type 4, subtype 1
        self.energies[9][10] = 1.226; # to type 4, subtype 2
        self.energies[9][11] = 1.0; # to type 5
        # From  type 4, subtype 2
        self.energies[10][3] = 1.31; # to type 2, subtype 1
        self.energies[10][6] = 1.12; # to type 3, subtype 1
        self.energies[10][7] = 1.291; # to type 3, subtype 2
        self.energies[10][9] = 1.09; # to type 4, subtype 1
        self.energies[10][10] = 1.175; # to type 4, subtype 2
        self.energies[10][11] = 1.1; # to type 5
        # From  type 5
        self.energies[11][3] = 1.507; # to type 2, subtype 1
        self.energies[11][15] = 1.382; # to type 3, subtype 0, detach
        self.energies[11][6] = 1.482; # to type 3, subtype 1
        self.energies[11][7] = 1.326; # to type 3, subtype 2
        self.energies[11][9] = 1.326; # to type 4, subtype 1
        self.energies[11][10] = 1.361; # to type 4, subtype 2
        self.energies[11][11] = 1.174; # to type 5
        
        self.concertedEnergies = 99999999*np.ones(9,dtype=float)
        self.concertedEnergies[0] = eImpossible; 
        self.concertedEnergies[1] = eImpossible; 
        self.concertedEnergies[2] = 0.021; # dimer
        self.concertedEnergies[3] = 0.148;
        self.concertedEnergies[4] = 0.157;
        self.concertedEnergies[5] = 0.220; 
        self.concertedEnergies[6] = 0.199; 
        self.concertedEnergies[7] = 0.369; 
        self.concertedEnergies[8] = 0.380; 

        self.multiAtomEnergies = 99999999*np.ones(4,dtype=float)
        self.multiAtomEnergies[0] = 0.654; # type 1, one of the atoms goes from 2 to 1 neighbour
        self.multiAtomEnergies[1] = 0.633; # type 2, both atom go from 2 to 2 neighbours.
        self.multiAtomEnergies[2] = 0.294; # type 3, one of the atoms goes from 2 to 3 neighbours.
        self.multiAtomEnergies[3] = 0.218; # type 4, one of the atoms goes from 2 to 4 neighbours.
