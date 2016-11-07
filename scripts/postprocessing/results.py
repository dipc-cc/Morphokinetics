# Structured results of Morphokinetics output
#
# Author: J. Alberdi-Rodriguez
import math
import re
import numpy as np

class Data:
    """ Structured results of Morphokinetics output"""
    
    def __init__(self, maxCoverage):
        w = 0
        self.data = []
        self.islandSizesList = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.islandRadiusList = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.gyradiusList = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.timeList = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.monomersList = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.neList = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.innerPerimeterList = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.outerPerimeterList = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.islandNumberList = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.readLines = -1

        self.timeList[0].append(0)
        self.neList[0].append(0)

    def appendIslandSize(self, cov, value):
        try:
            self.islandSizesList[cov].append(int(value))
            self.islandRadiusList[cov].append(int(math.sqrt(float(value))))
        except ValueError:
            pass  # ignore if is not a number

    def appendData(self, cov, line):
        dataList = re.split('\t|\n', line) # split line into a list
        self.timeList[cov].append(dataList[1])  # get the time and store it in a list
        self.monomersList[cov].append(dataList[6])  # get the number of monomers
        self.neList[cov].append(dataList[7])    # get number of events and store it in a list
        if (len(dataList) > 10):           # if gyradius was calculated store it
            self.gyradiusList[cov].append(float(dataList[9]))
        if (len(dataList) > 11):           # if perimeter was calculated store it
            self.innerPerimeterList[cov].append(int(dataList[10]))
            self.outerPerimeterList[cov].append(int(dataList[11]))
        self.islandNumberList[cov].append(int(dataList[3]))

    def addReadLines(self, readLines):
        self.readLines = readLines
        
class MeanData:
    """ Stores averages over several executions """

    def __init__(self, maxCoverage, chunk):
        w = 0
        self.histogMatrix = [[0 for x in range(w)] for y in range(maxCoverage)]
        self.averageSizes = []
        self.times = []
        self.monomers = []
        self.numberOfIslands = []
        self.averageRatio = []
        self.allGyradius = []
        self.chunk = chunk

    def updateData(self, index, islandSizes, data):
        verbose = False
        # do histogram
        self.histogMatrix[index].append(np.histogram(islandSizes, bins=range(0, max(islandSizes)+self.chunk, self.chunk), density=False))
        # average
        self.averageSizes.append(np.mean(islandSizes))
        self.times.append(np.mean(np.array(data.timeList[index]).astype(np.float)))
        self.monomers.append(np.mean(np.array(data.monomersList[index]).astype(np.float)))
        self.numberOfIslands.append(np.mean(np.array(data.islandNumberList[index])))
        self.averageRatio.append(np.mean(np.array(data.neList[index]).astype(np.float)))
        self.allGyradius.append(np.mean(np.array(data.gyradiusList[index]).astype(np.float)))
        if verbose:
            print("  coverage {}%  {} time {}".format(index, averageSizes[-1], times[-1]))
        if index == 30: # only count islands in 30% of coverage
            self.numberOfIsland = len(islandSizes)/(data.readLines/30) # divide all islands by number of iterations

class Slopes:
    """ Stores fit slopes of several measurements"""
    
    def __init__(self):
        self.growth = 0
        self.gyradius = 0
        self.perimeter = 0
        

class MeanValues:
    """ Stores means of slopes """

    def __init__(self):
        self.growthSlopes = []
        self.gyradiusSlopes = []
        self.perimeterSlopes = []
        self.numberOfIslands = []
        self.numberOfMonomers = []
        self.simulatedTimes = []
        self.totalRatio = []
        self.aeRatioTimesPossibleList = []

    def updateData(self, slopes, currentData, numberOfIsland):
        self.growthSlopes.append(slopes.growth)
        self.gyradiusSlopes.append(slopes.gyradius)
        self.perimeterSlopes.append(slopes.perimeter)
        self.numberOfIslands.append(numberOfIsland)
        self.numberOfMonomers.append(np.mean(np.array(currentData.monomersList[-1]).astype(np.float)))

    def updateTimeAndRatio(self, simulatedTime, numberOfEvents, aeRatioTimesPossible):
        self.simulatedTimes.append(simulatedTime)
        self.totalRatio.append(numberOfEvents/simulatedTime)
        self.aeRatioTimesPossibleList.append(aeRatioTimesPossible)
        
class Results:
    """ Stores results (mainly to be plotted)"""

    def __init__(self):
        self.results = []

    def append(self, meanValues):
        self.results.append([meanValues.growthSlopes, meanValues.totalRatio, meanValues.gyradiusSlopes, meanValues.numberOfIslands, meanValues.perimeterSlopes, meanValues.numberOfMonomers, meanValues.aeRatioTimesPossibleList, meanValues.simulatedTimes])

    def growthSlope(self):
        """ returns island size growth slopes for the last flux, for all temperatures """
        return np.array(self.results[-1][0])

    def totalRatio(self):
        """ returns total ratio for the last flux, for all temperatures """
        return np.array(self.results[-1][1])

    def gyradius(self):
        """ returns gyradius for the last flux, for all temperatures """
        return np.array(self.results[-1][2])
        
