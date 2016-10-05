import re
import math
import numpy as np
import matplotlib.pyplot as plt
import morphokineticsLow as mk
from scipy.optimize import curve_fit

def powerFunc(x, a, b):
    """ a*x^b function """
    return a*x**b

def getAllValues(f, maxCoverage, sqrt=True):
    """ reads all the values for the corresponding coverage """
    
    #get something like 0.05000 expression to be grepped 
    regExpression = '(0\...00)|(^0\...\s)' # corresponds to 0.??00 expression or 0.??[:spaces:] expression
    w = 0
    islandSizesList = [[0 for x in range(w)] for y in range(maxCoverage)]
    islandRadiusList = [[0 for x in range(w)] for y in range(maxCoverage)]
    gyradiusList = [[0 for x in range(w)] for y in range(maxCoverage)]
    timeList = [[0 for x in range(w)] for y in range(maxCoverage)]
    neList = [[0 for x in range(w)] for y in range(maxCoverage)]
    innerPerimeterList = [[0 for x in range(w)] for y in range(maxCoverage)]
    outerPerimeterList = [[0 for x in range(w)] for y in range(maxCoverage)]
    timeList[0].append(0)
    neList[0].append(0)
    cov = 0
    previousLine = ""
    dataLine = ""
    # if found the coverage, save the next line (with the histogram)
    for line in f:
        if cov:
            dataList = re.split(',|\[|\]', dataLine)
            iterList = iter(dataList) # get island histogram
            next(iterList) # skip the first and second entries of histogram ("histogram" word and value of unoccupied positions)
            next(iterList)
            j = next(iterList)
            while j != '\n': # save the current values (island sizes) to an array
                islandSizesList[cov].append(int(j))
                islandRadiusList[cov].append(int(math.sqrt(float(j))))
                j = next(iterList)
            cov = 0
        if re.match(regExpression, line):      # just hit a coverage
            cov = int(line[2]+line[3])         # get coverage
            dataList = re.split('\t|\n', line) # split line into a list
            timeList[cov].append(dataList[1])  # get the time and store it in a list
            neList[cov].append(dataList[7])    # get number of events and store it in a list
            if (len(dataList) > 10):           # if gyradius was calculated store it
                gyradiusList[cov].append(float(dataList[9]))
            if (len(dataList) > 11):           # if perimeter was calculated store it
                innerPerimeterList[cov].append(int(dataList[10]))
                outerPerimeterList[cov].append(int(dataList[11]))
            dataLine = previousLine
        previousLine = line

    if (sqrt):
        return islandRadiusList, timeList, gyradiusList, neList, innerPerimeterList, outerPerimeterList
    else:
        return islandSizesList, timeList, gyradiusList, neList, innerPerimeterList, outerPerimeterList

    
def getAverageGrowth(times, gyradiusList, sqrt=False, verbose=False, tmpFileName="tmpFig.png"):
    
    x = np.array(times)
    averageGyradius = []
    for gyradius in gyradiusList:
        if gyradius: #ensure that it is not null
            averageGyradius.append(np.mean(gyradius))
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
            plt.plot(x, averageGyradius)
            y = a*np.array(x)+b
            plt.plot(x, y, label=label)
            y = powerFunc(x, aPower, bPower)
            label = "{}x^{}".format(aPower, bPower)
            plt.plot(x, y, label=label)
            plt.legend(loc='upper left', prop={'size':6})
            plt.savefig(tmpFileName)
            plt.close()
        if sqrt:
            gyradiusSlope = aPower
        else:
            gyradiusSlope = a
    except TypeError:
        gyradiusSlope = 0

    return gyradiusSlope
