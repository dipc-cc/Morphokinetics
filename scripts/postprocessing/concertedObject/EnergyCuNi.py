import numpy as np

class EnergyCuNi:

    def __init__(self):
        """ Cu on Ni """
        
        self.energies = 99999999*np.ones(dtype=float, shape=(12,16))
        eImpossible = 99999999
        # From  type 0
        self.energies[0][0] = 0.052; # to type 0
        self.energies[0][1] = 0.044; # to type 1
        self.energies[0][2] = 0.029; # to type 2, subtype 0
        self.energies[0][3] = 0.005; # to type 2, subtype 1
        self.energies[0][5] = 0.0024; # to type 3, subtype 0
        # From  type 1
        self.energies[1][0] = 0.428; # to type 0
        self.energies[1][1] = 0.038; # to type 1
        self.energies[1][12] = 0.317; # to type 1, detach
        self.energies[1][2] = 0.026; # to type 2, subtype 0
        self.energies[1][13] = 0.258; # to type 2, subtype 0, detach
        self.energies[1][3] = 0.033; # to type 2, subtype 1
        self.energies[1][14] = 0.183; # to type 2, subtype 1, detach
        self.energies[1][4] = 0.0023; # to type 2, subtype 2
        self.energies[1][5] = 0.01; # to type 3, subtype 0
        self.energies[1][15] = 0.184; # to type 3, subtype 0, detach
        self.energies[1][6] = 0.0012; # to type 3, subtype 1
        self.energies[1][8] = 0.00086; # to type 4, subtype 0
        # From  type 2, subtype 0
        self.energies[2][0] = 0.736; # to type 0
        self.energies[2][1] = 0.36; # to type 1
        self.energies[2][12] = 0.625; # to type 1, detach
        self.energies[2][2] = 0.268; # to type 2, subtype 0
        self.energies[2][13] = 0.433; # to type 2, subtype 0, detach
        self.energies[2][3] = 0.261; # to type 2, subtype 1
        self.energies[2][14] = 0.383; # to type 2, subtype 1, detach
        self.energies[2][4] = 0.167; # to type 2, subtype 2
        self.energies[2][5] = 0.22; # to type 3, subtype 0
        self.energies[2][15] = 0.396; # to type 3, subtype 0, detach
        self.energies[2][6] = 0.164; # to type 3, subtype 1
        self.energies[2][8] = 0.144; # to type 4, subtype 0
        # From  type 2, subtype 1
        self.energies[3][0] = 0.75; # to type 0
        self.energies[3][1] = 0.397; # to type 1
        self.energies[3][12] = 0.565; # to type 1, detach
        self.energies[3][2] = 0.308; # to type 2, subtype 0
        self.energies[3][13] = 0.206; # to type 2, subtype 0, detach
        self.energies[3][3] = 0.293; # to type 2, subtype 1
        self.energies[3][14] = 0.167; # to type 2, subtype 1, detach
        self.energies[3][4] = 0.197; # to type 2, subtype 2
        self.energies[3][5] = 0.258; # to type 3, subtype 0
        self.energies[3][15] = 0.179; # to type 3, subtype 0, detach
        self.energies[3][6] = 0.185; # to type 3, subtype 1
        self.energies[3][7] = 0.651; # to type 3, subtype 2
        self.energies[3][8] = 0.176; # to type 4, subtype 0
        self.energies[3][9] = 0.503; # to type 4, subtype 1
        self.energies[3][10] = 0.439; # to type 4, subtype 2
        self.energies[3][11] = 0.43; # to type 5
        # From  type 2, subtype 2
        self.energies[4][1] = 0.403; # to type 1
        self.energies[4][2] = 0.198; # to type 2, subtype 0
        self.energies[4][3] = 0.189; # to type 2, subtype 1
        self.energies[4][4] = 0.373; # to type 2, subtype 2
        self.energies[4][5] = 0.102; # to type 3, subtype 0
        self.energies[4][6] = 0.328; # to type 3, subtype 1
        self.energies[4][8] = 0.292; # to type 4, subtype 0
        # From  type 3, subtype 0
        self.energies[5][0] = 1.01; # to type 0
        self.energies[5][1] = 0.663; # to type 1
        self.energies[5][12] = 0.828; # to type 1, detach
        self.energies[5][2] = 0.546; # to type 2, subtype 0
        self.energies[5][13] = 0.483; # to type 2, subtype 0, detach
        self.energies[5][3] = 0.539; # to type 2, subtype 1
        self.energies[5][14] = 0.413; # to type 2, subtype 1, detach
        self.energies[5][4] = 0.39; # to type 2, subtype 2
        self.energies[5][5] = 0.473; # to type 3, subtype 0
        self.energies[5][15] = 0.4; # to type 3, subtype 0, detach
        self.energies[5][6] = 0.369; # to type 3, subtype 1
        self.energies[5][8] = 0.357; # to type 4, subtype 0
        # From  type 3, subtype 1
        self.energies[6][1] = 0.697; # to type 1
        self.energies[6][2] = 0.502; # to type 2, subtype 0
        self.energies[6][3] = 0.479; # to type 2, subtype 1
        self.energies[6][4] = 0.215; # to type 2, subtype 2
        self.energies[6][5] = 0.386; # to type 3, subtype 0
        self.energies[6][6] = 0.188; # to type 3, subtype 1
        self.energies[6][7] = 0.905; # to type 3, subtype 2
        self.energies[6][8] = 0.184; # to type 4, subtype 0
        self.energies[6][9] = 0.804; # to type 4, subtype 1
        self.energies[6][10] = 0.683; # to type 4, subtype 2
        self.energies[6][11] = 0.615; # to type 5
        # From  type 3, subtype 2
        self.energies[7][3] = 0.899; # to type 2, subtype 1
        self.energies[7][6] = 0.748; # to type 3, subtype 1
        self.energies[7][7] = 1.01; # to type 3, subtype 2
        self.energies[7][9] = 0.841; # to type 4, subtype 1
        self.energies[7][10] = 0.687; # to type 4, subtype 2
        self.energies[7][11] = 0.726; # to type 5
        # From  type 4, subtype 0
        self.energies[8][1] = 0.947; # to type 1
        self.energies[8][2] = 0.748; # to type 2, subtype 0
        self.energies[8][3] = 0.957; # to type 2, subtype 1
        self.energies[8][4] = 0.851; # to type 2, subtype 2
        self.energies[8][5] = 0.627; # to type 3, subtype 0
        self.energies[8][6] = 0.448; # to type 3, subtype 1
        self.energies[8][8] = 0.423; # to type 4, subtype 0
        # From  type 4, subtype 1
        self.energies[9][3] = 1.01; # to type 2, subtype 1
        self.energies[9][6] = 0.895; # to type 3, subtype 1
        self.energies[9][7] = 1.107; # to type 3, subtype 2
        self.energies[9][8] = 0.763; # to type 4, subtype 0
        self.energies[9][9] = 0.855; # to type 4, subtype 1
        self.energies[9][10] = 0.813; # to type 4, subtype 2
        self.energies[9][11] = 0.763; # to type 5
        # From  type 4, subtype 2
        self.energies[10][3] = 1.02; # to type 2, subtype 1
        self.energies[10][6] = 0.815; # to type 3, subtype 1
        self.energies[10][7] = 0.964; # to type 3, subtype 2
        self.energies[10][9] = 0.807; # to type 4, subtype 1
        self.energies[10][10] = 0.733; # to type 4, subtype 2
        self.energies[10][11] = 0.729; # to type 5
        # From  type 5
        self.energies[11][3] = 1.144; # to type 2, subtype 1
        self.energies[11][15] = 1.01; # to type 3, subtype 0, detach
        self.energies[11][6] = 1.152; # to type 3, subtype 1
        self.energies[11][7] = 1.008; # to type 3, subtype 2
        self.energies[11][9] = 0.904; # to type 4, subtype 1
        self.energies[11][10] = 0.908; # to type 4, subtype 2
        self.energies[11][11] = 0.908; # to type 5

        self.concertedEnergies = 99999999*np.ones(9,dtype=float)
        self.concertedEnergies[0] = eImpossible; 
        self.concertedEnergies[1] = eImpossible; 
        self.concertedEnergies[2] = 0.062; # dimer
        self.concertedEnergies[3] = 0.161; 
        self.concertedEnergies[4] = 0.182; 
        self.concertedEnergies[5] = 0.222; 
        self.concertedEnergies[6] = 0.201; 
        self.concertedEnergies[7] = 0.403; 
        self.concertedEnergies[8] = 0.372; 

        self.multiAtomEnergies = 99999999*np.ones(4,dtype=float)
        self.multiAtomEnergies[0] = 0.481; # type 1, one of the atoms goes from 2 to 1 neighbour
        self.multiAtomEnergies[1] = 0.437; # type 2, both atom go from 2 to 2 neighbours.
        self.multiAtomEnergies[2] = 0.397; # type 3, one of the atoms goes from 2 to 3 neighbours.
        self.multiAtomEnergies[3] = 0.228; # type 4, one of the atoms goes from 2 to 4 neighbours.
