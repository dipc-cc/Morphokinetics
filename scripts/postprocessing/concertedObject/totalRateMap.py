#!/usr/bin/env python3

#import matplotlib as mpl
from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib import ticker
from matplotlib.colors import LogNorm
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
    #cs = ax.pcolor(x, y, f(totalRateM), cmap="RdBu_r", norm=LogNorm(vmin=1e-1, vmax=1e4))
    cs = ax.contourf(x, y, f(totalRateM), 50, cmap="RdBu_r", norm=LogNorm(vmin=1e-1, vmax=1e4), vmin=1e-1, vmax=1e4, locator=ticker.LogLocator(), levels=levels)
    cbar = plt.colorbar(cs)
    #ax.contour(x, y, f(totalRateM), levels=[0], cmap="RdBu_r", norm=LogNorm(vmin=1e-1, vmax=1e4), vmin=1e-1, vmax=1e4, locator=ticker.LogLocator())

fig = plt.figure()
ax = fig.add_subplot(111)
#cs = ax.contourf(x, y, np.log(totalRateM)/np.log(10), 50, cmap="plasma",
#                   extend='both', locator=ticker.LogLocator())
#cs = ax.contourf(x, y, totalRateM, 50, cmap="plasma", locator=ticker.LogLocator(), vmin=1e-1, vmax=1e5)
myPlot(lambda x:np.clip(x, 1e-1, 1e5))

ax.set_ylabel(r"coverage $\theta$")
ax.set_xlabel(r"$1/k_BT$")
#ax.set_zlabel("Total rate (log)")
#ax.set_zscale("log")
#ax.set_cbscale("log")
#ax.zaxis.set_scale('log')
#ax.view_init(15, 160)
#ax.set_zlim(0.1,1e3)


fig.savefig("totalRateMap.svg")

