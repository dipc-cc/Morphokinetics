#!/usr/bin/env python3
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import info as inf
import glob
import os
import numpy as np

def getOneCoverage(fileNumber):
    name = "dataCatalysis"
    data = np.loadtxt(fname=name+"{:03d}".format(fileNumber)+".txt")
    coverages = np.zeros(4)
    coverages[0:4] = data[-1,1:5] # get last coverages
    return coverages

def getAvgCoverages():
    p = inf.getInputParameters()
    files = glob.glob("dataCatalysis*")
    filesNumber = len(files)
    coverages = np.zeros(4)
    for i in range(0,filesNumber):
        try:
            coverages += getOneCoverage(i)
        except FileNotFoundError:
            filesNumber -= 1 
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
    markers=["o", "s","D","^","d","h","p"]
    cm = plt.get_cmap('tab20')
    fig, ax = plt.subplots(1, figsize=(5,4))
    fig.subplots_adjust(top=0.95,left=0.15, right=0.95)
    labels = [r"$CO_B$",r"$CO_C$",r"$O_B$",r"$O_C$",r"$V_B$",r"$V_C$"]
    lines = ["-",":"]
    for i in range(0,6):
        ax.plot(x, coverages[:,i], marker=markers[i],label=labels[i],color=cm(i), linestyle=lines[i%2])
    ax.legend(loc="best", prop={'size':6})
    ax.set_yscale("log")
    ax.set_ylabel(r"$\theta$")
    ax.set_xlabel(r"$1/k_BT$")
    ax.set_ylim(1e-4,2)
    fig.savefig("avgCoverages.pdf")
    
temperatures = inf.getTemperatures("float")
maxRanges = len(temperatures)
kb = 8.6173324e-5
p = inf.getInputParameters(glob.glob("*/output*")[0])
workingPath = os.getcwd()
coverages = getCoverages(p,temperatures,workingPath)
os.chdir(workingPath)
plotCoverages(1/kb/temperatures,coverages)
