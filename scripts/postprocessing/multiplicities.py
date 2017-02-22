import functions as f
import info
import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import math
import os
import sys


def computeMavgAndOmega(fileNumber):
    r_tt, temp, flux, sizI, sizJ, maxN, maxC, maxA = info.getInputParameters()
    matrix = np.loadtxt(fname="data"+str(fileNumber)+".txt", delimiter="\t")
    possiblesFromList = np.loadtxt(fname="possibleFromList"+str(fileNumber)+".txt")
    possiblesFromList = possiblesFromList[:,1:] # remove coverage
    time = np.array(matrix[:,1])
    length = len(time)
    hops = np.array(matrix[:,15])
    ratios = info.getRatio(temp, info.getHexagonalEnergies())
    Mavg = np.zeros(shape=(length,maxA))
    for i in range(0,maxA): # iterate alfa
        Mavg[:,i] = possiblesFromList[:,i]/time
    avgTotalHopRate2 = np.array(ratios.dot(np.transpose(Mavg)))
    avgTotalHopRate1 = hops/time
    # define omegas AgUc
    omega = np.zeros(shape=(length,maxA)) # [coverage, alfa]
    for i in range(0,length):
        omega[i,:] =  Mavg[i,:] * ratios / avgTotalHopRate2[i]
    np.shape(omega)
    return Mavg, omega, avgTotalHopRate1, avgTotalHopRate2


def computeMavgAndOmegaOverRuns():
    r_tt, temp, flux, sizI, sizJ, maxN, maxC, maxA = info.getInputParameters()
    files = glob.glob("possibleDiscrete*")
    files.sort()
    matrix = np.loadtxt(fname="data0.txt", delimiter="\t")
    length = len(matrix)
    sumMavg = np.zeros(shape=(length,maxA))  # [coverage, alfa]
    sumOmega = np.zeros(shape=(length,maxA)) # [coverage, alfa]
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
        axis.legend(prop={'size': 8}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    return slopes


temperatures = np.array(list(range(50,100,5))+list(range(100,150,10))+list(range(150,400,50))+list(range(450,1100,50)))
kb = 8.6173324e-5
tempMavg = []
tempOavg = []
tempR1avg = []
tempR2avg = []

workingPath = os.getcwd()

####################################################################################################
# read
####################################################################################################

try:
    tempMavg = info.readAe("tempMavg.txt")
    tempOavg = info.readAe("tempOavg.txt")
    tempR1avg = np.loadtxt("tempR1avg.txt")
    tempR2avg = np.loadtxt("tempR2avg.txt")

####################################################################################################
# compute
####################################################################################################
except FileNotFoundError:
    for t in temperatures:
        print(t)
        os.chdir(str(t)+"/results")
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

    #save
    info.writeAe("tempMavg.txt", tempMavg)
    info.writeAe("tempOavg.txt", tempOavg)
    np.savetxt("tempR1avg.txt",tempR1avg, fmt='%.18f')
    np.savetxt("tempR2avg.txt",tempR2avg, fmt='%.18f')

minTemperatureIndex = 4
tempMavg = tempMavg[minTemperatureIndex:]
tempOavg = tempOavg[minTemperatureIndex:]
tempR1avg = tempR1avg[minTemperatureIndex:]
tempR2avg = tempR2avg[minTemperatureIndex:]

temperatures = temperatures[minTemperatureIndex:]
# define ranges
rngt = defineRanges(temperatures)

ind = [0,4,8,12,15,20,24,27]
tempOmegaCov = []
tempEaCov = []
tempEaMCov = []
showPlot = False
if len(sys.argv) > 1:
    showPlot = sys.argv[1] == "p"
for cov in range(-49,0):
    x = 1/kb/temperatures+np.log(5e4**1.5)
    y = tempR1avg
    print(cov)
    if showPlot:
        fig, axarr = plt.subplots(3, sharex=True)
        fig.set_size_inches(6,6)
        fig.subplots_adjust(right=0.7)
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
            cbar_ax = fig.add_axes([0.4, 0.15, 0.25, 0.15])
            cbar_ax.plot(x, np.sum(tempOavg[:,cov,ind[2*i]:ind[2*i+1]],   axis=1), ".-")
            cbar_ax.set_ylim(-0.05,1.05)
            axarr[2].semilogy(x, np.sum(tempOavg[:,cov,ind[2*i]:ind[2*i+1]],   axis=1), ".-")
            axarr[2].set_ylim(1e-3,2)
        for j in range(0,3): # temperature ranges
            tempOmega[i][j] = np.exp(np.mean(np.log(np.sum(tempOavg[rngt[2*j]:rngt[2*j+1],cov,ind[2*i]:ind[2*i+1]],   axis=1))))
    tempOmegaCov.append(tempOmega)
    tempEaMCov.append(tempEaM)
    if showPlot:
        plt.savefig("plot"+str(cov)+".png")
        plt.close()
    

tempOmegaCov = np.array(tempOmegaCov) # [coverage, type (alfa), temperature range]
tempEaCov = -np.array(tempEaCov) # [coverage, temperature range]
tempEaMCov = np.array(tempEaMCov) # [coverage, type (alfa), temperature range]
tempEaRCov = np.zeros(np.shape(tempEaMCov))
energies = [0.1, 0.25, 0.33, 0.42]
for alfa in range(0,4):
    tempEaRCov[:,alfa,:] = energies[alfa]
cov = list(range(0,49))

plt.figure()
fig, axarr = plt.subplots(1, 3, sharey=True)
tempEaCov2 = np.sum(tempOmegaCov*(tempEaRCov-tempEaMCov), axis=1)
for i in range(0,3): # different temperature ranges (low, medium, high)
    ax = plt.gca()
    axarr[i].plot(cov, tempEaCov[:,2-i], label="{}".format(2-i))
    axarr[i].plot(cov, tempEaCov2[:,2-i], "x:", label="v2 {}".format(2-i))
    ax = axarr[i].twinx()
    ax.plot(cov, 1-tempEaCov2[:,2-i]/tempEaCov[:,2-i], "o", label="relative error")
    ax.set_ylim(0,1)
    #Label jartzea falta da
    plt.legend(loc="best", prop={'size':8})
plt.savefig("multiplicities.png")

rAndM = False
omegas = False
if len(sys.argv) > 1:
    rAndM = sys.argv[1] == "r"
    omegas = sys.argv[1] == "o"
if (rAndM): # plot total activation energy as the sum of ratios and multiplicities
    cm = plt.get_cmap('Accent')
    for j in range(0,3): # different temperature ranges (low, medium, high)
        partialSum1 = np.sum(tempOmegaCov[:,:,j]*(-tempEaMCov[:,:,j]), axis=1)
        partialSum2 = np.sum(tempOmegaCov[:,:,j]*(tempEaRCov[:,:,j]), axis=1)
        rev = np.sum(partialSum1) < 0
        partialSum = partialSum1 + partialSum2
        c = 0
        if rev:
            axarr[2-j].fill_between(cov, partialSum2, color=cm(c/3), alpha=0.8)
            c += 1
        for i in range(0,2):
            if rev:
                axarr[2-j].fill_between(cov,partialSum1, color=cm((c+i)/3), alpha=0.8)
                partialSum1 = partialSum1 + partialSum2
                
            else:
                axarr[2-j].fill_between(cov, partialSum, color=cm((c+i)/3), alpha=0.8)
                partialSum -= partialSum1
    plt.savefig("multiplicitiesRandM.png")

if (omegas):
    cm = plt.get_cmap('Set1')
    for j in range(0,3): # different temperature ranges (low, medium, high)
        partialSum = np.sum(tempOmegaCov[:,:,j]*(tempEaRCov[:,:,j]-tempEaMCov[:,:,j]), axis=1)
        for i in range(3,-1,-1): #alfa
            axarr[2-j].fill_between(cov, partialSum, color=cm(i/3))
            partialSum -= tempOmegaCov[:,i,j]*(tempEaRCov[:,i,j]-tempEaMCov[:,i,j])
    
    plt.legend(loc="best", prop={'size':8})
    plt.savefig("multiplicitiesOmegas.png")
