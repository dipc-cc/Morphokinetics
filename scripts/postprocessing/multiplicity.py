# grep AePossibleFromList dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=""; print > "possibleFromList"n".txt"} END{print n}'
# grep AePossibleDiscrete dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=""; print > "possibleDiscrete"n".txt"} END{print n}'
# grep AeRatioTimesPossible dataEvery1percentAndNucleation.txt | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=""; print > "ratioTimesPossible"n".txt"} END{print n}'
# grep AeMultiplicity dataEvery1percentAndNucleation.txt       | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=""; print > "multiplicity"n".txt"} END{print n}'

# grep AePossibleDiscrete dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=""; print > "possibleDiscrete"n".txt"}'

import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import os
import math


def hexagonal():
    temperatures = np.array(list(range(70,100,5))+list(range(100,150,10))+list(range(150,1100,50)))
    initFlux = 2
    endFlux = 7
    folderBase = "5e"
    energies = np.zeros(49, dtype=float)
    energies[0:4] = 0.10
    energies[8:12] = 0.25
    energies[15:20] = 0.33
    energies[24:27] = 0.42
    print(energies)
    
    oldEnergies = np.zeros(shape=(7,7), dtype=float)
    oldEnergies[0][0:4] = 0.10
    oldEnergies[1][1:5] = 0.25
    oldEnergies[2][1:6] = 0.33
    oldEnergies[3][3:6] = 0.42
    print(oldEnergies)
    return temperatures, initFlux, endFlux, folderBase

def basic():
    temperatures = np.array(list(range(120,326,5)))
    initFlux = -3
    endFlux = 1
    folderBase = "3.5e"
    energies = np.zeros(16, dtype=float)
    energies[0:4] = 0.2
    energies[5] = 0.45
    energies[6] = 0.36
    energies[7] = 0.35
    energies[9:12] = 0.435
    print(energies)
    
    oldEnergies = np.zeros(shape=(4,4), dtype=float)
    oldEnergies[0] = 0.2
    oldEnergies[1][1] = 0.45
    oldEnergies[1][2] = 0.36
    oldEnergies[1][3] = 0.35
    oldEnergies[2][1:] = 0.435
    print(oldEnergies)
    return temperatures, initFlux, endFlux, folderBase

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


def splitFiles():
    # split files
    os.system("rm *ossible[1-9]*.txt -f")
    os.system("rm multiplicity[1-9]*.txt -f")
    os.system("grep AePossibleFromList dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"possibleFromList\"n\".txt\"}'")
    os.system("grep AePossibleDiscrete dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"possibleDiscrete\"n\".txt\"}'")
    os.system("grep AeRatioTimesPossible dataEvery1percentAndNucleation.txt | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"ratioTimesPossible\"n\".txt\"}'")
    os.system("grep AeMultiplicity dataEvery1percentAndNucleation.txt       | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"multiplicity\"n\".txt\"}'")


def diffusivityDistance():
    r_tt, temp, flux, L1, L2, maxN = getInputParameters()
    allData = []

    filesN = glob.glob("data[0-9]*.txt")
    for i in range(0,len(filesN)-1):
        fileName = "data"+str(i)+".txt"
        allData.append(np.loadtxt(fname=fileName))
        #plt.loglog(allData[1], allData[15])


##########################################################
##########           Main function   #####################
##########################################################

workingPath = os.getcwd()
fluxes = glob.glob("flux*")
for f in fluxes:
    firstCollisionTime = []
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    temperatures = glob.glob("*")
    temperatures = np.array(temperatures).astype(int)
    temperatures.sort()
    for t in temperatures:
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            splitFiles()
            #diffusivityDistance()
            # find first dimer occurrence
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)


