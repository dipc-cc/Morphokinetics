import info as inf
import glob as glob
import numpy as np

def computeMavgAndOmega(fileNumber, p):
    possiblesFromList = np.loadtxt(fname="dataAePossibleFromList"+"{:03d}".format(fileNumber)+".txt")
    time = np.array(possiblesFromList[:,0])
    possiblesFromList = possiblesFromList[:,1:] # remove time
    length = len(time)
    Mavg = np.zeros(shape=(length,p.maxA))
    for i in range(0,p.maxA): # iterate alfa
        Mavg[:,i] = possiblesFromList[:,i]/time
    ratios = p.getRatios()

    avgTotalRate2 = np.array(ratios.dot(np.transpose(Mavg)))
    # define omegas 
    omega = np.zeros(shape=(length,p.maxA)) # [co2amount, alfa]
    for i in range(0,length):
        omega[i,:] =  Mavg[i,:] * ratios / avgTotalRate2[i]
    np.shape(omega)
    avgTotalHopRate1 = avgTotalHopRate3 = avgTotalRate2
    return Mavg, omega, avgTotalHopRate1, avgTotalRate2, avgTotalHopRate3


def computeMavgAndOmegaOverRuns():
    p = inf.getInputParameters()
    files = glob.glob("dataAePossibleDiscrete*")
    files.sort()
    filesNumber = len(files)
    matrix = np.loadtxt(fname=files[0])
    length = len(matrix)
    sumMavg = np.zeros(shape=(length,p.maxA))  # [time, alfa]
    sumOmega = np.zeros(shape=(length,p.maxA)) # [time, alfa]
    sumRate1 = np.zeros(length)
    sumRate2 = np.zeros(length)
    sumRate3 = np.zeros(length)
    #iterating over runs
    for i in range(0,filesNumber):
        tmpMavg, tmpOmega, tmpRate1, tmpRate2, tmpRate3 = computeMavgAndOmega(i, p)
        sumMavg = sumMavg + tmpMavg
        sumOmega = sumOmega + tmpOmega
        sumRate1 = sumRate1 + tmpRate1
        sumRate2 = sumRate2 + tmpRate2
        sumRate3 = sumRate3 + tmpRate3
    
    runMavg = sumMavg / filesNumber
    runOavg = sumOmega / filesNumber
    runR1avg = sumRate1 / filesNumber
    runR2avg = sumRate2 / filesNumber
    runR3avg = sumRate3 / filesNumber

    return runMavg, runOavg, runR1avg, runR2avg, runR3avg

