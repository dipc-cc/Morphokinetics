#!/usr/bin/env python3

import numpy as np
import matplotlib.pyplot as plt

import pdb

def getIndexFromCov(coverages, cov):
    return coverages.tolist().index(cov)

kb = 8.617332e-5

# read data
totalRateM = np.loadtxt("totalRate.txt")
totalRateE = np.loadtxt("totalRateEvents.txt")
temperatures = np.loadtxt("temperatures.txt")
coverages = np.loadtxt("coverages.txt")

# plot it
cm = plt.get_cmap('tab20')
markers=["o", "s","D","^","d","h","p"]
fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,4))
fig.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.15)
for color,i in enumerate(coverages[-101::10][1:]):
    index = getIndexFromCov(coverages, i)
    axarr.plot(1/kb/temperatures, totalRateE[index], "--", label=r"Ni on Cu: $E^{R}_{app}$ at "+str(coverages[index])+r"$\theta$", color="C"+str(color+1))
    axarr.plot(1/kb/temperatures, totalRateM[index], marker=markers[color+1], ms=5, ls="", label=r"Ni on Cu: $\sum \epsilon^{R}_\alpha$  at "+str(coverages[index])+r"$\theta$", color="C"+str(color+1))
    break # exits in the first loop

axarr.annotate(r"$\epsilon^{R}_\alpha=\omega^{R}_\alpha(E^k_\alpha+E^M_\alpha)$", xy=(0.7,0.4), xycoords="axes fraction")
axarr.set_yscale("log")
axarr.set_ylabel("Total rate per site")
axarr.set_xlabel(r"$1/k_BT$")
axarr.set_ylim(0.1,1.5e4)
axarr.legend(loc="best", prop={'size':6})
fig.savefig("TotalRates10.svg")
