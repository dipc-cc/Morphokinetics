import re
import os
import math
import sys
import numpy as np
import matplotlib.pyplot as plt

def getAllValues(f, maxCoverage, sqrt=True):
    """ reads all the values for the corresponding coverage """
    
    #get something like 0.05000 expression to be grepped 
    regExpression='(0\...00)|(^0\...\s)' # corresponds to 0.??00 expression or 0.??[:spaces:] expression
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
            cov=0
        if re.match(regExpression, line):
            cov=int(line[2]+line[3])
            time = re.split('\t', line)[1]
            dataLine = previousLine
        previousLine = line

    if (sqrt):
        return islandRadiusList, timeList, gyradiusList
    else:
        return islandSizesList, timeList, gyradiusList

def openAndRead(chunk, maxCoverage, sqrt=True, verbose=True):
    """reads the input file and makes the histogram and the average
island size. It returns the slope of the fit, which is the growth rate."""

    fileName = "dataEvery1percentAndNucleation.txt"
    try:
        f=open(fileName)
    except OSError:
        try:
            f=open("results/"+fileName)
        except OSError:
            print("Input file {} can not be openned. Exiting! ".format(fileName))
            sys.exit()

    i=0
    histog = []
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
            histogMatrix[index].append(np.histogram(islandSizes, bins=range(0, max(islandSizes)+chunk,chunk), density=False))
            # average
            averageSize = np.mean(islandSizes)
            averageSizes.append(np.mean(islandSizes))
            times.append(np.mean(np.array(timeList[index]).astype(np.float)))
            if verbose:
                print("  coverage {}% is {}. Time is {}".format(index,averageSizes[-1],times[-1]))
            #normalise
            for kk in histogMatrix[index]:
                currentHistogram = kk[0]
                halfBin = chunk
                cov = index/100
                yValues = []
                xValues = []
                for value in currentHistogram:
                    yValues.append(halfBin/averageSizes[-1])
                    xValues.append(value*averageSizes[-1]*averageSizes[-1]/cov)
                    halfBin += chunk
    averageGyradius = []
    for gyradius in gyradiusList:
        if gyradius: #ensure that it is not null
            averageGyradius.append(np.mean(gyradius))

    x = np.array(times)
    a, b = np.polyfit(x, averageSizes, 1)
    #a = averageSizes[-1]#/times[-1]
    #b = 0
    if verbose:
        print("fit a*x+b, a={} b={}".format(a, b))
        plt.plot(x,averageSizes)
        y = a*np.array(x)+b
        plt.plot(x,y)
        plt.savefig("tmpFig.png")
        plt.close()
    growthSlope = a

    if averageGyradius:
        a, b = np.polyfit(x, averageGyradius, 1)
        #a = averageGyradius[-1]#/times[-1]
        #b = 0
        if verbose:
            print("fit a*x+b, a={} b={}".format(a, b))
            plt.plot(x,averageGyradius)
            y = a*np.array(x)+b
            plt.plot(x,y)
            plt.savefig("tmpFig2.png")
            plt.close()
        gyradiusSlope = a
    else:
        gyradiusSlope = 0
    return growthSlope, gyradiusSlope

Rtt = ([39840, 86370, 176400, 341700, 631400, 1118000, 1907000, 3141000, 5015000, 7784000, 11770000, 17390000, 25130000, 35610000, 49540000, 67760000, 91250000, 121100000, 158600000, 205000000, 262000000])
def getRtt(index):
    return Rtt[index]

def getAllRtt():
    return Rtt

def getNumberOfEvents():
    numberOfEvents = []
    simulatedTime = []
    count = 0
    regExpression=("Need")
    fileName = "output"
    fail = False
    try:
        f=open(fileName)
        # if found the coverage, save the next line
        for line in f:
            if re.search(regExpression, line):
                numberOfEvents.append(float(re.split(' ', line)[-5]))
                count +=1
            if re.match("    00", line) and not fail:
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
    except OSError:
        print("input file {} can not be openned. Exiting! ".format(fileName))
        #sys.exit()

    if all(v == 0 for v in simulatedTime):
        print("all values are zero")
    return np.mean(np.array(numberOfEvents)), np.mean(np.array(simulatedTime))

def getIslandDistribution(sqrt=True):
    """ computes the island distribution """
    chunk=40
    coverage=31
    verbose=False
    growthSlopes = []
    gyradiusSlopes = []
    allNe = []
    workingPath = os.getcwd()
    for temperature in range(120, 221, 5):
        try:
            os.chdir(str(temperature))
            growthSlope, gyradiusSlope = openAndRead(chunk, coverage, sqrt, verbose)
            growthSlopes.append(growthSlope)
            gyradiusSlopes.append(gyradiusSlope)
            numberOfEvents, simulatedTime = getNumberOfEvents()
            allNe.append(numberOfEvents/simulatedTime)
            try:
                print("Temperature {} growth {:f} gyradius {:f} total rate {:d} ".format(temperature,growthSlope, gyradiusSlope, int(numberOfEvents/simulatedTime)))
            except ValueError:
                a=0 # skip the writing
        except OSError:
            print ("error changing to directory {}".format(temperature))
            a=0 #do nothing
        os.chdir(workingPath)

    return growthSlopes, allNe, gyradiusSlopes
