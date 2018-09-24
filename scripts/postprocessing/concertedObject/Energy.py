import numpy as np
import concertedEnergies as c
import EnergyCuNi
import EnergyNiCu

class Energy:
    kJtoeV = 96.485
    kb = 8.617332e-5
    mu = np.zeros(2)
    mu[0] = 1;
    mu[1] = 1;
    R = np.zeros(2) # m
    R[0] = 1.128e-10;
    R[1] = 1.21e-10;
    V = np.zeros(2) # Hz
    V[0] = 6.5e13;
    V[1] = 4.7e13;
    sigma = np.zeros(2)
    sigma[0] = 0.98;
    sigma[1] = 1.32;
    h = 6.6260695729e-34 #Planck constant (J·s).

    def __init__(self):
        """ Must choose from an energy library """
        library = EnergyNiCu.EnergyNiCu()
        self.energies = library.energies
        self.concertedEnergies = library.concertedEnergies
        self.multiAtomEnergies = library.multiAtomEnergies
        
    def getEnergies(self):
        return np.concatenate([self.energies.flatten(),self.concertedEnergies, self.multiAtomEnergies])
        
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
        values = func()
        if self.maxA == 7:  # for farkas TOF
            values[4] = values[22]
            values[5] = values[23]
            values[6] = values[24]
            
        return values
   
    def getCatalysisEnergiesAmmonia():
        energies = getCatalysisEnergiesAmmoniaTotal()
        return energies[0:4] # change the range
    
    def getCatalysisEnergiesAmmoniaTotal():
        energies = 99999999*np.ones(18, dtype=float)
        energies[0] = 0     # P1
        energies[1] = 1.46  # P2
        energies[2] = 0     # P3
        energies[3] = 1.26  # P4
        energies[4] = 0.55  # P5
        energies[5] = 0.27  # P6
        energies[6] = 0     # P7
        energies[7] = 0     # P8
        energies[8] = 0.14  # P9
        energies[9] = 0.27  # P10
        energies[10] = 1.49 # P11
        energies[11] = 0.96 # P12
        energies[12] = 0.93 # P13
        energies[13] = 1.12 # P14
        energies[14] = 1.0  # P15
        energies[15] = 0    # P16
        energies[16] = 0.26 # P17
        energies[17] = 0.9  # P18
        return energies
    
    
    def getRatio(calc, temperature, energies):
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
       
 
        
  
    
    
    # In catalysis, prefactor changes with temperature.
    # I think not in Ammonia!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    def getEaCorrections(p,temperatures):
        correction = np.zeros(shape=(28,len(temperatures)))
        correction[0:4,:] = kb*temperatures # reaction
        correction[4:6,:] = -kb*temperatures/2.0 # adsorption
        correction[6:8,:] = 3.0*kb*temperatures + getDesorptionCorrection(temperatures,0) # desorption CO
        correction[8:12,:] = 3.0*kb*temperatures + getDesorptionCorrection(temperatures,1) # desorption O
        correction[12:20,:] = kb*temperatures # diffusion
        correction[20:22,:] = 3.0*kb*temperatures + getDesorptionCorrection(temperatures,0)
        correction[22:25,:] = kb*temperatures # reaction
        correction[25:27,:] = kb*temperatures # diffusion
    
        if p.maxA == 7:  # for farkas TOF
            correction[4,:] = correction[22,:]
            correction[5,:] = correction[23,:]
            correction[6,:] = correction[24,:]
        correction = correction.transpose() # temperatures, alfa
        return correction
