import re
import os
import math
import glob
import numpy as np
import matplotlib.pyplot as plt
from scipy.optimize import curve_fit

def getAllValues(f, maxCoverage, sqrt=True):
    """ reads all the values for the corresponding coverage """
    
    #get something like 0.05000 expression to be grepped 
    regExpression = '(0\...00)|(^0\...\s)' # corresponds to 0.??00 expression or 0.??[:spaces:] expression
    w = 0
    islandSizesList = [[0 for x in range(w)] for y in range(maxCoverage)]
    islandRadiusList = [[0 for x in range(w)] for y in range(maxCoverage)]
    gyradiusList = [[0 for x in range(w)] for y in range(maxCoverage)]
    timeList = [[0 for x in range(w)] for y in range(maxCoverage)]
    cov = 0
    previousLine = ""
    dataLine = ""
    # if found the coverage, save the next line
    for line in f:
        if cov:
            myList = re.split(',|\[|\]', dataLine)
            iterList = iter(myList)
            next(iterList) # Skip the first and second entries
            next(iterList)
            j = next(iterList)
            while j != '\n': #save the current values (island sizes) to an array
                islandSizesList[cov].append(int(j))
                islandRadiusList[cov].append(int(math.sqrt(float(j))))
                timeList[cov].append(time)
                j = next(iterList)
            myList = re.split('\t|\n', previousLine)
            if (len(myList) > 10):
                gyradiusList[cov].append(float(myList[9]))
            cov = 0
        if re.match(regExpression, line):
            cov = int(line[2]+line[3])
            time = re.split('\t', line)[1]
            dataLine = previousLine
        previousLine = line

    if (sqrt):
        return islandRadiusList, timeList, gyradiusList
    else:
        return islandSizesList, timeList, gyradiusList

def powerFunc(x, a, b):
    """ a*x^b function """
    return a*x**b

def openAndRead(chunk, maxCoverage, sqrt=True, verbose=True):
    """reads the input file and makes the histogram and the average
island size. It returns the slope of the fit, which is the growth rate."""

    numberOfIsland = 0
    fileName = "dataEvery1percentAndNucleation.txt"
    try:
        f = open(fileName)
    except OSError:
        try:
            f = open("results/"+fileName)
        except OSError:
            print("Input file {} can not be openned. Exiting! ".format(fileName))
            growthSlope = 0
            gyradiusSlope = 0
            time30cov = 1
            return growthSlope, gyradiusSlope, time30cov, numberOfIsland

    islandSizesList, timeList, gyradiusList = getAllValues(f, maxCoverage, sqrt)
    w = 0
    histogMatrix = [[0 for x in range(w)] for y in range(maxCoverage)]
    averageSizes = []
    times = []
    if verbose:
        print("Average island size for")

    for index, islandSizes in enumerate(islandSizesList):
        if islandSizes: #ensure that it is not null
            # do histogram
            histogMatrix[index].append(np.histogram(islandSizes, bins=range(0, max(islandSizes)+chunk, chunk), density=False))
            # average
            numberOfIsland += len(islandSizes)
            averageSizes.append(np.mean(islandSizes))
            times.append(np.mean(np.array(timeList[index]).astype(np.float)))
            if verbose:
                print("  coverage {}%  {} time {}".format(index,averageSizes[-1],times[-1]))

    averageGyradius = []
    for gyradius in gyradiusList:
        if gyradius: #ensure that it is not null
            averageGyradius.append(np.mean(gyradius))

    # Curve fitting
    x = np.array(times)
    try:
        a, b = np.polyfit(x, averageSizes, 1)
        popt = curve_fit(powerFunc, x, averageSizes)
        aPower = popt[0][0]
        bPower = popt[0][1]
        #a = averageSizes[-1]#/times[-1]
        #b = 0
            
        if verbose:
            plt.close()
            label = "{}x+{}".format(a, b)
            print(label)
            plt.plot(x,averageSizes)
            y = a*np.array(x)+b
            plt.plot(x,y, label=label)
            label = "{}x^{}".format(aPower, bPower)
            y = powerFunc(x, aPower, bPower)
            plt.plot(x, y, label=label)
            plt.legend(loc='upper left', prop={'size':6})
            plt.savefig("tmpFig.png")
            plt.close()

        if sqrt:
            growthSlope = aPower
        else:
            growthSlope = a
    except TypeError:
        growthSlope = 0

    try:
        a, b = np.polyfit(x, averageGyradius, 1)
        popt = curve_fit(powerFunc, x, averageGyradius)
        aPower = popt[0][0]
        bPower = popt[0][1]
        #a = averageGyradius[-1]#/times[-1]
        #b = 0
        if verbose:
            label = "{}x+{}".format(a, b)
            print(label)
            plt.plot(x,averageGyradius)
            y = a*np.array(x)+b
            plt.plot(x,y, label=label)
            y = powerFunc(x, aPower, bPower)
            label = "{}x^{}".format(aPower, bPower)
            plt.plot(x, y, label=label)
            plt.legend(loc='upper left', prop={'size':6})
            plt.savefig("tmpFig2.png")
            plt.close()
        if sqrt:
            gyradiusSlope = aPower
        else:
            gyradiusSlope = a
    except TypeError:
        gyradiusSlope = 0
    time30cov = np.mean(np.array(timeList[30]).astype(np.float))
    return growthSlope, gyradiusSlope, time30cov, numberOfIsland

Rtt = ([39840, 86370, 176400, 341700, 631400, 1118000, 1907000, 3141000, 5015000, 7784000, 11770000, 17390000, 25130000, 35610000, 49540000, 67760000, 91250000, 121100000, 158600000, 205000000, 262000000])
def getRtt(index):
    return Rtt[index]

def getAllRtt():
    return Rtt

def getNumberOfEvents(time30cov):
    numberOfEvents = []
    simulatedTime = []
    regExpression = ("Need")
    fail = False
    fileName = "unknown"
    try:
        fileName = glob.glob("output*")[-1]
        f = open(fileName)
        # if found the coverage, save the next line
        for line in f:
            if re.search(regExpression, line):
                numberOfEvents.append(float(re.split(' ', line)[-5]))
            if re.match("    0", line) and not fail:
                time = float(re.split('\t',line)[1])
                if time == 0:
                    fail = True
                else:
                    simulatedTime.append(time)
            if fail and re.search("Average", line):
                line = next(f)
                line = next(f)
                line = next(f)
                line = next(f)
                time = float(re.split('\t',line)[1])
                simulatedTime.append(time)
    except (OSError,IndexError):
        print("input file {} can not be openned. Exiting! ".format(fileName))
        #sys.exit()

    if all(v == 0 for v in simulatedTime):
        print("all values are zero ")
        averageNumberOfEvents = np.mean(np.array(numberOfEvents))
        if (math.isnan(averageNumberOfEvents)):
            averageNumberOfEvents = 1
        if (math.isnan(time30cov)):
            time30cov = 1
        return averageNumberOfEvents, time30cov
    else:
        return np.mean(np.array(numberOfEvents)), np.mean(np.array(simulatedTime))

def getIslandDistribution(sqrt=True):
    """ computes the island distribution """
    chunk = 40
    coverage = 31
    verbose = False
    growthSlopes = []
    gyradiusSlopes = []
    numberOfIslands = []
    totalRatio = []
    workingPath = os.getcwd()
    for temperature in range(120, 221, 5):
        try:
            os.chdir(str(temperature))
            growthSlope, gyradiusSlope, time30cov, numberOfIsland = openAndRead(chunk, coverage, sqrt, verbose)
            growthSlopes.append(growthSlope)
            gyradiusSlopes.append(gyradiusSlope)
            numberOfIslands.append(numberOfIsland)
            numberOfEvents, simulatedTime = getNumberOfEvents(time30cov)
            if (math.isnan(numberOfEvents) or math.isnan(simulatedTime) or simulatedTime == 0):
                print("something went wrong")
                print("\t"+str(numberOfEvents))
                print("\t"+str(simulatedTime))
            totalRatio.append(numberOfEvents/simulatedTime)
            try:
                print("Temperature {} growth {:f} gyradius {:f} total rate {:d} ".format(temperature, growthSlope, gyradiusSlope, int(numberOfEvents/simulatedTime)))
            except ValueError:
                a = 0 # skip the writing
        except OSError:
            print ("error changing to directory {}".format(temperature))
            a = 0 #do nothing
        os.chdir(workingPath)

    return growthSlopes, totalRatio, gyradiusSlopes, numberOfIslands
