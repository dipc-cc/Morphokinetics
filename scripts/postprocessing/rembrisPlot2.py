#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data0.txt
import info
import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import os
import math
import functions as fun


def diffusivityDistance():
    p = info.getInputParameters()
    allData = []

    filesN = glob.glob("data[0-9]*.txt")
    for i in range(0,len(filesN)):
        fileName = "data"+str(i)+".txt"
        allData.append(np.loadtxt(fname=fileName, delimiter="\t"))

    # read all histograms
    islandDistribution = []
    for i in range(0,len(filesN)):
        fileName = "histogram"+str(i)+".txt"
        islandDistribution.append(info.readHistogram(fileName))

    islB2 = np.mean([[np.count_nonzero(h[1:]) for h in run] for run in islandDistribution],axis=0)
    islB3 = np.mean([[np.count_nonzero(h[1:]>2) for h in run] for run in islandDistribution],axis=0)
    islB4 = np.mean([[np.count_nonzero(h[1:]>3) for h in run] for run in islandDistribution],axis=0)
    islB5 = np.mean([[np.count_nonzero(h[1:]>4) for h in run] for run in islandDistribution],axis=0)

    cove = np.mean([i[:,0]  for i in allData], axis=0)
    time = np.mean([i[:,1]  for i in allData], axis=0)
    isld = np.mean([i[:,3]  for i in allData], axis=0)
    depo = np.mean([i[:,4]  for i in allData], axis=0)
    prob = np.mean([i[:,5]  for i in allData], axis=0)
    even = np.mean([i[:,7]  for i in allData], axis=0)
    hops = np.mean([i[:,15] for i in allData], axis=0)
    diff = np.mean([i[:,12] for i in allData], axis=0)
    neg0 = np.mean([i[:,16] for i in allData], axis=0)
    neg1 = np.mean([i[:,17] for i in allData], axis=0)
    neg2 = np.mean([i[:,18] for i in allData], axis=0)
    neg3 = np.mean([i[:,19] for i in allData], axis=0)
    if p.maxN == 6:
        neg4 = np.mean([i[:,20] for i in allData], axis=0) + \
               np.mean([i[:,21] for i in allData], axis=0) + \
               np.mean([i[:,22] for i in allData], axis=0)
    parti = np.mean([i[:,0]*p.sizI*p.sizJ for i in allData], axis=0)
    #hops = even - parti+1

    # Plot 2
    plt.clf()
    x = time
    cm = plt.get_cmap("Accent")
    #plt.figure(num=None, figsize=(8,6))
    plt.loglog(x, diff/p.sizI/p.sizJ, label=r"$R^2 (\times \frac{1}{N_1 N_2})$",
               marker="s", ls="", mew=0, markerfacecolor=cm(0/8), alpha=0.8)
    plt.loglog(x, hops/p.sizI/p.sizJ, label=r"$N_h l^2 (\times \frac{1}{N_1 N_2})$",
               marker="p", ls="", mew=0, markerfacecolor=cm(1/8), alpha=0.8)
    plt.loglog(x, diff/hops, ".", label=r"$\frac{R^2}{N_h l^2}$",
               marker="H", ls="", mew=0, markerfacecolor=cm(2/8), alpha=0.8)
    plt.loglog(x, diff/time/p.sizI/p.sizJ/p.r_tt, label=r"$\frac{R^2}{t} (\times \frac{1}{N_1 N_2 r_{tt}})$",
               marker="o", ls="", mew=0, markerfacecolor=cm(3/8), alpha=0.8)
    plt.loglog(x, hops/time/p.sizI/p.sizJ/p.r_tt, label=r"$\frac{N_h l^2}{t} (\times \frac{1}{N_1 N_2 r_{tt}})$",
               marker="D", ls="", mew=0, markerfacecolor=cm(4/8), alpha=0.8)
    plt.loglog(x, diff/(parti*time*p.r_tt), label=r"$\frac{R^2}{Nt} (\times \frac{1}{r_{tt}})$",
               marker=">", ls="", mew=0, markerfacecolor=cm(5/8), alpha=0.8)
    plt.loglog(x, hops/(parti*time*p.r_tt), label=r"$\frac{N_h l^2}{Nt} (\times \frac{1}{r_{tt}})$",
               marker="*", ls="", mew=0, markerfacecolor=cm(6/8), alpha=0.8)
    #coverages
    cm = plt.get_cmap("Set1")
    plt.loglog(x, neg0/p.sizI/p.sizJ, label=r"$\theta_0$", color=cm(0/5), ls=(0,(1,1)))
    plt.loglog(x, neg1/p.sizI/p.sizJ, label=r"$\theta_1$", color=cm(1/5), ls=(0,(1,1)))
    plt.loglog(x, neg2/p.sizI/p.sizJ, label=r"$\theta_2$", color=cm(2/5), ls=(0,(1,1)))
    plt.loglog(x, neg3/p.sizI/p.sizJ, label=r"$\theta_3$", color=cm(3/5), ls=(0,(1,1)))
    if p.maxN == 6:
        plt.loglog(x, neg4/p.sizI/p.sizJ, label=r"$\theta_{4+}$", color=cm(4/5), ls=(0,(1,1)))
    plt.loglog(x, isld/p.sizI/p.sizJ, label="number of islands", color=cm(5/5))

    cm = plt.get_cmap("Set3")
    plt.loglog(x, fun.theta(time, p.flux), label=r"$1-e^{-Ft}$", color=cm(0))
    plt.loglog(x, cove, ".", color=cm(3/12), label=r"$\theta$", markerfacecolor="None")
    plt.subplots_adjust(left=0.12, bottom=0.1, right=0.7, top=0.9, wspace=0.2, hspace=0.2)

    plt.legend(numpoints=1, prop={'size':12}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    plt.grid()
    plt.title("flux: {:.1e} temperature: {:d}".format(p.flux, int(p.temp)))
    plt.savefig("plot"+str(p.flux)+str(p.temp)+".png")

    return time, neg1

##########################################################
##########           Main function   #####################
##########################################################

workingPath = os.getcwd()
fluxes = info.getFluxes()
for f in fluxes:
    firstCollisionTime = []
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    for t in info.getTemperatures():
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            info.splitDataFiles()
            time, neg1 = diffusivityDistance()
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

