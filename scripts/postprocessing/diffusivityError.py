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

def setUpPlot(ax, p):
    ax.set_xlabel(r"$\theta$", size=16)
    ax.set_xlim([1e-5,1e0])
    ax.legend(loc=(1.05,.0), numpoints=1, prop={'size':6}, markerscale=1, labelspacing=1, ncol=1, columnspacing=.7, borderpad=0.3)
    if  p.temp == 950:
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.9)
        note = "$F=$".format(int(p.temp))+fun.base10(p.flux)+"$ML/s$\n${}".format(p.sizI)+r"\times{}$".format(p.sizJ)
        ax.annotate(note, xy=(0.06,0.8), xycoords="axes fraction", size=14, bbox=bbox_props)
    
def computeError(ax1=0, ax2=0, i=-1, t=150):
    p = inf.getInputParameters()
    d = inf.readAverages()

    cove = d.getRecomputedCoverage()/p.sizI/p.sizJ
    ratios = p.getRatios()
    Na = cove * p.sizI * p.sizJ

    x = list(range(0,len(d.time)))
    x = cove
    cm1 = plt.get_cmap("coolwarm_r")
    cm2 = plt.get_cmap("coolwarm_r")
    alpha = 0.5
    mew = 0
    handles = []
    ax1.loglog(x, d.hops/d.diff, label=str(t)+" K",
               ls="-", color=cm1(i/20), lw=1)
    diff = fun.timeDerivative(d.diff, d.time)/(4*Na)
    hops = fun.timeDerivative(d.hops, d.time)/(4*Na)
    ax2.loglog(x, abs(hops/diff), label=str(t)+" K",
              ls="-", color=cm2(i/20), lw=1)

    setUpPlot(ax1, p)
    ax1.set_ylabel(r"$\frac{l^2\langle N_h \rangle}{\langle R^2 \rangle}$")
    ax1.set_yscale("linear")
    ax1.set_ylim(0,5)
    setUpPlot(ax2, p)
    ax2.set_ylabel(r"$\frac{l^2d\langle N_h\rangle / dt}{\langle R^2\rangle / dt}$")
  
  
##########################################################
##########           Main function   #####################
##########################################################

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
f = "flux5e4"
for f in fluxes:
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fig1, ax1 = plt.subplots(1, 1, sharey=True,figsize=(5.7,4))
    fig2, ax2 = plt.subplots(1, 1, sharey=True,figsize=(5.7,4))
    fPath = os.getcwd()
    for t in list(reversed(inf.getTemperatures()[14:-2])):
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            fig1.subplots_adjust(top=0.95, bottom=0.15, wspace=0.08, left=0.15, right=0.85)
            fig2.subplots_adjust(top=0.95, bottom=0.15, wspace=0.08, left=0.15, right=0.85)
            computeError(ax1=ax1, ax2=ax2, i=i, t=t)
            i += 1
            fig1.savefig("../../../errorIntegrated.pdf")#, bbox_inches='tight')
            fig2.savefig("../../../errorDerivated.pdf")#, bbox_inches='tight')
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)

