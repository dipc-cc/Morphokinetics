import numpy as np

def agUc(self):
    return getHexagonalEnergies()
        
def basic(self):
    if self.rLib == "version2":
        return getBasic2Energies()
    else:
        return getBasicEnergies()

def graphene(self):
    return getGrapheneSimpleEnergies()

def catalysis(self):
    libSwitcher = {
        "reuter": getCatalysisEnergiesReuter,
        "kiejna": getCatalysisEnergiesKiejna,
        "seitsonen": getCatalysisEnergiesSeitsonen,
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
    energies = 99999999*np.ones(4, dtype=float)
    energies[0] = 1.5
    energies[1] = 0.8
    energies[2] = 1.2
    energies[3] = 0.9
    return energies

def getCatalysisEnergiesKiejna():
    energies = 99999999*np.ones(4, dtype=float)
    energies[0] = 1.48
    energies[1] = 0.61
    energies[2] = 0.99
    energies[3] = 0.78
    return energies

def getCatalysisEnergiesSeitsonen():
    energies = 99999999*np.ones(4, dtype=float)
    energies[0] = 1.4
    energies[1] = 0.6
    energies[2] = 0.74
    energies[3] = 0.71
    return energies

def getRatio(calc, temperature, energies):
    kb = 8.617332e-5
    if calc == "catalysis":
        hev = 4.136e-15
        p = 0.5* kb * temperature / hev
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
    indexes = np.where((temperatures >= 300) & (temperatures <= 340))
    iSl = indexes[0][0]
    iFl = indexes[0][-1]
    indexes = np.where((temperatures >= 340) & (temperatures <= 355))
    iSm = indexes[0][0]
    iFm = indexes[0][-1]
    indexes = np.where((temperatures >= 355) & (temperatures <= 370))
    iSh = indexes[0][0]
    iFh = indexes[0][-1]
    return list([iSl, iSm, iSh, iFh])

def kiejna(temperatures):
    indexes = np.where((temperatures >= 300) & (temperatures <= 325))
    iSl = indexes[0][0]
    iFl = indexes[0][-1]
    indexes = np.where((temperatures >= 330) & (temperatures <= 340))
    iSm = indexes[0][0]
    iFm = indexes[0][-1]
    indexes = np.where((temperatures >= 345) & (temperatures <= 370))
    iSh = indexes[0][0]
    iFh = indexes[0][-1]
    return list([iSl, iSm, iSh, iFh])

def seitsonen(temperatures):
    indexes = np.where((temperatures >= 300) & (temperatures <= 335))
    iSl = indexes[0][0]
    iFl = indexes[0][-1]
    indexes = np.where((temperatures >= 335) & (temperatures <= 345))
    iSm = indexes[0][0]
    iFm = indexes[0][-1]
    indexes = np.where((temperatures >= 350) & (temperatures <= 370))
    iSh = indexes[0][0]
    iFh = indexes[0][-1]
    return list([iSl, iSm, iSh, iFh])
    
# https://www.pydanny.com/why-doesnt-python-have-switch-case.html
def defineRangesCatalysis(calculationMode, ratesLibrary, temperatures):
    ranges = []
    switcher = {
        "reuter": reuter,
        "kiejna": kiejna,
        "seitsonen": seitsonen,
    }
    # Get the function from switcher dictionary
    func = switcher.get(ratesLibrary, lambda: "nothing")
    # Execute the function
    return func(temperatures)
