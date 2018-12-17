#!/usr/bin/env python3

import sys
import os
import numpy as np
import matplotlib.pyplot as plt
import multiplicitiesPlot as mp
import Info as inf
import Label

import pdb

#plot function
def plot(ax, ymin, indexes):
    # actual plot
    ax.plot(x, tgt[:,covIndex]*1000,
            marker="o",label=r"$E^{R}_{app}$", color="red")
    ax.plot(x, rct[:,covIndex]*1000, "--",
            label=r"$\sum \epsilon^{R}_\alpha$", color="green")
    ax.plot(x, abs(np.array(tgt[:,covIndex])-np.array(rct[:,covIndex]))*1000, label="Absolute error", color="black")
    for i in range(0,shape[2]):
        if any(abs(epsilon[-1,::-1,i][indexes]) > ymin): # 0.1meV
            #ax.plot(x, epsilon[-1,::-1,i], label=labelAlfa[a], color=cm(abs(i/20)), marker=markers[i%8])
            ax.fill_between(x, lastOmegas[covIndex,:,i]*1000, label=labelAlfa[i], color=cm(i%20/(19)))


covIndex = int(sys.argv[1])
kb = 8.617332e-5
system = os.getcwd().split("/")[-1]
if system == "CuNi":
    min0energy = 0.05
    min1energy = 0.0005
    max0energy = 500
    labelIndex = 0
else:
    min0energy = 0.01
    min1energy = 0.0003
    max0energy = 200
    labelIndex = 1

# data
shape = np.loadtxt("shape.txt").astype(int)
x = np.loadtxt("x_index.txt")
epsilon = np.loadtxt("epsilon.txt").reshape(shape)
lastOmegas = np.loadtxt("lastOmegas.txt").reshape(shape)
tgt = np.loadtxt("targetE.txt").reshape(shape[1],shape[0])
rct = np.loadtxt("rcomptE.txt").reshape(shape[1],shape[0])


# pre
labels = Label.Label()
labelAlfa = labels.getLabels()
cm = plt.get_cmap('tab20')

split = 40
x0min = 12.5
x0max = split
x1min = split
x1max = 510

x0indexes = [i for i,v in enumerate(x) if v < split]
x1indexes = [i for i,v in enumerate(x) if v > split]

# plot
figR, ax = plt.subplots(nrows=1, ncols=2, figsize=(5,3.5))
figR.subplots_adjust(top=0.88,left=0.15,right=0.9,bottom=0.15, wspace=0.1)
plot(ax[0], min0energy, x0indexes)
plot(ax[1], min1energy, x1indexes)

# post
ax[0].set_xlabel(r"$1/k_BT$")#, size=14)
ax[1].set_xlabel(r"$1/k_BT$")#, size=14)
ax[0].set_ylabel(r"Activation Energy $(meV)$")#, size=14)
ax[labelIndex].annotate(r"$\epsilon^{R}_\alpha=\omega^{R}_\alpha(E^k_\alpha+E^M_\alpha)$", xy=(0.25,0.3), xycoords="axes fraction")
ax[1].yaxis.tick_right()

#inf.smallerFont(ax[0], 14)
#inf.smallerFont(ax[1], 14)
ax[0].set_ylim(0, max0energy)
ax[0].set_xlim(x0min, x0max)
ax[1].set_ylim(0,60)
ax[1].set_xlim(x1min, x1max)

ax02 = mp.setY2TemperatureLabels(ax[0],kb, majors=np.array([300, 400, 500, 900]))
ax12 = mp.setY2TemperatureLabels(ax[1],kb, majors=np.array([25, 50, 75, 200]))
ax[0].legend(loc="best", prop={'size':9})
ax[1].legend(loc="best", prop={'size':9})
plt.savefig("resume"+str(covIndex)+".svg")
