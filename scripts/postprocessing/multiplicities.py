import functions as f
import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import math
import os


def getInformationFromFile():
    fileName = glob.glob("../output*")[0]
    f = open(fileName)
    hit = False
    for line in f:
        if re.search("calculationMode", line):
            calc = list(filter(None,re.split(" |,",line)))[1]
        if re.search("cartSizeX", line):
            sizX = int(list(filter(None,re.split(" |,",line)))[1])
        if re.search("cartSizeY", line):
            sizY = int(list(filter(None,re.split(" |,",line)))[1])
        if re.search("temperature", line):
            temp = float(list(filter(None,re.split(" |,",line)))[1])
        if re.search("depositionFlux", line):
            flux = float(list(filter(None,re.split(" |,",line)))[1])
        if hit:
            r_tt = float(re.split(' ', line)[0])
            return r_tt, temp, flux, calc, sizX, sizY
        if re.match("These", line):
            hit = True
            

def getInputParameters():
    r_tt, temp, flux, calcType, sizI, sizJ = getInformationFromFile()
    maxN = 3
    if re.match("Ag", calcType): # Adjust J in hexagonal lattices
        sizJ = round(sizJ / math.sin(math.radians(60)))
        maxN = 6
    return r_tt, temp, flux, sizI, sizJ, maxN

def getHexagonalEnergies():
    energies = 999999999*np.ones(49, dtype=float)
    energies[0:4] = 0.10
    energies[8:12] = 0.25
    energies[15:20] = 0.33
    energies[24:27] = 0.42
    return energies

def getBasicEnergies():
    energies = 999999999*np.ones(16, dtype=float)
    energies[0:4] = 0.2
    energies[4] = 0.45
    energies[5] = 0.36
    energies[6:8] = 0.35
    energies[9:12] = 0.435
    return energies
    

def getRatio(temperature, energies):
    kb = 8.62e-5
    p = 1e13
    return p * np.exp(-energies/kb/temperature)

def computeMavgAndOmega(fileNumber):
    r_tt, temp, flux, sizI, sizJ, maxN = getInputParameters()
    discretes = np.loadtxt(fname="possibleDiscrete"+str(fileNumber)+".txt")
    matrix = np.loadtxt(fname="data"+str(fileNumber)+".txt", delimiter="\t")
    possiblesFromList = np.loadtxt(fname="possibleFromList"+str(fileNumber)+".txt")
    possiblesFromList = possiblesFromList[:,1:] # remove coverage
    time = np.array(matrix[:,1])
    length = len(time)
    neg0 = matrix[:,16]
    hops = np.array(matrix[:,15])
    even = np.array(matrix[:,7])
    cove = np.array(matrix[:,0])
    ratios = getRatio(temp, getHexagonalEnergies())
    Mavg = np.zeros(shape=(length,49))
    for i in range(0,49):
        Mavg[:,i] = possiblesFromList[:,i]/time
    avgTotalHopRate2 = np.array(ratios.dot(np.transpose(Mavg)))
    avgTotalHopRate1 = hops/time
    # define omegas AgUc
    omega = np.zeros(shape=(length, 49))
    for i in range(0,length):
        omega[i,:] =  Mavg[i,:] * ratios / avgTotalHopRate2[i]
    np.shape(omega)
    return Mavg, omega, avgTotalHopRate1, avgTotalHopRate2


def computeMavgAndOmegaOverRuns():
    files = glob.glob("possibleDiscrete*")
    files.sort()
    matrix = np.loadtxt(fname="data0.txt", delimiter="\t")
    length = len(matrix)
    sumMavg = np.zeros(shape=(length,49))
    sumOmega = np.zeros(shape=(length,49))
    sumRate1 = np.zeros(length)
    sumRate2 = np.zeros(length)
    #iterating over runs
    for i in range(0,len(files)-1):
        tmpMavg, tmpOmega, tmpRate1, tmpRate2 = computeMavgAndOmega(i)
        sumMavg = sumMavg + tmpMavg
        sumOmega = sumOmega + tmpOmega
        sumRate1 = sumRate1 + tmpRate1
        sumRate2 = sumRate2 + tmpRate2
        
    
    runMavg = sumMavg / (len(files)-1)
    runOavg = sumOmega / (len(files)-1)
    runR1avg = sumRate1 / (len(files)-1)
    runR2avg = sumRate2 / (len(files)-1)
    
    #plt.loglog(avgTotalHopRate2)
    #plt.loglog(avgTotalHopRate1)
    #They should be the same, check if they are with many executions
        
    #plt.figure()
    # plt.plot(np.sum(runAvgOmega[:,0:4], axis=1), lw=2, label="0:4")
    # plt.plot(np.sum(runAvgOmega[:,8:12], axis=1), lw=2, label="8:12")
    # plt.plot(np.sum(runAvgOmega[:,15:20], axis=1), label="15:20")
    # plt.plot(np.sum(runAvgOmega[:,24:27], axis=1), label="24:27")
    # plt.legend(loc="best")
    # plt.show()
    # plt.savefig("omegaAvg.png")
    
    #plt.figure()
    # plt.loglog(np.sum(runMavg[:,0:4], axis=1), lw=2, label="0:4")
    # plt.loglog(np.sum(runMavg[:,8:12], axis=1), lw=2, label="8:12")
    # plt.loglog(np.sum(runMavg[:,15:20], axis=1), label="15:20")
    # plt.loglog(np.sum(runMavg[:,24:27], axis=1), label="24:27")
    # plt.show()

    return runMavg, runOavg, runR1avg, runR2avg

def defineRanges(temperatures):
    indexes = np.where((temperatures >= 70) & (temperatures <= 150))
    iSl = indexes[0][0]
    iFl = indexes[0][-1]
    indexes = np.where((temperatures >= 150) & (temperatures <= 450))
    iSm = indexes[0][0]
    iFm = indexes[0][-1]
    indexes = np.where((temperatures >= 450) & (temperatures <= 1100))
    iSh = indexes[0][0]
    iFh = indexes[0][-1]
    return list([iSl, iFl, iSm, iFm, iSh, iFh])

def fitAndPlot(x, y, rngt, axis, alfa):
    slopes = []
    axis.semilogy(x, y, "x-")
    a, b = f.fit(x, y, rngt[0], rngt[1])
    slopes.append(b)
    axis.semilogy(x[rngt[0]:rngt[1]+1], f.exp(x[rngt[0]:rngt[1]+1], a, b), label="{} low {:03.3f} ".format(alfa,b))
    a, b = f.fit(x, y, rngt[2], rngt[3])
    slopes.append(b)
    axis.semilogy(x[rngt[2]-1:rngt[3]+1], f.exp(x[rngt[2]-1:rngt[3]+1], a, b), label="{} low {:03.3f} ".format(alfa,b))
    a, b = f.fit(x, y, rngt[4], rngt[5])
    slopes.append(b)
    axis.semilogy(x[rngt[4]-1:], f.exp(x[rngt[4]-1:], a, b), label="{} low {:03.3f} ".format(alfa,b))
    axis.legend(loc="best", prop={'size':7})
    return slopes

def fitAndPlotLinear(x, y, rngt, axis, alfa, showPlot):
    slopes = []
    if showPlot:   
        axis.plot(x, y, "x-")
    a, b = f.linearFit(x, y, rngt[0], rngt[1])
    slopes.append(b)
    if showPlot:
        axis.semilogy(x[rngt[0]:rngt[1]+1], np.exp(f.linear(x[rngt[0]:rngt[1]+1], a, b)), label="{} low {:03.3f} ".format(alfa,b))
    a, b = f.linearFit(x, y, rngt[2], rngt[3])
    slopes.append(b)
    if showPlot:
        axis.semilogy(x[rngt[2]-1:rngt[3]+1], np.exp(f.linear(x[rngt[2]-1:rngt[3]+1], a, b)), label="{} med {:03.3f}".format(alfa,b))
    a, b = f.linearFit(x, y, rngt[4], rngt[5])
    slopes.append(b)
    if showPlot:
        axis.semilogy(x[rngt[4]-1:], np.exp(f.linear(x[rngt[4]-1:], a, b)), label="{} high {:03.3f}".format(alfa,b))
        axis.legend(loc="best", prop={'size':7})
    return slopes

temperatures = np.array(list(range(50,100,5))+list(range(100,150,10))+list(range(150,400,50))+list(range(450,1100,50)))
kb = 8.6173324e-5
#tempMavg = {}
#tempOavg = {}
tempMavg = []
tempOavg = []
tempR1avg = []
tempR2avg = []

workingPath = os.getcwd()
for t in temperatures:
    print(t)
    os.chdir(str(t)+"/results")
    #tempMavg[t], tempOavg[t] = computeMavgAndOmegaOverRuns()
    tmp1, tmp2, tmp3, tmp4 = computeMavgAndOmegaOverRuns()
    tempMavg.append(tmp1)
    tempOavg.append(tmp2)
    tempR1avg.append(tmp3)
    tempR2avg.append(tmp4)
    os.chdir(workingPath)

tempMavg = np.array(tempMavg)
tempOavg = np.array(tempOavg)
tempR1avg = np.array(tempR1avg)
tempR2avg = np.array(tempR2avg)

minTemperatureIndex = 4
tempMavg = tempMavg[minTemperatureIndex:]
tempOavg = tempOavg[minTemperatureIndex:]
tempR1avg = tempR1avg[minTemperatureIndex:]
tempR2avg = tempR2avg[minTemperatureIndex:]

temperatures = temperatures[minTemperatureIndex:]
# define ranges
rngt = defineRanges(temperatures)


#one coverage
ind = [0,4,8,12,15,20,24,27]
# for i in range(0,4):
#     y = np.sum(tempMavg[:,cov,ind[2*i]:ind[2*i+1]],   axis=1)
#     fitAndPlot(x, y, rngt, axarr[1])
#for cov in [-49, -39, -29, -19, -9, -1]:
tempOmegaCov = []
tempEaCov = []
tempEaMCov = []
showPlot = True
#for cov in [-49, -39, -29, -19, -9, -1]:
for cov in range(-49,0):
    x = 1/kb/temperatures+np.log(5e4**1.5)
    y = tempR1avg
    print(cov)
    if showPlot:
        fig, axarr = plt.subplots(3, sharex=True)
        plt.xlim(20,200)
    else:
        axarr = np.zeros(3)
    # N_h
    tempEaCov.append(fitAndPlotLinear(x, y[:,cov], rngt, axarr[0], -1, showPlot))
    tempOmega = np.zeros((4,3))
    tempEaM = []
    for i in range(0,4): # alfa
        y = np.sum(tempMavg[:,cov,ind[2*i]:ind[2*i+1]],   axis=1)
        tempEaM.append(fitAndPlotLinear(x, y, rngt, axarr[1], i, showPlot))
        if showPlot:
            axarr[2].semilogy(x, np.sum(tempOavg[:,cov,ind[2*i]:ind[2*i+1]],   axis=1), ".-")
            axarr[2].set_ylim(-0.05,1.05)
            axarr[2].set_ylim(1e-3,2)
        for j in range(0,3): # temperature ranges
            tempOmega[i][j] = np.exp(np.mean(np.log(np.sum(tempOavg[rngt[2*j]:rngt[2*j+1],cov,ind[2*i]:ind[2*i+1]],   axis=1))))
    tempOmegaCov.append(tempOmega)
    tempEaMCov.append(tempEaM)
    plt.savefig("plot"+str(cov)+".png")
    

    #plt.show()
print(len(tempOmegaCov))
tempOmegaCov = np.array(tempOmegaCov) # [coverage, type (alfa), temperature range]
tempEaCov = -np.array(tempEaCov) # [coverage, temperature range]
tempEaMCov = np.array(tempEaMCov) # [coverage, type (alfa), temperature range]
tempEaRCov = np.zeros(np.shape(tempEaMCov))
energies = [0.1, 0.25, 0.33, 0.42]
for alfa in range(0,4):
    tempEaRCov[:,alfa,:] = energies[alfa]
cov = [1, 10, 20, 30, 40, 49]
cov = list(range(0,49))

plt.close()
plt.figure()
tempEaCov2 = np.sum(tempOmegaCov*(tempEaRCov-tempEaMCov), axis=1)
for i in range(0,3): # different temperature ranges (low, medium, high)
    plt.plot(cov, tempEaCov[:,i], label="{}".format(i))
    plt.plot(cov, tempEaCov2[:,i], "x:", label="v2 {}".format(i))
    plt.plot(cov, 1-tempEaCov2[:,i]/tempEaCov[:,i], "o", label="relative error")
plt.legend(loc="best", prop={'size':8})
plt.savefig("multiplicities.png")



    #plt.clf()

#plt.ylim(1e-4,1e0)
#plt.xlim(20,200)

#"all" coverages
# for i in [-49, -39, -29, -19, -9, -1]:
#     plt.figure()
#     plt.title(str(50+i))
#     plt.semilogy(1/kb/temperatures, np.sum(tempMavg[:,i,0:4],axis=1))
#     plt.semilogy(1/kb/temperatures, np.sum(tempMavg[:,i,8:12],axis=1))
#     plt.semilogy(1/kb/temperatures, np.sum(tempMavg[:,i,15:20],axis=1))
#     plt.semilogy(1/kb/temperatures, np.sum(tempMavg[:,i,24:27],axis=1))
#     plt.show()


plt.loglog(Mavg)

plt.clf()
# Multiplicities M
style = ":"
plt.loglog(cove, np.sum(Mavg[:,0:4], axis=1),  ls=style, label="0:4")
plt.loglog(cove, Mavg[:,4],                    ls=style, label="4")
plt.loglog(cove, Mavg[:,5],                    ls=style, label=5)
plt.loglog(cove, np.sum(Mavg[:,6:8], axis=1),  ls=style, label="6:8")
plt.loglog(cove, np.sum(Mavg[:,9:12], axis=1), ls=style, label="9:12")
plt.legend(loc="best")

#plt.clf()
# plotting omegas basic
plt.loglog(time[:], np.sum(Mavg[:,0:4], axis=1)*ratios[0]/hopsAvg, label="0:4")
plt.loglog(time[:], Mavg[:,4]*ratios[4]/hopsAvg, label="4")
plt.loglog(time[:], Mavg[:,5]*ratios[5]/hopsAvg, label=5)
plt.loglog(time[:], np.sum(Mavg[:,6:8], axis=1)*ratios[6]/hopsAvg, label="6:8")
plt.loglog(time[:], np.sum(Mavg[:,9:12], axis=1)*ratios[9]/hopsAvg, label="9:12")
plt.legend(loc="best")


plt.loglog(time, cove, ls="--")


for i in range(0,16):
    plt.loglog(cove, discretes[:,i+1])
plt.show()

dtim.dot(Mij)

for i in range(0,50):
    plt.loglog(possiblesFromList[:,i]/time)
