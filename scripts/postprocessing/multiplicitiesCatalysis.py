import sys
import info as inf
import energies as e
import matplotlib.pyplot as plt
from matplotlib.ticker import LogLocator
from matplotlib.ticker import FixedFormatter
import matplotlib.ticker as plticker
import os
import glob
import numpy as np
import functions as fun
import multiplicitiesPlot as mp


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



##########################################################
##########           Main function   #####################
##########################################################

temperatures = inf.getTemperatures()
kb = 8.6173324e-5
p = inf.getInputParameters(glob.glob("*/output*")[0])
maxAlfa = 4
ind = [0,1,1,2,2,3,3,4]
energies = e.catalysis(p)
tempMavg = []
tempOavg = []
tempR1avg = []
tempR2avg = []
tempR3avg = []

workingPath = os.getcwd()
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
    tmp1, tmp2, tmp3, tmp4, tmp5 = computeMavgAndOmegaOverRuns()
    tempMavg.append(tmp1)
    tempOavg.append(tmp2)
    tempR1avg.append(tmp3)
    tempR2avg.append(tmp4)
    tempR3avg.append(tmp5)
   
os.chdir(workingPath) 
tempMavg = np.array(tempMavg)
tempOavg = np.array(tempOavg)
tempR1avg = np.array(tempR1avg)
tempR2avg = np.array(tempR2avg)
tempR3avg = np.array(tempR3avg)

#print(tempMavg)
print(np.shape(tempMavg))
# print(tempR1avg)
# print(tempR2avg)
# print(tempR3avg)
showPlot=False
if len(sys.argv) > 1:
    showPlot = sys.argv[1] == "p"

tempOmegaCo2 = []
tempEaMCo2 = []
tempEaCo2 = []
tempEafCo2 = []
rngt = e.defineRangesCatalysis(p.calc, p.rLib, temperatures) #list([0, 3])

maxRanges = len(rngt) - 1
labelAlfa = ["$CO_2^B+O^B$","$CO_2^B+O^C$","$CO_2^C+O^B$","$CO_2^C+O^C$"]
for co2 in range(0,100): # created co2: 10,20,30...1000
    print(co2)
    x = 1/kb/temperatures
    y = tempR1avg
    if showPlot:
        cm = plt.get_cmap('Set1')
        fig, axarr = plt.subplots(3, sharex=True, figsize=(5,6))
        fig.subplots_adjust(right=0.7, hspace=0.1)
    else:
        axarr = np.zeros(3)
    # N_h
    tempEaCo2.append(mp.fitAndPlotLinear(x, y[:,co2], rngt, axarr[0], -1, showPlot, labelAlfa, co2))
    tempOmega = np.zeros((maxAlfa,maxRanges))
    tempEaM = []
    y2 = tempR1avg/tempR3avg
    tempEafCo2.append(mp.fitAndPlotLinear(x, y2[:,co2], rngt, axarr[0], -2, False, labelAlfa, co2))
    
    for i in range(0,maxAlfa): # alfa
        y = np.sum(tempMavg[:,co2,ind[2*i]:ind[2*i+1]], axis=1)
        tempEaM.append(mp.fitAndPlotLinear(x, y, rngt, axarr[1], i, showPlot, labelAlfa, co2))
        for j in range(0,maxRanges): # temperature ranges
            tempOmega[i][j] = np.exp(np.mean(np.log(np.sum(tempOavg[rngt[j]:rngt[j+1],co2,ind[2*i]:ind[2*i+1]], axis=1))))
        if showPlot:
            y = np.sum(tempOavg[:,co2,ind[2*i]:ind[2*i+1]], axis=1)
            mp.plotOmegas(x, y, axarr[-1], i, tempOmega[i], rngt, labelAlfa)
    
    tempOmegaCo2.append(tempOmega)
    tempEaMCo2.append(tempEaM)
    if showPlot:
        plt.savefig("plot"+str(co2)+".png", bbox_inches='tight')
        plt.close()

tempOmegaCo2 = np.array(tempOmegaCo2) # [co2, type (alfa), temperature range]
tempEaCo2 = -np.array(tempEaCo2) # [co2, temperature range]
tempEaMCo2 = np.array(tempEaMCo2) # [co2, type (alfa), temperature range]
tempEaRCo2 = np.zeros(np.shape(tempEaMCo2))
for alfa in range(0,maxAlfa):
    tempEaRCo2[:,alfa,:] = energies[alfa]

fig, axarr = plt.subplots(1, maxRanges, sharey=True, figsize=(8,5))
fig.subplots_adjust(wspace=0.1)
tempEaCov2 = np.sum(tempOmegaCo2*(tempEaRCo2-tempEaMCo2), axis=1)-tempEafCo2

rAndM = False
omegas = False
if len(sys.argv) > 1:
    rAndM = sys.argv[1] == "r"
    omegas = sys.argv[1] == "o"
    
axarr[0].set_ylabel("eV")
co2 = list(range(0,100))
for i in range(0,maxRanges): # different temperature ranges (low, medium, high)
    targt = tempEaCov2[:,maxRanges-1-i]
    rcmpt = tempEaCo2[:,maxRanges-1-i]
    error = abs(1-tempEaCov2[:,maxRanges-1-i]/tempEaCo2[:,maxRanges-1-i])
    handles = mp.plotSimple(co2, targt, rcmpt, error, axarr[i],
                             maxRanges, i, not rAndM and not omegas)

plt.savefig("multiplicities.png", bbox_inches='tight')

if (rAndM): # plot total activation energy as the sum of ratios and multiplicities
    label = ["multiplicity", "sum", "ratio"]
    cm = plt.get_cmap('Accent')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        partialSum1 = np.sum(tempOmegaCo2[:,:,j]*(-tempEaMCo2[:,:,j]), axis=1)
        partialSum2 = np.sum(tempOmegaCo2[:,:,j]*(tempEaRCo2[:,:,j]), axis=1)
        mp.plotRandM(co2, partialSum1, partialSum2, axarr[maxRanges-1-j],
                     handles, j == maxRanges-1)

    plt.savefig("multiplicitiesRandM.png", bbox_inches='tight')

if (omegas):
    labels = ["0", "20", "40", "60", "80", "100"]
    cm = plt.get_cmap('Set2')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        axarr[maxRanges-1-j].get_xaxis().set_major_formatter(FixedFormatter(labels))
        partialSum = np.sum(tempOmegaCo2[:,:,j]*(tempEaRCo2[:,:,j]-tempEaMCo2[:,:,j]), axis=1)
        lgs = []
        for i in range(0,maxAlfa): #alfa
            lgs.append(axarr[maxRanges-1-j].fill_between(co2, partialSum, color=cm(i/(maxAlfa-1)), label=labelAlfa[i]))
            partialSum -= tempOmegaCo2[:,i,j]*(tempEaRCo2[:,i,j]-tempEaMCo2[:,i,j])
    
    myLegends = []
    myLabels = [r"$E_a$", r"$E^f + \sum_\alpha \;\epsilon_\alpha$"]
    myLegends += lgs
        
    for i in range(maxAlfa-1,-1,-1): #alfa
        myLabels.append(labelAlfa[i])
    myLabels.append("Rel. err.")
    plt.figlegend(myLegends, myLabels, loc=(0.68,0.15), prop={'size':11})
    plt.savefig("multiplicitiesOmegasP.png", bbox_inches='tight')
