import info as inf
import glob
import os
import numpy as np
import matplotlib.pyplot as plt

def getOneCoverage(fileNumber):
    name = "dataCatalysis"
    data = np.loadtxt(fname=name+"{:03d}".format(fileNumber)+".txt")
    coverages = np.zeros(4)
    coverages[0:4] = data[-1,1:5] # get last coverages
    return coverages

def getAvgCoverages():
    p = inf.getInputParameters()
    files = glob.glob("dataCatalysis*")
    filesNumber = len(files)-1
    coverages = np.zeros(4)
    for i in range(0,filesNumber):
        coverages += getOneCoverage(i)
    coverages = coverages / filesNumber
    coveragesWithEmpty = np.zeros(6)
    coveragesWithEmpty[0:4] = coverages
    coveragesWithEmpty[4] = 1-(coverages[0] + coverages[2])
    coveragesWithEmpty[5] = 1-(coverages[1] + coverages[3])
    return coveragesWithEmpty

def getCoverages(p,temperatures,workingPath):
    coverages = np.zeros(shape=(len(temperatures),6))
    for i,t in enumerate(temperatures):
        print(t)
        os.chdir(workingPath)
        try:
            os.chdir(str(t)+"/results")
            runFolder = glob.glob("*/");
            runFolder.sort()
            os.chdir(runFolder[-1])
        except FileNotFoundError:
            continue
        coverages[i,:] = getAvgCoverages()
    return coverages

def plotCoverages(x,coverages):
    fig, ax = plt.subplots(1, figsize=(5,4))
    fig.subplots_adjust(top=0.95,left=0.15, right=0.95)
    labels = [r"$CO^B$",r"$CO^C$",r"$O^B$",r"$O^C$",r"$V^B$",r"$V^C$"]
    for i in range(0,6):
        ax.plot(x, coverages[:,i], "o-",label=labels[i])
    ax.legend(loc="best", prop={'size':6})
    ax.set_yscale("log")
    fig.savefig("avgCoverages.svg")
    
temperatures = inf.getTemperatures("float")
maxRanges = len(temperatures)
kb = 8.6173324e-5
p = inf.getInputParameters(glob.glob("*/output*")[0])
workingPath = os.getcwd()
coverages = getCoverages(p,temperatures,workingPath)
os.chdir(workingPath)
plotCoverages(1/kb/temperatures,coverages)
