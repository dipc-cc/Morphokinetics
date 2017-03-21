#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data0.txt
import info as inf
import numpy as np
import matplotlib.pyplot as plt
import glob
import os
import sys
import functions as fun
from scipy.signal import savgol_filter


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

    #coverages
    cm1 = plt.get_cmap("Set1")

    k=0
    label = r"$n_"+str(k)+"$"
    lg, = plt.loglog(x, d.negs[k]/p.sizI/p.sizJ, label=label, ms=1,  marker=".", color=cm1((k+2)/9))
    if smooth:
        ySmooth = np.exp(savgol_filter(np.log(d.negs[k]), 9, 1))
        plt.loglog(x, ySmooth/p.sizI/p.sizJ, lw=2)
        d.negs[k] = ySmooth
    handles.append(lg)
    islD = inf.readHistograms()
    islB3 = islD.islB3()
    lg, = plt.loglog(x, fun.timeAverage(islB3, d.time)/p.sizI/p.sizJ, color=cm(3/12), label=r"$N_{isl}$", markerfacecolor="None")
    handles.append(lg)
        
    handles = [lgR, lgN] + handles
    plt.subplots_adjust(left=0.12, bottom=0.1, right=0.7, top=0.9, wspace=0.2, hspace=0.2)
    plt.legend(handles=handles, numpoints=1, prop={'size':8}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    plt.grid()
    plt.title("flux: {:.1e} temperature: {:d} size {:d}x{:d} \n {}".format(p.flux, int(p.temp), p.sizI, p.sizJ, os.getcwd()), fontsize=10)
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
    for t in inf.getTemperatures():
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            diffusivityDistance(debug, smooth, smoothCalc, binned)
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)
plt.legend()

