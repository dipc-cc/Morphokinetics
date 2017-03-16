#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data0.txt
import info as inf
import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import os
import math
import functions as fun
import itertools


def diffusivityDistance(index):
    p = inf.getInputParameters()
    d = inf.readAverages()
    
    # read all histograms
    filesN = glob.glob("histogram[0-9]*.txt")
    islandDistribution = []
    for i in range(0,len(filesN)):
        fileName = "histogram"+str(i)+".txt"
        islandDistribution.append(inf.readHistogram(fileName))

    islB3 = np.mean([[np.count_nonzero(h[1:]>2) for h in run] for run in islandDistribution],axis=0)

    cove = d.getRecomputedCoverage()/p.sizI/p.sizJ

    ratios = inf.getRatios(p)

    isld = fun.getOnlyAscending(d.isld)

    #Correct the simulated time:
    tp=np.zeros(len(d.time))
    tp[0]=d.even[0]/(d.prob[0]+d.depo[0])
    for j in range(1,len(d.prob)):
        tp[j] = tp[j-1]+(d.even[j]-d.even[j-1])/(d.prob[j]+d.depo[j]);

    # Plot 2
    plt.clf()
    x = list(range(0,len(d.time)))
    x = cove
    plt.loglog(x, d.time, "s", label="time")
    plt.loglog(x, tp, "x", label="corrected time")
    #time = tp
    cm = plt.get_cmap("Accent")
    plt.loglog(x, d.diff, label=r"$R^2 (\times \frac{1}{N_1 N_2})$",
               marker="s", ls="", mew=0, markerfacecolor=cm(0/8), alpha=0.8)
    plt.loglog(x, d.hops, label=r"$N_h l^2 (\times \frac{1}{N_1 N_2})$",
               marker="p", ls="", mew=0, markerfacecolor=cm(7/8), alpha=0.8)
    plt.loglog(x, fun.timeAverage(d.hops,d.time)*d.time, label=r"$\overline{R_{h}} \;\; l^2 t$")
               
    #coverages
    cm = plt.get_cmap("Set1")
  
    aneg = []
    for k in range(0,7):
        aneg.append(fun.timeAverage(d.negs[k], d.time))
        if k < 4:
            plt.loglog(x, aneg[k]/p.sizI/p.sizJ, label="aneg{} ".format(k))
    plt.loglog(x, (aneg[4]+aneg[5]+aneg[6])/p.sizI/p.sizJ, label="aneg{} ".format(k))
 
    acov = fun.timeAverage(cove, d.time)
    plt.loglog(x, acov, "o", label="acov")

    hopsCalc0 = (d.time * 6 * aneg[0] *ratios[0])
    plt.loglog(x, hopsCalc0, label="hops calc0")
    hopsCalc = d.time * (6 * aneg[0]*ratios[0]+2*aneg[1]*ratios[8]+2*aneg[2]*ratios[15]+0.1*aneg[3]*ratios[24])
    plt.loglog(x, hopsCalc, label="hops calc")

    ratios = inf.getRatio(p.temp, inf.getHexagonalEnergies())

    cm = plt.get_cmap("Set3")
    plt.loglog(x, fun.thetaAverage(d.time, p.flux), label=r"$1-\frac{1-e^{-Ft}}{Ft}$", color=cm(0))
    plt.loglog(x, fun.timeAverage(islB3, d.time)/p.sizI/p.sizJ, ".", color=cm(3/12), label=r"$N_{isl}$", markerfacecolor="None")

    plt.subplots_adjust(left=0.12, bottom=0.1, right=0.7, top=0.9, wspace=0.2, hspace=0.2)

    plt.legend(numpoints=1, prop={'size':8}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    plt.grid()
    plt.title("flux: {:.1e} temperature: {:d}".format(p.flux, int(p.temp)))
    plt.savefig("../../../plot"+str(p.flux)+str(p.temp)+".png")


##########################################################
##########           Main function   #####################
##########################################################

workingPath = os.getcwd()
fluxes = inf.getFluxes()
for f in fluxes:
    firstCollisionTime = []
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    for i,t in enumerate(inf.getTemperatures()):
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            inf.splitDataFiles()
            diffusivityDistance(i)
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)
plt.legend()

