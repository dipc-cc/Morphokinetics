#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data0.txt
import info as inf
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker
from matplotlib.patches import Rectangle
import glob
import os
import sys
import functions as fun
from scipy.signal import savgol_filter


def diffusivityDistance(binned, fig=0, ax=0, i=-1):
    p = inf.getInputParameters()
    if binned:
        d = inf.readBinnedAverages()
    else:
        d = inf.readAverages()

    cove = d.getRecomputedCoverage()/p.sizI/p.sizJ
    ratios = p.getRatios()
    Na = cove * p.sizI * p.sizJ

    fig = plt.figure(num=33, figsize=(6,5))
    ax = plt.gca()
        
    x = list(range(0,len(d.time)))
    x = cove
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    diff = fun.timeDerivative(d.diff, d.time)/(4*Na)
    handles = []
    #lg, = ax.loglog(x, d.diff/d.time/(4*Na), label=r"$\frac{1}{2dN_a} \; \frac{\langle R^2\rangle}{t}$", ls="-", color=cm(3/8), lw=2); handles.append(lg)
    lg, = ax.loglog(x, d.hops/d.time/(4*Na), label=r"$\frac{l^2}{2dN_a} \; \frac{\langle N_h\rangle}{t}$",
                      marker="x", color=cm(4.1/8), ls="", solid_capstyle="round",lw=5); handles.append(lg)

    Malpha = inf.readPossibleFromList()#/d.time
    MalphaP = inf.readInstantaneous(False)
    for k in range(0,4):
        Malpha[k] = Malpha[k]/d.time
        label = r"$\theta_{"+str(k)+"}$"
        lg, = ax.loglog(x, fun.timeAverage(d.negs[k]/p.sizI/p.sizJ, d.time), label=label, ms=1, lw=2, ls="-", color=cm(k/8)); handles.append(lg)
        #lg, = plt.loglog(x, MalphaP[k]/d.negs[k], label=r"$m_"+str(k)+"$");    handles.append(lg) # Around 6, 2, 2, 0.1

 
    individualHopsCalc = []
    individualHopsCalc.append((Malpha[0]*ratios[0])/(4*Na))
    individualHopsCalc.append((Malpha[1]*ratios[8])/(4*Na))
    individualHopsCalc.append((Malpha[2]*ratios[15])/(4*Na))
    individualHopsCalc.append((Malpha[3]*ratios[24])/(4*Na))
    lg, = plt.loglog(x, individualHopsCalc[0], label="hops calc0")
    handles.append(lg)
    lg, = plt.loglog(x, individualHopsCalc[1], label="hops calc1")#,
    handles.append(lg)
    lg, = plt.loglog(x, individualHopsCalc[2], label="hops calc2")#
    handles.append(lg)
    lg, = plt.loglog(x, individualHopsCalc[3], label="hops calc3")#
    handles.append(lg)
    hopsCalc = np.sum(individualHopsCalc, axis=0)
    lgC, = plt.loglog(x, hopsCalc, label="hops calc")
#                   marker="*", ls="", mew=mew, markerfacecolor=cm(5/8), ms=5, alpha=alpha)
    handles.append(lgC)
    ax.grid()
    ax.set_xlabel(r"$\theta$", size=16)
    #ax.set_ylim([1e-7,1e13])
    ax.set_xlim([1e-5,1e0])
    ax.legend(loc="best", prop={'size':6})
    #ax.legend(handles=handles, loc=(0.46,0.3), numpoints=1, prop={'size':15}, markerscale=2)
    fig.savefig("../../../plot"+str(p.flux)+str(p.temp)+".png")
    plt.close(33)
          


##########################################################
##########           Main function   #####################
##########################################################

try:
    smooth = sys.argv[1] == "y"
except IndexError:
    smooth = False
try:
    binned = sys.argv[2] == "y"
except IndexError:
    binned = False

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
for f in fluxes:
    if f != "flux5e4":
        continue
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fig1, axarr = plt.subplots(1, 3, sharey=True,figsize=(15,7))
    fPath = os.getcwd()
    for t in inf.getTemperatures()[14:]:
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            diffusivityDistance(binned)
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)

