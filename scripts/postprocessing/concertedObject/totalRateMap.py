#!/usr/bin/env python3

import numpy as np
import matplotlib.pyplot as plt
from matplotlib import ticker
from matplotlib.colors import LogNorm
import Info as inf
import multiplicitiesPlot as mp
import pdb

kb = 8.617332e-5

# read data
totalRateM = np.loadtxt("totalRate.txt")
totalRateE = np.loadtxt("totalRateEvents.txt")
temperatures = np.loadtxt("temperatures.txt")
coverages = np.loadtxt("coverages.txt")

#prepare x, y
x = np.zeros((len(coverages),len(temperatures)))
y = np.zeros((len(coverages),len(temperatures)))

l = len(temperatures)

for i in range(0,len(coverages)):
    for j in range(0,len(temperatures)):
        y[i,j] = coverages[i]
        x[i,j] = 1/kb/temperatures[j]#[l-j-1]

def myPlot(f):
    levels=np.array([[10**i,2*10**i] for i in range(-1,5)]).flatten()
    cs = ax.contourf(x, y, f(totalRateM), 50, cmap="RdBu_r", norm=LogNorm(vmin=1e-1, vmax=1e4), vmin=1e-1, vmax=1e4, locator=ticker.LogLocator(), levels=levels)
    cbar = plt.colorbar(cs)
    cbar.set_label("Total rate per site", size=14)

fig = plt.figure()
ax = fig.add_subplot(111)
myPlot(lambda x:np.clip(x, 1e-1, 1e5))

ax.set_ylabel(r"coverage $\theta$", size=14)
ax.set_xlabel(r"$1/k_BT$", size=14)

ax2 = mp.setY2TemperatureLabels(ax,kb)
inf.smallerFont(ax2, 12)
inf.smallerFont(ax, 14)
fig.savefig("totalRateMap.svg")

