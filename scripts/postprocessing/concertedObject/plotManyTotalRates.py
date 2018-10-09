#!/usr/bin/env python3

import numpy as np
import matplotlib.pyplot as plt
import os
import pdb

def getIndexFromCov(coverages, cov):
    return coverages.tolist().index(cov)

kb = 8.617332e-5
latSize = 46000

cm = plt.get_cmap('tab20')

if os.getcwd()[-4:] == "CuNi":
    color0 = cm(0/20)
    color1 = cm(1/20)
    label = "Cu/Ni"
    marker0 = "o"
    marker1 = "2"
else:
    color0 = cm(2/20)
    color1 = cm(3/20)
    label = "Ni/Cu"
    marker0 = "s"
    marker1 = "+"
    

# read data
totalRateM = np.loadtxt("totalRate.txt")
totalRateE = np.loadtxt("totalRateEvents.txt")
temperatures = np.loadtxt("temperatures.txt")
coverages = np.loadtxt("coverages.txt")

#Remove adsorption events
for i in coverages:
    index = getIndexFromCov(coverages, i)
    totalRateE[index,:] -= i
# plot it
fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,4))
fig.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.15)
for i in coverages[-101::10][1:]:
    index = getIndexFromCov(coverages, i)
    axarr.plot(1/kb/temperatures, totalRateM[index], marker=marker0, ms=5, ls="", label=label+r": $\sum_\alpha \epsilon^{R}_\alpha$  at "+str(coverages[index])+r"$\theta$", color=color0)
    axarr.plot(1/kb/temperatures, totalRateE[index], ":", marker=marker1, ms=7, label=label+r": $N_e^{R}/L$ at "+str(coverages[index])+r"$\theta$", color=color1)

    break # exits in the first loop

axarr.annotate(r"$\epsilon^{R}_\alpha=\omega^{R}_\alpha(E^k_\alpha+E^M_\alpha)$", xy=(0.7,0.4), xycoords="axes fraction")
axarr.set_yscale("log")
axarr.set_ylabel("Total rate per site")
axarr.set_xlabel(r"$1/k_BT$")
axarr.set_ylim(1e-2,1.5e4)
axarr.legend(loc="best", prop={'size':6})
fig.savefig("TotalRates10.svg")
