import functions as fun
import info as inf
import numpy as np
import matplotlib
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
    p = inf.getInputParameters()
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


def putLabels(ax, calc, alfa):
    arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
    if calc == "AgUc":
        xI = 45
        xII = 94
        if alfa == -1:
            yMin = 1e1
            yMax = 1e6
        elif alfa == 0:
            yMin = 1e-1
            yMax = 1e5
    else:
        xI = 59
        xII = 77
        if alfa == -1:
            yMin = 1e1
            yMax = 1e5
        elif alfa == 0:
            yMin = 1e-1
            yMax = 1e5
        
    bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
    if alfa == -1:
        ax.set_ylabel(r"$gl^2 \; \frac{\langle N_h \rangle}{t}$")
        ax.annotate("(a)", xy=(-0.2, 0.93), xycoords="axes fraction")
        label = r"$\theta=0.30$"
        ax.annotate(label, xy=(0.75,0.85), xycoords="axes fraction",
                    bbox=bbox_props)
    elif alfa == 0:
        ax.set_ylabel(r"$\overline{\langle M_\alpha \rangle}$")
        ax.annotate("(a)", xy=(-0.2, 0.93), xycoords="axes fraction")
        label = r"$\theta=0.30$"
        ax.annotate(label, xy=(0.78,0.55), xycoords="axes fraction",
                    bbox=bbox_props, size=8)
    if alfa < 1:
        ax.annotate("", xy=(xI,yMin), xytext=(xI,yMax), arrowprops=arrow, ha="center", va="center")
        ax.annotate("", xy=(xII,yMin), xytext=(xII,yMax), arrowprops=arrow, ha="center", va="center")

        

def fitAndPlotLinear(x, y, rngt, ax, alfa, showPlot, labelAlfa, p):
    markers=["o", "s","D","^","d","h","p","o"]
    labelRange = ['low', 'med', 'high']
    labelRange = labelRange+list([str(i) for i in rngt])
    cm = plt.get_cmap('Set1')
    cm1 = plt.get_cmap('Set3')
    slopes = []
    if showPlot:
        inf.smallerFont(ax, 8)
        ax.scatter(x, y, color=cm(abs(alfa/9)), alpha=0.75, edgecolors='none', marker=markers[alfa])#, "o", lw=0.5)
        arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
        putLabels(ax, p.calc, alfa)
    for i in range(0,len(rngt)-1):
        a, b = fun.linearFit(x, y, rngt[i], rngt[i+1])
        slopes.append(b)
        if showPlot:
            ax.semilogy(x[rngt[i]:rngt[i+1]+1], np.exp(fun.linear(x[rngt[i]:rngt[i+1]+1], a, b)), ls="-", color=cm1((i+abs(alfa)*3)/12))
            xHalf = (x[rngt[i]]+x[rngt[i+1]]+1)/2
            text = "{:03.3f}".format(-b)
            yHalf = np.exp(fun.linear(xHalf, a, b))
            if alfa == -1:
                ax.text(xHalf, 2e1, r"$"+roman.toRoman(i+1)+r"$", color="gray", ha="right", va="center")#, transform=axarr[i].transAxes)
                xHalf *= 1.15
                yHalf *= 5
                text = r"$E_a^{Arrh}="+text+r"$"

            bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.6)
            ax.text(xHalf,yHalf, text, color=cm(abs(alfa/9)), bbox=bbox_props, ha="center", va="center", size=8)
    if showPlot and alfa > -1:
        locator = LogLocator(100,[1e-1])
        ax.yaxis.set_major_locator(locator)
    return slopes

def plotOmegas(x, y, axis, i, averageLines):
    inf.smallerFont(axis, 8)
    markers=["o", "s","D","^","d","h","p","o"]
    newax = fig.add_axes([0.43, 0.15, 0.25, 0.25])
    newax.scatter(x, y, color=cm(abs(i/9)), alpha=0.75, edgecolors='none', label=labelAlfa[i], marker=markers[i])
    newax.set_ylim(-0.05,1.05)
    loc = plticker.MultipleLocator(40.0) # this locator puts ticks at regular intervals
    newax.xaxis.set_major_locator(loc)
    loc = plticker.MultipleLocator(1/3) # this locator puts ticks at regular intervals
    newax.yaxis.set_major_locator(loc)
    newax.yaxis.set_major_formatter(plticker.FixedFormatter(("0", "$0$", r"$\frac{1}{3}$", r"$\frac{2}{3}$", "$1$")))
    inf.smallerFont(newax,8)
    newax.set_xlim(xmin,xmax)
    lg = newax.legend(prop={'size': 7}, loc=(0.5,0.13), scatterpoints=1)
    newax.add_artist(lg)
    newax.legend(prop={'size': 7}, loc=(0.5,1.55), scatterpoints=1)
    axis.semilogy(x, y, ls="",color=cm(abs(i/9)), label=labelAlfa[i], marker=markers[i], mec='none',alpha=0.75)
    
    for j in range(0,3):
        axis.semilogy(x[rngt[j]:rngt[j+1]], fun.constant(x[rngt[j]:rngt[j+1]], averageLines[j]), color=cm(abs(i/9)))
    axis.set_ylim(2e-4,2)
    axis.set_ylabel(r"$\omega_\alpha$")
    axis.set_xlabel(r"$1/k_BT$")
    arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
    if i == 0: # range separation lines
        axis.annotate("", xy=(45,2e-4), xytext=(45,2), arrowprops=arrow)
        axis.annotate("", xy=(94,2e-4), xytext=(94,2), arrowprops=arrow)
        axis.annotate("(b)", xy=(-0.2, 0.93), xycoords="axes fraction")

    
temperatures = inf.getTemperatures()
p = inf.getInputParameters(glob.glob("*/output*")[0])
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
    tempMavg = inf.readAe("tempMavg.txt")
    tempOavg = inf.readAe("tempOavg.txt")
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
    inf.writeAe("tempMavg.txt", tempMavg)
    inf.writeAe("tempOavg.txt", tempOavg)
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
        xmin = 45
        xmax = 120
        energies = [0.2, 0.35, 0.36, 0.435, 0.45, 0.535]
        labelAlfa = [r"$\alpha=0$", r"$\alpha=a$", r"$\alpha=1$", r"$\alpha=b$", r"$\alpha=c$", r"$\alpha=2$"]
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
rngt = inf.defineRanges(calculationMode, p.rLib, temperatures)

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
        fig, axarr = plt.subplots(2, sharex=True, figsize=(5,4))
        fig.subplots_adjust(right=0.7, hspace=0.1)
        plt.xlim(xmin,xmax)
    else:
        axarr = np.zeros(3)
    # N_h
    tempEaCov.append(fitAndPlotLinear(x, y[:,cov], rngt, axarr[0], -1, False, labelAlfa, p))
    tempOmega = np.zeros((maxAlfa,maxRanges))
    tempEaM = []
    for i in range(0,maxAlfa): # alfa
        y = np.sum(tempMavg[:,cov,ind[2*i]:ind[2*i+1]], axis=1)
        if p.calc == "basic" and p.rLib == "version2" and i == 1:
            y += tempMavg[:,cov,11]
        if p.calc == "graphene" and i == 1:
            y += np.sum(tempMavg[:,cov,9:11], axis=1)
        
        tempEaM.append(fitAndPlotLinear(x, y, rngt, axarr[0], i, showPlot, labelAlfa, p))
        for j in range(0,maxRanges): # temperature ranges
            tempOmega[i][j] = np.exp(np.mean(np.log(np.sum(tempOavg[rngt[j]:rngt[j+1],cov,ind[2*i]:ind[2*i+1]], axis=1))))
        if showPlot:
            y = np.sum(tempOavg[:,cov,ind[2*i]:ind[2*i+1]], axis=1)
            if p.calc == "basic" and p.rLib == "version2" and i == 1:
                y += tempOavg[:,cov,11]
            if p.calc == "graphene" and i == 1:
                y += np.sum(tempOavg[:,cov,9:11], axis=1)
            plotOmegas(x, y, axarr[-1], i, tempOmega[i])
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
    ax2 = axarr[i].twinx()
    lgErr, = ax2.plot(coverage, abs(1-tempEaCov2[:,maxRanges-1-i]/tempEaCov[:,maxRanges-1-i]),lw=5, ls="dotted", solid_capstyle="round", color=cm(3/4), label="relative error")
    ax2.set_ylim(0,0.5)
    maxY = max(abs(1-tempEaCov2[:,maxRanges-1-i]/tempEaCov[:,maxRanges-1-i])[30:])+0.015 # get maximum for the arrow (>30% coverage)
    ax2.annotate(' ', xy=(.6, maxY), xytext=(.2, maxY-1e-2), arrowprops=dict(arrowstyle="->", connectionstyle="angle3", edgecolor=cm(3/4), facecolor=cm(3/4)))
    maxYlabel = "{:03.2f}%".format(maxY*100)
    bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.8)
    ax2.text(0.65, maxY, maxYlabel, bbox=bbox_props, color=cm(3/4))
    if i != 2:
        ax2.yaxis.set_major_formatter(plticker.NullFormatter())
    else:
        ax2.set_ylabel("Relative error")

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
    myLabels = [r"$E_a^{Arrh}$", r"$\sum_\alpha \;\epsilon_\alpha$"]
    myLegends += lgs
    myLegends += [lgErr]
    if p.calc == "AgUc":
        labelAlfa = [r"$\epsilon_0$", r"$\epsilon_1$", r"$\epsilon_2$", r"$\epsilon_3$"]
    else:
        labelAlfa = [r"$\epsilon_0$", r"$\epsilon_a$",r"$\epsilon_1$", r"$\epsilon_b$",r"$\epsilon_c$", r"$\epsilon_2$"]
        
    for i in range(maxAlfa-1,-1,-1): #alfa
        myLabels.append(labelAlfa[i])
    myLabels.append("Rel. err.")
    plt.figlegend(myLegends, myLabels, loc=(0.68,0.5), prop={'size':11})
    plt.savefig("multiplicitiesOmegas.pdf", bbox_inches='tight')
