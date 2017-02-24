import re
import os
import glob
import math
import numpy as np

class fileData:
    def __init__(self, data):
        print(data)
        self.r_tt = data[0] # terrace to terrace rate
        self.temp = data[1] # temperature
        self.flux = data[2] # flux
        self.calc = data[3] # calculation mode: basic, AgUc ...
        self.sizI = data[4] # simulation size I
        self.sizJ = data[5] # simulation size J
        self.maxN = data[6] # max simulated coverage
        self.maxC = data[7] # max number of neighbour or atom types
        self.maxA = data[8] # max alfa: possible transition types (i.e. different energies)
        
def getFluxes():
    return glob.glob("flux*")


def getTemperatures():
    temperatures = glob.glob("[1-9]*")
    temperatures = np.array(temperatures).astype(int)
    temperatures.sort()
    return temperatures

    
def getInputParameters(fileName = ""):
    r_tt, temp, flux, calcType, sizI, sizJ, maxC = getInformationFromFile(fileName)
    maxN = 3
    maxA = 16
    if re.match("Ag", calcType): # Adjust J in hexagonal lattices
        sizJ = round(sizJ / math.sin(math.radians(60)))
        maxN = 6
        maxA = 49 # maximum possible transitions (from terrace to terrace, edge to edge and so on
    return fileData([r_tt, temp, flux, calcType, sizI, sizJ, maxN, maxC, maxA])


def getInformationFromFile(fileName):
    if fileName == "":
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
        if re.search("coverage", line):
            maxC = int(float(list(filter(None,re.split(" |,",line)))[1]))
        if hit:
            r_tt = float(re.split(' ', line)[0])
            return r_tt, temp, flux, calc, sizX, sizY, maxC
        if re.match("These", line):
            hit = True


def splitDataFiles():
    # split files
    os.system("rm data[1-9]*.txt -f")
    os.system("grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($2<prev) {n++}prev=$2;} {print > \"data\"n\".txt\"}'")
    os.system("sed -i '1d' data0.txt")

    
def splitAeFiles():
    # split files
    os.system("rm *ossible[1-9]*.txt -f")
    os.system("rm multiplicity[1-9]*.txt -f")
    os.system("grep AePossibleFromList dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"possibleFromList\"n\".txt\"}'")
    os.system("grep AePossibleDiscrete dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"possibleDiscrete\"n\".txt\"}'")
    os.system("grep AeRatioTimesPossible dataEvery1percentAndNucleation.txt | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"ratioTimesPossible\"n\".txt\"}'")
    os.system("grep AeMultiplicity dataEvery1percentAndNucleation.txt       | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"multiplicity\"n\".txt\"}'")


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


def writeAe(fileName, data):
    """ https://stackoverflow.com/questions/3685265/how-to-write-a-multidimensional-array-to-a-text-file """
    f = open(fileName, "wb")
    #Iterating through a ndimensional array produces slices along
    # the last axis. This is equivalent to data[i,:,:] in this case
    f.write(bytes('# Array shape: '+str(data.shape)+'\n', "UTF-8"))
    f.write(b'#[coverage, type (alfa), temperatureRange]\n')
    for data_slice in data:
    
        # The formatting string indicates that I'm writing out
        # the values in left-justified columns x characters in width
        # with 18 decimal places.  
        np.savetxt(f, data_slice, fmt='%.18f')
    
        # Writing out a break to indicate different slices...
        f.write(b'# New slice\n')


def readAe(fileName):
    f = open(fileName)
    for line in f:
        shape = re.split(",|\(|\)",line)
        shape = list(map(int,shape[1:4]))
        break
    return np.loadtxt(fileName).reshape(shape)
