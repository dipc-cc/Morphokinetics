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

def plotManyTotalRates(axarr, index, d, latSize, annotate=True):
    kb = 8.617332e-5
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

    axarr.plot(1/kb/temperatures, totalRateE[index], "-", marker=marker[0], ms=7, label=label+r": $R_e = N_e/T$", color=color[0])
    axarr.plot(1/kb/temperatures, totalRateH[index], ":", marker=marker[2], ms=7, label=label+r": $R_d = N_d/T$", color=color[2],markerfacecolor="None")
    axarr.plot(1/kb/temperatures, totalRateM[index], marker=marker[1], ms=7, ls="", label=label+r": $R_e' = \sum_{\alpha \in \{e\}} m_\alpha k_\alpha$", color=color[1])
    
    if annotate:
        #axarr.annotate(r"$\epsilon^{R}_\alpha=\omega^{R}_\alpha(E^k_\alpha+E^M_\alpha)$", xy=(0.47,0.1), xycoords="axes fraction")
        axarr.annotate(r"$\theta =$"+str(np.round(coverages[index],1)),xy=(0.1,0.57), xycoords="axes fraction")
        axarr.set_ylabel("Total rate per site")
        axarr.set_xlabel(r"$1/k_BT$")
        mp.setY2TemperatureLabels(axarr,kb)
    else:
        axarr.plot(1/kb/temperatures, np.ones(len(temperatures))*coverages[index], "--", label=r"$R_a$"+" "+r"$(R = R_a + R_d)$", color=cm(9/20))
    axarr.set_yscale("log")
    #axarr.set_ylim(3e-2,1e3)
    axarr.legend(loc="best", prop={'size':8})

