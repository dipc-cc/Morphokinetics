import os
import re
import math
import glob
import numpy as np
import csv
import morphokineticsLow as mk
import results


def powerFunc(x, a, b):
    """ a*x^b function """
    return a*x**b

def fractalDFunc(x):
    """Fractal dimension for x (= rtt/flux)"""
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
    return np.array(y)

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

def readFractalD(flux):
    return np.array(mk.readFractalD(flux)).astype(float)

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


def readData(chunk, maxCoverage, sqrt=True, verbose=True, growth=True, temperature=-1, flux=-1):
    """reads the input file and makes the histogram and the average
island size. It returns the slope of the fit, which is the growth rate."""

    slopes = results.Slopes()
    try:
        averageData = mk.readAllValues(maxCoverage, temperature, flux)    
    except (OSError, IndexError):
        print("averaged data was not found. Trying to compute it...")
        numberOfIsland = 0
        lastGyradius = 0
        averageData = results.AverageData(maxCoverage, chunk)
        completeData = results.CompleteData(maxCoverage)
        try:
            completeData = mk.getAllValues(maxCoverage, growth)
        except OSError:
            averageData.slopes = slopes
            return averageData
        
        if sqrt:
            islandSizesList = completeData.islandSizesSqrt
        else:
            islandSizesList = completeData.islandSizes
        averageData = results.AverageData(maxCoverage, chunk) # create object
        if verbose:
            print("Average island size for")

        filename = "dataFile"+'{:E}'.format(flux)+"_"+str(temperature)+".txt"
        with open(filename, 'w', newline='') as csvfile:
            outwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
            outwriter.writerow(["%","index, temperature, flux, monomers[-1], index/100, times[-1], islandsAmount[-1], averageSizes[-1], averageRatio[-1]/times[-1], allGyradius[-1], stdSizes, stdGyradius, sumProb, s^2, r_g^2, islandsAmount**2, monomers**2"])
            for index, islandSizes in enumerate(islandSizesList):
                if islandSizes: #ensure that it is not null
                    averageData.updateData(index, islandSizes, completeData)
                    outwriter.writerow([index, temperature, flux, averageData.monomers[-1], index/100, averageData.times[-1], averageData.islandsAmount[-1], averageData.sizes[-1], averageData.ratio[-1]/averageData.times[-1], averageData.gyradius[-1], averageData.stdSizes[-1], averageData.stdGyradius[-1], averageData.sumProb[-1], averageData.sizes2[-1], averageData.gyradius2[-1], averageData.islandsAmount2[-1], averageData.monomers2[-1]])
                

    islandAmount = float(averageData.lastIslandAmount())
    # Curve fitting
    slopes.growth = mk.getSlope(list(range(1,maxCoverage)), averageData.sizes, sqrt, verbose, "tmpFig.png")
    slopes.gyradius = mk.getSlope(list(range(1,maxCoverage)), averageData.gyradius, sqrt, verbose, "tmpTimeVsGyradius.png")
    mk.getSlope(averageData.sizes, averageData.gyradius, sqrt, verbose, "tmpFig4.png")
    coverages = 400*400/100*np.arange(0.01,maxCoverage-1, 1)/(islandAmount+1)
    mk.getSlope(averageData.times, coverages, sqrt, verbose, "tmpFig5.png")
    #slopes.perimeter = mk.getAverageGrowth(averageData.times, completeData.outerPerimeter, sqrt=False, verbose=verbose, tmpFileName="tmpFig3.png")
    slopes.monomers = mk.getSlope(averageData.times, averageData.monomers, sqrt, verbose)
    averageData.slopes = slopes
    return averageData

def getRtt(temperatures):
    kb = 8.6173324e-5
    Rtt = []
    for index,i in enumerate(temperatures):
        Rtt.append(1e13*np.exp(-0.2/(kb*i)))
    return np.array(Rtt)

def getAllRtt():
    return Rtt

def getNumberOfEvents(time30cov):
    """ gets data from output* file """
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
            if re.match("    0", line):
                try:
                    time = float(re.split('\t',line)[1])
                    simulatedTime.append(time)
                except (ValueError,IndexError):
                    fail = True

            if fail and re.search("Average", line): # if individual times are not found, try to get it from the end of the file.
                line = next(f)
                line = next(f)
                line = next(f)
                line = next(f)
                time = float(re.split('\t',line)[1])
                simulatedTime.append(time)
    except (OSError,IndexError) as ex:
        print("Input file {} can not be openned. Exiting! ".format(fileName))

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
    workingPath = os.getcwd()
    meanValues = results.MeanValues()
    for temperature in temperatures:
        try:
            os.chdir(str(temperature))
        except OSError:
            print("error changing to temperature in directory {}".format(temperature), end="") #do nothing
            print(", creating it...")
            os.mkdir(str(temperature))
        else:
            averageData = readData(chunk, coverage, sqrt, verbose, temperature=temperature, flux=flux)
            meanValues.updateData(averageData) 
            if (interval):
                time2 = averageData.getTime(30)
                time1 = averageData.getTime(20)
                ne2 = averageData.getNe(30)
                ne1 = averageData.getNe(20)
                
                numberOfEvents = ne2-ne1
                simulatedTime = time2-time1
            else:
                time30cov = averageData.lastTime() 
                numberOfEvents, simulatedTime, aeRatioTimesPossible = getNumberOfEvents(time30cov)
                if (math.isnan(numberOfEvents) or math.isnan(simulatedTime) or simulatedTime == 0):
                    print("something went wrong")
                    print("\t"+str(numberOfEvents))
                    print("\t"+str(simulatedTime))
            meanValues.updateTimeAndRatio(simulatedTime, numberOfEvents, aeRatioTimesPossible)
            try:
                print("Temperature {} growth {:f} gyradius {:f} total rate {:d} ".format(temperature, averageData.slopes.growth, averageData.slopes.gyradius, int(numberOfEvents/simulatedTime)))
            except (ValueError, ZeroDivisionError):
                a = 0 # skip the writing

        os.chdir(workingPath)
    return meanValues
