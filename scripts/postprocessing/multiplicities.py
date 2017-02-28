import functions as f
import info
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker
import glob
import re
import math
import os
import sys


def computeMavgAndOmega(fileNumber):
    p = info.getInputParameters()
    matrix = np.loadtxt(fname="data"+str(fileNumber)+".txt", delimiter="\t")
    possiblesFromList = np.loadtxt(fname="possibleFromList"+str(fileNumber)+".txt")
    possiblesFromList = possiblesFromList[:,1:] # remove coverage
    time = np.array(matrix[:,1])
    length = len(time)
    hops = np.array(matrix[:,15])
    if p.calc == "basic":
        if p.rLib == "version2":
            ratios = info.getRatio(p.temp, info.getBasic2Energies())
        else:
            ratios = info.getRatio(p.temp, info.getBasicEnergies())
    elif (p.calc == "AgUc"):
        ratios = info.getRatio(p.temp, info.getHexagonalEnergies())
    else:
        ratios = info.getRatio(p.temp, info.getGrapheneSimpleEnergies())/100
    Mavg = np.zeros(shape=(length,p.maxA))
    for i in range(0,p.maxA): # iterate alfa
        Mavg[:,i] = possiblesFromList[:,i]/time
    avgTotalHopRate2 = np.array(ratios.dot(np.transpose(Mavg)))
    avgTotalHopRate1 = hops/time
    # define omegas AgUc
    omega = np.zeros(shape=(length,p.maxA)) # [coverage, alfa]
    for i in range(0,length):
        omega[i,:] =  Mavg[i,:] * ratios / avgTotalHopRate2[i]
    np.shape(omega)
    return Mavg, omega, avgTotalHopRate1, avgTotalHopRate2


def computeMavgAndOmegaOverRuns():
    p = info.getInputParameters()
    files = glob.glob("possibleDiscrete*")
    files.sort()
    matrix = np.loadtxt(fname="data0.txt", delimiter="\t")
    length = len(matrix)
    sumMavg = np.zeros(shape=(length,p.maxA))  # [coverage, alfa]
    sumOmega = np.zeros(shape=(length,p.maxA)) # [coverage, alfa]
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


def defineRanges(calculationMode, ratesLibrary, temperatures):
    if calculationMode == "AgUc":
        indexes = np.where((temperatures >= 70) & (temperatures <= 150))
        iSl = indexes[0][0]
        iFl = indexes[0][-1]
        indexes = np.where((temperatures >= 150) & (temperatures <= 450))
        iSm = indexes[0][0]
        iFm = indexes[0][-1]
        indexes = np.where((temperatures >= 450) & (temperatures <= 1100))
        iSh = indexes[0][0]
        iFh = indexes[0][-1]
    elif calculationMode == "basic":
        if ratesLibrary == "version2":
            indexes = np.where((temperatures >= 120) & (temperatures <= 195))
            iSl = indexes[0][0]
            iFl = indexes[0][-1]
            indexes = np.where((temperatures >= 195) & (temperatures <= 265))
            iSm = indexes[0][0]
            iFm = indexes[0][-1]
            indexes = np.where((temperatures >= 265) & (temperatures <= 400))
            iSh = indexes[0][0]
            iFh = indexes[0][-1]
            # in principle, it doesn't have intermediate temperature range
            #iSm = iFl-1
            #iFm = iSh+1
        else:
            indexes = np.where((temperatures >= 120) & (temperatures <= 190))
            iSl = indexes[0][0]
            iFl = indexes[0][-1]
            indexes = np.where((temperatures >= 190) & (temperatures <= 270))
            iSm = indexes[0][0]
            iFm = indexes[0][-1]
            indexes = np.where((temperatures >= 270) & (temperatures <= 1100))
            iSh = indexes[0][0]
            iFh = indexes[0][-1]
    else:
        indexes = np.where((temperatures >= 200) & (temperatures <= 500))
        iSl = indexes[0][0]
        iFl = indexes[0][-1]
        indexes = np.where((temperatures >= 500) & (temperatures <= 1000))
        iSm = indexes[0][0]
        iFm = indexes[0][-1]
        indexes = np.where((temperatures >= 1000) & (temperatures <= 1500))
        iSh = indexes[0][0]
        iFh = indexes[0][-1]
        print("sdfadf")
        
    return list([iSl, iFl, iSm, iFm, iSh, iFh])


def fitAndPlotLinear(x, y, rngt, axis, alfa, showPlot, labelAlfa):
    cm = plt.get_cmap('Set1')
    slopes = []
    if showPlot:   
        axis.scatter(x, y, color=cm(abs(alfa/9)), alpha=0.75, edgecolors='none')#, "o", lw=0.5)
    a, b = f.linearFit(x, y, rngt[0], rngt[1])
    slopes.append(b)
    if showPlot:
        axis.semilogy(x[rngt[0]:rngt[1]+1], np.exp(f.linear(x[rngt[0]:rngt[1]+1], a, b)), ls="-", label="{} low {:03.3f} ".format(labelAlfa[alfa],b))
    a, b = f.linearFit(x, y, rngt[2], rngt[3])
    slopes.append(b)
    if showPlot:
        axis.semilogy(x[rngt[2]-1:rngt[3]+1], np.exp(f.linear(x[rngt[2]-1:rngt[3]+1], a, b)), ls="-", label="{} med {:03.3f}".format(labelAlfa[alfa],b))
    a, b = f.linearFit(x, y, rngt[4], rngt[5])
    slopes.append(b)
    if showPlot:
        axis.semilogy(x[rngt[4]-1:], np.exp(f.linear(x[rngt[4]-1:], a, b)), label="{} high {:03.3f}".format(labelAlfa[alfa],b)) 
        axis.set_ylabel("eV")
        if alfa == -1:
            axis.legend(prop={'size': 8}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
        else:
            axis.legend(prop={'size': 5.1}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0., ncol=2)
    return slopes


temperatures = info.getTemperatures()
p = info.getInputParameters(glob.glob("*/output*")[0])
p.maxC -= 1
calculationMode = p.calc
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

if calculationMode == "AgUc":
    minTemperatureIndex = 4
    tempMavg = tempMavg[minTemperatureIndex:]
    tempOavg = tempOavg[minTemperatureIndex:]
    tempR1avg = tempR1avg[minTemperatureIndex:]
    tempR2avg = tempR2avg[minTemperatureIndex:]
    temperatures = temperatures[minTemperatureIndex:]
    ind = [0,4,8,12,15,20,24,27]
    maxAlfa = 4
    xmin = 20
    xmax = 200
    energies = [0.1, 0.25, 0.33, 0.42]
    labelAlfa = ["$E_0$", "$E_1$", "$E_2$", "$E_3$"]
elif calculationMode == "basic":
    if p.rLib == "version2":
        #       d   a
        ind = [0,4,5,8] # and 11 too
        maxAlfa = 2
        xmin = 30
        xmax = 120
        energies = [0.1, 0.4]
        labelAlfa = ["$E_d$", "$E_a$"]
    else:
        #       d   a   f   b    c   g
        ind = [0,4,6,8,5,6,9,12,4,5,8,9]
        maxAlfa = 6
        xmin = 40
        xmax = 120
        energies = [0.2, 0.35, 0.36, 0.435, 0.45, 0.535]
        labelAlfa = ["$E_d$", "$E_a$", "$E_f$", "$E_b$", "$E_c$", "$E_g$"]
else:
    #       a   b   c   d
    ind = [0,4,4,5,5,7,8,9]
    maxAlfa = 4
    xmin = 20
    xmax = 80
    energies = [0.5, 2.6, 1.8, 3.9]
    labelAlfa = ["$E_a$", "$E_b$", "$E_c$", "$E_d$"]
    
labelAlfa.append("EA")
# define ranges
rngt = defineRanges(calculationMode, p.rLib, temperatures)

tempOmegaCov = []
tempEaCov = []
tempEaMCov = []
showPlot = False
coverage = list(range(0,p.maxC))
if len(sys.argv) > 1:
    showPlot = sys.argv[1] == "p"
for cov in range(-p.maxC,0):
    x = 1/kb/temperatures+np.log(5e4**1.5)
    y = tempR1avg
    print(cov)
    if showPlot:
        cm = plt.get_cmap('Set1')
        fig, axarr = plt.subplots(3, sharex=True)
        fig.set_size_inches(6,6)
        fig.subplots_adjust(right=0.7, hspace=0.1)
        plt.xlim(xmin,xmax)
    else:
        axarr = np.zeros(3)
    # N_h
    tempEaCov.append(fitAndPlotLinear(x, y[:,cov], rngt, axarr[0], -1, showPlot, labelAlfa))
    tempOmega = np.zeros((maxAlfa,3))
    tempEaM = []
    for i in range(0,maxAlfa): # alfa
        y = np.sum(tempMavg[:,cov,ind[2*i]:ind[2*i+1]], axis=1)
        if p.calc == "basic" and p.rLib == "version2" and i == 1:
            y += tempMavg[:,cov,11]
        if p.calc == "graphene" and i == 1:
            y += np.sum(tempMavg[:,cov,9:11], axis=1)
        
        tempEaM.append(fitAndPlotLinear(x, y, rngt, axarr[1], i, showPlot, labelAlfa))
        if showPlot:
            ax = fig.add_axes([0.4, 0.15, 0.25, 0.15])
            y = np.sum(tempOavg[:,cov,ind[2*i]:ind[2*i+1]], axis=1)
            if p.calc == "basic" and p.rLib == "version2" and i == 1:
                y += tempOavg[:,cov,11]
            if p.calc == "graphene" and i == 1:
                y += np.sum(tempOavg[:,cov,9:11], axis=1)
            ax.scatter(x, y, color=cm(abs(i/9)), alpha=0.75, edgecolors='none')
            ax.set_ylim(-0.05,1.05)
            loc = plticker.MultipleLocator(40.0) # this locator puts ticks at regular intervals
            ax.xaxis.set_major_locator(loc)
            loc = plticker.MultipleLocator(1/3) # this locator puts ticks at regular intervals
            ax.yaxis.set_major_locator(loc)
            ax.yaxis.set_major_formatter(plticker.FixedFormatter(("0", "$0$", "$1/3$", "$2/3$", "$1$")))
            ax.set_xlim(xmin,xmax)
            axarr[2].semilogy(x, np.sum(tempOavg[:,cov,ind[2*i]:ind[2*i+1]],   axis=1), ".-", color=cm(abs(i/9)), label=labelAlfa[i])
            axarr[2].set_ylim(1e-3,2)
            axarr[2].legend(prop={'size': 8}, bbox_to_anchor=(1.05, 0), loc="lower left", borderaxespad=0.)
            axarr[2].set_ylabel(r"$\omega_\alpha$")
        for j in range(0,3): # temperature ranges
            tempOmega[i][j] = np.exp(np.mean(np.log(np.sum(tempOavg[rngt[2*j]:rngt[2*j+1],cov,ind[2*i]:ind[2*i+1]],   axis=1))))
    tempOmegaCov.append(tempOmega)
    tempEaMCov.append(tempEaM)
    if showPlot:
        axarr[2].set_xlabel(r"$\theta$ Coverage")
        plt.savefig("plot"+str(p.maxC+cov)+".png")
        plt.close()
    

tempOmegaCov = np.array(tempOmegaCov) # [coverage, type (alfa), temperature range]
tempEaCov = -np.array(tempEaCov) # [coverage, temperature range]
tempEaMCov = np.array(tempEaMCov) # [coverage, type (alfa), temperature range]
tempEaRCov = np.zeros(np.shape(tempEaMCov))
for alfa in range(0,maxAlfa):
    tempEaRCov[:,alfa,:] = energies[alfa]

plt.figure()
fig, axarr = plt.subplots(1, 3, sharey=True)
tempEaCov2 = np.sum(tempOmegaCov*(tempEaRCov-tempEaMCov), axis=1)

cm = plt.get_cmap('gist_earth')
ax = []
axarr[0].set_ylabel("eV")
for i in range(0,3): # different temperature ranges (low, medium, high)
    axarr[i].set_xlabel(r"$\theta$ Coverage")
    lgEaCov2, = axarr[i].plot(coverage, tempEaCov2[:,2-i], ls="dashed", solid_capstyle="round", lw=5, label="Recomputed AE", alpha=0.6, color=cm(1/3))
    lgEaCov, = axarr[i].plot(coverage, tempEaCov[:,2-i], "-",  solid_capstyle="round", lw=5, label="Activation energy", alpha=0.6, color=cm(2/3))
    ax.append(axarr[i].twinx())
    lgErr, = ax[i].plot(coverage, abs(1-tempEaCov2[:,2-i]/tempEaCov[:,2-i]),lw=5, ls="dotted", solid_capstyle="round", color=cm(3/4), label="relative error")
    ax[i].set_ylim(0,1)
    
    #if i != 2:
     #   plt.setp(ax, visible=False)

plt.figlegend((lgEaCov, lgEaCov2, lgErr),("Activation energy", "Recomputed AE", "Error"), "upper right", prop={'size':8})
plt.savefig("multiplicities.png")
rAndM = False
omegas = False
if len(sys.argv) > 1:
    rAndM = sys.argv[1] == "r"
    omegas = sys.argv[1] == "o"
if (rAndM): # plot total activation energy as the sum of ratios and multiplicities
    label = ["multiplicity", "sum", "ratio"]
    cm = plt.get_cmap('Accent')
    for j in range(0,3): # different temperature ranges (low, medium, high)
        partialSum1 = np.sum(tempOmegaCov[:,:,j]*(-tempEaMCov[:,:,j]), axis=1)
        partialSum2 = np.sum(tempOmegaCov[:,:,j]*(tempEaRCov[:,:,j]), axis=1)
        rev = np.sum(partialSum1) < 0
        partialSum = partialSum1 + partialSum2
        c = 0
        if rev:
            lgSum = axarr[2-j].fill_between(coverage, partialSum2, color=cm(c/3), alpha=0.8, label=label[i])
            c += 1
            lgR = []
        for i in range(0,2):
            if rev:
                lg = axarr[2-j].fill_between(coverage,partialSum1, color=cm((c+i)/3), alpha=0.8, label=label[i])
                lgR.append(lg)
                partialSum1 = partialSum1 + partialSum2
                
            else:
                lg = axarr[2-j].fill_between(coverage, partialSum, color=cm((c+i)/3), alpha=0.8, label=label[i])
                lgR.append(lg)
                partialSum -= partialSum1
    plt.figlegend((lgEaCov, lgEaCov2, lgErr, lgR[0], lgR[1], lgSum),("Activation energy", "Recomputed AE", "Error", "R", "sum", "M"), "upper right", prop={'size':8})
    plt.savefig("multiplicitiesRandM.png")

if (omegas):
    cm = plt.get_cmap('Set1')
    for j in range(0,3): # different temperature ranges (low, medium, high)
        partialSum = np.sum(tempOmegaCov[:,:,j]*(tempEaRCov[:,:,j]-tempEaMCov[:,:,j]), axis=1)
        lgs = []
        for i in range(maxAlfa-1,-1,-1): #alfa
            lgs.append(axarr[2-j].fill_between(coverage, partialSum, color=cm(i/(maxAlfa-1)), label=labelAlfa[i]))
            partialSum -= tempOmegaCov[:,i,j]*(tempEaRCov[:,i,j]-tempEaMCov[:,i,j])
    
    myLegends = [lgEaCov, lgEaCov2, lgErr]
    myLabels = ["Activation energy", "Recomputed AE", "Error"]  
    myLegends += lgs
    for i in range(maxAlfa-1,-1,-1): #alfa
        myLabels.append(labelAlfa[i])
    plt.figlegend(myLegends, myLabels, "upper right", prop={'size':8})
    plt.savefig("multiplicitiesOmegas.png")
