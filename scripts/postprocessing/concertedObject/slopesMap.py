#!/usr/bin/env python3

#import matplotlib as mpl
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
import multiplicitiesPlot as mp

kb = 8.617332e-5

# read data
totalRateM = np.loadtxt("totalRate.txt")
totalRateE = np.loadtxt("totalRateEvents.txt")
temperatures = np.loadtxt("temperatures.txt")
coverages = np.loadtxt("coverages.txt")
slopes = np.loadtxt("slopes.txt")

#prepare x, y
x = np.zeros((len(temperatures),len(coverages)))
y = np.zeros((len(temperatures),len(coverages)))

l = len(temperatures)

for i in range(0,len(temperatures)):
    for j in range(0,len(coverages)):
        x[i,j] = 1/kb/temperatures[i]#[l-j-1]
        y[i,j] = coverages[j]

def myPlot(f):
    cs = ax.contourf(x, y, f(slopes), 10, vmin=0, vmax=0.1, cmap="RdBu_r")
    cbar = plt.colorbar(cs)
    cbar.set_label("Activation energy (eV)")
    
fig = plt.figure()
ax = fig.add_subplot(111)
myPlot(lambda x:np.clip(x, 0,0.10))

    
ax.set_xlabel(r"$1/k_BT$")
ax.set_ylabel(r"coverage $\theta$")


mp.setY2TemperatureLabels(ax,kb)
fig.savefig("slopesMap.svg")


