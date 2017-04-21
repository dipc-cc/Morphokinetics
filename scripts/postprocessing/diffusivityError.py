#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data0.txt
import info as inf
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker
import matplotlib.lines as mlines
from matplotlib.patches import Rectangle
from matplotlib.ticker import LogLocator
import glob
import os
import sys
import functions as fun
from scipy.signal import savgol_filter
from PIL import Image
    
def computeError(ax1=0, ax2=0, i=-1, t=150):
    p = inf.getInputParameters()
    d = inf.readAverages()

    cove = d.getRecomputedCoverage()/p.sizI/p.sizJ
    ratios = p.getRatios()
    Na = cove * p.sizI * p.sizJ

    x = list(range(0,len(d.time)))
    x = cove
    cm1 = plt.get_cmap("hsv")
    cm2 = plt.get_cmap("rainbow")
    alpha = 0.5
    mew = 0
    handles = []
    ax1.loglog(x, d.hops/d.diff, label=str(t)+" K",
               ls="-", color=cm1(i/20), lw=1)
    diff = fun.timeDerivative(d.diff, d.time)/(4*Na)
    hops = fun.timeDerivative(d.hops, d.time)/(4*Na)
    ax2.loglog(x, abs(hops/diff), label=str(t)+" K",
              ls="-", color=cm2(i/20), lw=1)

    #ax.grid()
    ax1.set_xlabel(r"$\theta$", size=16)
    ax2.set_xlabel(r"$\theta$", size=16)
    #ax.set_ylim([1e-2,1e13])
    ax1.set_xlim([1e-5,1e0])
    ax2.set_xlim([1e-5,1e0])
    #ax.set_yscale("linear")
    #ax.set_ylim(-1,5)
    ax1.legend(loc=(1.05,.0), numpoints=1, prop={'size':6}, markerscale=1, labelspacing=1, ncol=1, columnspacing=.7, borderpad=0.3)
    ax2.legend(loc=(1.05,.0), numpoints=1, prop={'size':6}, markerscale=1, labelspacing=1, ncol=1, columnspacing=.7, borderpad=0.3)
  
##########################################################
##########           Main function   #####################
##########################################################

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
f = "flux5e4"
#for f in fluxes:
if True:
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fig1, ax1 = plt.subplots(1, 1, sharey=True,figsize=(5,4))
    fig2, ax2 = plt.subplots(1, 1, sharey=True,figsize=(5,4))
    fPath = os.getcwd()
    for t in list(reversed(inf.getTemperatures()[14:-2])):
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            #diffusivityDistance(smooth, binned)
            fig1.subplots_adjust(top=0.95, bottom=0.08, wspace=0.08)
            computeError(ax1=ax1, ax2=ax2, i=i, t=t)
            i += 1
            fig1.savefig("../../../errorIntegrated.pdf", bbox_inches='tight')
            fig2.savefig("../../../errorDerivated.pdf", bbox_inches='tight')
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)

