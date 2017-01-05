import os
import re
import math
import glob
import numpy as np
import csv
import morphokineticsLow as mk
import results
import matplotlib.pyplot as plt


def powerFunc(x, a, b):
    """ a*x^b function """
    return a*x**b

def expFunc(x, a, b):
    """ ae^bx function """
    return a*np.exp(x*b)

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
    except (OSError, IndexError, TypeError):
        print("averaged data was not found. Trying to compute it...")
        numberOfIsland = 0
        lastGyradius = 0
        averageData = results.AverageData(maxCoverage, chunk) # create object
        completeData = results.CompleteData(maxCoverage)      # create object
        try:
            completeData = mk.getAllValues(maxCoverage, growth)
        except OSError:
            averageData.slopes = slopes
            return averageData
        
        if sqrt:
            islandSizesList = completeData.islandSizesSqrt
        else:
            islandSizesList = completeData.islandSizes
        if verbose:
            print("Average island size for")

        filename = "dataFile"+'{:E}'.format(flux)+"_"+str(temperature)+".txt"
        with open(filename, 'w', newline='') as csvfile:
            outwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
            outwriter.writerow(["%","index, temperature, flux, monomers[-1], coverage, times[-1], islandsAmount[-1], averageSizes[-1], averageRatio[-1]/times[-1], allGyradius[-1], stdSizes, stdGyradius, sumProb, s^2, r_g^2, islandsAmount**2, monomers**2, innerPerimeter, outerPerimeter, stdInner, stdOuter, numberOfEvents"])
            for index, islandSizes in enumerate(islandSizesList, start=0):
                if islandSizes: #ensure that it is not null
                    averageData.updateData(index, islandSizes, completeData)
                    outwriter.writerow([index, temperature, flux, averageData.monomers[-1], (index+1)/100, averageData.times[-1], averageData.islandsAmount[-1], averageData.sizes[-1], averageData.ratio[-1]/averageData.times[-1], averageData.gyradius[-1], averageData.stdSizes[-1], averageData.stdGyradius[-1], averageData.sumProb[-1], averageData.sizes2[-1], averageData.gyradius2[-1], averageData.islandsAmount2[-1], averageData.monomers2[-1], averageData.innerPerimeter[-1], averageData.outerPerimeter[-1], averageData.stdInnerPerimeter[-1], averageData.stdOuterPerimeter[-1], averageData.ne[-1]])

        if verbose:
            plt.figure(num=None, figsize=(4,4), dpi=80)
            for i in range(5,maxCoverage):
                plt.semilogy(averageData.histogX[i], averageData.histogY[i], label=i+1)
            plt.legend(loc='upper right', prop={'size':4})
            plt.xlim(0,5)
            plt.savefig("histo.png")

    islandAmount = float(averageData.lastIslandAmount())
    # Curve fitting
    slopes.growth = mk.getSlope(list(range(0,maxCoverage)), averageData.sizes, maxCoverage, sqrt, verbose, "tmpFig.png")
    slopes.gyradius = mk.getSlope(list(range(0,maxCoverage)), averageData.gyradius, maxCoverage, sqrt, verbose, "tmpTimeVsGyradius.png")
    mk.getSlope(averageData.sizes, averageData.gyradius, maxCoverage, sqrt, verbose, "tmpFig4.png")
    coverages = 400*400/100*np.arange(0.01,maxCoverage, 1)/(islandAmount+1)
    mk.getSlope(averageData.times, coverages, maxCoverage, sqrt, verbose, "tmpFig5.png")
    slopes.innerPerimeter =  mk.getSlope(averageData.times, averageData.innerPerimeter, maxCoverage, sqrt=True, verbose=verbose, tmpFileName="tmpFigInnerPerimeter.png")
    slopes.outerPerimeter = mk.getSlope(averageData.times, averageData.outerPerimeter, maxCoverage, sqrt=True, verbose=verbose, tmpFileName="tmpFigOuterPerimeter.png")
    slopes.monomers = mk.getSlope(averageData.times, averageData.monomers, maxCoverage, sqrt, verbose)
    slopes.islandsAmount = mk.getSlope(averageData.times, averageData.islandsAmount, maxCoverage,sqrt, verbose)
    averageData.slopes = slopes
    return averageData

def getRtt(temperatures):
    kb = 8.6173324e-5
    Rtt = []
    for i in temperatures:
        Rtt.append(1e13*np.exp(-0.2/(kb*i)))
    return np.array(Rtt)

def getAllRtt():
    return Rtt

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

def getIslandDistribution(temperatures, sqrt=True, interval=False, growth=True, verbose = False, flux=-1, maxCoverage=30):
    """ computes the island distribution """
    chunk = 40
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
            averageData = readData(chunk, maxCoverage, sqrt, verbose, temperature=temperature, flux=flux)
            meanValues.updateData(averageData) 
            if (interval):
                time2 = averageData.getTime(30)
                time1 = averageData.getTime(20)
                ne2 = averageData.getNe(30)
                ne1 = averageData.getNe(20)
                
                numberOfEvents = ne2-ne1
                simulatedTime = time2-time1
            else:
                simulatedTime = averageData.lastTime()
                numberOfEvents = averageData.lastNe()
            meanValues.updateTimeAndRatio(simulatedTime, numberOfEvents)
            try:
                print("Temperature {} growth {:f} gyradius {:f} total rate {:d} ".format(temperature, averageData.slopes.growth, averageData.slopes.gyradius, int(numberOfEvents/simulatedTime)))
            except (ValueError, ZeroDivisionError):
                a = 0 # skip the writing

        os.chdir(workingPath)
    return meanValues
