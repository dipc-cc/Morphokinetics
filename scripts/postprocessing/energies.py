import numpy as np

kJtoeV = 96.485
kb = 8.617332e-5
Na = 6.022e26 #Avogadro constant (1/mol) * 1000 (g -> kg). 6.022e23·1000.
mass = [28.01055 / Na, 2*15.9994 / Na] #Mass of molecule (kg/molecule).
kBInt = 1.381e-23
mu = np.zeros(2)
mu[0] = 1;
mu[1] = 1;
R = np.zeros(2) # m
R[0] = 1.128e-10;
R[1] = 1.21e-10;
V = np.zeros(2) # Hz
V[0] = 6.5e13;
V[1] = 4.7e13;
reducedMass = np.zeros(2)
reducedMass[0] = (12.01115 * 15.9994) / ((12.01115 + 15.9994) * Na);
reducedMass[1] = 15.9994 / (2.0 * Na);
sigma = np.zeros(2)
sigma[0] = 0.98;
sigma[1] = 1.32;


def agUc(self):
    return getHexagonalEnergies()
        
def basic(self):
    if self.rLib == "version2":
        return getBasic2Energies()
    else:
        return getBasicEnergies()

def graphene(self):
    return getGrapheneSimpleEnergies()

def catalysisEnergies(self):
    libSwitcher = {
        "reuter": getCatalysisEnergiesReuter,
        "reuterOver": getCatalysisEnergiesReuterOver,
        "kiejna": getCatalysisEnergiesKiejna,
        "seitsonen": getCatalysisEnergiesSeitsonen,
        "farkas": getCatalysisEnergiesFarkas,
    }
    func = libSwitcher.get(self.rLib)
    return func()

def catalysisEnergiesTotal(self):
    libSwitcher = {
        "reuter": getCatalysisEnergiesReuterTotal,
        "reuterOver": getCatalysisEnergiesReuterOverTotal,
        "kiejna": getCatalysisEnergiesKiejnaTotal,
        "seitsonen": getCatalysisEnergiesSeitsonenTotal,
        "farkas": getCatalysisEnergiesFarkasTotal,
    }
    func = libSwitcher.get(self.rLib)
    return func()
    
def getHexagonalEnergies():
    energies = 999999999*np.ones(49, dtype=float)
    energies[0:4] = 0.10
    energies[8:12] = 0.25
    energies[15:20] = 0.33
    energies[24:27] = 0.42
    energies[7] = 1.5
    energies[14] = 1.58
    energies[21] = 2.0
    energies[22:24] = 0.75
    return energies


def getBasicEnergies():
    energies = 999999999*np.ones(16, dtype=float)
    energies[0:4] = 0.2
    energies[4] = 0.45
    energies[5] = 0.36
    energies[6:8] = 0.35
    energies[8] = 0.535
    energies[9:12] = 0.435
    return energies


def getBasic2Energies():
    energies = 999999999*np.ones(16, dtype=float)
    energies[0:4] = 0.1
    energies[5:8] = 0.4
    energies[11] = 0.4
    return energies


def getGrapheneSimpleEnergies():
    energies = 999999999*np.ones(16, dtype=float)
    energies[0:4]  = 0.5
    energies[4]    = 2.6
    energies[5:7]  = 1.8
    energies[8]    = 3.9
    energies[9:11] = 2.6
    return energies


def getCatalysisEnergiesReuter():
    energies = getCatalysisEnergiesReuterTotal()
    return energies[0:4]

def getCatalysisEnergiesReuterTotal():
    energies = 99999999*np.ones(20, dtype=float)
    energies[0] = 1.5  # CO^B + O^B -> CO_2 
    energies[1] = 0.8  # CO^B + O^C -> CO_2 
    energies[2] = 1.2  # CO^C + O^B -> CO_2 
    energies[3] = 0.9  # CO^C + O^C -> CO_2#
    energies[4] = 0.    # rate must be obtained in another way
    energies[5] = 0.    # rate must be obtained in another way
    energies[6] = 1.85  # CO^B -> V
    energies[7] = 1.32  # CO^C -> V
    energies[8] = 4.82  # O^B + O^B -> V^B + V^B
    energies[9] = 3.30  # O^B + O^C -> V^B + V^C
    energies[10] = 3.0  # O^C + O^B -> V^C + V^B
    energies[11] = 1.78 # O^C + O^C -> V^C + V^C
    energies[12] = 0.70 # CO^B -> CO^B
    energies[13] = 2.06 # CO^B -> CO^C
    energies[14] = 1.40 # CO^C -> CO^B
    energies[15] = 1.57 # CO^C -> CO^C
    energies[16] = 0.90 # O^B  -> O^B
    energies[17] = 1.97 # O^B  -> O^C
    energies[18] = 0.70 # O^C  -> O^B
    energies[19] = 1.53 # O^C  -> O^C
    return energies

def getCatalysisEnergiesReuterOver():
    energies = getCatalysisEnergiesReuterOverTotal()
    return energies[0:4]

def getCatalysisEnergiesReuterOverTotal():
    energies = 99999999*np.ones(4, dtype=float)
    energies[0] = 1.54  # CO^B + O^B -> CO_2 
    energies[1] = 0.76  # CO^B + O^C -> CO_2 
    energies[2] = 1.25  # CO^C + O^B -> CO_2 
    energies[3] = 0.89  # CO^C + O^C -> CO_2#
    energies[4] = 0.    # rate must be obtained in another way
    energies[5] = 0.    # rate must be obtained in another way
    energies[6] = 1.6   # CO^B -> V
    energies[7] = 1.3   # CO^C -> V
    energies[8] = 4.6   # O^B + O^B -> V^B + V^B
    energies[9] = 3.3   # O^B + O^C -> V^B + V^C
    energies[10] = 3.3  # O^C + O^B -> V^C + V^B
    energies[11] = 2.0  # O^C + O^C -> V^C + V^C
    
    energies[12] = 0.6 # CO^B -> CO^B
    energies[13] = 1.6 # CO^B -> CO^C
    energies[14] = 1.3 # CO^C -> CO^B
    energies[15] = 1.7 # CO^C -> CO^C
    energies[16] = 0.7 # O^B  -> O^B
    energies[17] = 2.3 # O^B  -> O^C
    energies[18] = 1.0 # O^C  -> O^B
    energies[19] = 1.6 # O^C  -> O^C    
    return energies

def getCatalysisEnergiesKiejna():
    energies = getCatalysisEnergiesKiejnaTotal()
    return energies[0:4]

def getCatalysisEnergiesKiejnaTotal():
    energies = 99999999*np.ones(20, dtype=float)
    energies[0] = 1.48  # CO^B + O^B -> CO_2 
    energies[1] = 0.61  # CO^B + O^C -> CO_2 
    energies[2] = 0.99  # CO^C + O^B -> CO_2 
    energies[3] = 0.78  # CO^C + O^C -> CO_2#
    energies[4] = 0.    # rate must be obtained in another way
    energies[5] = 0.    # rate must be obtained in another way
    energies[6] = 1.69  # CO^B -> V
    energies[7] = 1.31  # CO^C -> V
    energies[8] = 4.66  # O^B + O^B -> V^B + V^B
    energies[9] = 3.19  # O^B + O^C -> V^B + V^C
    energies[10] = 3.19  # O^C + O^B -> V^C + V^B
    energies[11] = 1.72 # O^C + O^C -> V^C + V^C
    energies[12] = 0.6 # CO^B -> CO^B
    energies[13] = 1.6 # CO^B -> CO^C
    energies[14] = 1.3 # CO^C -> CO^B
    energies[15] = 1.7 # CO^C -> CO^C
    energies[16] = 0.7 # O^B  -> O^B
    energies[17] = 2.3 # O^B  -> O^C
    energies[18] = 1.0 # O^C  -> O^B
    energies[19] = 1.6 # O^C  -> O^C
    return energies

def getCatalysisEnergiesSeitsonen():
    energies = getCatalysisEnergiesSeitsonenTotal()
    return energies[0:4]

def getCatalysisEnergiesSeitsonenTotal():
    energies = 99999999*np.ones(20, dtype=float)
    energies[0] = 1.4  # CO^B + O^B -> CO_2
    energies[1] = 0.6  # CO^B + O^C -> CO_2
    energies[2] = 0.74 # CO^C + O^B -> CO_2
    energies[3] = 0.71 # CO^C + O^C -> CO_2#
    energies[4] = 0.    # rate must be obtained in another way
    energies[5] = 0.    # rate must be obtained in another way
    energies[6] = 1.85  # CO^B -> V
    energies[7] = 1.32  # CO^C -> V
    energies[8] = 4.82  # O^B + O^B -> V^B + V^B
    energies[9] = 3.30  # O^B + O^C -> V^B + V^C
    energies[10] = 3.30 # O^C + O^B -> V^C + V^B
    energies[11] = 1.78 # O^C + O^C -> V^C + V^C
    energies[12] = 0.70 # CO^B -> CO^B
    energies[13] = 2.06 # CO^B -> CO^C
    energies[14] = 1.40 # CO^C -> CO^B
    energies[15] = 1.57 # CO^C -> CO^C
    energies[16] = 0.90 # O^B  -> O^B
    energies[17] = 1.97 # O^B  -> O^C
    energies[18] = 0.70 # O^C  -> O^B
    energies[19] = 1.53 # O^C  -> O^C
    return energies

def getCatalysisEnergiesFarkas():
    energies = getCatalysisEnergiesFarkasTotal()
    return energies[0:4]

def getCatalysisEnergiesFarkasTotal():
    energies = 99999999*np.ones(20, dtype=float)
    energies[0] = 133 / kJtoeV  # CO^B + O^B -> CO_2 
    energies[1] =  91 / kJtoeV  # CO^B + O^C -> CO_2 
    energies[2] =  89 / kJtoeV  # CO^C + O^B -> CO_2 
    energies[3] =  89 / kJtoeV  # CO^C + O^C -> CO_2#
    
    energies[4] = 0.    # rate must be obtained in another way
    energies[5] = 0.    # rate must be obtained in another way
    energies[6] = 1.85  # CO^B -> V
    energies[7] = 1.32  # CO^C -> V
    energies[8] = 4.82  # O^B + O^B -> V^B + V^B
    energies[9] = 3.30  # O^B + O^C -> V^B + V^C
    energies[10] = 3.0  # O^C + O^B -> V^C + V^B
    energies[11] = 1.78 # O^C + O^C -> V^C + V^C
    energies[12] = 0.70 # CO^B -> CO^B
    energies[13] = 2.06 # CO^B -> CO^C
    energies[14] = 1.40 # CO^C -> CO^B
    energies[15] = 1.57 # CO^C -> CO^C
    energies[16] = 0.90 # O^B  -> O^B
    energies[17] = 1.97 # O^B  -> O^C
    energies[18] = 0.70 # O^C  -> O^B
    energies[19] = 1.53 # O^C  -> O^C
    
    return energies
    

def getRatio(calc, temperature, energies):
    if calc == "catalysis":
        hev = 4.136e-15
        p = 1.0 * kb * temperature / hev
    else:
        p = 1e13
    return p * np.exp(-energies/kb/temperature)


def defineRanges(calculationMode, ratesLibrary, temperatures):
    ranges = []
    if calculationMode == "AgUc":
        indexes = np.where((temperatures >= 90) & (temperatures <= 150))
        iSl = indexes[0][0]
        iFl = indexes[0][-1]
        indexes = np.where((temperatures >= 150) & (temperatures <= 400))
        iSm = indexes[0][0]
        iFm = indexes[0][-1]
        indexes = np.where((temperatures >= 400) & (temperatures <= 1100))
        iSh = indexes[0][0]
        iFh = indexes[0][-1]
        #ranges = list(range(0,30,3))
    elif calculationMode == "basic":
        if ratesLibrary == "version2":
            # it has 4 ranges
            ranges = list([0, 19, 33, 48, 58])
        else:
            indexes = np.where((temperatures >= 120) & (temperatures <= 190))
            iSl = indexes[0][0]
            indexes = np.where((temperatures >= 190) & (temperatures <= 270))
            iSm = indexes[0][0]
            indexes = np.where((temperatures >= 270) & (temperatures <= 339))
            iSh = indexes[0][0]
            iFh = indexes[0][-1]
            #ranges = list(range(0,30,3))
    else:
        indexes = np.where((temperatures >= 200) & (temperatures <= 500))
        iSl = indexes[0][0]
        indexes = np.where((temperatures >= 500) & (temperatures <= 1000))
        iSm = indexes[0][0]
        indexes = np.where((temperatures >= 1000) & (temperatures <= 1500))
        iSh = indexes[0][0]
        iFh = indexes[0][-1]

    if len(ranges) > 0:
        return ranges
    else:
        return list([iSl, iSm, iSh, iFh])

def reuter(temperatures):

    ranges = list([0, 4, 8])
    return ranges

def reuterOver(temperatures):

    ranges = list([0, 4, 8])
    return ranges

def kiejna(temperatures):

    ranges = list([0, 4, 8, 12, 15])
    return ranges

def seitsonen(temperatures):
    ranges = list(np.arange(0,16,3))
    return ranges

def farkas(temperatures):
    ranges = list(np.arange(0,16,3))
    return ranges
    
# https://www.pydanny.com/why-doesnt-python-have-switch-case.html
def defineRangesCatalysis(calculationMode, ratesLibrary, temperatures):
    ranges = []
    switcher = {
        "reuter": reuter,
        "reuterOver": reuterOver,
        "kiejna": kiejna,
        "seitsonen": seitsonen,
        "farkas": farkas,
    }
    # Get the function from switcher dictionary
    func = switcher.get(ratesLibrary, lambda: "nothing")
    # Execute the function
    return func(temperatures)

def computeAdsorptionRate(p,pressure,type):
    areaHalfUc = 10.0308e-20
    return pressure * areaHalfUc / (np.sqrt(2.0 * np.pi * mass[type] * kBInt * p.temp))
    
def computeDesorptionRate(p,pressure,type, adsorptionRate, energy):
    h = 6.6260695729e-34 #Planck constant (J·s).
    qt = np.zeros(2)
    qt[type] = pow(2.0 * np.pi * mass[type] * kBInt * p.temp / pow(h, 2.0), (3.0 / 2.0))
    qr = np.zeros(2)
    qr[type] = 8.0 * pow(np.pi, 2.0) * reducedMass[type] * pow(R[type], 2.0) * kBInt * p.temp / (sigma[type] * pow(h, 2.0));

    # vibrational partition function
    qv = np.zeros(2)
    qv[type] = 1.0 / (1.0 - np.exp(-h * V[type] / (kBInt * p.temp)));

    mu = np.zeros(2)
    mu[type] = -kb * p.temp * np.log(kBInt * p.temp / pressure * qt[type] * qr[type] * qv[type]);
    correction = 1
    if (type == 0):
        correction = 0.5

    #correction = type + 1 # adsorption rate for O is for an atom, this is for a O2 molecule.

    return correction * adsorptionRate * np.exp(-(energy + mu[type]) / (kb * p.temp));
