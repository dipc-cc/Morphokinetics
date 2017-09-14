import info as inf
import glob as glob
import numpy as np

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
    filesNumber = len(files)
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

