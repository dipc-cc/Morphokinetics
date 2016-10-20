import re
import os
import math
import glob
import numpy as np
import morphokineticsLow as mk


def powerFunc(x, a, b):
    """ a*x^b function """
    return a*x**b

def fractalDFunc(x):
    """"""
    minD = 1.66
    y = []
    for i in range(len(x)):
        if (x[i] <= 3e7):
            y.append(minD)
        else:
            if (x[i] > 5e8):
                y.append(2.0)
            else:
                #y.append(minD+(2-minD)/np.log(5e8/3e7)*np.log(x[i]))
                y.append(minD+(2-minD)/(5e8-3e7)*(x[i]-3e7))
    return y

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
            perimeterSlope = 0
            time30cov = []
            for i in range(0,maxCoverage):
                time30cov.append(1)
            neList = 0
            monomersList = []
            monomersList.append(0.0)
            return growthSlope, gyradiusSlope, perimeterSlope, time30cov, numberOfIsland, neList, monomersList

    islandSizesList, timeList, gyradiusList, neList, innerPerimeterList, outerPerimeterList, readLines, monomersList, islandNumberList = mk.getAllValues(f, maxCoverage, sqrt)
    w = 0
    histogMatrix = [[0 for x in range(w)] for y in range(maxCoverage)]
    averageSizes = []
    times = []
    monomers = []
    if verbose:
        print("Average island size for")

    for index, islandSizes in enumerate(islandSizesList):
        if islandSizes: #ensure that it is not null
            # do histogram
            histogMatrix[index].append(np.histogram(islandSizes, bins=range(0, max(islandSizes)+chunk, chunk), density=False))
            # average
            averageSizes.append(np.mean(islandSizes))
            times.append(np.mean(np.array(timeList[index]).astype(np.float)))
            monomers.append(np.mean(np.array(monomersList[index]).astype(np.float)))
            if verbose:
                print("  coverage {}%  {} time {}".format(index,averageSizes[-1],times[-1]))
            if index == 30: # only count islands in 30% of coverage
                numberOfIsland = len(islandSizes)/(readLines/30) # divide all islands by number of iterations

    numberOfIsland = np.mean(np.array(islandNumberList[30]))

    # Curve fitting
    growthSlope = mk.getAverageGrowth(times, averageSizes, sqrt, verbose, "tmpFig.png")
    gyradiusSlope = mk.getAverageGrowth(times, gyradiusList, sqrt, verbose, "tmpFig2.png")
    mk.getAverageGrowth(averageSizes, gyradiusList, sqrt, verbose, "tmpFig4.png")
    coverages = 400*400/100*np.arange(0.0,maxCoverage, 1)/(numberOfIsland+1)
    mk.getAverageGrowth(times, coverages, sqrt, verbose, "tmpFig5.png")
    perimeterSlope = mk.getAverageGrowth(times, outerPerimeterList, sqrt=False, verbose=verbose, tmpFileName="tmpFig3.png")
    return growthSlope, gyradiusSlope, perimeterSlope, timeList, numberOfIsland, neList, monomersList

def getRtt(temperatures):
    kb = 8.6173324e-5
    Rtt = []
    for index,i in enumerate(temperatures):
        Rtt.append(1e13*np.exp(-0.2/(kb*i)))
    return Rtt

def getAllRtt():
    return Rtt

def getNumberOfEvents(time30cov):
    numberOfEvents = []
    simulatedTime = []
    regExpression = ("Need")
    aeExpression = ("AeRatioTimesPossible")
    fail = False
    fileName = "unknown"
    aeRatioTimesPossible = 0
    try:
        fileName = glob.glob("output*")[-1]
        f = open(fileName)
        # if found the coverage, save the next line
        for line in f:
            if re.search(aeExpression, line):
                try:
                    newValue = float(re.split(' ', line)[1])
                    if (aeRatioTimesPossible < newValue):
                        aeRatioTimesPossible = newValue
                except ValueError:
                    aeRatioTimesPossible = 0
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
        return averageNumberOfEvents, time30cov, aeRatioTimesPossible
    else:
        return np.mean(np.array(numberOfEvents)), np.mean(np.array(simulatedTime)), aeRatioTimesPossible

def getIslandDistribution(temperatures, sqrt=True, interval=False):
    """ computes the island distribution """
    chunk = 40
    coverage = 31
    verbose = False
    growthSlopes = []
    gyradiusSlopes = []
    perimeterSlopes = []
    numberOfIslands = []
    totalRatio = []
    numberOfMonomers = []
    workingPath = os.getcwd()
    aeRatioTimesPossible = 0
    aeRatioTimesPossibleList = []
    for temperature in temperatures:
        try:
            os.chdir(str(temperature))
        except OSError:
            print ("error changing to directory {}".format(temperature)) #do nothing
        else:
            growthSlope, gyradiusSlope, perimeterSlope, timeList, numberOfIsland, neList, monomersList = openAndRead(chunk, coverage, sqrt, verbose)
            growthSlopes.append(growthSlope)
            gyradiusSlopes.append(gyradiusSlope)
            perimeterSlopes.append(perimeterSlope)
            numberOfIslands.append(numberOfIsland)
            numberOfMonomers.append(np.mean(np.array(monomersList[-1]).astype(np.float)))
            if (interval):
                time2 = np.mean(np.array(timeList[30]).astype(np.float)) # get time at 30% of coverage
                time1 = np.mean(np.array(timeList[20]).astype(np.float)) # get time at 20% of coverage
                neList30 = [item for item in neList[30] if (float(item) >= 0)]  # remove negative values
                ne2 = np.mean(np.array(neList30).astype(np.float))
                neList20 = [item for item in neList[20] if (float(item) >= 0)]
                ne1 = np.mean(np.array(neList20).astype(np.float))

                numberOfEvents = ne2-ne1
                simulatedTime = time2-time1
            else:
                time30cov = np.mean(np.array(timeList[coverage-1]).astype(np.float))
                numberOfEvents, simulatedTime, aeRatioTimesPossible = getNumberOfEvents(time30cov)
                aeRatioTimesPossibleList.append(aeRatioTimesPossible)
                if (math.isnan(numberOfEvents) or math.isnan(simulatedTime) or simulatedTime == 0):
                    print("something went wrong")
                    print("\t"+str(numberOfEvents))
                    print("\t"+str(simulatedTime))
            totalRatio.append(numberOfEvents/simulatedTime)
            try:
                print("Temperature {} growth {:f} gyradius {:f} total rate {:d} ".format(temperature, growthSlope, gyradiusSlope, int(numberOfEvents/simulatedTime)))
            except ValueError:
                a = 0 # skip the writing

        os.chdir(workingPath)
    return growthSlopes, totalRatio, gyradiusSlopes, numberOfIslands, perimeterSlopes, numberOfMonomers, aeRatioTimesPossibleList
