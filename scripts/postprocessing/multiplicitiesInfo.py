import matplotlib
matplotlib.use("Agg")
import info as inf
import glob as glob
import numpy as np
import os
import multiplicitiesPlot as mp
import matplotlib.pyplot as plt
import energies as e

def printRatios(ratios):
    rows = 12
    columns = 16
    for i in range(0,rows):
        for j in range(0,columns):
            if ratios[i*columns + j] < 1e-120:
                print("          ", end="")
            else:
                print("%1.3E"% (ratios[i*columns + j]), end=" ")
        print()

def computeMavgAndOmega(fileNumber, p):
    if p.calc == "catalysis":
        name = "dataAeAll"
    else:
        name = "dataAePossibleFromList"
    pTemp = inf.getInputParameters()
    ratios = pTemp.getRatiosTotal()[p.minA:p.maxA]
    possiblesFromList = np.loadtxt(fname=name+"{:03d}".format(fileNumber)+".txt")
    time = np.array(possiblesFromList[:p.mMsr,0])
    possiblesFromList = possiblesFromList[:,1:] # remove time
    if p.maxA == 7: # for farkas TOF
        possiblesFromList[:,4] = possiblesFromList[:,22]
        possiblesFromList[:,5] = possiblesFromList[:,23]
        possiblesFromList[:,6] = possiblesFromList[:,24]
        ratios = p.getRatiosTotal()
        ratios[4] = ratios[22]
        ratios[5] = ratios[23]
        ratios[6] = ratios[24]
        ratios = ratios[0:7]
    Mavg = np.zeros(shape=(p.mMsr,p.maxA-p.minA))
    for i,a in enumerate(range(p.minA,p.maxA)): # iterate alfa
        Mavg[:,i] = possiblesFromList[:p.mMsr,a]/time/p.sizI/p.sizJ

    totalRate = np.array(ratios.dot(np.transpose(Mavg)))
    # define omegas 
    omega = np.zeros(shape=(p.mMsr,p.maxA-p.minA)) # [co2amount, alfa]
    for i in range(0,p.mMsr):
        omega[i,:] =  Mavg[i,:] * ratios / totalRate[i]
    return Mavg, omega, totalRate, ratios


def computeMavgAndOmegaOverRuns(p):
    if p.calc == "catalysis":
        files = glob.glob("dataAeAll*")
    else:
        files = glob.glob("dataAePossibleFromList*")
    files.sort()
    filesNumber = len(files)
    sumMavg = np.zeros(shape=(p.mMsr,p.maxA-p.minA))  # [time|CO2, alfa]
    sumOmega = np.zeros(shape=(p.mMsr,p.maxA-p.minA)) # [time|CO2, alfa]
    sumRate = np.zeros(p.mMsr)
    #iterating over runs
    for i in range(0,filesNumber):
        try:
            tmpMavg, tmpOmega, tmpRate, ratios = computeMavgAndOmega(i, p)
            sumMavg = sumMavg + tmpMavg
            sumOmega = sumOmega + tmpOmega
            sumRate = sumRate + tmpRate
        except (FileNotFoundError,ValueError): # there is no file, or the file has less lines that previous lines
            filesNumber -= 1
    
    runMavg = sumMavg / filesNumber
    runOavg = sumOmega / filesNumber
    totalRate = sumRate / filesNumber

    totalRateEvents, rates = getTotalRate(p)
    return runMavg, runOavg, totalRate, totalRateEvents, rates, ratios


def getMavgAndOmega(p,temperatures,workingPath):
    maxTemp = len(temperatures)
    p.mMsr = max(int(p.nCo2/10),p.mCov)
    tempMavg = np.zeros(shape=(p.mMsr,maxTemp,p.maxA-p.minA))
    tempOavg = np.zeros(shape=(p.mMsr,maxTemp,p.maxA-p.minA))
    totalRate = np.zeros(shape=(p.mMsr,maxTemp))
    ratios = np.zeros(shape=(maxTemp,p.maxA-p.minA)) # Used ratios for simulation
    totalRateEvents = np.zeros(shape=(p.mMsr,maxTemp))
    rates = np.zeros(shape=(p.mMsr,maxTemp,4)) # adsorption, desorption, reaction and diffusion rates
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
        tempMavg[:,i,:], tempOavg[:,i,:], totalRate[:,i], totalRateEvents[:,i], rates[:,i,:], ratios[i,:] = computeMavgAndOmegaOverRuns(p)
        
    return tempMavg, tempOavg, totalRate, totalRateEvents, rates, ratios

def getMultiplicityEa(p,temperatures,labelAlfa,sp,tempMavg,omega,totalRate,ext="",one=False):
    maxRanges = len(temperatures)
    kb = 8.6173324e-5
                  # [co2, type (alfa), temperature range]
    multiplicityEa   = np.zeros(shape=(p.mMsr,maxRanges,p.maxA-p.minA))
    activationEnergy    = np.zeros(shape=(p.mMsr,maxRanges))
    total = ext == "T"
    for co2 in range(0,p.mMsr): # created co2: 10,20,30...1000
        print(co2,"/",p.mMsr,sp)
        showPlot = sp #and float(co2+(p.mMsr/10)+1) % float(p.mMsr/10) == 0
        if float(co2+(p.mMsr/10)+1) % float(p.mMsr/10) == 0:
            print(co2)
        x = 1/kb/temperatures
        y = totalRate
        if showPlot:
            if one:
                axarr = list()
                fig, ax = plt.subplots(1, sharex=True, figsize=(5,4))
                axarr.append(ax)
                o = 0
                ymin = 1e-8
            else:
                fig, axarr = plt.subplots(2, sharex=True, figsize=(4,7))
                o = 1
                ymin = 1e-4
            #axarr[0].annotate("(a)", xy=(-0.13, 0.93), xycoords="axes fraction", size=8)
            #axarr[1].annotate("(b)", xy=(-0.13, 0.93), xycoords="axes fraction", size=8)
            fig.subplots_adjust(top=0.95,left=0.15, right=0.95)
        else:
            axarr = np.zeros(3)
        # N_h

        activationEnergy[co2,:] = mp.localAvgAndPlotLinear(x, y[co2,:], axarr[0], -1, False, co2, total=total, verbose=True)

        first = True
        omegaSumTof = np.zeros(shape=(len(temperatures)))
        for i,a in enumerate(range(p.minA,p.maxA)): # alfa
            spl = False
            if showPlot:
                y = np.sum(omega[co2,:,i:i+1], axis=1)
                if i < 4:
                    omegaSumTof += y
                if p.maxA == 28 and (i == 22 or i == 23 or i == 24):
                    omegaSumTof += y
                if any(abs(y) >= ymin):
                    if i == 5:
                        y *= 2 # adsorption of O has to be double. Rate is from atom and we need to compute molecule.
                        mp.plotOmegas(x, y, axarr[o], i, omega[co2,:,i], ext=="T", labelAlfa,ymin)
                    else:
                        mp.plotOmegas(x, y, axarr[o], i, omega[co2,:,i], ext=="T", labelAlfa,ymin)
                    spl = True
                else:
                    spl = False
            y = np.sum(tempMavg[co2,:,i:i+1], axis=1)
            multiplicityEa[co2,:,i] = mp.localAvgAndPlotLinear(x, y, axarr[0], i, spl and not one, co2, first, total)
            if spl and first:
                first = False
        if showPlot:
            if one:
                axarr[0].plot(x,omegaSumTof,ls="-", label=r"TOF/R", color="C2")
                axarr[0].plot(x,2*omegaSumTof, ls=":", label=r"2 $\times$ TOF/R", color="C2")
                axarr[0].plot(x,0.05*omegaSumTof, ls="--", label=r" 0.05$ \times $ TOF/R", color="C2")
                axarr[0].legend(prop={'size': 5}, loc="best", scatterpoints=1) 
            fig.savefig(p.rLib+"Plot"+str(one)+str(co2)+ext+".svg")
            plt.close(fig)

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


# Computes total rates from number of events
def getTotalRate(p):
    if p.calc == "catalysis":
        return getTotalRateCatalysis()
    else:
        return getTotalRateConcerted(p)

def getTotalRateCatalysis():
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

def getTotalRateConcerted(p):
    files = glob.glob("dataAe0*.txt")
    files.sort()
    #files = files[:-1]
    totalRate = 0
    for t in files:
        data = np.loadtxt(t,comments=['#', '[', 'h'])
        events = data[:p.mMsr,7] # column number 8 is "number of events"
        try:
            totalRate += events / data[:p.mMsr,1] # last time, column 2
        except ValueError:
            continue
    totalRate = totalRate / len(files) / p.sizI / p.sizJ
    return totalRate, -1
