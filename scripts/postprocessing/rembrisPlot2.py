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
    for i in range(0,len(filesN)-1):
        fileName = "data"+str(i)+".txt"
        allData.append(np.loadtxt(fname=fileName, delimiter="\t"))


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
    #plt.figure(num=None, figsize=(8,6))
    plt.loglog(time, diff/p.sizI/p.sizJ, "s", color="black", label=r"$R^2 (\times \frac{1}{N_1 N_2})$", markerfacecolor="None")
    plt.loglog(time, hops/p.sizI/p.sizJ, "*", markeredgecolor="gray", label=r"$N_h l^2 (\times \frac{1}{N_1 N_2})$", markerfacecolor="None")
    plt.loglog(time, diff/hops, ".", color="darkblue", label=r"$\frac{R^2}{N_h l^2}$", markerfacecolor="None")
    plt.loglog(time, diff/time/p.sizI/p.sizJ/p.r_tt, "-o", markeredgecolor="red", label=r"$\frac{R^2}{t} (\times \frac{1}{N_1 N_2 r_{tt}})$", markerfacecolor="None", color="red", lw=2)
    plt.loglog(time, hops/time/p.sizI/p.sizJ/p.r_tt, "-x", markeredgecolor="lightpink", label=r"$\frac{N_h l^2}{t} (\times \frac{1}{N_1 N_2 r_{tt}})$", markerfacecolor="None", color="lightpink", lw=2)
    plt.loglog(time, diff/(parti*time*p.r_tt), ">", markeredgecolor="lightblue", label=r"$\frac{R^2}{Nt} (\times \frac{1}{r_{tt}})$", markerfacecolor="None")
    plt.loglog(time, hops/(parti*time*p.r_tt), "+", markeredgecolor="lightskyblue", label=r"$\frac{N_h l^2}{Nt} (\times \frac{1}{r_{tt}})$", markerfacecolor="None")
    #coverages
    plt.loglog(time, neg0/p.sizI/p.sizJ, label=r"$\theta_0$")
    plt.loglog(time, neg1/p.sizI/p.sizJ, label=r"$\theta_1$")
    plt.loglog(time, neg2/p.sizI/p.sizJ, label=r"$\theta_2$")
    plt.loglog(time, neg3/p.sizI/p.sizJ, label=r"$\theta_3$")
    if p.maxN == 6:
        plt.loglog(time, neg4/p.sizI/p.sizJ, label=r"$\theta_{4+}$")
    plt.loglog(time, isld/p.sizI/p.sizJ, label="number of islands")

    plt.loglog(time, fun.theta(time, p.flux), label=r"$1-e^{-Ft}$")
    plt.loglog(time, cove, ".", color="orange", label=r"$\theta$", markerfacecolor="None")
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

