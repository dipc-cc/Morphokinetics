#!/usr/bin/env python3
import matplotlib
matplotlib.use("Agg")
import glob
import os
import sys
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
import matplotlib.ticker as ticker
import info as inf
import math
import multiplicitiesPlot as mp

import pdb
        
kb = 8.6173324e-5

def getTotalRate():
    files = glob.glob("dataAe0*.txt")
    totalRate = 0
    rates = np.zeros(4)
    indexes = list(range(8,12)) + [21, 22]

    for t in files:
        data = np.loadtxt(t,comments=['#', '[', 'h'])
        events = data[:,7] # column number 8 is "number of events"
        try:
            totalRate += events / data[:,1] # last time, column 2
        except ValueError:
            continue

    totalRate = totalRate / len(files)
    return totalRate

def plot(x,y,p,ax,label="",marker="o"):
    # Labels
    ax.set_ylabel(r"events/site/s")
    ax.set_xlabel(r"$1/k_BT$")
    ref = False
    color = "w"
    if label=="":
        color = "C5"
        label = p.rLib.title()
        label = r"$R=R_a+R_d+R_r+R_h$ (total) "
        ref = True
        cm = plt.get_cmap('Set1')
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
        font = FontProperties()
        font.set_size(7)
        ax.annotate("10 runs\nSize: "+str(p.sizI)+"x"+str(p.sizJ)+"\n"+r"$P_{CO} = 2$ mbar, $P_O = 1$ mbar ", xy=(0.07, 0.16), xycoords="axes fraction",
                    bbox=bbox_props, fontproperties=font, horizontalalignment='left', verticalalignment='top',)
        ax.annotate("TOF",xy=(0.3,0.5), xycoords="axes fraction",zorder=+1)
    ucArea = 3.12*6.43/4
    toCm = 1e-8
    area = p.sizI * p.sizJ * ucArea * toCm * toCm
    x = np.array(x)
    y = np.array(y)
    x = 1 / kb / x
    y = y / (p.sizI * p.sizJ)
    ax.plot(x,y,label=label, ls="-", marker=marker, mfc=color)
    ax.set_yscale("log")
    ax.legend(loc="best", prop={'size':6})

    

workingPath = os.getcwd()
x = []
y = []
temperatures = True
try:
    iter = inf.getTemperatures()
except ValueError:
    iter = inf.getPressures()

y2 = np.zeros(shape=(len(iter),7))
print(np.shape(y2))
if iter[0] < 15:
    temperatures = False
i = 0
for f in iter:
    try:
        os.chdir(str(f)+"/results")
        runFolder = glob.glob("*/");
        runFolder.sort()
        os.chdir(runFolder[-1])
    except FileNotFoundError:
        pass
    os.getcwd()
    rate = 0
    try:
        totalRate = getTotalRate()
    except ZeroDivisionError:
        totalRate = 0
    print(f,totalRate[-1])
    x.append(f)
    y.append(totalRate[-100])
    os.chdir(workingPath)
    i += 1

p = inf.getInputParameters(glob.glob("*/output*")[0])

fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,3.5))
fig.subplots_adjust(top=0.85, bottom=0.15, left=0.15, right=0.95, hspace=0.25,
                    wspace=0.35)
plot(x,y,p,ax,"total rate","x")
#labels=[r"$R_a$ (adsorption)", r"$R_d$ (desorption)", r"$R_r$ (reaction)", r"$R_h$ (diffusion)", "NO", "N2", "TOF"]
#markers=["o", "+","x","1","s","d","h","v"]
#for i in range(0,len(y2[0])):
#    plot(x, y2[:,i], p, ax, labels[i], markers[i+1])
mp.setY2TemperatureLabels(ax,kb)
fig.savefig("totalRate.pdf")#, bbox_inches='tight')
