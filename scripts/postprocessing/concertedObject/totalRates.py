#!/usr/bin/env python3

import numpy as np
import matplotlib.pyplot as plt
import multiplicitiesPlot as mp
import Info as inf
import os
import pdb

def readData(cwd="."):
    os.chdir(cwd)
    # read data
    totalRateM = np.loadtxt("totalRate.txt")
    totalRateE = np.loadtxt("totalRateEvents.txt")
    temperatures = np.loadtxt("temperatures.txt")
    coverages = np.loadtxt("coverages.txt")
    totalRateH = np.zeros((np.shape(totalRateE)))
    
    #Remove adsorption events
    for i in coverages:
        index = inf.getIndexFromCov(coverages, i)
        totalRateH[index,:] = totalRateE[index,:] - i
    return totalRateM, totalRateE, temperatures, coverages, totalRateH

def plotManyTotalRates(axarr, index, d, annotate=True):
    kb = 8.617332e-5
    latSize = 46000
    totalRateM, totalRateE, temperatures, coverages, totalRateH = d
    
    cm = plt.get_cmap('tab20c')
    
    if os.getcwd()[-4:] == "CuNi":
        color = [cm(0/20),cm(1/20),cm(2/20)]
        marker = ["o", "2", "^"]
        label = "Cu/Ni"
    else:
        color = [cm(4/20),cm(5/20),cm(6/20)]
        marker = ["d", "+", "s"]
        label = "Ni/Cu"
    
    axarr.plot(1/kb/temperatures, totalRateM[index], marker=marker[0], ms=7, ls="", label=label+r": $\sum_\alpha \epsilon^{R}_\alpha$ (multiplicity)", color=color[1])
    axarr.plot(1/kb/temperatures, totalRateH[index], "-", marker=marker[2], ms=7, label=label+r": $N_d^{R}/L$ (diffusion)", color=color[2],markerfacecolor="None")
    axarr.plot(1/kb/temperatures, totalRateE[index], ":", marker=marker[1], ms=7, label=label+r": $N_e^{R}/L$ (events)", color=color[0])
    
    if annotate:
        axarr.annotate(r"$\epsilon^{R}_\alpha=\omega^{R}_\alpha(E^k_\alpha+E^M_\alpha)$", xy=(0.47,0.1), xycoords="axes fraction")
        axarr.annotate(str(coverages[index])+r"$\theta$", xy=(0.1,0.57), xycoords="axes fraction")
        axarr.set_ylabel("Total rate per site")
        axarr.set_xlabel(r"$1/k_BT$")
        mp.setY2TemperatureLabels(axarr,kb)
    axarr.set_yscale("log")
    #axarr.set_ylim(3e-2,1e3)
    axarr.legend(loc="best", prop={'size':8})

