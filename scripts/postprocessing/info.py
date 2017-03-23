import re
import os
import glob
import math
import numpy as np

class fileData:
    def __init__(self, data):
        self.r_tt = data[0] # terrace to terrace rate
        self.temp = data[1] # temperature
        self.flux = data[2] # flux
        self.calc = data[3] # calculation mode: basic, AgUc ...
        self.rLib = data[4]
        self.sizI = data[5] # simulation size I
        self.sizJ = data[6] # simulation size J
        self.maxN = data[7] # max simulated coverage
        self.maxC = data[8] # max number of neighbour or atom types
        self.maxA = data[9] # max alfa: possible transition types (i.e. different energies)

    def getRatios(self):
        ratios = 0
        if self.calc == "AgUc":
            ratios = getRatio(self.temp, getHexagonalEnergies())
        elif self.calc == "basic":
            if self.rLib == "version2":
                ratios = getRatio(self.temp, getBasic2Energies())
            else:
                ratios = getRatio(self.temp, getBasicEnergies())
        else:
            ratios = info.getRatio(p.temp, info.getGrapheneSimpleEnergies())
        return ratios


def getFluxes():
    return glob.glob("flux*")


def getTemperatures():
    temperatures = glob.glob("[1-9]*")
    temperatures = np.array(temperatures).astype(int)
    temperatures.sort()
    return temperatures

    
def getInputParameters(fileName = ""):
    r_tt, temp, flux, calcType, ratesLib, sizI, sizJ, maxC = getInformationFromFile(fileName)
    maxN = 3
    maxA = 16
    if re.match("Ag", calcType): # Adjust J in hexagonal lattices
        sizJ = round(sizJ / math.sin(math.radians(60)))
        maxN = 6
        maxA = 49 # maximum possible transitions (from terrace to terrace, edge to edge and so on
    return fileData([r_tt, temp, flux, calcType, ratesLib, sizI, sizJ, maxN, maxC, maxA])


def getInformationFromFile(fileName):
    if fileName == "":
        fileName = glob.glob("../output*")[0]
    f = open(fileName)
    hit = False
    for line in f:
        if re.search("calculationMode", line):
            calc = list(filter(None,re.split(" |,",line)))[1]
        if re.search("ratesLibrary", line):
            ratesLib = list(filter(None, re.split(" |,",line)))[1]
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
            return r_tt, temp, flux, calc, ratesLib, sizX, sizY, maxC
        if re.match("These", line):
            hit = True

class avgData:
    def __init__(self, data):
        self.cove = data[0] # coverage
        self.time = data[1] # simulation time
        self.isld = data[2] # number of islands
        self.depo = data[3] # deposition probability (Ra)
        self.prob = data[4] # instantaneous total hops probability (Rh)
        self.even = data[5] # number of events
        self.diff = data[6] # diffusivity distance
        self.hops = data[7] # total number of hops
        self.negs = data[8] # neighbours vector

    def getRecomputedCoverage(self):
        return np.sum(self.negs[:],axis=0)

def readAverages():
    p = getInputParameters()
    allData = []

    filesN = glob.glob("data[0-9]*.txt")
    for i in range(0,len(filesN)-1):
        fileName = "data"+str(i)+".txt"
        allData.append(np.loadtxt(fname=fileName, delimiter="\t"))

    cove = np.mean([i[:,0]  for i in allData], axis=0)
    time = np.mean([i[:,1]  for i in allData], axis=0)
    isld = np.mean([i[:,3]  for i in allData], axis=0)
    depo = np.mean([i[:,4]  for i in allData], axis=0)
    prob = np.mean([i[:,5]  for i in allData], axis=0)
    even = np.mean([i[:,7]  for i in allData], axis=0)
    diff = np.mean([i[:,12] for i in allData], axis=0)
    hops = np.exp(np.mean(np.log([i[:,15] for i in allData]), axis=0))
    negs = []
    negs.append(np.mean([i[:,16] for i in allData], axis=0))
    negs.append(np.mean([i[:,17] for i in allData], axis=0))
    negs.append(np.mean([i[:,18] for i in allData], axis=0))
    negs.append(np.mean([i[:,19] for i in allData], axis=0))
    if p.maxN == 6:
        negs.append(np.mean([i[:,20] for i in allData], axis=0))
        negs.append(np.mean([i[:,21] for i in allData], axis=0))
        negs.append(np.mean([i[:,22] for i in allData], axis=0))

    return avgData([cove, time, isld, depo, prob, even, diff, hops, negs])


def processData(xInterpolated, xValues, yValues, logMean=False):
    """ Interpolated given values to xInterpolated vector """
    yValuesI = []
    for i in range(0, len(xValues)):
        yValuesI.append(np.interp(xInterpolated, xValues[i], yValues[i]))
    yInterpolated = np.mean(yValuesI, axis=0)
    if logMean:
        yInterpolated = np.exp(np.mean(np.log(yValuesI), axis=0))
    return yInterpolated
                
def readBinnedAverages():
    p = getInputParameters()
    allData = []

    filesN = glob.glob("data[0-9]*.txt")
    for i in range(0,len(filesN)-1):
        fileName = "data"+str(i)+".txt"
        allData.append(np.loadtxt(fname=fileName, delimiter="\t"))

    firstExecCov = allData[0][:,0]
    hCov = np.exp(np.histogram(np.log(firstExecCov),100)[1])
    #read all raw data, all different length
    covT = [i[:,0]  for i in allData]
    cove = processData(hCov, covT, [i[:,0]  for i in allData])
    time = processData(hCov, covT, [i[:,1]  for i in allData])
    isld = processData(hCov, covT, [i[:,3]  for i in allData])
    depo = processData(hCov, covT, [i[:,4]  for i in allData])
    prob = processData(hCov, covT, [i[:,5]  for i in allData])
    even = processData(hCov, covT, [i[:,7]  for i in allData])
    diff = processData(hCov, covT, [i[:,12] for i in allData])
    hops = processData(hCov, covT, [i[:,15] for i in allData], True)
    negs = []
    negs.append(processData(hCov, covT, [i[:,16] for i in allData]))
    negs.append(processData(hCov, covT, [i[:,17] for i in allData]))
    negs.append(processData(hCov, covT, [i[:,18] for i in allData]))
    negs.append(processData(hCov, covT, [i[:,19] for i in allData]))
    if p.maxN == 6:
        negs.append(processData(hCov, covT, [i[:,20] for i in allData]))
        negs.append(processData(hCov, covT, [i[:,21] for i in allData]))
        negs.append(processData(hCov, covT, [i[:,22] for i in allData]))

    return avgData([cove, time, isld, depo, prob, even, diff, hops, negs])

def readInstantaneous():
    """ reads instantaneous multiplicities, for the moment only valid for AgUc """
    allData = []

    filesN = glob.glob("instantaneous[0-9]*.txt")
    for i in range(0,len(filesN)-1):
        fileName = "instantaneous"+str(i)+".txt"
        data = np.loadtxt(fname=fileName)
        Malpha = data[:,1:]
        m=[]
        m.append(data[:,0])
        m.append(np.sum(Malpha[:,0:7], axis=1))
        m.append(np.sum(Malpha[:,7:14], axis=1))
        m.append(np.sum(Malpha[:,14:21], axis=1))
        m.append(np.sum(Malpha[:,21:28], axis=1))
        allData.append(m)
    firstExecCov = allData[0][:][0]
    hCov = np.exp(np.histogram(np.log(firstExecCov),100)[1])
    covT = [i[:][0]  for i in allData]
    Malpha = []
    Malpha.append(processData(hCov, covT, [i[1] for i in allData]))
    Malpha.append(processData(hCov, covT, [i[2] for i in allData]))
    Malpha.append(processData(hCov, covT, [i[3] for i in allData]))
    Malpha.append(processData(hCov, covT, [i[4] for i in allData]))

    return Malpha

class islandDistribution:
    def __init__(self, data):
        self.islD = data
        
    def islB2(self):
        """ islands bigger than 2 atoms """
        return np.mean([[np.count_nonzero(h[1:]) for h in run] for run in self.islD],axis=0)
    def islB3(self):
        """ islands bigger than 3 atoms """
        return np.mean([[np.count_nonzero(h[1:]>2) for h in run] for run in self.islD],axis=0)
    def islB4(self):
        """ islands bigger than 4 atoms """
        return np.mean([[np.count_nonzero(h[1:]>3) for h in run] for run in self.islD],axis=0)
    def islB5(self):
        """ islands bigger than 5 atoms """
        return np.mean([[np.count_nonzero(h[1:]>4) for h in run] for run in self.islD],axis=0)    
    
def readHistograms():
    """ It reads histograms and then, we are able to get islands bigger than 2, 3, 4 or 6 atoms """
    # read all histograms
    filesN = glob.glob("histogram[0-9]*.txt")
    islD = []
    for i in range(0,len(filesN)):
        fileName = "histogram"+str(i)+".txt"
        islD.append(readHistogram(fileName))
    return islandDistribution(islD)

def splitDataFiles():
    # split files
    os.system("rm data[1-9]*.txt -f")
    os.system("grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($2<prev) {n++}prev=$2;} {print > \"data\"n\".txt\"}'")
    os.system("sed -i '1d' data0.txt")

    
def splitAeFiles():
    # split files
    os.system("rm *ossible[1-9]*.txt -f")
    os.system("rm multiplicity[1-9]*.txt -f")
    os.system("grep AeInstananeousDiscrete dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"instantaneous\"n\".txt\"}'")
    os.system("grep AePossibleFromList dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"possibleFromList\"n\".txt\"}'")
    os.system("grep AePossibleDiscrete dataEvery1percentAndNucleation.txt   | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"possibleDiscrete\"n\".txt\"}'")
    os.system("grep AeRatioTimesPossible dataEvery1percentAndNucleation.txt | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"ratioTimesPossible\"n\".txt\"}'")
    os.system("grep AeMultiplicity dataEvery1percentAndNucleation.txt       | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"multiplicity\"n\".txt\"}'")

def splitHistogramFiles():
    os.system("grep histo dataEvery1percentAndNucleation.txt | sed -e 's/histogram\|\]\|\[\|,/ /g' | awk -v n=-1 '{if ($1>prev) {n++} prev=$1; {print > \"histogram\"n\".txt\"}}'")

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
        

def getRatio(temperature, energies):
    kb = 8.617332e-5
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

def readHistogram(fileName):
    f = open(fileName)
    histogram = []
    for line in f:
        histogram.append(np.array(line.split()).astype(int))
    return histogram
