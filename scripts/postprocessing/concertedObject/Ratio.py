import Energy
import numpy as np

import pdb

class Ratio:
    
    kb = 8.617332e-5
    kBInt = 1.381e-23
    Na = 6.022e26 #Avogadro constant (1/mol) * 1000 (g -> kg). 6.022e23·1000.
    mass = [(14.006 + 1.007 * 3) / Na, 2*15.9994 / Na] #Mass of molecule (kg/molecule). NH3, O2
    
    def __init__(self, energy, temperature, pressures, flux):
        self.energy = energy
        self.temperature = temperature
        self.pressures = pressures
        self.flux = flux

    def getRatios(self):
        p = 1e13
        allRatios = p * np.exp(-self.energy.getEnergies()/self.kb/self.temperature)
        allRatios[-1] = self.flux # add deposition flux
        return allRatios

    # Adsorption rate for CO molecule and for O2 molecule 
    def computeAdsorptionRate(self,type):
        area = 20.992e-20 #Angstrom² x 10⁻¹⁰ x 10⁻¹⁰
        sites = 3.0 # 3 sites in a unit cell of 3x1
        density = sites / area
        correction = 2.0 # This correction is need to reproduce fig 7 of the paper.
        return correction * self.pressures[type] / (density * np.sqrt(2.0 * np.pi * self.mass[type] * self.kBInt * self.temperature))
