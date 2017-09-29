import sys
import info as inf
import energies as e
import matplotlib.pyplot as plt
from matplotlib.ticker import LogLocator
from matplotlib.ticker import FixedFormatter
import matplotlib.ticker as plticker
from matplotlib.font_manager import FontProperties
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
         ax.set_ylabel(r"$\overline{\langle M_\alpha \rangle}$")
         #ax.annotate("(a)", xy=(-0.13, 0.93), xycoords="axes fraction", size=8)
         #label = r"$CO_2="+str(co2*10)+r"$"
         #ax.annotate(label, xy=(0.97,0.95), xycoords="axes fraction",
         #            bbox=bbox_props, size=8, horizontalalignment='right', verticalalignment='top')
    # if alfa < 1:
    #     ax.annotate("", xy=(xI,yMin), xytext=(xI,yMax), arrowprops=arrow, ha="center", va="center")
    #     ax.annotate("", xy=(xII,yMin), xytext=(xII,yMax), arrowprops=arrow, ha="center", va="center")



def plotOmegas(x, y, axis, i, averageLines, rngt, labelAlfa):
    inf.smallerFont(axis, 8)
    cm = plt.get_cmap('tab20')
    markers=["o", "s","D","^","d","h","p"]
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
    axis.semilogy(x, y, ls="",color=cm(abs(i/20)), label=labelAlfa[i], marker=markers[i%7], mec='none',alpha=0.75)

    #for j in range(0,len(rngt)-1):
    #    axis.semilogy(x[rngt[j]:rngt[j+1]], fun.constant(x[rngt[j]:rngt[j+1]], averageLines[j]), color=cm(abs(i/9)))
    axis.set_ylim(1e-4,2)
    axis.set_ylabel(r"$\omega_\alpha$")
    axis.set_xlabel(r"$1/k_BT$")
    arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
    axis.legend(prop={'size': 5}, loc="best", scatterpoints=1)
    if i == 0: # range separation lines
        axis.annotate("", xy=(45,2e-4), xytext=(45,2), arrowprops=arrow)
        axis.annotate("", xy=(94,2e-4), xytext=(94,2), arrowprops=arrow)


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


def localAvgAndPlotLinear(x, y, ax, alfa, sp, co2, first=False):
    showPlot = sp# and (alfa == 4 or alfa == 5)
    markers=["o", "s","D","^","d","h","p"]
    cm = plt.get_cmap('tab20')
    cm1 = plt.get_cmap('hsv')
    slopes = []
    l = 1
    if showPlot:
        #inf.smallerFont(ax, 8)
        ax.scatter(x, y, color=cm(abs(alfa/20)), alpha=0.75, edgecolors='none', marker=markers[alfa%7])#, "o", lw=0.5)
        arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
        a = alfa
        if first:
            a = 0
        putLabels(ax, co2+1, a)
    # all
    s = allSlopes(x,y)
    if any(np.isinf(s)) or any(np.isnan(s)):
        print("error fitting",alfa)
        
    for i in range(0,len(x)):
        a, b = fitA(x,y,s,i)
        slopes.append(b)
        if showPlot:
            ax.set_yscale("log")
            ax.plot(x[i-1:i+2], np.exp(fun.linear(x[i-1:i+2], a, b)), ls="-", color=cm1(i/30), alpha=0)
            xHalf = x[i]
            text = "{:03.3f}".format(-b)
            yHalf = np.exp(fun.linear(xHalf, a, b))
            if alfa == -1:
                xHalf *= 1
                yHalf *= 5
                text = r"$E_a="+text+r"$"

            bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.6)
            if alfa == -10:
                ax.text(xHalf,yHalf, text, color=cm(abs(alfa/9)), bbox=bbox_props, ha="center", va="center", size=6)
    return slopes


def allSlopes(x,y):
    return fun.timeDerivative(np.log(y),x)


def fitA(x,y,slopes, i):
    b = slopes[i]
    if np.isinf(b) or np.isnan(b):
        b = 0
        a = 0
    else:
        a = np.log(y[i])-b*x[i]
    return a,b


def plotSimple(x, targt, rcmpt, error, ax, maxRanges, i, legend):
    #print(maxRanges-i, targt[-1],"\t", rcmpt[-1], "\t", error[-1],"\t", abs(targt[-1]-rcmpt[-1]))
    cm = plt.get_cmap('gist_earth')
    ax.text(0.5, 0.95, r"$"+roman.toRoman(maxRanges-i)+r"$", color="gray", transform=ax.transAxes)
    ax.set_xlabel(r"$CO_2\cdot10$")
    lgRcmpt, = ax.plot(x, rcmpt,
                       ls="dashed", solid_capstyle="round", lw=5,
                       alpha=0.6, color=cm(1/3),
                       label="Recomputed AE")
    lgTargt, = ax.plot(x, targt,
                       "-",  solid_capstyle="round", lw=5,
                       alpha=0.6, color=cm(2/3),
                       label="Activation energy")
    ax2 = ax.twinx()
    lgError, = ax2.plot(x, error,
                        ls="dotted", solid_capstyle="round", lw=5,
                        color=cm(3/4),
                        label="relative error")
    #ax.set_ylim(0,5)
    ax2.set_ylim(0,1)
    maxY = max(error[30:])+0.015 # get maximum for the arrow (>30% co2)
    if maxY > 1:
        maxY = 1
    ax2.annotate(' ', xy=(.6, maxY), xytext=(.2, maxY-1e-2), arrowprops=dict(arrowstyle="->", connectionstyle="angle3", edgecolor=cm(3/4), facecolor=cm(3/4)))
    maxYlabel = "{:03.2f}%".format(maxY*100)
    if i != maxRanges-1:
        ax2.yaxis.set_major_formatter(plticker.NullFormatter())
    else:
        ax2.set_ylabel("Relative error")

    # Annotate last energies and absolute error
    font = FontProperties()
    font.set_size(6)
    label = "{:03.4f}".format(rcmpt[-1])
    bbox_props = dict(boxstyle="round", fc=cm(1/3), ec=cm(1/3), alpha=0.7)
    ax.text(30,rcmpt[-1]+1,label, bbox=bbox_props, fontproperties=font)
    label = "{:03.4f}".format(targt[-1])
    bbox_props = dict(boxstyle="round", fc=cm(2/3), ec=cm(2/3), alpha=0.7)
    ax.text(30,targt[-1]-1,label, bbox=bbox_props, fontproperties=font)
    label = "{:03.4f}".format(abs(rcmpt[-1]-targt[-1]))
    bbox_props = dict(boxstyle="round", fc=cm(3/3), ec=cm(3/3), alpha=0.7)
    ax.text(30,targt[-1]-0.5,label, bbox=bbox_props, fontproperties=font)
    bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.8)
    ax2.text(0.65, maxY, maxYlabel, bbox=bbox_props)

    handles = [lgTargt, lgRcmpt, lgError]
    if legend and i == maxRanges-1:
        ax.legend(handles=handles, loc="upper right", prop={'size':8})
    return handles
        
def plotRandM(x, sum1, sum2, ax, handles, legend):
    label = ["M (multiplicity)", " R (ratio)", "sum"]
    cm = plt.get_cmap('Accent')
    rev = np.sum(sum1) < 0
    partialSum = sum1 + sum2
    c = 0
    lgR = []
    if rev:
        lg = ax.fill_between(x, sum2, color=cm(c/3), alpha=0.8, label=label[c])
        lgR.append(lg)
        c += 1
    for i in range(0,2):
        if rev:
            lg = ax.fill_between(x, sum1, color=cm((c+i)/3), alpha=0.8, label=label[c+i])
            lgR.append(lg)
            sum1 = sum1 + sum2
        else:
            lg = ax.fill_between(x, partialSum, color=cm((c+i)/3), alpha=0.8, label=label[c+i])
            lgR.append(lg)
            partialSum -= sum1
    if legend:
        handles = handles + lgR
        ax.legend(handles=handles, loc=(0.3,0.7), prop={'size':8})
        
def plotOmega(x, y, ax, maxRanges, i):
    cm = plt.get_cmap('Set2')
    ax.fill_between(co2, partialSum, color=cm(i/(maxAlfa-1)), label=labelAlfa[i])

def plotSensibility(sensibilityCo2,temperatures,labelAlfa,total=False):
    maxAlfa = 20
    fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,4))
    fig.subplots_adjust(wspace=0.1)
    cm = plt.get_cmap('tab20')
    markers=["o", "s","D","^","d","h","p","o"]
    for i in range(0,maxAlfa):
        if any(abs(sensibilityCo2[-1,:,i]) > 0.05):
            axarr.plot(1000/temperatures, sensibilityCo2[-1,:,i], label=labelAlfa[i],color=cm(abs(i/20)), marker=markers[i%8] )
    #axarr.set_ylim(-0.5,0.5)
    #axarr.set_yscale("log")
    axarr.legend(loc=(1.10,0.0), prop={'size':6})
    name = "sensibility"
    if total:
        name += "T"
    fig.savefig(name+".svg", bbox_inches='tight')

def plotKindOfSensibility(x,y,label,name):
    markers=["o", "s","D","^","d","h","p"]
    cm = plt.get_cmap('tab20')
    fig, axarr = plt.subplots(2, 2, sharex=True, sharey=True, figsize=(10,8))
    fig.subplots_adjust(wspace=0.1)

    i=0
    axarr[0][0].plot(x, y[:,0],label=label[i],color=cm(abs(i/20)), marker=markers[i%8])  #adsorption
    i=1
    axarr[0][0].plot(x, y[:,1],label=label[i],color=cm(abs(i/20)), marker=markers[i%8])  #adsorption
    axarr[0][0].set_title("Adsorption")
    axarr[0][0].legend(loc="best", prop={'size':6})

    print(np.shape(y))
    # desorption
    for i in range(6,12):
        axarr[0][1].set_title("Desorption")
        axarr[0][1].plot(x, y[:,i],label=label[i],color=cm(abs(i/20)), marker=markers[i%7])
        axarr[0][1].legend(loc="best", prop={'size':6})
    # reaction
    for i in range(0,4):
        axarr[1][0].set_title("Reaction")
        axarr[1][0].plot(x, y[:,i],label=label[i],color=cm(abs(i/20)), marker=markers[i%7])
        axarr[1][0].legend(loc="best", prop={'size':6})
    # diffusion
    for i in range(12,20):
        axarr[1][1].set_title("Diffusion")
        axarr[1][1].plot(x, y[:,i],label=label[i],color=cm(abs(i/20)), marker=markers[i%7])
        axarr[1][1].legend(loc="best", prop={'size':6})
    fig.savefig(name+".svg")
