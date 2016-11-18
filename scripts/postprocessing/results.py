# Structured results of Morphokinetics output
#
# Author: J. Alberdi-Rodriguez
import math
import re
import numpy as np

class CompleteData:
    """ Structured complete data of Morphokinetics output for a flux and temperature"""
    
    def __init__(self, maxCoverage):
        w = 0
        self.data = []
        self.islandSizes = [[0 for x in range(w)] for y in range(maxCoverage)] # data taken from histogram
        self.islandSizesSqrt = [[0 for x in range(w)] for y in range(maxCoverage)] # data taken from histogram
        self.gyradius = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.time = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.monomers = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.ne = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.sumProb = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.innerPerimeter = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.outerPerimeter = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.islandAmount = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.readLines = -1

        self.time[0].append(0)
        self.ne[0].append(0)

        self.maxCoverage = maxCoverage

    def appendIslandSize(self, cov, value):
        """  saves the current values (island sizes) to an array. Data is obtained from the histogram """
        try:
            self.islandSizes[cov].append(int(value))
            self.islandSizesSqrt[cov].append(int(math.sqrt(float(value))))
        except ValueError:
            pass  # ignore if is not a number

    def appendData(self, cov, line):
        dataList = re.split('\t|\n', line) # split line into a list
        self.time[cov].append(dataList[1])  # get the time and store it in a list
        self.monomers[cov].append(dataList[6])  # get the number of monomers
        self.ne[cov].append(dataList[7])    # get number of events and store it in a list
        self.sumProb[cov].append(dataList[8])  # sum of probabilities
        if (len(dataList) > 10):           # if gyradius was calculated store it
            self.gyradius[cov].append(float(dataList[9]))
        if (len(dataList) > 11):           # if perimeter was calculated store it
            self.innerPerimeter[cov].append(int(dataList[10]))
            self.outerPerimeter[cov].append(int(dataList[11]))
        self.islandAmount[cov].append(int(dataList[3]))

    def addReadLines(self, readLines):
        self.readLines = readLines

    #def getIslandAmount(self):
    #    return np.mean(np.array(self.islandAmountList[self.maxCoverage-1]))

class AverageData:
    """ Stores averages over several executions for a flux and a temperature """

    def __init__(self, maxCoverage, chunk):
        w = 0
        self.histogMatrix = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.sizes = []
        self.sizes2 = [] # stores the average of s^2 (s: island sizes)
        self.times = [] # simulated times while executing (from data* file)     
        self.monomers = []
        self.islandsAmount = []
        self.ratio = [] # total ratio (from data* file)
        self.gyradius = []
        self.gyradius2 = [] # stores the average of r_g^2 (r_g: gyradius)
        self.stdSizes = []
        self.stdGyradius = []
        self.sumProb = []
        self.chunk = chunk
        self.maxCoverage = maxCoverage
        self.nePositive = []
        self.slopes = Slopes()

    def updateData(self, index, islandSizes, completeData):
        verbose = False
        # do histogram
        self.histogMatrix[index].append(np.histogram(islandSizes, bins=range(0, max(islandSizes)+self.chunk, self.chunk), density=False))
        # average
        self.sizes.append(np.mean(islandSizes))
        self.sizes2.append(np.mean(np.array(islandSizes)**2))
        self.times.append(np.mean(np.array(completeData.time[index]).astype(np.float)))
        self.monomers.append(np.mean(np.array(completeData.monomers[index]).astype(np.float)))
        self.islandsAmount.append(np.mean(np.array(completeData.islandAmount[index])))
        self.ratio.append(np.mean(np.array(completeData.ne[index]).astype(np.float)))
        self.gyradius.append(np.mean(np.array(completeData.gyradius[index]).astype(np.float)))
        self.gyradius2.append(np.mean(np.array(completeData.gyradius[index]).astype(np.float)**2))
        self.stdSizes.append(np.std(islandSizes))
        self.stdGyradius.append(np.std(np.array(completeData.gyradius[index]).astype(np.float)))
        self.sumProb.append(np.mean(np.array(completeData.sumProb[index]).astype(np.float)))
        onlyPositives = [item for item in completeData.ne[index] if (float(item) >= 0)]  # remove negative values
        self.nePositive.append(np.mean(np.array(onlyPositives).astype(np.float)))
        if verbose:
            print("  coverage {}%  {} time {}".format(index, sizes[-1], times[-1]))
        if index == 30: # only count islands in 30% of coverage
            # Pretty sure is not properly calculated, better use "islandsAmount"
            self.numberOfIsland = len(islandSizes)/(completeData.readLines/30) # divide all islands by number of iterations
        
    def lastIslandAmount(self):
        try:
            return self.islandsAmount[-1]
        except IndexError:
            return float('nan')

    def lastSize(self):
        try:
            return self.sizes[-1]
        except IndexError:
            return float('nan')
    
    def lastSize2(self):
        try:
            return self.sizes2[-1]
        except IndexError:
            return float('nan')

    def lastGyradius(self):
        try:
            return self.gyradius[-1]
        except IndexError:
            return float('nan')

    def lastGyradius2(self):
        return self.gyradius2[-1]
    
    def lastTime(self):
        try:
            return self.times[-1]
        except IndexError:
            return float('nan')

    def getTime(self, coverage):
        return self.times[coverage]

    def lastNe(self):
        """ returns average number of events, removing negative values"""
        return nePositive[-1]

    def getNe(self, index):
        """ returns average number of events, removing negative values"""
        return nePositive[index]

    def lastMonomerAmount(self):
        try:
            return self.monomers[-1]
        except IndexError:
            return float('nan')

class Slopes:
    """ Stores fit slopes of several measurements"""
    
    def __init__(self):
        self.growth = 0
        self.gyradius = 0
        self.perimeter = 0
        

class MeanValues:
    """ Stores averages over several executions for a flux and ALL temperatures """

    def __init__(self):
        self.growthSlopes = []
        self.gyradiusSlopes = []
        self.lastGyradius = []
        self.perimeterSlopes = []
        self.numberOfIslands = []
        self.numberOfMonomers = []
        self.simulatedTimes = []
        self.totalRatio = []
        self.sizes = []
        self.sizes2 = []
        self.aeRatioTimesPossibleList = []

    def updateData(self, averageData):
        self.growthSlopes.append(averageData.slopes.growth)
        self.gyradiusSlopes.append(averageData.slopes.gyradius)
        self.lastGyradius.append(averageData.lastGyradius())
        self.perimeterSlopes.append(averageData.slopes.perimeter)
        self.numberOfIslands.append(averageData.lastIslandAmount())
        self.numberOfMonomers.append(averageData.lastMonomerAmount())
        self.sizes.append(averageData.lastSize())
        self.sizes2.append(averageData.lastSize2())

    def updateTimeAndRatio(self, simulatedTime, numberOfEvents, aeRatioTimesPossible):
        self.simulatedTimes.append(simulatedTime)
        self.totalRatio.append(numberOfEvents/simulatedTime)
        self.aeRatioTimesPossibleList.append(aeRatioTimesPossible)
        
class Results:
    """ Stores results (mainly to be plotted)"""

    def __init__(self, temperatures = [], useNaN = True):
        self.results = []
        self.temperatures = temperatures
        self.useNaN = useNaN

    def append(self, meanValues):
        self.results.append([meanValues.growthSlopes, meanValues.totalRatio, meanValues.gyradiusSlopes, meanValues.numberOfIslands, meanValues.perimeterSlopes, meanValues.numberOfMonomers, meanValues.aeRatioTimesPossibleList, meanValues.simulatedTimes, meanValues.lastGyradius, meanValues.sizes, meanValues.sizes2])
        while(len(self.temperatures) > len(self.results[-1][0])):
            self.results[-1][0].append(self._addNull_())
            self.results[-1][1].append(self._addNull_())
            self.results[-1][2].append(self._addNull_())
            self.results[-1][3].append(self._addNull_())
            self.results[-1][4].append(self._addNull_())
            self.results[-1][5].append(self._addNull_())
            self.results[-1][6].append(self._addNull_())
            self.results[-1][7].append(self._addNull_())
            self.results[-1][8].append(self._addNull_())

    def growthSlope(self):
        """ returns island size growth slopes for the last flux, for all temperatures """
        return np.array(self.results[-1][0])

    def totalRatio(self):
        """ returns average total ratio for the last flux, for all temperatures """
        return np.array(self.results[-1][1])

    def gyradius(self):
        """ returns average gyradius growth for the last flux, for all temperatures """
        return np.array(self.results[-1][2])

    def lastGyradius(self):
        """ returns average gyradius for the last coverage, last flux, for all temperatures """
        return np.array(self.results[-1][8])
        
    def islands(self):
        """ returns average number of islands for the last flux, for all temperatures """
        return np.array(self.results[-1][3])

    def perimeter(self):
        """ returns average perimeter length growth for the last flux, for all temperatures """
        return np.array(self.results[-1][4])
    
    def monomers(self):
        """ returns average number of monomers for the last flux, for all temperatures """
        return np.array(self.results[-1][5])

    def aeRatioTimesPossible(self):
        """  """
        return np.array(self.results[-1][6])

    def times(self):
        """ returns average simulated times for the last flux, for all temperatures """
        return np.array(self.results[-1][7])

    def sizes(self):
        """ returns average island sizes for the last flux, for all temperatures """
        return np.array(self.results[-1][9])

    def sizes2(self):
        """ returns average squared island sizes for the last flux, for all temperatures """
        return np.array(self.results[-1][10])

    def _addNull_(self):
        if self.useNaN:
            return np.nan
        else:
            return 0
