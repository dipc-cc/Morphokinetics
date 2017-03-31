import functions as fun
import info
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker
from matplotlib.ticker import FixedFormatter
from matplotlib.ticker import LogLocator
import glob
import re
import math
import os
import sys
import roman

def computeMavgAndOmega(fileNumber, p):
    matrix = np.loadtxt(fname="data"+str(fileNumber)+".txt", delimiter="\t")
    possiblesFromList = np.loadtxt(fname="possibleFromList"+str(fileNumber)+".txt")
    possiblesFromList = possiblesFromList[:,1:] # remove coverage
    time = np.array(matrix[:,1])
    length = len(time)
    coverage = matrix[:,0]
    hops = np.array(matrix[:,15])/(4*coverage*p.sizI*p.sizJ) # scale all data
    ratios = p.getRatios()
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
    filesNumber = len(files)
    sumMavg = np.zeros(shape=(length,p.maxA))  # [coverage, alfa]
    sumOmega = np.zeros(shape=(length,p.maxA)) # [coverage, alfa]
    sumRate1 = np.zeros(length)
    sumRate2 = np.zeros(length)
    #iterating over runs
    for i in range(0,filesNumber):
        tmpMavg, tmpOmega, tmpRate1, tmpRate2 = computeMavgAndOmega(i, p)
        sumMavg = sumMavg + tmpMavg
        sumOmega = sumOmega + tmpOmega
        sumRate1 = sumRate1 + tmpRate1
        sumRate2 = sumRate2 + tmpRate2
        
    
    runMavg = sumMavg / filesNumber
    runOavg = sumOmega / filesNumber
    runR1avg = sumRate1 / filesNumber
    runR2avg = sumRate2 / filesNumber

    return runMavg, runOavg, runR1avg, runR2avg


def defineRanges(calculationMode, ratesLibrary, temperatures):
    ranges = []
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
            # it has 4 ranges
            ranges = list([0, 19, 33, 48, 58])
        else:
            indexes = np.where((temperatures >= 120) & (temperatures <= 190))
            iSl = indexes[0][0]
            indexes = np.where((temperatures >= 190) & (temperatures <= 270))
            iSm = indexes[0][0]
            indexes = np.where((temperatures >= 270) & (temperatures <= 350))
            iSh = indexes[0][0]
            iFh = indexes[0][-1]
    else:
        indexes = np.where((temperatures >= 200) & (temperatures <= 500))
        iSl = indexes[0][0]
        indexes = np.where((temperatures >= 500) & (temperatures <= 1000))
        iSm = indexes[0][0]
        indexes = np.where((temperatures >= 1000) & (temperatures <= 1500))
        iSh = indexes[0][0]
        iFh = indexes[0][-1]

    if len(ranges) > 0:
        return ranges
    else:
        return list([iSl, iSm, iSh, iFh])


def fitAndPlotLinear(x, y, rngt, axis, alfa, showPlot, labelAlfa):
    markers=["o", "s","D","^","d","h","p","o"]
    labelRange = ['low', 'med', 'high']
    labelRange = labelRange+list([str(i) for i in rngt])
    cm = plt.get_cmap('Set1')
    cm1 = plt.get_cmap('Set3')
    slopes = []
    if showPlot:   
        axis.scatter(x, y, color=cm(abs(alfa/9)), alpha=0.75, edgecolors='none', marker=markers[alfa])#, "o", lw=0.5)
        arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
        xI = 42
        xII = 94
        if alfa == -1:
            axis.text(-15, 4e5, "(a)", ha="center", va="center")
            axis.set_ylabel(r"$\frac{l^2}{2dN_a} \; \frac{\langle N_h \rangle}{t}$")
            yMin = 1e1
            yMax = 1e6
        elif alfa == 0:
            axis.text(-15, 1.5e4, "(b)", ha="center", va="center")
            axis.set_ylabel(r"$M_\alpha$")
            yMin = 1e-1
            yMax = 1e5
        if alfa < 1:
            axis.annotate("", xy=(xI,yMin), xytext=(xI,yMax), arrowprops=arrow, ha="center", va="center")
            axis.annotate("", xy=(xII,yMin), xytext=(xII,yMax), arrowprops=arrow, ha="center", va="center")
    for i in range(0,len(rngt)-1):
        a, b = fun.linearFit(x, y, rngt[i], rngt[i+1])
        slopes.append(b)
        if showPlot:
            axis.semilogy(x[rngt[i]:rngt[i+1]+1], np.exp(fun.linear(x[rngt[i]:rngt[i+1]+1], a, b)), ls="-", color=cm1((i+abs(alfa)*3)/12))
            xHalf = (x[rngt[i]]+x[rngt[i+1]]+1)/2
            text = "{:03.3f}".format(-b)
            yHalf = np.exp(fun.linear(xHalf, a, b))
            if alfa == -1:
                xHalf *= 1.15
                yHalf *= 5
                text = r"$E_a^{"+roman.toRoman(i+1)+r", Arrh}="+text+r"$"
                xText = [22, 60, 120]
                axis.text(xText[i], 30, r"$"+roman.toRoman(maxRanges-i)+r"$", color="gray")#, transform=axarr[i].transAxes)
            bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.6)
            axis.text(xHalf,yHalf, text, color=cm(abs(alfa/9)), bbox=bbox_props, ha="center", va="center", size=8)
    if showPlot and alfa > -1:
        locator = LogLocator(100,[1e-1])
        axis.yaxis.set_major_locator(locator)
    return slopes

def plotOmegas(x, y, axis, i):
    markers=["o", "s","D","^","d","h","p","o"]
    newax = fig.add_axes([0.43, 0.15, 0.25, 0.15])
    newax.scatter(x, y, color=cm(abs(i/9)), alpha=0.75, edgecolors='none', label=labelAlfa[i], marker=markers[i])
    newax.set_ylim(-0.05,1.05)
    loc = plticker.MultipleLocator(40.0) # this locator puts ticks at regular intervals
    newax.xaxis.set_major_locator(loc)
    loc = plticker.MultipleLocator(1/3) # this locator puts ticks at regular intervals
    newax.yaxis.set_major_locator(loc)
    newax.yaxis.set_major_formatter(plticker.FixedFormatter(("0", "$0$", r"$\frac{1}{3}$", r"$\frac{2}{3}$", "$1$")))
    newax.set_xlim(xmin,xmax)
    lg = newax.legend(prop={'size': 7}, loc=(0.45,0.13), scatterpoints=1)
    newax.add_artist(lg)
    newax.legend(prop={'size': 7}, loc=(0.45,1.6), scatterpoints=1)
    axis.semilogy(x, y, ls="",color=cm(abs(i/9)), label=labelAlfa[i], marker=markers[i], markeredgecolor=cm(abs(i/9)))
    axis.set_ylim(2e-4,2)
    axis.set_ylabel(r"$\omega_\alpha$")
    axis.set_xlabel(r"$1/k_BT$")
    arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
    if i == 0:
        axis.annotate("", xy=(42,2e-4), xytext=(42,2), arrowprops=arrow)
        axis.annotate("", xy=(94,2e-4), xytext=(94,2), arrowprops=arrow)
        axis.text(-15, 1, "(c)", ha="center", va="center")
    
temperatures = info.getTemperatures()
p = info.getInputParameters(glob.glob("*/output*")[0])
p.maxC += 1
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
    xmax = 185
    energies = [0.1, 0.25, 0.33, 0.42]
    labelAlfa = [r"$\alpha=0$", r"$\alpha=1$", r"$\alpha=2$", r"$\alpha=3$"]
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
    
labelAlfa.append(r"$E_a \; \frac{\langle N_h \rangle}{t}$")
# define ranges
rngt = defineRanges(calculationMode, p.rLib, temperatures)

tempOmegaCov = []
tempEaCov = []
tempEaMCov = []
showPlot = False
maxRanges = len(rngt) - 1
coverage = list(range(0,p.maxC))
if len(sys.argv) > 1:
    showPlot = sys.argv[1] == "p"
for cov in range(-p.maxC,0):
    #if cov < -71 or cov > -68:
     #   continue
    x = 1/kb/temperatures+np.log(5e4**1.5)
    y = tempR1avg
    print(cov)
    if showPlot:
        cm = plt.get_cmap('Set1')
        fig, axarr = plt.subplots(3, sharex=True)
        fig.set_size_inches(5,6)
        fig.subplots_adjust(right=0.7, hspace=0.1)
        plt.xlim(xmin,xmax)
    else:
        axarr = np.zeros(3)
    # N_h
    tempEaCov.append(fitAndPlotLinear(x, y[:,cov], rngt, axarr[0], -1, showPlot, labelAlfa))
    tempOmega = np.zeros((maxAlfa,maxRanges))
    tempEaM = []
    for i in range(0,maxAlfa): # alfa
        y = np.sum(tempMavg[:,cov,ind[2*i]:ind[2*i+1]], axis=1)
        if p.calc == "basic" and p.rLib == "version2" and i == 1:
            y += tempMavg[:,cov,11]
        if p.calc == "graphene" and i == 1:
            y += np.sum(tempMavg[:,cov,9:11], axis=1)
        
        tempEaM.append(fitAndPlotLinear(x, y, rngt, axarr[1], i, showPlot, labelAlfa))
        if showPlot:
            y = np.sum(tempOavg[:,cov,ind[2*i]:ind[2*i+1]], axis=1)
            if p.calc == "basic" and p.rLib == "version2" and i == 1:
                y += tempOavg[:,cov,11]
            if p.calc == "graphene" and i == 1:
                y += np.sum(tempOavg[:,cov,9:11], axis=1)
            plotOmegas(x, y, axarr[2], i)
        for j in range(0,maxRanges): # temperature ranges
            tempOmega[i][j] = np.exp(np.mean(np.log(np.sum(tempOavg[rngt[j]:rngt[j+1],cov,ind[2*i]:ind[2*i+1]],   axis=1))))
    tempOmegaCov.append(tempOmega)
    tempEaMCov.append(tempEaM)
    if showPlot:
        plt.savefig("plot"+str(p.maxC+cov)+".pdf", bbox_inches='tight')
        plt.close()
    

tempOmegaCov = np.array(tempOmegaCov) # [coverage, type (alfa), temperature range]
tempEaCov = -np.array(tempEaCov) # [coverage, temperature range]
tempEaMCov = np.array(tempEaMCov) # [coverage, type (alfa), temperature range]
tempEaRCov = np.zeros(np.shape(tempEaMCov))
for alfa in range(0,maxAlfa):
    tempEaRCov[:,alfa,:] = energies[alfa]

fig, axarr = plt.subplots(1, maxRanges, sharey=True, figsize=(8,5))
fig.subplots_adjust(wspace=0.1)
tempEaCov2 = np.sum(tempOmegaCov*(tempEaRCov-tempEaMCov), axis=1)

cm = plt.get_cmap('gist_earth')
ax = []
axarr[0].set_ylabel("eV")
coverage = np.array(coverage)/100
for i in range(0,maxRanges): # different temperature ranges (low, medium, high)
    axarr[i].text(0.5, 0.95, r"$"+roman.toRoman(maxRanges-i)+r"$", color="gray", transform=axarr[i].transAxes)
    axarr[i].set_xlabel(r"$\theta$")
    lgEaCov2, = axarr[i].plot(coverage, tempEaCov2[:,maxRanges-1-i], ls="dashed", solid_capstyle="round", lw=5, label="Recomputed AE", alpha=0.6, color=cm(1/3))
    lgEaCov, = axarr[i].plot(coverage, tempEaCov[:,maxRanges-1-i], "-",  solid_capstyle="round", lw=5, label="Activation energy", alpha=0.6, color=cm(2/3))
    ax.append(axarr[i].twinx())
    lgErr, = ax[i].plot(coverage, abs(1-tempEaCov2[:,maxRanges-1-i]/tempEaCov[:,maxRanges-1-i]),lw=5, ls="dotted", solid_capstyle="round", color=cm(3/4), label="relative error")
    ax[i].set_ylim(0,1)
    maxY = max(abs(1-tempEaCov2[:,maxRanges-1-i]/tempEaCov[:,maxRanges-1-i])[30:])+0.05 # get maximum for the arrow (>30% coverage)
    ax[i].annotate(' ', xy=(.8, maxY), xytext=(.4, maxY), arrowprops=dict(arrowstyle="->", edgecolor=cm(3/4), facecolor=cm(3/4)))
    if i != 2:
        ax[i].yaxis.set_major_formatter(plticker.NullFormatter())
    else:
        ax[i].set_ylabel("Relative error")

rAndM = False
omegas = False
if len(sys.argv) > 1:
    rAndM = sys.argv[1] == "r"
    omegas = sys.argv[1] == "o"
if not rAndM and not omegas:
    plt.figlegend((lgEaCov, lgEaCov2, lgErr),("Activation energy", "Recomputed AE", "Error"), "upper right", prop={'size':8})
plt.savefig("multiplicities.svg", bbox_inches='tight')
if (rAndM): # plot total activation energy as the sum of ratios and multiplicities
    label = ["multiplicity", "sum", "ratio"]
    cm = plt.get_cmap('Accent')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        partialSum1 = np.sum(tempOmegaCov[:,:,j]*(-tempEaMCov[:,:,j]), axis=1)
        partialSum2 = np.sum(tempOmegaCov[:,:,j]*(tempEaRCov[:,:,j]), axis=1)
        rev = np.sum(partialSum1) < 0
        partialSum = partialSum1 + partialSum2
        c = 0
        if rev:
            lgSum = axarr[maxRanges-1-j].fill_between(coverage, partialSum2, color=cm(c/3), alpha=0.8, label=label[c])
            c += 1
            lgR = []
        for i in range(0,2):
            if rev:
                lg = axarr[maxRanges-1-j].fill_between(coverage,partialSum1, color=cm((c+i)/3), alpha=0.8, label=label[i])
                lgR.append(lg)
                partialSum1 = partialSum1 + partialSum2
                
            else:
                lg = axarr[maxRanges-1-j].fill_between(coverage, partialSum, color=cm((c+i)/3), alpha=0.8, label=label[i])
                lgR.append(lg)
                partialSum -= partialSum1
    plt.figlegend((lgEaCov, lgEaCov2, lgErr, lgR[0], lgR[1], lgSum),("Activation energy", "Recomputed AE", "Error", "R", "sum", "M"), loc=(0.7,0.7), prop={'size':8})
    plt.savefig("multiplicitiesRandM.svg", bbox_inches='tight')

if (omegas):
    labels = ["0", "0.2", "0.4", "0.6", "0.8", "1"]
    cm = plt.get_cmap('Set1')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        axarr[maxRanges-1-j].get_xaxis().set_major_formatter(FixedFormatter(labels))
        partialSum = np.sum(tempOmegaCov[:,:,j]*(tempEaRCov[:,:,j]-tempEaMCov[:,:,j]), axis=1)
        lgs = []
        for i in range(maxAlfa-1,-1,-1): #alfa
            lgs.append(axarr[maxRanges-1-j].fill_between(coverage, partialSum, color=cm(i/(maxAlfa-1)), label=labelAlfa[i]))
            partialSum -= tempOmegaCov[:,i,j]*(tempEaRCov[:,i,j]-tempEaMCov[:,i,j])
    
    myLegends = [lgEaCov, lgEaCov2]
    myLabels = [r"$E_a^{Arrh}$", r"$E_a^{calc} = \sum_\alpha\omega_\alpha(E_\alpha^r+E_\alpha^M)$"]  
    myLegends += lgs
    myLegends += [lgErr]
    labelAlfa = [r"$\omega_0(E_0^r+E_0^M)$", r"$\omega_1(E_1^r+E_1^M)$",r"$\omega_2(E_2^r+E_2^M)$", r"$\omega_3(E_3^r+E_3^M)$"]
    for i in range(maxAlfa-1,-1,-1): #alfa
        myLabels.append(labelAlfa[i])
    myLabels.append("Relative error")
    plt.figlegend(myLegends, myLabels, loc=(0.67,0.55), prop={'size':9})
    plt.savefig("multiplicitiesOmegas.pdf", bbox_inches='tight')
