import info as inf
import glob as glob
import numpy as np
import os

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

   
