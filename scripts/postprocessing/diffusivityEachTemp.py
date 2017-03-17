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


def diffusivityDistance(index, debug, smooth, smoothCalc):
    p = inf.getInputParameters()
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
    lgR, = plt.loglog(x, diff, label=r"$\frac{1}{2dN_a} \; \frac{d(R^2)}{dt}$",
               marker="s", ls="", mew=mew, markerfacecolor=cm(0/8), ms=8, alpha=alpha)
    hops = fun.timeDerivative(d.hops, d.time)/(4*Na)
    lgN, = plt.loglog(x, hops, label=r"$\frac{l^2}{2dN_a} \; \frac{d(N_h)}{dt}$",
               marker="p", ls="", mew=mew, markerfacecolor=cm(7/8), ms=7, alpha=alpha)

    lgRh, = plt.loglog(x, d.prob/(4*Na), label=r"$\frac{l^2}{2dN_a} R_{h} $",
               marker="o", ls="", mew=mew, markerfacecolor=cm(1/8), ms=5.5, alpha=alpha)
               
    #coverages
    cm1 = plt.get_cmap("Set1")

    handles = []
    lg, = plt.loglog(x, cove, label=r"$\theta$", color=cm1(1/9))
    handles.append(lg)
    for k in range(0,7):
        if k < 4:
            label = r"${n_"+str(k)+"}$"
            lg, = plt.loglog(x, d.negs[k]/p.sizI/p.sizJ, label=label, ms=1,  marker=".", color=cm1((k+2)/9))
            if smooth:
                ySmooth = np.exp(savgol_filter(np.log(d.negs[k]), 9, 1))
                plt.loglog(x, ySmooth/p.sizI/p.sizJ, lw=2)
                d.negs[k] = ySmooth
            handles.append(lg)

    hopsCalc0 = (6 * d.negs[0] *ratios[0])/(4*Na)
    lgCAll = []
    if debug:
        lgC0, = plt.loglog(x, hopsCalc0, "p-", label="hops calc0")
        lgCAll.append(lgC0)
        hopsCalc1 = (2*d.negs[1]*ratios[8])/(4*Na)
        lgC1, = plt.loglog(x, hopsCalc1, "x-", label="hops calc1")#,
        lgCAll.append(lgC1)
        hopsCalc2 = (2*d.negs[2]*ratios[15])/(4*Na)
        lgC2, = plt.loglog(x, hopsCalc2, "o-", label="hops calc2")#
        lgCAll.append(lgC2)
        hopsCalc3 = (0.1*d.negs[3]*ratios[24])/(4*Na)
        lgC3, = plt.loglog(x, hopsCalc3, "*-", label="hops calc3")#
        lgCAll.append(lgC3)
    else:
        lgC0, = plt.loglog(x, hopsCalc0, label="hops calc0",
                           marker="", ls=":", mew=mew, color=cm(8/8), ms=4, alpha=1)
        lgCAll.append(lgC0)
        
    if p.calc == "AgUc":
        hopsCalc = (6 * d.negs[0]*ratios[0]+2*d.negs[1]*ratios[8]+2*d.negs[2]*ratios[15]+0.1*d.negs[3]*ratios[24])/(4*Na)
        if smoothCalc:
            hopsCalc = np.exp(savgol_filter(np.log(hopsCalc), 9, 1))
        lgC, = plt.loglog(x, hopsCalc, label="hops calc",
                   marker="*", ls="", mew=mew, markerfacecolor=cm(5/8), ms=5, alpha=alpha)
        handles = [lgR, lgN, lgRh, lgC]+ lgCAll + handles 
    else:
        handles = [lgR, lgN, lgRh, lgC0] + handles
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
    
workingPath = os.getcwd()
fluxes = inf.getFluxes()
for f in fluxes:
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    for i,t in enumerate(inf.getTemperatures()):
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            diffusivityDistance(i, debug, smooth, smoothCalc)
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)
plt.legend()

