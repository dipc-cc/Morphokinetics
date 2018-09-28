class EnergyNiCu:

    def __init__(self):
        """ Ag/Ag """
        self.energies = 99999999*np.ones(dtype=float, shape=(12,16))
        eImpossible = 99999999

        # From  type 0
        self.energies[0][0] = 0.059; # to type 0
        self.energies[0][1] = 0.05; # to type 1
        self.energies[0][2] = 0.055; # to type 2, subtype 0
        self.energies[0][3] = 0.317; # to type 2, subtype 1
        self.energies[0][5] = 0.043; # to type 3, subtype 0
        # From  type 1
        self.energies[1][0] = 0.388; # to type 0
        self.energies[1][1] = 0.065; # to type 1
        self.energies[1][12] = 0.3; # to type 1, detach
        self.energies[1][2] = 0.06; # to type 2, subtype 0
        self.energies[1][13] = 0.317; # to type 2, subtype 0, detach
        self.energies[1][3] = 0.06; # to type 2, subtype 1
        self.energies[1][14] = 0.748; # to type 2, subtype 1, detach
        self.energies[1][4] = 0.046; # to type 2, subtype 2
        self.energies[1][5] = 0.04; # to type 3, subtype 0
        self.energies[1][15] = 0.483; # to type 3, subtype 0, detach
        self.energies[1][6] = 0.045; # to type 3, subtype 1
        self.energies[1][8] = 0.04; # to type 4, subtype 0
        # From  type 2, subtype 0
        self.energies[2][0] = 0.633; # to type 0
        self.energies[2][1] = 0.338; # to type 1
        self.energies[2][12] = 0.584; # to type 1, detach
        self.energies[2][2] = 0.289; # to type 2, subtype 0
        self.energies[2][13] = 0.584; # to type 2, subtype 0, detach
        self.energies[2][3] = 0.302; # to type 2, subtype 1
        self.energies[2][14] = 0.323; # to type 2, subtype 1, detach
        self.energies[2][4] = 0.256; # to type 2, subtype 2
        self.energies[2][5] = 0.274; # to type 3, subtype 0
        self.energies[2][15] = 0.497; # to type 3, subtype 0, detach
        self.energies[2][6] = 0.253; # to type 3, subtype 1
        self.energies[2][8] = 0.24; # to type 4, subtype 0
        # From  type 2, subtype 1
        self.energies[3][0] = 0.668; # to type 0
        self.energies[3][1] = 0.363; # to type 1
        self.energies[3][12] = 0.5; # to type 1, detach
        self.energies[3][2] = 0.322; # to type 2, subtype 0
        self.energies[3][13] = 0.321; # to type 2, subtype 0, detach
        self.energies[3][3] = 0.329; # to type 2, subtype 1
        self.energies[3][14] = 0.308; # to type 2, subtype 1, detach
        self.energies[3][4] = 0.29; # to type 2, subtype 2
        self.energies[3][5] = 0.309; # to type 3, subtype 0
        self.energies[3][15] = 0.281; # to type 3, subtype 0, detach
        self.energies[3][6] = 0.466; # to type 3, subtype 1
        self.energies[3][7] = 0.279; # to type 3, subtype 2
        self.energies[3][8] = 0.72; # to type 4, subtype 0
        self.energies[3][9] = 0.444; # to type 4, subtype 1
        self.energies[3][10] = 0.44; # to type 4, subtype 2
        self.energies[3][11] = 0.455; # to type 5
        # From  type 2, subtype 2
        self.energies[4][1] = 0.336; # to type 1
        self.energies[4][2] = 0.281; # to type 2, subtype 0
        self.energies[4][3] = 0.28; # to type 2, subtype 1
        self.energies[4][4] = 0.725; # to type 2, subtype 2
        self.energies[4][5] = 0.219; # to type 3, subtype 0
        self.energies[4][6] = 0.143; # to type 3, subtype 1
        self.energies[4][8] = 0.105; # to type 4, subtype 0
        # From  type 3, subtype 0
        self.energies[5][0] = 0.851; # to type 0
        self.energies[5][1] = 0.571; # to type 1
        self.energies[5][12] = 0.778; # to type 1, detach
        self.energies[5][2] = 0.514; # to type 2, subtype 0
        self.energies[5][13] = 0.52; # to type 2, subtype 0, detach
        self.energies[5][3] = 0.533; # to type 2, subtype 1
        self.energies[5][14] = 0.493; # to type 2, subtype 1, detach
        self.energies[5][4] = 0.465; # to type 2, subtype 2
        self.energies[5][5] = 0.493; # to type 3, subtype 0
        self.energies[5][15] = 0.504; # to type 3, subtype 0, detach
        self.energies[5][6] = 0.463; # to type 3, subtype 1
        self.energies[5][8] = 0.445; # to type 4, subtype 0
        # From  type 3, subtype 1
        self.energies[6][1] = 0.591; # to type 1
        self.energies[6][2] = 0.511; # to type 2, subtype 0
        self.energies[6][3] = 0.508; # to type 2, subtype 1
        self.energies[6][4] = 0.376; # to type 2, subtype 2
        self.energies[6][5] = 0.447; # to type 3, subtype 0
        self.energies[6][6] = 0.378; # to type 3, subtype 1
        self.energies[6][7] = 0.72; # to type 3, subtype 2
        self.energies[6][8] = 0.34; # to type 4, subtype 0
        self.energies[6][9] = 0.703; # to type 4, subtype 1
        self.energies[6][10] = 0.649; # to type 4, subtype 2
        self.energies[6][11] = 0.709; # to type 5
        # From  type 3, subtype 2
        self.energies[7][3] = 0.819; # to type 2, subtype 1
        self.energies[7][6] = 0.803; # to type 3, subtype 1
        self.energies[7][7] = 0.815; # to type 3, subtype 2
        self.energies[7][9] = 0.82; # to type 4, subtype 1
        self.energies[7][10] = 0.744; # to type 4, subtype 2
        self.energies[7][11] = 0.812; # to type 5
        # From  type 4, subtype 0
        self.energies[8][1] = 0.771; # to type 1
        self.energies[8][2] = 0.69; # to type 2, subtype 0
        self.energies[8][5] = 0.65; # to type 3, subtype 0
        self.energies[8][6] = 0.582; # to type 3, subtype 1
        self.energies[8][8] = 0.546; # to type 4, subtype 0
        # From  type 4, subtype 1
        self.energies[9][3] = 0.952; # to type 2, subtype 1
        self.energies[9][6] = 0.954; # to type 3, subtype 1
        self.energies[9][7] = 0.943; # to type 3, subtype 2
        self.energies[9][8] = 0.9; # to type 4, subtype 0
        self.energies[9][9] = 0.951; # to type 4, subtype 1
        self.energies[9][10] = 0.855; # to type 4, subtype 2
        self.energies[9][11] = 1.0; # to type 5
        # From  type 4, subtype 2
        self.energies[10][3] = 0.937; # to type 2, subtype 1
        self.energies[10][6] = 0.877; # to type 3, subtype 1
        self.energies[10][7] = 0.924; # to type 3, subtype 2
        self.energies[10][9] = 0.871; # to type 4, subtype 1
        self.energies[10][10] = 0.879; # to type 4, subtype 2
        self.energies[10][11] = 1.1; # to type 5
        # From  type 5
        self.energies[11][3] = 1.06; # to type 2, subtype 1
        self.energies[11][6] = 1.01; # to type 3, subtype 1
        self.energies[11][7] = 1.05; # to type 3, subtype 2
        self.energies[11][9] = 0.999; # to type 4, subtype 1
        self.energies[11][10] = 1.02; # to type 4, subtype 2
        self.energies[11][11] = 0.993; # to type 5

        self.concertedEnergies = 99999999*np.ones(9,dtype=float)
        self.concertedEnergies[2] = 0.097; # dimer
        self.concertedEnergies[3] = 0.152; 
        self.concertedEnergies[4] = 0.188; 
        self.concertedEnergies[5] = 0.278; 
        self.concertedEnergies[6] = 0.239; 
        self.concertedEnergies[7] = 0.376; 
        self.concertedEnergies[8] = 0.401; 

        self.multiAtomEnergies = 99999999*np.ones(4,dtype=float)
        self.multiAtomEnergies[0] = 0.281; # type 1, one of the atoms goes from 2 to 1 neighbour
        self.multiAtomEnergies[1] = 0.275; # type 2, both atom go from 2 to 2 neighbours.
        self.multiAtomEnergies[2] = 0.257; # type 3, one of the atoms goes from 2 to 3 neighbours.
        self.multiAtomEnergies[3] = 0.231; # type 4, one of the atoms goes from 2 to 4 neighbours.
        
