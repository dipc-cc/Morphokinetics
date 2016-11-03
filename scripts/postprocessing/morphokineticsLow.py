import re
import math
import numpy as np
import matplotlib.pyplot as plt
import morphokineticsLow as mk
import scipy.special
from scipy.optimize import curve_fit

def powerFunc(x, a, b):
    """ a*x^b function """
    return a*x**b

def errFunc(x, a, b, c, sigma):
    return a*scipy.special.erf(sigma*(x-c))+b

def ourErrFunc(x, a, b, c, sigma):
    return a*(np.exp(sigma*(x-c))/(1+np.exp(sigma*(x-c))))+b

def getAllValues(f, maxCoverage, sqrt=True, getSlopes=True):
    """ reads all the values for the corresponding coverage """
    
    #get something like 0.05000 expression to be grepped 
    regExpression = '(0\...00)|(^0\...\s)' # corresponds to 0.??00 expression or 0.??[:spaces:] expression
    w = 0
    islandSizesList = [[0 for x in range(w)] for y in range(maxCoverage)]
    islandRadiusList = [[0 for x in range(w)] for y in range(maxCoverage)]
    gyradiusList = [[0 for x in range(w)] for y in range(maxCoverage)]
    timeList = [[0 for x in range(w)] for y in range(maxCoverage)]
    monomersList = [[0 for x in range(w)] for y in range(maxCoverage)]
    neList = [[0 for x in range(w)] for y in range(maxCoverage)]
    innerPerimeterList = [[0 for x in range(w)] for y in range(maxCoverage)]
    outerPerimeterList = [[0 for x in range(w)] for y in range(maxCoverage)]
    islandNumberList = [[0 for x in range(w)] for y in range(maxCoverage)]
    timeList[0].append(0)
    neList[0].append(0)
    cov = 0
    previousLine = ""
    dataLine = ""
    readLines = 0
    # if found the coverage, save the next line (with the histogram)
    for line in f:
        if cov and getSlopes:
            readLines += 1
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
            if (cov >= maxCoverage):           
                cov = 0                        # coverage is bigger than wanted, skip
            else:
                dataList = re.split('\t|\n', line) # split line into a list
                timeList[cov].append(dataList[1])  # get the time and store it in a list
                monomersList[cov].append(dataList[6])  # get the number of monomers
                neList[cov].append(dataList[7])    # get number of events and store it in a list
                if (len(dataList) > 10):           # if gyradius was calculated store it
                    gyradiusList[cov].append(float(dataList[9]))
                if (len(dataList) > 11):           # if perimeter was calculated store it
                    innerPerimeterList[cov].append(int(dataList[10]))
                    outerPerimeterList[cov].append(int(dataList[11]))
                islandNumberList[cov].append(int(dataList[3]))
                dataLine = previousLine
        previousLine = line

    if (sqrt):
        return islandRadiusList, timeList, gyradiusList, neList, innerPerimeterList, outerPerimeterList, readLines, monomersList, islandNumberList
    else:
        return islandSizesList, timeList, gyradiusList, neList, innerPerimeterList, outerPerimeterList, readLines, monomersList, islandNumberList

    
def getAverageGrowth(times, valueList, sqrt=False, verbose=False, tmpFileName="tmpFig.png"):
    x = np.array(times)
    averageValues = []
    for value in valueList:
        if value: #ensure that it is not null
            averageValues.append(np.mean(value))
    try:
        a, b = np.polyfit(x, averageValues, 1)
        popt = curve_fit(powerFunc, x, averageValues)
        aPower = popt[0][0]
        bPower = popt[0][1]
        if verbose:
            label = "{}x+{}".format(a, b)
            print(label)
            plt.plot(x, averageValues)
            y = a*np.array(x)+b
            plt.plot(x, y, label=label)
            y = powerFunc(x, aPower, bPower)
            label = "{}x^{}".format(aPower, bPower)
            plt.plot(x, y, label=label)
            plt.legend(loc='upper left', prop={'size':6})
            plt.savefig(tmpFileName)
            plt.close()
        if sqrt:
            valueSlope = aPower
        else:
            valueSlope = a
    except TypeError:
        valueSlope = 0

    return valueSlope


def getIslandSizeError(numberOfIslands, valueList):
    """On the one hand, all island's atoms are counted and divided by the
    number of them. On the other hand, coverage is multiplied by
    simulation sizes and divided by the number of islands. """
    numberOfIslands = numberOfIslands[1:31]
    averageValues = []
    averageNumberOfIslands = []
    index = 0
    for value in valueList:
        if value: #ensure that it is not null
            averageValues.append(np.mean(value))
            numberOfIslandsCoverage = np.array(numberOfIslands[index]).astype(np.float)
            if (len(numberOfIslandsCoverage) > 0):
                averageNumberOfIslands.append(np.mean(numberOfIslandsCoverage)/(400*400))
        index += 1
    coverages = (np.arange(0.01, 0.31, 0.01))/averageNumberOfIslands
    x = np.array(np.array(coverages))
    return abs((averageValues/coverages)-1)
    

def getFractalDimensionSingle(verbose=False):
    """returns fractal dimension of the current single flake
simulation. Takes only into account the regime where the islands are
growing or steady. It requires to have matrix.txt file in results
folder, matrix.sh script is useful to obtain this from
dataEvery1percentAndNucleation.txt output file    """

    try:
        matrix = np.loadtxt(fname="matrix.txt", delimiter="\t")
    except OSError:
        print("error opening matrix.txt file")
        return -1
    
    # Get columns 13 and 14 (only when the number of islands is 1)
    gyradius =  [matrix[i][12] for i in range(0,matrix.shape[0]) if matrix[i][3] == 1]
    islandSize = [matrix[i][13] for i in range(0,matrix.shape[0]) if matrix[i][3] == 1]
    popt = curve_fit(powerFunc, gyradius, islandSize)
    a = popt[0][0]
    b = popt[0][1]
    return b
    
def getFractalDimension(verbose=False):
    """returns fractal dimension of the current simulation. Takes only
into account the regime where the islands are growing or steady. It
requires to have matrix.txt file in results folder, matrix.sh script
is useful to obtain this from dataEvery1percentAndNucleation.txt
output file"""
    try:
        matrix=np.loadtxt(fname="matrix.txt", delimiter="\t")
    except OSError:
        print("error opening matrix.txt file")
        return -1
    sumIsland = []
    sumRg = []
    for i in range(30):
        sumIsland.append(0)
        sumRg.append(0)
        
    i=0
    while i < len(matrix):
        for j in range(30):
            sumIsland[j] += matrix[i][3]
            sumRg[j] += matrix[i][9]
            i += 1
            if (i >= len(matrix)):
                break

    coverages = []
    gyradius = []
    islands = []
    for k in range(30):
        coverages.append(k/100+0.01)
        gyradius.append(sumRg[k]/(i/30))
        islands.append(sumIsland[k]/(i/30))
        if (verbose):
            print("\t",k/100+0.01, sumRg[k]/(i/30), sumIsland[k]/(i/30))

    currentIsland = 0
    index = 1
    coveragesPlot = []
    gyradiusPlot = []
    islandsPlot = []

    while np.float(islands[index]) >= np.float(currentIsland):
        if (index+1 >= len(islands)):
            break
        #if np.float(islands[index]) == np.float(currentIsland):
        #    index = index + 1
        #    continue
        coveragesPlot.append(coverages[index])
        gyradiusPlot.append(gyradius[index])
        islandsPlot.append(islands[index])
        currentIsland = islands[index]
        index = index + 1
        if (index > 30):
            break

    secondDerivative = np.diff(np.diff(gyradiusPlot)/np.diff(np.array(coveragesPlot)/np.array(islandsPlot)))
    print(secondDerivative)
    slopeChangeIndexList = np.where(secondDerivative > 0)
    print("lenght?", slopeChangeIndexList, len(slopeChangeIndexList), len(slopeChangeIndexList) == 0)
    if len(slopeChangeIndexList[0]) == 0:
        slopeChange = index
    else:
        slopeChange = slopeChangeIndexList[0][0]
    print("slopechangeindexlist", slopeChange, len(islandsPlot))
    #for i in range(slopeChange, len(islandsPlot)):
    #    islandsPlot.pop()
    #    gyradiusPlot.pop()
    #    islandsPlot.pop()
    coveragesPlot=[coverages[i] for i in range(0,slopeChange-1)]
    gyradiusPlot=[gyradius[i] for i in range(0,slopeChange-1)]
    islandsPlot=[islands[i] for i in range(0,slopeChange-1)]

    print(coveragesPlot, gyradiusPlot, islandsPlot)
    fractalDimension = float('nan')
    if len(islandsPlot) > 2:
        popt = curve_fit(powerFunc, gyradiusPlot, np.array(coveragesPlot)/np.array(islandsPlot))
        a = popt[0][0]
        b = popt[0][1]
        fractalDimension = b
        if (verbose):
            label = "{}x^{}".format(a, b)
            plt.grid(True)
            plt.loglog(gyradiusPlot, np.array(coveragesPlot)/np.array(islandsPlot), ".")
            plt.plot(gyradiusPlot, np.array(islandsPlot)/1e4, ".")
            plt.loglog(gyradiusPlot, powerFunc(gyradiusPlot, a, b), label=label)
            plt.xlabel("Gyradius")
            plt.ylabel("Coverage/number of islands")
            plt.legend(loc='upper left', prop={'size':6})
            plt.savefig("figFractal.png")
            plt.close()
    return fractalDimension

def plot(x, y):
    maxCoverage = 31
    #gyradiusList vs averageSizes
    indexes = range(1, maxCoverage)
    xPlot = np.array(x)[indexes]
    xPlot = [np.mean(i) for i in xPlot]
    yPlot = np.array(y)
    plt.xlabel("Gyradius")
    plt.ylabel("Island size")
    plt.grid(True)
    plt.loglog(xPlot, yPlot, ".")
    # fit
    popt = curve_fit(powerFunc, xPlot, yPlot)
    aPower = popt[0][0]
    bPower = popt[0][1]
    label = "{}x^{}".format(aPower, bPower)
    yPlot = powerFunc(xPlot, aPower, bPower)
    plt.loglog(xPlot, yPlot, label=label)
    plt.legend(loc='upper left', prop={'size':12})
    plt.savefig("gyradiusVsSize.png")
    plt.close()
