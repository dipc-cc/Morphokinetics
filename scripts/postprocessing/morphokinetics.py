import re
import os
import math
import glob
import numpy as np
import csv
import morphokineticsLow as mk


def powerFunc(x, a, b):
    """ a*x^b function """
    return a*x**b

def fractalDFunc(x):
    """"""
    minD = 1.66
    maxRatio = 5e8
    minRatio = 3e7
    y = []
    for i in range(len(x)):
        if (x[i] <= minRatio):
            y.append(minD)
        else:
            if (x[i] > maxRatio):
                y.append(2.0)
            else:
                #y.append(minD+(2-minD)/np.log(5e8/3e7)*np.log(x[i]))
                y.append(minD+(2-minD)/(maxRatio-minRatio)*(x[i]-minRatio))
    return y

def fractDFuncTemperature(temp):
    maxD = 2.00
    minD = 1.75
    maxTemp = 62
    minTemp = 55
    y = []
    for i in range(len(temp)):
        if (temp[i] <= minTemp):
            y.append(maxD)
        else:
            if (temp[i] > maxTemp):
                y.append(minD)
            else:
                y.append(maxD-(maxD-minD)/(maxTemp-minTemp)*(temp[i]-minTemp))#    minD+(2-minD)/(maxTemp-minTemp)*(temp[i]-minTemp))
    return y
    

def fractalDimensionFunc(x):
    """returns fractal dimension, based on a fit of a study of single
flake simulations. """
    a=0.27
    b=1.73
    c=19
    sigma=2
    x0=np.exp(c)
    d=b+a*(x**sigma)/(x**sigma+x0**sigma)
    return np.array(d)

def shapeFactorFunc(x):
    """returns shape factor, based on a fit of a study of single flake
simulations. """
    a=5.5
    b=3.5
    c=18
    sigma=1
    x0=np.exp(c)
    f=b+a*(x**sigma)/(x**sigma+x0**sigma)
    max = 7.3
    f=[max-(i-max)  if i > max else i for i in f]
    return np.array(f)


def openAndRead(chunk, maxCoverage, sqrt=True, verbose=True, growth=True, temperature=-1, flux=-1):
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

    islandSizesList, timeList, gyradiusList, neList, innerPerimeterList, outerPerimeterList, readLines, monomersList, islandNumberList = mk.getAllValues(f, maxCoverage, sqrt, growth)
    w = 0
    histogMatrix = [[0 for x in range(w)] for y in range(maxCoverage)]
    averageSizes = []
    times = []
    monomers = []
    numberOfIslands = []
    averageRatio = []
    allGyradius = []
    if verbose:
        print("Average island size for")

    filename = "outputFile"+'{:E}'.format(flux)+"_"+str(temperature)+".txt"
    with open(filename, 'w', newline='') as csvfile:
        outwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
        outwriter.writerow(["%[index, temperature, flux, monomers[-1], index/100, times[-1], numberOfIslands[-1], averageSizes[-1], averageRatio[-1]/times[-1], allGyradius[-1]]"])
        for index, islandSizes in enumerate(islandSizesList):
            if islandSizes: #ensure that it is not null
                # do histogram
                histogMatrix[index].append(np.histogram(islandSizes, bins=range(0, max(islandSizes)+chunk, chunk), density=False))
                # average
                averageSizes.append(np.mean(islandSizes))
                times.append(np.mean(np.array(timeList[index]).astype(np.float)))
                monomers.append(np.mean(np.array(monomersList[index]).astype(np.float)))
                numberOfIslands.append(np.mean(np.array(islandNumberList[index])))
                averageRatio.append(np.mean(np.array(neList[index]).astype(np.float)))
                allGyradius.append(np.mean(np.array(gyradiusList[index]).astype(np.float)))
                if verbose:
                    print("  coverage {}%  {} time {}".format(index,averageSizes[-1],times[-1]))
                if index == 30: # only count islands in 30% of coverage
                    numberOfIsland = len(islandSizes)/(readLines/30) # divide all islands by number of iterations
                outwriter.writerow([index, temperature, flux, monomers[-1], index/100, times[-1], numberOfIslands[-1], averageSizes[-1], averageRatio[-1]/times[-1], allGyradius[-1]])
                

    numberOfIsland = np.mean(np.array(islandNumberList[30]))

    # Curve fitting
    growthSlope = mk.getAverageGrowth(times, averageSizes, sqrt, verbose, "tmpFig.png")
    gyradiusSlope = mk.getAverageGrowth(times, gyradiusList, sqrt, verbose, "tmpTimeVsGyradius.png")
    mk.getAverageGrowth(averageSizes, gyradiusList, sqrt, verbose, "tmpFig4.png")
    coverages = 400*400/100*np.arange(0.0,maxCoverage, 1)/(numberOfIsland+1)
    mk.getAverageGrowth(times, coverages, sqrt, verbose, "tmpFig5.png")
    perimeterSlope = mk.getAverageGrowth(times, outerPerimeterList, sqrt=False, verbose=verbose, tmpFileName="tmpFig3.png")
    #gyradiusList vs averageSizes
    mk.plot(gyradiusList, averageSizes)
    return growthSlope, gyradiusSlope, perimeterSlope, timeList, numberOfIsland, neList, monomersList

def getRtt(temperatures):
    kb = 8.6173324e-5
    Rtt = []
    for index,i in enumerate(temperatures):
        Rtt.append(1e13*np.exp(-0.2/(kb*i)))
    return np.array(Rtt)

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


def getAllFractalDimensions(temperatures, verbose=False):
    workingPath = os.getcwd()
    fractalDimensions = []
    for temperature in temperatures:
        try:
            os.chdir(str(temperature)+"/results")
        except OSError:
            print ("error changing to directory {}".format(temperature)) #do nothing
        else:
            fractalDimension = 0
            fractalDimension = mk.getFractalDimensionSingle(verbose)
            fractalDimensions.append(fractalDimension)
            print(temperature, fractalDimension)
        os.chdir(workingPath)
    return fractalDimensions

def getIslandDistribution(temperatures, sqrt=True, interval=False, growth=True, verbose = False, flux=-1):
    """ computes the island distribution """
    chunk = 40
    coverage = 31
    growthSlopes = []
    gyradiusSlopes = []
    perimeterSlopes = []
    numberOfIslands = []
    totalRatio = []
    numberOfMonomers = []
    workingPath = os.getcwd()
    aeRatioTimesPossible = 0
    aeRatioTimesPossibleList = []
    simulatedTimes = []
    for temperature in temperatures:
        try:
            os.chdir(str(temperature))
        except OSError:
            print ("error changing to directory {}".format(temperature)) #do nothing
        else:
            growthSlope, gyradiusSlope, perimeterSlope, timeList, numberOfIsland, neList, monomersList = openAndRead(chunk, coverage, sqrt, verbose, temperature=temperature, flux=flux)
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
            simulatedTimes.append(simulatedTime)
            totalRatio.append(numberOfEvents/simulatedTime)
            try:
                print("Temperature {} growth {:f} gyradius {:f} total rate {:d} ".format(temperature, growthSlope, gyradiusSlope, int(numberOfEvents/simulatedTime)))
            except ValueError:
                a = 0 # skip the writing

        os.chdir(workingPath)
    return growthSlopes, totalRatio, gyradiusSlopes, numberOfIslands, perimeterSlopes, numberOfMonomers, aeRatioTimesPossibleList, simulatedTimes
