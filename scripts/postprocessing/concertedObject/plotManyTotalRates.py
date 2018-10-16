#!/usr/bin/env python3

import numpy as np
import matplotlib.pyplot as plt
import multiplicitiesPlot as mp
import os
import pdb

def getIndexFromCov(coverages, cov):
    return coverages.tolist().index(cov)

kb = 8.617332e-5
latSize = 46000

cm = plt.get_cmap('tab20c')

if os.getcwd()[-4:] == "CuNi":
    color = [cm(0/20),cm(1/20),cm(2/20)]
    marker = ["o", "2", "^"]
    label = "Cu/Ni"
else:
    color = [cm(4/20),cm(5/20),cm(6/20)]
    marker = ["d", "+", "<"]
    label = "Ni/Cu"

# read data
totalRateM = np.loadtxt("totalRate.txt")
totalRateE = np.loadtxt("totalRateEvents.txt")
temperatures = np.loadtxt("temperatures.txt")
coverages = np.loadtxt("coverages.txt")
totalRateH = np.zeros((np.shape(totalRateE)))

#Remove adsorption events
for i in coverages:
    index = getIndexFromCov(coverages, i)
    totalRateH[index,:] = totalRateE[index,:] - i

# plot it
for i in coverages[-101::10][1:]:
#for i in coverages:
    fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,3.5))
    fig.subplots_adjust(top=0.88,left=0.15,right=0.95,bottom=0.15)

    index = getIndexFromCov(coverages, i)
    axarr.plot(1/kb/temperatures, totalRateM[index], marker=marker[0], ms=7, ls="", label=label+r": $\sum_\alpha \epsilon^{R}_\alpha$  at "+str(coverages[index])+r"$\theta$", color=color[1])
    axarr.plot(1/kb/temperatures, totalRateH[index], "-", marker=marker[2], ms=7, label=label+r": $N_h^{R}/L$ at "+str(coverages[index])+r"$\theta$ (hops)", color=color[2],markerfacecolor="None")
    axarr.plot(1/kb/temperatures, totalRateE[index], ":", marker=marker[1], ms=7, label=label+r": $N_e^{R}/L$ at "+str(coverages[index])+r"$\theta$ (events)", color=color[0])


    axarr.annotate(r"$\epsilon^{R}_\alpha=\omega^{R}_\alpha(E^k_\alpha+E^M_\alpha)$", xy=(0.7,0.4), xycoords="axes fraction")
    axarr.set_yscale("log")
    axarr.set_ylabel("Total rate per site")
    axarr.set_xlabel(r"$1/k_BT$")
    axarr.set_ylim(3e-2,1e3)
    axarr.legend(loc="best", prop={'size':6})
    mp.setY2TemperatureLabels(axarr,kb)
    fig.savefig("TotalRates_{:d}_{:5f}.svg".format(index,coverages[index]))
    plt.close(fig)
