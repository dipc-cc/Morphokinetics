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
    allData = []

    filesN = glob.glob("data[0-9]*.txt")
    for i in range(0,len(filesN)):
        fileName = "data"+str(i)+".txt"
        allData.append(np.loadtxt(fname=fileName, delimiter="\t"))

    # read all histograms
    islandDistribution = []
    for i in range(0,len(filesN)):
        fileName = "histogram"+str(i)+".txt"
        islandDistribution.append(inf.readHistogram(fileName))

    islB2 = np.mean([[np.count_nonzero(h[1:]) for h in run] for run in islandDistribution],axis=0)
    islB3 = np.mean([[np.count_nonzero(h[1:]>2) for h in run] for run in islandDistribution],axis=0)
    islB4 = np.mean([[np.count_nonzero(h[1:]>3) for h in run] for run in islandDistribution],axis=0)
    islB5 = np.mean([[np.count_nonzero(h[1:]>4) for h in run] for run in islandDistribution],axis=0)

    dimers = islB2-islB3
    trimers = islB3-islB4
    islB2 = fun.getOnlyAscending(islB2)
    #islB3 = fun.getOnlyAscending(islB3)
    islB4 = fun.getOnlyAscending(islB4)
    islB5 = fun.getOnlyAscending(islB5)

    cove = np.mean([i[:,0]  for i in allData], axis=0)
    time = np.mean([i[:,1]  for i in allData], axis=0)
    isld = np.mean([i[:,3]  for i in allData], axis=0)
    Ra   = np.mean([i[:,4]  for i in allData], axis=0)
    Rh   = np.mean([i[:,5]  for i in allData], axis=0)
    even = np.mean([i[:,7]  for i in allData], axis=0)
    hops = np.mean([i[:,15] for i in allData], axis=0)
    diff = np.mean([i[:,12] for i in allData], axis=0)
    peri = np.mean([i[:,10] for i in allData], axis=0)
    neg0 = np.mean([i[:,16] for i in allData], axis=0)
    neg1 = np.mean([i[:,17] for i in allData], axis=0)
    neg2 = np.mean([i[:,18] for i in allData], axis=0)
    neg3 = np.mean([i[:,19] for i in allData], axis=0)
    if p.maxN == 6:
        neg4 = np.mean([i[:,20] for i in allData], axis=0)
        neg5 = np.mean([i[:,21] for i in allData], axis=0)
        neg6 = np.mean([i[:,22] for i in allData], axis=0)

    neg = []
    neg.append(neg0)
    neg.append(neg1)
    neg.append(neg2)
    neg.append(neg3)
    neg.append(neg4)
    neg.append(neg5)
    neg.append(neg6)
    parti = np.mean([i[:,0]*p.sizI*p.sizJ for i in allData], axis=0)
    cove = np.sum(neg[:],axis=0)/p.sizI/p.sizJ

    ratios = inf.getRatio(p.temp, inf.getHexagonalEnergies())
    Na = cove * p.sizI * p.sizJ

    isld = fun.getOnlyAscending(isld)

    #Correct the simulated time:
    tp=np.zeros(len(time))
    tp[0]=even[0]/(Rh[0]+Ra[0])
    for j in range(1,len(Rh)):
        tp[j] = tp[j-1]+(even[j]-even[j-1])/(Rh[j]+Ra[j]);

    # Plot 2
    plt.clf()
    x = list(range(0,len(time)))
    x = cove
    plt.loglog(x, time, "s", label="time")
    plt.loglog(x, tp, "x", label="corrected time")
    #time = tp
    cm = plt.get_cmap("Accent")
    plt.loglog(x, diff, label=r"$R^2 (\times \frac{1}{N_1 N_2})$",
               marker="s", ls="", mew=0, markerfacecolor=cm(0/8), alpha=0.8)
    plt.loglog(x, hops, label=r"$N_h l^2 (\times \frac{1}{N_1 N_2})$",
               marker="p", ls="", mew=0, markerfacecolor=cm(7/8), alpha=0.8)
    plt.loglog(x, fun.timeAverage(Rh,time)*time, label=r"$\overline{R_{h}} \;\; l^2 t$")
               
    #coverages
    cm = plt.get_cmap("Set1")
  
    aneg = []
    for k in range(0,7):
        aneg.append(fun.timeAverage(neg[k], time))
        if k < 4:
            plt.loglog(x, aneg[k]/p.sizI/p.sizJ, label="aneg{} ".format(k))
    plt.loglog(x, (aneg[4]+aneg[5]+aneg[6])/p.sizI/p.sizJ, label="aneg{} ".format(k))
 
    acov = fun.timeAverage(cove, time)
    plt.loglog(x, acov, "o", label="acov")

    hopsCalc0 = (time * 6 * aneg[0] *ratios[0])
    plt.loglog(x, hopsCalc0, label="hops calc0")
    hopsCalc = time * (6 * aneg[0]*ratios[0]+2*aneg[1]*ratios[8]+2*aneg[2]*ratios[15]+0.1*aneg[3]*ratios[24])
    plt.loglog(x, hopsCalc, label="hops calc")

    ratios = inf.getRatio(p.temp, inf.getHexagonalEnergies())

    cm = plt.get_cmap("Set3")
    plt.loglog(x, fun.thetaAverage(time, p.flux), label=r"$1-\frac{1-e^{-Ft}}{Ft}$", color=cm(0))
    plt.loglog(x, fun.timeAverage(islB3, time)/p.sizI/p.sizJ, ".", color=cm(3/12), label=r"$N_{isl}$", markerfacecolor="None")
    perimeter = (peri)/p.sizI/p.sizJ
    vp = np.gradient(perimeter)/np.gradient(cove)#/np.gradient(time)

    plt.subplots_adjust(left=0.12, bottom=0.1, right=0.7, top=0.9, wspace=0.2, hspace=0.2)

    plt.legend(numpoints=1, prop={'size':8}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    plt.grid()
    plt.title("flux: {:.1e} temperature: {:d}".format(p.flux, int(p.temp)))
    #plt.xscale("linear")
    plt.savefig("../../../plot"+str(p.flux)+str(p.temp)+".png")

    return time, neg1

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
            time, neg1 = diffusivityDistance(i)
            # find first dimer occurrence
            i = np.argmax(neg1>0)
            firstCollisionTime.append(time[i])
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    kb = 8.6173324e-5
    #plt.semilogy(1/kb/temperatures, firstCollisionTime, ".-", label=f)
    os.chdir(workingPath)
plt.legend()

