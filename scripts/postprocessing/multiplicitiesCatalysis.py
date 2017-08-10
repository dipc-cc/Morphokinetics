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
import roman


def computeMavgAndOmega(fileNumber, p):
    possiblesFromList = np.loadtxt(fname="dataAePossibleFromList"+"{:03d}".format(fileNumber)+".txt")
    time = np.array(possiblesFromList[:,0])
    possiblesFromList = possiblesFromList[:,1:] # remove time
    length = len(time)
    Mavg = np.zeros(shape=(length,p.maxA))
    for i in range(0,p.maxA): # iterate alfa
        Mavg[:,i] = possiblesFromList[:,i]/time
    ratios = p.getRatios()
    ##matrix = np.loadtxt(fname="data"+str(fileNumber)+".txt", delimiter="\t")
    ##co2amount = matrix[:,0]
    ##hops = np.array(matrix[:,15])/(4*co2amount*p.sizI*p.sizJ) # scale all data
    avgTotalHopRate2 = np.array(ratios.dot(np.transpose(Mavg)))
    ##avgTotalHopRate3 = hops/time
    ##avgTotalHopRate1 = matrix[:,12]/(4*co2amount*p.sizI*p.sizJ)/time # diffusivity
    # define omegas AgUc
    omega = np.zeros(shape=(length,p.maxA)) # [co2amount, alfa]
    for i in range(0,length):
        omega[i,:] =  Mavg[i,:] * ratios / avgTotalHopRate2[i]
    np.shape(omega)
    avgTotalHopRate1 = avgTotalHopRate3 = avgTotalHopRate2
    return Mavg, omega, avgTotalHopRate1, avgTotalHopRate2, avgTotalHopRate3


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
    for i in range(0,filesNumber-1):
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

def putLabels(ax, co2, alfa):
    arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
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
        ax.set_ylabel(r"$CO_2$ Molecules/s");
        ax.annotate("(a)", xy=(-0.2, 0.93), xycoords="axes fraction")
        label = r"$CO_2="+str(co2*10)+r"$"
        ax.annotate(label, xy=(0.75,0.85), xycoords="axes fraction",
                    bbox=bbox_props)
    elif alfa == 0:
         ax.set_ylabel(r"$\overline{\langle M_\alpha \rangle}$", size=8)
         ax.annotate("(a)", xy=(-0.2, 0.93), xycoords="axes fraction", size=8)
         label = r"$CO_2="+str(co2*10)+r"$"
         ax.annotate(label, xy=(0.78,0.55), xycoords="axes fraction",
                     bbox=bbox_props, size=8)
    # if alfa < 1:
    #     ax.annotate("", xy=(xI,yMin), xytext=(xI,yMax), arrowprops=arrow, ha="center", va="center")
    #     ax.annotate("", xy=(xII,yMin), xytext=(xII,yMax), arrowprops=arrow, ha="center", va="center")



def plotOmegas(x, y, axis, i, averageLines):
    inf.smallerFont(axis, 8)
    markers=["o", "s","D","^","d","h","p","o"]
    # #newax = fig.add_axes([0.43, 0.15, 0.25, 0.25])
    # newax.scatter(x, y, color=cm(abs(i/9)), alpha=0.75, edgecolors='none', label=labelAlfa[i], marker=markers[i])
    # newax.set_ylim(-0.05,1.05)
    # loc = plticker.MultipleLocator(40.0) # this locator puts ticks at regular intervals
    # newax.xaxis.set_major_locator(loc)
    # loc = plticker.MultipleLocator(1/3) # this locator puts ticks at regular intervals
    # newax.yaxis.set_major_locator(loc)
    # newax.yaxis.set_major_formatter(plticker.FixedFormatter(("0", "$0$", r"$\frac{1}{3}$", r"$\frac{2}{3}$", "$1$")))
    # inf.smallerFont(newax,8)
    #newax.set_xlim(xmin,xmax)
    # lg = newax.legend(prop={'size': 7}, loc=(0.5,0.13), scatterpoints=1)
    # newax.add_artist(lg)
    # newax.legend(prop={'size': 7}, loc=(0.5,1.55), scatterpoints=1)
    axis.semilogy(x, y, ls="",color=cm(abs(i/9)), label=labelAlfa[i], marker=markers[i], mec='none',alpha=0.75)
    
    for j in range(0,3):
        axis.semilogy(x[rngt[j]:rngt[j+1]], fun.constant(x[rngt[j]:rngt[j+1]], averageLines[j]), color=cm(abs(i/9)))
    axis.set_ylim(2e-4,2)
    axis.set_ylabel(r"$\omega_\alpha$", size=8)
    axis.set_xlabel(r"$1/k_BT$", size=8)
    arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
    axis.legend(prop={'size': 7}, loc=(1.1,1.55), scatterpoints=1)
    if i == 0: # range separation lines
        axis.annotate("", xy=(45,2e-4), xytext=(45,2), arrowprops=arrow)
        axis.annotate("", xy=(94,2e-4), xytext=(94,2), arrowprops=arrow)
        axis.annotate("(b)", xy=(-0.2, 0.93), xycoords="axes fraction", size=8)


def fitAndPlotLinear(x, y, rngt, ax, alfa, showPlot, labelAlfa, co2):
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
        putLabels(ax, co2, alfa)
    for i in range(0,len(rngt)-1):
        #print(rngt[i], rngt[i+1], x, y, len(x), len(y))
        a, b = fun.linearFit(x, y, rngt[i], rngt[i+1])
        slopes.append(b)
        if showPlot:
            ax.semilogy(x[rngt[i]:rngt[i+1]+1], np.exp(fun.linear(x[rngt[i]:rngt[i+1]+1], a, b)), ls="-", color=cm1((i+abs(alfa)*3)/12))
            xHalf = (x[rngt[i]]+x[rngt[i]])/2 # (x[rngt[i]]+x[rngt[i+1]+1])/2
            text = "{:03.3f}".format(-b)
            yHalf = np.exp(fun.linear(xHalf, a, b))
            if alfa == -1:
                ax.text(xHalf, 2e1, r"$"+roman.toRoman(i+1)+r"$", color="gray", ha="right", va="center")#, transform=axarr[i].transAxes)
                xHalf *= 1
                yHalf *= 5
                text = r"$E_a="+text+r"$"

            bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.6)
            ax.text(xHalf,yHalf, text, color=cm(abs(alfa/9)), bbox=bbox_props, ha="center", va="center", size=6)
    if showPlot and alfa > -1:
        locator = LogLocator(100,[1e-1])
        ax.yaxis.set_major_locator(locator)
    return slopes


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
    tempEaCo2.append(fitAndPlotLinear(x, y[:,co2], rngt, axarr[0], -1, showPlot, labelAlfa, co2))
    tempOmega = np.zeros((maxAlfa,maxRanges))
    tempEaM = []
    y2 = tempR1avg/tempR3avg
    tempEafCo2.append(fitAndPlotLinear(x, y2[:,co2], rngt, axarr[0], -2, False, labelAlfa, co2))
    
    for i in range(0,maxAlfa): # alfa
        y = np.sum(tempMavg[:,co2,ind[2*i]:ind[2*i+1]], axis=1)
        tempEaM.append(fitAndPlotLinear(x, y, rngt, axarr[1], i, showPlot, labelAlfa, co2))
        for j in range(0,maxRanges): # temperature ranges
            tempOmega[i][j] = np.exp(np.mean(np.log(np.sum(tempOavg[rngt[j]:rngt[j+1],co2,ind[2*i]:ind[2*i+1]], axis=1))))
        if showPlot:
            y = np.sum(tempOavg[:,co2,ind[2*i]:ind[2*i+1]], axis=1)
            if p.calc == "basic" and p.rLib == "version2" and i == 1:
                y += tempOavg[:,co2,11]
            if p.calc == "graphene" and i == 1:
                y += np.sum(tempOavg[:,co2,9:11], axis=1)
            plotOmegas(x, y, axarr[-1], i, tempOmega[i])
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

cm = plt.get_cmap('gist_earth')
ax = []
axarr[0].set_ylabel("eV")
co2 = list(range(0,100))
for i in range(0,maxRanges): # different temperature ranges (low, medium, high)
    axarr[i].text(0.5, 0.95, r"$"+roman.toRoman(maxRanges-i)+r"$", color="gray", transform=axarr[i].transAxes)
    axarr[i].set_xlabel(r"$CO_2\cdot10$")
    lgEaCov2, = axarr[i].plot(co2, tempEaCov2[:,maxRanges-1-i], ls="dashed", solid_capstyle="round", lw=5, label="Recomputed AE", alpha=0.6, color=cm(1/3))
    lgEaCov, = axarr[i].plot(co2, tempEaCo2[:,maxRanges-1-i], "-",  solid_capstyle="round", lw=5, label="Activation energy", alpha=0.6, color=cm(2/3))
    ax2 = axarr[i].twinx()
    lgErr, = ax2.plot(co2, abs(1-tempEaCov2[:,maxRanges-1-i]/tempEaCo2[:,maxRanges-1-i]),lw=5, ls="dotted", solid_capstyle="round", color=cm(3/4), label="relative error")
    ax2.set_ylim(0,1)
    maxY = max(abs(1-tempEaCov2[:,maxRanges-1-i]/tempEaCo2[:,maxRanges-1-i])[30:])+0.015 # get maximum for the arrow (>30% co2)
    ax2.annotate(' ', xy=(.6, maxY), xytext=(.2, maxY-1e-2), arrowprops=dict(arrowstyle="->", connectionstyle="angle3", edgecolor=cm(3/4), facecolor=cm(3/4)))
    maxYlabel = "{:03.2f}%".format(maxY*100)
    bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.8)
    ax2.text(0.65, maxY, maxYlabel, bbox=bbox_props)
    if i != maxRanges-1:
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
plt.savefig("multiplicities.png", bbox_inches='tight')

if (rAndM): # plot total activation energy as the sum of ratios and multiplicities
    label = ["multiplicity", "sum", "ratio"]
    cm = plt.get_cmap('Accent')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        partialSum1 = np.sum(tempOmegaCo2[:,:,j]*(-tempEaMCo2[:,:,j]), axis=1)
        partialSum2 = np.sum(tempOmegaCo2[:,:,j]*(tempEaRCo2[:,:,j]), axis=1)
        rev = np.sum(partialSum1) < 0
        partialSum = partialSum1 + partialSum2
        c = 0
        lgR = []
        if rev:
            lgSum = axarr[maxRanges-1-j].fill_between(co2, partialSum2, color=cm(c/3), alpha=0.8, label=label[c])
            c += 1
        for i in range(0,2):
            if rev:
                lg = axarr[maxRanges-1-j].fill_between(co2,partialSum1, color=cm((c+i)/3), alpha=0.8, label=label[i])
                lgR.append(lg)
                partialSum1 = partialSum1 + partialSum2
                
            else:
                lg = axarr[maxRanges-1-j].fill_between(co2, partialSum, color=cm((c+i)/3), alpha=0.8, label=label[i])
                lgR.append(lg)
                partialSum -= partialSum1
    plt.figlegend((lgEaCov, lgEaCov2, lgErr, lgR[0], lgR[1], lgSum),("Activation energy", "Recomputed AE", "Error", "R", "sum", "M"), loc=(0.7,0.7), prop={'size':8})
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
    
    myLegends = [lgEaCov, lgEaCov2]
    myLabels = [r"$E_a$", r"$E^f + \sum_\alpha \;\epsilon_\alpha$"]
    myLegends += lgs
    myLegends += [lgErr]
        
    for i in range(maxAlfa-1,-1,-1): #alfa
        myLabels.append(labelAlfa[i])
    myLabels.append("Rel. err.")
    plt.figlegend(myLegends, myLabels, loc=(0.68,0.15), prop={'size':11})
    plt.savefig("multiplicitiesOmegasP.png", bbox_inches='tight')
