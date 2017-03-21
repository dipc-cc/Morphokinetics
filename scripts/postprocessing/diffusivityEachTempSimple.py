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
        newax = fig.add_axes([0.15, 0.4, 0.3, 0.3], anchor='NE', zorder=+100)
        newax.imshow(im)
        newax.yaxis.set_major_locator(plticker.NullLocator())
        newax.xaxis.set_major_locator(plticker.NullLocator())
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.9)
        newax.annotate(r"$\theta = 30\%$", xy=[200,100], bbox=bbox_props)
        #newax.axis('off')
    except IndexError:
        pass
    
def diffusivityDistance(debug, smooth, smoothCalc, binned):
    p = inf.getInputParameters()
    if binned:
        d = inf.readBinnedAverages()
    else:
        d = inf.readAverages()

    cove = d.getRecomputedCoverage()/p.sizI/p.sizJ
    ratios = p.getRatios()
    Na = cove * p.sizI * p.sizJ

    plt.clf()
    fig, ax = plt.subplots()
    x = list(range(0,len(d.time)))
    x = cove
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    diff = fun.timeDerivative(d.diff, d.time)/(4*Na)
    handles = []
    lgR, = plt.loglog(x, diff, label=r"$\frac{1}{2dN_a} \; \frac{d(R^2)}{dt}$",
               marker="s", ls="", mew=mew, markerfacecolor=cm(0/8), ms=8, alpha=alpha)
    hops = fun.timeDerivative(d.hops, d.time)/(4*Na)
    lgN, = plt.loglog(x, hops, label=r"$\frac{l^2}{2dN_a} \; \frac{d(N_h)}{dt}$",
               marker="p", ls="", mew=mew, markerfacecolor=cm(7/8), ms=7, alpha=alpha)

    k=0
    label = r"$n_"+str(k)+"$"
    lg, = plt.loglog(x, d.negs[k]/p.sizI/p.sizJ, label=label, ms=1, lw=2, marker=".", color=cm(1/8))
    if smooth:
        ySmooth = np.exp(savgol_filter(np.log(d.negs[k]), 9, 1))
        plt.loglog(x, ySmooth/p.sizI/p.sizJ, lw=2)
        d.negs[k] = ySmooth
    handles.append(lg)
    islD = inf.readHistograms()
    isld = islD.islB2()
    isld = d.isld
    lg, = plt.loglog(x, isld/p.sizI/p.sizJ, ls="--", lw=2, color=cm(6/8), label=r"$N_{isl}$", markerfacecolor="None")
    handles.append(lg)

    handles = [lgR, lgN] + handles
    plt.subplots_adjust(left=0.12, bottom=0.1, right=0.7, top=0.9, wspace=0.2, hspace=0.2)
    plt.legend(handles=handles, numpoints=1, prop={'size':12}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    plt.grid()
    plt.xlabel(r"$\theta$")
    plt.title("flux: {:.1e} temperature: {:d} size {:d}x{:d} \n {}".format(p.flux, int(p.temp), p.sizI, p.sizJ, os.getcwd()), fontsize=10)
    addSurface(fig, p.temp)
    plt.savefig("../../../plot"+str(p.flux)+str(p.temp)+".png")


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
    for t in inf.getTemperatures()[15:]:
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            diffusivityDistance(debug, smooth, smoothCalc, binned)
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)
plt.legend()

