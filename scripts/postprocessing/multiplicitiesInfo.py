import info as inf
import glob as glob
import numpy as np
import os
import multiplicitiesPlot as mp
import matplotlib.pyplot as plt
import energies as e

def computeMavgAndOmega(fileNumber, p, total):
    if total:
        name = "dataAeAll"
        ratios = p.getRatiosTotal()
    else:
        name = "dataAePossibleFromList"
        ratios = p.getRatios()
    possiblesFromList = np.loadtxt(fname=name+"{:03d}".format(fileNumber)+".txt")
    time = np.array(possiblesFromList[:,0])
    possiblesFromList = possiblesFromList[:,1:] # remove time
    length = len(time)
    Mavg = np.zeros(shape=(length,p.maxA))
    for i in range(0,p.maxA): # iterate alfa
        Mavg[:,i] = possiblesFromList[:,i]/time

    avgTotalRate = np.array(ratios.dot(np.transpose(Mavg)))
    # define omegas 
    omega = np.zeros(shape=(length,p.maxA)) # [co2amount, alfa]
    for i in range(0,length):
        omega[i,:] =  Mavg[i,:] * ratios / avgTotalRate[i]
    return Mavg, omega, avgTotalRate


def computeMavgAndOmegaOverRuns(total=False):
    p = inf.getInputParameters()
    if total:
        p.maxA = 20
    files = glob.glob("dataAePossibleFromList*")
    files.sort()
    filesNumber = len(files)-1
    matrix = np.loadtxt(fname=files[0])
    length = len(matrix)
    sumMavg = np.zeros(shape=(length,p.maxA))  # [time, alfa]
    sumOmega = np.zeros(shape=(length,p.maxA)) # [time, alfa]
    sumRate = np.zeros(length)
    #iterating over runs
    for i in range(0,filesNumber):
        tmpMavg, tmpOmega, tmpRate = computeMavgAndOmega(i, p, total)
        sumMavg = sumMavg + tmpMavg
        sumOmega = sumOmega + tmpOmega
        sumRate = sumRate + tmpRate
    
    runMavg = sumMavg / filesNumber
    runOavg = sumOmega / filesNumber
    runRavg = sumRate / filesNumber

    return runMavg, runOavg, runRavg


def getMavgAndOmega(temperatures,workingPath,total=False):
    tempMavg = []
    tempOavg = []
    tempRavg = []
    for t in temperatures:
        print(t)
        os.chdir(workingPath)
        try:
            os.chdir(str(t)+"/results")
            runFolder = glob.glob("*/");
            runFolder.sort()
            os.chdir(runFolder[-1])
        except FileNotFoundError:
            continue
        tmp1, tmp2, tmp3 = computeMavgAndOmegaOverRuns(total)
        tempMavg.append(tmp1)
        tempOavg.append(tmp2)
        tempRavg.append(tmp3)
        
    tempMavg = np.array(tempMavg)
    tempOavg = np.array(tempOavg)
    tempRavg = np.array(tempRavg)
    return tempMavg, tempOavg, tempRavg

def getEaMandEaR(p,temperatures,labelAlfa,sp,tempMavg,tempOavg,tempRavg):
    maxCo2 = int(p.nCo2/10)
    maxAlfa = p.maxA
    rngt = e.defineRangesCatalysis(p.calc, p.rLib, temperatures) #list([0, 3])
    kb = 8.6173324e-5
    tempOmegaCo2 = []
    tempEaMCo2 = []
    tempEaCo2 = []
    tempEafCo2 = []
    maxRanges = len(temperatures)
    for co2 in range(0,maxCo2): # created co2: 10,20,30...1000
        showPlot = sp and float(co2+(maxCo2/10)+1) % float(maxCo2/10) == 0
        if float(co2+(maxCo2/10)+1) % float(maxCo2/10) == 0:
            print(co2)
        x = 1/kb/temperatures
        y = tempRavg
        if showPlot:
            fig, axarr = plt.subplots(3, sharex=True, figsize=(5,6))
            fig.subplots_adjust(right=0.7, hspace=0.1)
        else:
            axarr = np.zeros(3)
        # N_h
        tempEaCo2.append(mp.localAvgAndPlotLinear(x, y[:,co2], axarr[0], -1, showPlot, co2))
        tempOmega = np.zeros((maxAlfa,maxRanges))
        tempEaM = []
        
        for i in range(0,maxAlfa): # alfa
            y = np.sum(tempMavg[:,co2,i:i+1], axis=1)
            tempEaM.append(mp.localAvgAndPlotLinear(x, y, axarr[1], i, showPlot, co2))
            if showPlot:
                y = np.sum(tempOavg[:,co2,i:i+1], axis=1)
                mp.plotOmegas(x, y, axarr[-1], i, tempOmega[i], rngt, labelAlfa)
            tempOmega[i] = list(tempOavg[:, co2, i:i+1])
        
        tempOmegaCo2.append(tempOmega)
        tempEaMCo2.append(tempEaM)
        if showPlot:
            fig.savefig("plot"+str(co2)+".svg", bbox_inches='tight') 

    tempOmegaCo2 = np.array(tempOmegaCo2) # [co2, type (alfa), temperature range]
    tempEaCo2 = -np.array(tempEaCo2) # [co2, temperature range]
    tempEaMCo2 = np.array(tempEaMCo2) # [co2, type (alfa), temperature range]
    tempEaRCo2 = np.zeros(np.shape(tempEaMCo2))
    return tempOmegaCo2, tempEaCo2, tempEaMCo2, tempEaRCo2
