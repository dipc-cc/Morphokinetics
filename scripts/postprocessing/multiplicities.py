

#import sys
#sys.modules[__name__].__dict__.clear()

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
hops = np.array(matrix[:,15])
even = np.array(matrix[:,7])
cove = np.array(matrix[:,0])

ratios = getRatio(temp, getHexagonalEnergies())
Mavg = np.zeros(shape=(length,49))

for i in range(0,49):
    Mavg[:,i]=possiblesFromList[:,i]/time
avgTotalHopRate2 = np.array(ratios.dot(np.transpose(Mavg)))

avgTotalHopRate1=hops/time

plt.loglog(avgTotalHopRate2)
plt.loglog(avgTotalHopRate1)
#They should be the same, check if they are with many executions

# plotting omegas AgUc
omega = np.zeros(shape=(length, 49))
for i in range(0,length):
    omega[i,:] =  Mavg[i,:] * ratios / hopsAvg[i]

plt.figure()
plt.plot(cove, np.sum(omega[:,0:4], axis=1), label="0:4")
plt.plot(cove, np.sum(omega[:,8:12], axis=1), label="8:12")
plt.plot(cove, np.sum(omega[:,15:20], axis=1), label="15:20")
plt.plot(cove, np.sum(omega[:,24:27], axis=1), label="24:27")
plt.legend(loc="best")

plt.loglog(Mavg)

plt.clf()
# Multiplicities M
style = ":"
plt.loglog(cove, np.sum(Mavg[:,0:4], axis=1),  ls=style, label="0:4")
plt.loglog(cove, Mavg[:,4],                    ls=style, label="4")
plt.loglog(cove, Mavg[:,5],                    ls=style, label=5)
plt.loglog(cove, np.sum(Mavg[:,6:8], axis=1),  ls=style, label="6:8")
plt.loglog(cove, np.sum(Mavg[:,9:12], axis=1), ls=style, label="9:12")
plt.legend(loc="best")

#plt.clf()
# plotting omegas basic
plt.loglog(time[:], np.sum(Mavg[:,0:4], axis=1)*ratios[0]/hopsAvg, label="0:4")
plt.loglog(time[:], Mavg[:,4]*ratios[4]/hopsAvg, label="4")
plt.loglog(time[:], Mavg[:,5]*ratios[5]/hopsAvg, label=5)
plt.loglog(time[:], np.sum(Mavg[:,6:8], axis=1)*ratios[6]/hopsAvg, label="6:8")
plt.loglog(time[:], np.sum(Mavg[:,9:12], axis=1)*ratios[9]/hopsAvg, label="9:12")
plt.legend(loc="best")


plt.loglog(time, cove, ls="--")


for i in range(0,16):
    plt.loglog(cove, discretes[:,i+1])
plt.show()

dtim.dot(Mij)

for i in range(0,50):
    plt.loglog(possiblesFromList[:,i]/time)
