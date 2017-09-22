import re
import os
import glob
import math
import numpy as np
import energies as e

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
        self.nCo2 = data[10] # created CO2 molecules. For catalysis
        self.prCO = data[11] # pressure. For catalysis
        self.prO2 = data[12] # pressure. For catalysis
        self.minA = 0 # min alfa: possible transition types (i.e. different energies)

        
    def getRatios(self):
        ratios = 0
        calcSwitcher = {
            "AgUc": e.agUc,
            "basic": e.basic,
            "graphene": e.graphene,
            "catalysis": e.catalysisEnergies,
        }
        func = calcSwitcher.get(self.calc, lambda: "nothing")
        ratios = e.getRatio(self.calc, self.temp, func(self))
        return ratios

    # For catalysis
    def getRatiosTotal(self):
        energies = e.catalysisEnergiesTotal(self)
        ratios = np.zeros(20)
        ratios[0:4] = e.computeReactionRate(self,energies[0:4])
        ratios[4] = e.computeAdsorptionRate(self,self.prCO, 0)/2.0
        ratios[5] = e.computeAdsorptionRate(self,self.prO2, 1)/2.0
        ratios[6] = e.computeDesorptionRate(self,self.prCO, 0, ratios[4],  energies[6]) #CO^B
        ratios[7] = e.computeDesorptionRate(self,self.prCO, 0, ratios[4],  energies[7]) #CO^C
        ratios[8] = e.computeDesorptionRate(self,self.prO2, 1, ratios[5],  energies[8]) #O^B + O^B
        ratios[9] = e.computeDesorptionRate(self,self.prO2, 1, ratios[5],  energies[9]) #O^B + O^C
        ratios[10] = e.computeDesorptionRate(self,self.prO2, 1, ratios[5], energies[10]) #O^C + O^B
        ratios[11] = e.computeDesorptionRate(self,self.prO2, 1, ratios[5], energies[11]) #O^C + O^C
        ratios[12:20] = e.computeDiffusionRate(self,energies[12:20])
        return ratios
        


def getFluxes():
    fluxes = glob.glob("flux*")
    fluxes.sort()
    return fluxes


def getTemperatures(*types):
    temperatures = glob.glob("[1-9]*")
    if len(types) > 0:
        temperatures = np.array(temperatures).astype(float)
    else:
        temperatures = np.array(temperatures).astype(int)
    temperatures.sort()
    return temperatures


def getPressures():
    temperatures = glob.glob("[0-9]*")
    temperatures = np.array(temperatures).astype(float)
    temperatures.sort()
    return temperatures
    
def getInputParameters(fileName = ""):
    r_tt, temp, flux, calcType, ratesLib, sizI, sizJ, maxC, nCO2, prCO, prO2 = getInformationFromFile(fileName)
    maxN = 3
    maxA = 16
    if re.match("Ag", calcType): # Adjust J in hexagonal lattices
        sizJ = round(sizJ / math.sin(math.radians(60)))
        maxN = 6
        maxA = 49 # maximum possible transitions (from terrace to terrace, edge to edge and so on
    if re.match("catalysis", calcType):
        maxA = 4 # Production of CO2, CO^B+O^B | CO^B+O^C | CO^C+O^B | CO^C+O^C
    return fileData([r_tt, temp, flux, calcType, ratesLib, sizI, sizJ, maxN, maxC, maxA, nCO2, prCO, prO2])


def getInformationFromFile(fileName):
    if fileName == "":
        try:
            fileName = glob.glob("../output*")[0]
        except IndexError:
            fileName = glob.glob("../../output*")[-1]
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
        if re.search("\"coverage\"", line):
            maxC = int(float(list(filter(None,re.split(" |,",line)))[1]))
        if re.search("numberOfCo2", line):
            nCO2 = int(list(filter(None,re.split(" |,",line)))[1])
        if re.search("pressureCO", line):
            prCO = float(list(filter(None,re.split(" |,",line)))[1])
            prCO = prCO * 101325.0 #to Pa
        if re.search("pressureO2",line):
            prO2 = float(list(filter(None,re.split(" |,",line)))[1])
            prO2 = prO2 * 101325.0  #to Pa /2
        if hit:
            try:
                r_tt = float(re.split(' ', line)[0])
            except ValueError:
                r_tt = 0
            return r_tt, temp, flux, calc, ratesLib, sizX, sizY, maxC, nCO2, prCO, prO2
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
        self.cmDf = data[11] # centre of mass diffusivity distance
        self.negS = data[12] # standar deviation coverage

    def getRecomputedCoverage(self):
        return np.sum(self.negs[:], axis=0)
    
    def getStdCoverage(self):
        return np.std(self.negs[:], axis=0)

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
    cmDf = np.mean([i[:,13] for i in allData], axis=0)
    #hops = np.exp(np.mean(np.log([i[:,15] for i in allData]), axis=0))
    hops = np.mean([i[:,15] for i in allData], axis=0)
    empt = []
    negs = []
    negs.append(np.mean([i[:,16] for i in allData], axis=0))
    negs.append(np.mean([i[:,17] for i in allData], axis=0))
    negs.append(np.mean([i[:,18] for i in allData], axis=0))
    negs.append(np.mean([i[:,19] for i in allData], axis=0))
    #negS = np.std(np.sum([i[:,16:20] for i in allData], axis=2), axis=0)
    negS = 0
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
            

    return avgData([cove, time, isld, depo, prob, even, diff, hops, negs, difS, empt, cmDf, negS])


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

    
def splitAeFiles(fileName = "dataEvery1percentAndNucleation.txt"):
    # split files
    os.system("rm *ossible[1-9]*.txt -f")
    os.system("rm multiplicity[1-9]*.txt -f")
    os.system("grep AeInstantaneousDiscrete "+fileName+" | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"instantaneous\"n\".txt\"}'")
    os.system("grep AePossibleFromList "+fileName+" | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"possibleFromList\"n\".txt\"}'")
    os.system("grep AePossibleDiscrete "+fileName+" | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"possibleDiscrete\"n\".txt\"}'")
    os.system("grep AeRatioTimesPossible "+fileName+" | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"ratioTimesPossible\"n\".txt\"}'")
    os.system("grep AeMultiplicity "+fileName+"       | awk -v prev=100 -v n=-1 '{if ($1<prev) {n++}prev=$1;} {$2=\"\"; print > \"multiplicity\"n\".txt\"}'")

    
def splitHistogramFiles():
    os.system("grep histo dataEvery1percentAndNucleation.txt | sed -e 's/histogram\|\]\|\[\|,/ /g' | awk -v n=-1 '{if ($1>prev) {n++} prev=$1; {print > \"histogram\"n\".txt\"}}'")


def smallerFont(ax, size=10):
    ax.tick_params(axis='both', which='major', labelsize=size)
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
