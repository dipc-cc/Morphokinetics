#!/usr/bin/env python3

import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
import multiplicitiesPlot as mp
import Info as inf
import pdb

kb = 8.617332e-5

vmin = 0.0
vmax = 0.099

# read data
totalRateM = np.loadtxt("totalRate.txt")
totalRateE = np.loadtxt("totalRateEvents.txt")
temperatures = np.loadtxt("temperatures.txt")[::-1]
coverages = np.loadtxt("coverages.txt")
slopes = np.loadtxt("slopes.txt")

#prepare x, y
x = np.zeros((len(temperatures),len(coverages)))
y = np.zeros((len(temperatures),len(coverages)))

l = len(temperatures)

for i in range(0,len(temperatures)):
    for j in range(0,len(coverages)):
        x[i,j] = 1/kb/temperatures[i]#[l-i-1]
        #x[i,j] = 1/kb/temperatures[l-i-1]
        y[i,j] = coverages[j]

def myPlot(f):
    cs = ax.contourf(x, y, f(slopes), 100, vmin=vmin, vmax=vmax, cmap="RdBu_r")
    cbar = plt.colorbar(cs)
    cbar.set_label("Activation energy (eV)", size=14)
    
fig = plt.figure()
ax = fig.add_subplot(111)
myPlot(lambda x:np.clip(x, vmin, vmax))

    
ax.set_xlabel(r"$1/k_BT$", size=14)
ax.set_ylabel(r"coverage $\theta$", size=14)


ax2 = mp.setY2TemperatureLabels(ax,kb)
inf.smallerFont(ax2, 12)
inf.smallerFont(ax, 14)
fig.savefig("slopesMap.svg")


