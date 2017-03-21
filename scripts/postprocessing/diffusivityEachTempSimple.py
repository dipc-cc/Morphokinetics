#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data0.txt
import info as inf
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker
import glob
import os
import sys
import functions as fun
from scipy.signal import savgol_filter
from PIL import Image

def addSurface(fig, temperature):
    try:
        fileName = glob.glob("/home/jalberdi004/mk_test/activationEnergy/agUc/200/2.-surfaces/flux3.5e4/"+str(int(temperature))+"/results/*/surface000.png")[0]
        im = Image.open(fileName)
        newax = fig.add_axes([0.16, 0.3, 0.3, 0.3], zorder=+100)
        newax.imshow(im)
        newax.yaxis.set_major_locator(plticker.NullLocator())
        newax.xaxis.set_major_locator(plticker.NullLocator())
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.9)
        newax.annotate(r"$\theta = 30\%$", xy=[200,100], bbox=bbox_props)
    except IndexError:
        pass

def addFreeDiffusivity(fig, x, p):
    ax = fig.gca()
    sublabel = {150: "a)", 250: "b)", 750: "c)"}
    try:
        note = sublabel[p.temp]+" T={}, F={:1.0E}".format(int(p.temp),p.flux)
        ax.annotate(note, xy=(0.16,0.68), xycoords="figure fraction", size=14)
    except KeyError:
        pass
    x = x[0:20]
    y = np.ones(len(x))
    y = y * 3/2*p.getRatios()[0]
    cm = plt.get_cmap("Accent")
    ax.plot(x, y, "-", color=cm(8/8))
    ax.annotate(r"$\frac{1}{2\alpha}m_{tt}\nu_{tt}l^2 = \frac{3}{2}\nu_{tt}$", xytext=(2e-2,4e11), textcoords="data",
                xy=(x[-1],y[-1]), xycoords='data', arrowprops=dict(arrowstyle="->", connectionstyle="arc3", color=cm(8/8)))
    
def diffusivityDistance(debug, smooth, smoothCalc, binned):
    p = inf.getInputParameters()
    if binned:
        d = inf.readBinnedAverages()
    else:
        d = inf.readAverages()

    cove = d.getRecomputedCoverage()/p.sizI/p.sizJ
    ratios = p.getRatios()
    Na = cove * p.sizI * p.sizJ

    fig = plt.figure(num=None, figsize=(5,7))
    ax = plt.gca()
    x = list(range(0,len(d.time)))
    x = cove
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    diff = fun.timeDerivative(d.diff, d.time)/(4*Na)
    handles = []
    lgR, = ax.loglog(x, diff, label=r"$\frac{1}{2dN_a} \; \frac{d(R^2)}{dt}$",
               marker="s", ls="", mew=mew, markerfacecolor=cm(0/8), ms=8, alpha=alpha)
    hops = fun.timeDerivative(d.hops, d.time)/(4*Na)
    lgN, = ax.loglog(x, hops, label=r"$\frac{l^2}{2dN_a} \; \frac{d(N_h)}{dt}$",
               marker="p", ls="", mew=mew, markerfacecolor=cm(7/8), ms=7, alpha=alpha)

    k=0
    label = r"$\theta_0$"
    lg, = ax.loglog(x, d.negs[k]/p.sizI/p.sizJ, label=label, ms=1, lw=2, marker=".", color=cm(1/8))
    if smooth:
        ySmooth = np.exp(savgol_filter(np.log(d.negs[k]), 9, 1))
        ax.loglog(x, ySmooth/p.sizI/p.sizJ, lw=2)
        d.negs[k] = ySmooth
    handles.append(lg)
    islD = inf.readHistograms()
    isld = islD.islB2()
    isld = d.isld
    lg, = ax.loglog(x, isld/p.sizI/p.sizJ, ls="--", lw=2, color=cm(6/8), label=r"$N_{isl}$", markerfacecolor="None")
    handles.append(lg)

    handles = [lgR, lgN] + handles
    #plt.subplots_adjust(left=0.12, bottom=0.1, right=0.7, top=0.9, wspace=0.2, hspace=0.2)
    ax.legend(handles=handles, loc=(0.46,0.3), numpoints=1, prop={'size':15})#, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    ax.grid()
    ax.set_xlabel(r"$\theta$", size=16)
    ax.set_ylim([1e-7,1e13])
    ax.set_xlim([1e-5,1e0])
    addFreeDiffusivity(fig, x, p)
    addSurface(fig, p.temp)
    fig.savefig("../../../plot"+str(p.flux)+str(p.temp)+".png")
    return fig


##########################################################
##########           Main function   #####################
##########################################################

try:
    debug = sys.argv[1] == "d"
except IndexError:
    debug = False
try:
    smooth = sys.argv[2] == "y"
except IndexError:
    smooth = False
try:
    smoothCalc = sys.argv[3] == "y"
except IndexError:
    smoothCalc = False
try:
    binned = sys.argv[4] == "y"
except IndexError:
    binned = False

workingPath = os.getcwd()
fluxes = inf.getFluxes()
for f in fluxes:
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    figs = []
    for t in inf.getTemperatures():
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            fig = diffusivityDistance(debug, smooth, smoothCalc, binned)
            if t == 150 or t == 250 or t == 750:
                fig.savefig("../../../p"+str(t)+".pdf")
                figs.append(fig)
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)

fig = plt.figure(num=None, figsize=(15,7))
#fig1, axarr = plt.subplots(1, 3, sharey=True,figsize=(15,7))
for f,i in enumerate(figs):
    #axarr[i] = f
    fig.axes.append(f.gca())
    
fig.savefig("figure.pdf")
