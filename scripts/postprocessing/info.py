import re
import os
import glob
import math
import numpy as np

exclude = 0

class fileData:
    def __init__(self, data):
        self.r_tt = data[0] # terrace to terrace rate
        self.temp = data[1] # temperature
        self.flux = data[2] # flux
        self.calc = data[3] # calculation mode: basic, AgUc ...
        self.rLib = data[4]
        self.sizI = data[5] # simulation size I
        self.sizJ = data[6] # simulation size J
        self.maxN = data[7] # max number of neighbour or atom types
        self.maxC = data[8] # max simulated coverage
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
            ratios = getRatio(self.temp, getGrapheneSimpleEnergies())
        return ratios


def getFluxes():
    fluxes = glob.glob("flux*")
    fluxes.sort()
    return fluxes


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

        self.difS = data[9] # standard deviation of diffusivity
        self.empt = data[10] # types of empty sites

    def getRecomputedCoverage(self):
        return np.sum(self.negs[:],axis=0)

def readAverages():
    p = getInputParameters()
    allData = []

    filesN = glob.glob("data[0-9]*.txt")
    for i in range(0,len(filesN)-exclude):
        fileName = "data"+str(i)+".txt"
        allData.append(np.loadtxt(fname=fileName, delimiter="\t"))

    cove = np.mean([i[:,0]  for i in allData], axis=0)
    time = np.mean([i[:,1]  for i in allData], axis=0)
    isld = np.mean([i[:,3]  for i in allData], axis=0)
    depo = np.mean([i[:,4]  for i in allData], axis=0)
    prob = np.mean([i[:,5]  for i in allData], axis=0)
    even = np.mean([i[:,7]  for i in allData], axis=0)
    diff = np.mean([i[:,12] for i in allData], axis=0)
    difS = np.std([i[:,12] for i in allData], axis=0)
    #hops = np.exp(np.mean(np.log([i[:,15] for i in allData]), axis=0))
    hops = np.mean([i[:,15] for i in allData], axis=0)
    empt = []
    negs = []
    negs.append(np.mean([i[:,16] for i in allData], axis=0))
    negs.append(np.mean([i[:,17] for i in allData], axis=0))
    negs.append(np.mean([i[:,18] for i in allData], axis=0))
    negs.append(np.mean([i[:,19] for i in allData], axis=0))
    if p.maxN == 6:
        negs.append(np.mean([i[:,20] for i in allData], axis=0))
        negs.append(np.mean([i[:,21] for i in allData], axis=0))
        negs.append(np.mean([i[:,22] for i in allData], axis=0))
        try:
            empt.append(np.mean([i[:,23] for i in allData], axis=0))
            empt.append(np.mean([i[:,24] for i in allData], axis=0))
            empt.append(np.mean([i[:,25] for i in allData], axis=0))
            empt.append(np.mean([i[:,26] for i in allData], axis=0))
            empt.append(np.mean([i[:,27] for i in allData], axis=0))
            empt.append(np.mean([i[:,28] for i in allData], axis=0))
            empt.append(np.mean([i[:,29] for i in allData], axis=0))
        except IndexError:
            pass
            

    return avgData([cove, time, isld, depo, prob, even, diff, hops, negs, difS, empt])


def getAvgDataCoverage(p, d, coverage):
    if p.calc == "AgUc":
        factor1 = 0.7
        factor2 = 1.5
    else:
        factor1 = 0.8
        factor2 = 1.78
    try:
        i = len(d.cove)-(100-coverage)
        divisor = 4*coverage*p.sizI*p.sizJ*d.time[i]*(p.flux**factor1)
        r = [p.flux, d.cove[i]/divisor, d.diff[i]/divisor, d.hops[i]/divisor, np.max(d.isld[-95:])/(p.flux**0.27), factor2]#d.isld[i]/(p.flux**0.27)#
        if math.isnan(d.hops[i]) or d.hops[i] < 0: # sometimes the number of hops is not properly saved, because it was an int instead of long
            r[3] = (d.even[i] - (d.cove[i]*p.sizI*p.sizJ))/divisor # recalculating it: even - depositions
    except IndexError:
        r = [p.flux, None, None, None, None, factor2]
    return r


def processData(xInterpolated, xValues, yValues, logMean=False):
    """ Interpolated given values to xInterpolated vector """
    yValuesI = []
    for i in range(0, len(xValues)-exclude):
        yValuesI.append(np.interp(xInterpolated, xValues[i], yValues[i]))
    yInterpolated = np.mean(yValuesI, axis=0)
    if logMean:
        yInterpolated = np.exp(np.mean(np.log(yValuesI), axis=0))
    return yInterpolated


def readBinnedAverages():
    p = getInputParameters()
    allData = []

    filesN = glob.glob("data[0-9]*.txt")
    for i in range(0,len(filesN)-exclude):
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



def readPossibles():
    """ reads XXXX multiplicities, for the moment only valid for AgUc """
    allData = []
    allMij = []

    filesN = glob.glob("possibleFromList[0-9]*.txt")
    for i in range(0,len(filesN)-exclude):
        fileName = "possibleFromList"+str(i)+".txt"
        data = np.loadtxt(fname=fileName)
        Malpha = data[:,1:]
        Mij = data[:,1:]
        M=[]
        M.append(data[:,0])
        M.append(np.sum(Malpha[:,0:7], axis=1))
        M.append(np.sum(Malpha[:,7:14], axis=1))
        M.append(np.sum(Malpha[:,14:21], axis=1))
        M.append(np.sum(Malpha[:,21:28], axis=1))
        allData.append(M)
        allMij.append(Mij)
        
    MalphaOrig = np.array(Malpha)
    Malpha = []
    allData = np.array(allData)
    for j in range(1,5): # alpha
        Malpha.append(np.mean(allData[:,j,:], axis=0))
    Mij = np.mean(allMij, axis=0)
    return Malpha, Mij



def readDiscrete():
    """ reads discrete multiplicities, for the moment only valid for AgUc """
    allData = []

    filesN = glob.glob("possibleDiscrete[0-9]*.txt")
    for i in range(0,len(filesN)-exclude):
        fileName = "possibleDiscrete"+str(i)+".txt"
        data = np.loadtxt(fname=fileName)
        Malpha = data[:,1:]
        Mij = data[:,1:]
        m=[]
        m.append(data[:,0])
        m.append(np.sum(Malpha[:,0:7], axis=1))
        m.append(np.sum(Malpha[:,7:14], axis=1))
        m.append(np.sum(Malpha[:,14:21], axis=1))
        m.append(np.sum(Malpha[:,21:28], axis=1))
        allData.append(m)
        
    MalphaOrig = np.array(Malpha)
    Malpha = []
    allData = np.array(allData)
    for j in range(1,5): # alpha
        Malpha.append(np.mean(allData[:,j,:], axis=0))
    return Malpha, MalphaOrig


def readInstantaneous(doBin=True):
    """ reads instantaneous multiplicities, for the moment only valid for AgUc """
    allData = []
    allMij = []

    filesN = glob.glob("instantaneous[0-9]*.txt")
    for i in range(0,len(filesN)-exclude):
        fileName = "instantaneous"+str(i)+".txt"
        data = np.loadtxt(fname=fileName)
        Mij = data[:,1:]
        M=[]
        M.append(data[:,0])
        M.append(np.sum(Mij[:,0:7], axis=1))
        M.append(np.sum(Mij[:,7:14], axis=1))
        M.append(np.sum(Mij[:,14:21], axis=1))
        M.append(np.sum(Mij[:,21:28], axis=1))
        allData.append(M)
        allMij.append(Mij)
    if doBin:
        firstExecCov = allData[0][:][0]
        hCov = np.exp(np.histogram(np.log(firstExecCov),100)[1])
        covT = [i[:][0]  for i in allData]
        Malpha = []
        Malpha.append(processData(hCov, covT, [i[1] for i in allData]))
        Malpha.append(processData(hCov, covT, [i[2] for i in allData]))
        Malpha.append(processData(hCov, covT, [i[3] for i in allData]))
        Malpha.append(processData(hCov, covT, [i[4] for i in allData]))
    else:
        Malpha = []
        allData = np.array(allData)
        for j in range(1,5): # alpha
            Malpha.append(np.mean(allData[:,j,:], axis=0))
        Mij = np.mean(allMij, axis=0)
            
    return Malpha, Mij


def readPossibleFromList():
    """ reads instantaneous multiplicities, for the moment only valid for AgUc """
    allData = []

    filesN = glob.glob("possibleFromList[0-9]*.txt")
    for i in range(0,len(filesN)-exclude):
        fileName = "possibleFromList"+str(i)+".txt"
        data = np.loadtxt(fname=fileName)
        Malpha = data[:,1:]
        m=[]
        m.append(data[:,0])
        m.append(np.sum(Malpha[:,0:7], axis=1))
        m.append(np.sum(Malpha[:,7:14], axis=1))
        m.append(np.sum(Malpha[:,14:21], axis=1))
        m.append(np.sum(Malpha[:,21:28], axis=1))
        allData.append(m)
    Malpha = []
    allData = np.array(allData)
    for j in range(1,5): # alpha
        Malpha.append(np.mean(allData[:,j,:], axis=0))
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
    for i in range(0,len(filesN)-exclude):
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
        

def getRatio(temperature, energies):
    kb = 8.617332e-5
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

    
def smallerFont(ax, size=10):
    for tick in ax.xaxis.get_major_ticks():
        tick.label.set_fontsize(size)
    for tick in ax.yaxis.get_major_ticks():
        tick.label.set_fontsize(size)


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
