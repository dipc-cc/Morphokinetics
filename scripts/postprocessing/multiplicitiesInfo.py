import info as inf
import glob as glob
import numpy as np
import os
import multiplicitiesPlot as mp
import matplotlib.pyplot as plt
import energies as e

def computeMavgAndOmega(fileNumber, p):
    name = "dataAeAll"
    ratios = p.getRatiosTotal()[p.minA:p.maxA]
    possiblesFromList = np.loadtxt(fname=name+"{:03d}".format(fileNumber)+".txt")
    time = np.array(possiblesFromList[:,0])
    possiblesFromList = possiblesFromList[:,1:] # remove time
    length = len(time)
    Mavg = np.zeros(shape=(length,p.maxA-p.minA))
    for i,a in enumerate(range(p.minA,p.maxA)): # iterate alfa
        Mavg[:,i] = possiblesFromList[:,a]/time

    totalRate = np.array(ratios.dot(np.transpose(Mavg)))
    # define omegas 
    omega = np.zeros(shape=(length,p.maxA-p.minA)) # [co2amount, alfa]
    for i in range(0,length):
        omega[i,:] =  Mavg[i,:] * ratios / totalRate[i]
    return Mavg, omega, totalRate


def computeMavgAndOmegaOverRuns(pAlfa):
    p = inf.getInputParameters()
    p.minA = pAlfa.minA
    p.maxA = pAlfa.maxA
    files = glob.glob("dataAeAll*")
    files.sort()
    filesNumber = len(files)
    matrix = np.loadtxt(fname=files[0])
    maxCO2 = len(matrix)
    sumMavg = np.zeros(shape=(maxCO2,p.maxA-p.minA))  # [time|CO2, alfa]
    sumOmega = np.zeros(shape=(maxCO2,p.maxA-p.minA)) # [time|CO2, alfa]
    sumRate = np.zeros(maxCO2)
    #iterating over runs
    for i in range(0,filesNumber):
        tmpMavg, tmpOmega, tmpRate = computeMavgAndOmega(i, p)
        sumMavg = sumMavg + tmpMavg
        sumOmega = sumOmega + tmpOmega
        sumRate = sumRate + tmpRate
    
    runMavg = sumMavg / filesNumber
    runOavg = sumOmega / filesNumber
    totalRate = sumRate / filesNumber

    totalRateEvents, rates = getTotalRate()
    return runMavg, runOavg, totalRate, totalRateEvents


def getMavgAndOmega(p,temperatures,workingPath):
    maxTemp = len(temperatures)
    maxCo2 = int(p.nCo2/10)
    tempMavg = np.zeros(shape=(maxCo2,maxTemp,p.maxA-p.minA))
    tempOavg = np.zeros(shape=(maxCo2,maxTemp,p.maxA-p.minA))
    totalRate = np.zeros(shape=(maxCo2,maxTemp))
    totalRateEvents = np.zeros(shape=(maxCo2,maxTemp))
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
        tmp1, tmp2, tmp3, tmp4 = computeMavgAndOmegaOverRuns(p)
        tempMavg[:,i,:] = tmp1
        tempOavg[:,i,:] = tmp2
        totalRate[:,i] = tmp3
        totalRateEvents[:,i] = tmp4
        
    return tempMavg, tempOavg, totalRate, totalRateEvents

def getMultiplicityEa(p,temperatures,labelAlfa,sp,tempMavg,tempOavg,totalRate):
    maxRanges = len(temperatures)
    maxCo2 = int(p.nCo2/10)
    rngt = e.defineRangesCatalysis(p.calc, p.rLib, temperatures) #list([0, 3])
    kb = 8.6173324e-5
                  # [co2, type (alfa), temperature range]
    multiplicityEa   = np.zeros(shape=(maxCo2,maxRanges,p.maxA-p.minA))
    activationEnergy    = np.zeros(shape=(maxCo2,maxRanges))
    for co2 in range(0,maxCo2): # created co2: 10,20,30...1000
        showPlot = sp and float(co2+(maxCo2/10)+1) % float(maxCo2/10) == 0
        if float(co2+(maxCo2/10)+1) % float(maxCo2/10) == 0:
            print(co2)
        x = 1/kb/temperatures
        y = totalRate
        if showPlot:
            fig, axarr = plt.subplots(3, sharex=True, figsize=(5,6))
            fig.subplots_adjust(right=0.7, hspace=0.1)
        else:
            axarr = np.zeros(3)
        # N_h

        activationEnergy[co2,:] = mp.localAvgAndPlotLinear(x, y[co2,:], axarr[0], -1, showPlot, co2)
        
        for i,a in enumerate(range(p.minA,p.maxA)): # alfa
            y = np.sum(tempMavg[co2,:,i:i+1], axis=1)
            multiplicityEa[co2,:,i] = mp.localAvgAndPlotLinear(x, y, axarr[1], i, showPlot, co2)
            if showPlot:
                y = np.sum(tempOavg[co2,:,i:i+1], axis=1)
                mp.plotOmegas(x, y, axarr[-1], i, tempOavg[co2,i], rngt, labelAlfa)
        if showPlot:
            fig.savefig("plot"+str(co2)+".svg", bbox_inches='tight') 

    activationEnergy = -activationEnergy
    return activationEnergy, multiplicityEa

def getTofSensibility(p,omega,ratioEa,multiplicityEa):
    sensibilityCo2 = np.zeros(np.shape(omega))
    sumBeta = 0
    sumOmegaBeta = 0
    for beta in range(0,4):
        sumBeta += omega[:,:,beta]*(ratioEa[:,:,beta]-multiplicityEa[:,:,beta])
        sumOmegaBeta += omega[:,:,beta]
    for a in range(p.minA,p.maxA):
        sensibilityCo2[:,:,a] = omega[:,:,a]/sumOmegaBeta*(sumBeta/ratioEa[:,:,a])

    return sensibilityCo2

def getTotalSensibility(p,omega,ratioEa,multiplicityEa):
    sensibilityCo2 = np.zeros(np.shape(omega))
    for i in range(p.minA,p.maxA):
        sensibilityCo2[:,:,i] = omega[:,:,i]*(1-multiplicityEa[:,:,i]/ratioEa[:,:,i])
    return sensibilityCo2


def getTotalRate():
    files = glob.glob("dataCatalysis0*.txt")
    totalRate = 0
    rates = np.zeros(4)
    for t in files:
        data = np.loadtxt(t)
        events = 0
        eventsA = np.zeros(4)
        for i in range(5,9):
            events += data[-1,i] - data[0,i]
            eventsA[i-5] += data[-1,i] - data[0,i]
        totalRate += events / data[-1,0] # last time
        rates += eventsA / data[-1,0]
    totalRate = totalRate / len(files)
    rates = rates / len(files)
    return totalRate, rates
