# Instantaneous multiplicities are Mij,
# time averaged values are Mavg

import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import math
import os


def getInformationFromFile():
    fileName = glob.glob("../output*")[0]
    f = open(fileName)
    hit = False
    for line in f:
        if re.search("calculationMode", line):
            calc = list(filter(None,re.split(" |,",line)))[1]
        if re.search("cartSizeX", line):
            sizX = int(list(filter(None,re.split(" |,",line)))[1])
        if re.search("cartSizeY", line):
            sizY = int(list(filter(None,re.split(" |,",line)))[1])
        if re.search("temperature", line):
            temp = float(list(filter(None,re.split(" |,",line)))[1])
        if re.search("depositionFlux", line):
            flux = float(list(filter(None,re.split(" |,",line)))[1])
        if hit:
            r_tt = float(re.split(' ', line)[0])
            return r_tt, temp, flux, calc, sizX, sizY
        if re.match("These", line):
            hit = True
            

def getInputParameters():
    r_tt, temp, flux, calcType, sizI, sizJ = getInformationFromFile()
    maxN = 3
    if re.match("Ag", calcType): # Adjust J in hexagonal lattices
        sizJ = round(sizJ / math.sin(math.radians(60)))
        maxN = 6
    return r_tt, temp, flux, sizI, sizJ, maxN

def getHexagonalEnergies():
    energies = 999999999*np.ones(49, dtype=float)
    energies[0:4] = 0.10
    energies[8:12] = 0.25
    energies[15:20] = 0.33
    energies[24:27] = 0.42
    return energies

def getBasicEnergies():
    energies = 999999999*np.ones(16, dtype=float)
    energies[0:4] = 0.2
    energies[4] = 0.45
    energies[5] = 0.36
    energies[6:8] = 0.35
    energies[9:12] = 0.435
    return energies
    

def getRatio(temperature, energies):
    kb = 8.62e-5
    p = 1e13
    return p * np.exp(-energies/kb/temperature)

r_tt, temp, flux, sizI, sizJ, maxN = getInputParameters()

discretes = np.loadtxt(fname="possibleDiscrete0.txt")
matrix = np.loadtxt(fname="data0.txt", delimiter="\t")
possiblesFromList = np.loadtxt(fname="possibleFromList0.txt")
possiblesFromList = possiblesFromList[:,1:] # remove coverage
time = np.array(matrix[:,1])
length = len(time)
neg0 = matrix[:,16]
#neg0 = neg0[1:]
hops = np.array(matrix[:,15])
even = np.array(matrix[:,7])
#hops = hops[1:]
cove = np.array(matrix[:,0])

ratios = getRatio(temp, getHexagonalEnergies())
#discretes[:,1] = discretes[:,1] + 4 # add 4 to T->T
#Mij = discretes[0,:]
#Mij = discretes[1:]-discretes[0:-1]
Mij=np.append(discretes[0], discretes[1:]-discretes[0:-1])
Mij=Mij.reshape(len(Mij)/50,50)
Mij = Mij[:,1:] # remove coverage
dtim = np.append(time[0], time[1:]-time[0:-1])
devn = np.append(even[0], even[1:]-even[0:-1])
dhps = hops[1:]-hops[0:-1]
suma = np.zeros(49) # this is equal to AePossibleFromList
sumN = 0
Mavg = np.zeros(shape=(length,49))
Navg = np.zeros(length)
i = 0
for dt in dtim:
    suma += dt*Mij[i,:]/(devn[i]+1e-10)
    sumN += dt*neg0[i]
    Mavg[i] = suma / time[i]
    Navg[i] = sumN / time[i]
    i+=1

    
hopsAvg = np.array(ratios.dot(np.transpose(Mavg)))
