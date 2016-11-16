import re
import numpy as np
import matplotlib.pyplot as plt
import results
import scipy.special
from scipy.optimize import curve_fit

def powerFunc(x, a, b):
    """ a*x^b function """
    return a*x**b

def errFunc(x, a, b, c, sigma):
    return a*scipy.special.erf(sigma*(x-c))+b

def ourErrFunc(x, a, b, c, sigma):
    return a*(np.exp(sigma*(x-c))/(1+np.exp(sigma*(x-c))))+b

def getAllValues(f, maxCoverage, getSlopes=True):
    """ reads all the values for the corresponding coverage """
    
    #get something like 0.05000 expression to be grepped 
    regExpression = '(0\...00)|(^0\...\s)' # corresponds to 0.??00 expression or 0.??[:spaces:] expression
    currentData = results.Data(maxCoverage) # init results class
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
                currentData.appendIslandSize(cov, j)
                j = next(iterList)
            cov = 0
        if re.match(regExpression, line):      # just hit a coverage
            cov = int(line[2]+line[3])         # get coverage
            if (cov >= maxCoverage):           
                cov = 0                        # coverage is bigger than wanted, skip
            else:
                currentData.appendData(cov, line)
                dataLine = previousLine
        previousLine = line

    return currentData

    
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
