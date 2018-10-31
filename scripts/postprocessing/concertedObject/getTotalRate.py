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
from  Info import getIndexFromCov

import pdb
        
kb = 8.6173324e-5
mMsr = 127

def getTotalRate():
    files = glob.glob("dataAe0*.txt")
    files.sort()
    totalRate = 0
    #allRates = np.zeros((20,mMsr))
    allRates = np.full((20,mMsr), np.nan)
    rates = np.zeros(4)
    indexes = list(range(8,12)) + [21, 22]
    filesLenght = 0

    for i,t in enumerate(files):
        data = np.loadtxt(t,comments=['#', '[', 'h'])
        events = data[:,7] # column number 8 is "number of events"
        try:
            totalRate += events #/ data[:,1] # last time, column 2
            allRates[i] = events
            coverages = data[:,0]
            filesLenght += 1
        except ValueError:
            continue

    totalRate = totalRate / filesLenght
    return totalRate, allRates, coverages

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

def plotSome(x, y, p, ax, i):
    cm = plt.get_cmap('tab20')
    x = 1 / kb / x
    y = y / (p.sizI * p.sizJ)
    ax.plot(x, y,marker=".", color=cm(i/20))
    ax.set_yscale("log")

def plotError(x, y, p, ax):
    ax.boxplot(y)

workingPath = os.getcwd()
x = []
y = []
temperatures = True
try:
    mIter = inf.getTemperatures()
except ValueError:
    mIter = inf.getPressures()

y2 = np.zeros(shape=(len(mIter),7))
print(np.shape(y2))
if mIter[0] < 15:
    temperatures = False
i = 0

allRates = np.zeros((len(mIter), 20, mMsr))
for i,f in enumerate(mIter):
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
        totalRate, allRatesT, coverages = getTotalRate()
        allRates[i] = allRatesT
    except ZeroDivisionError:
        totalRate = 0
    cov = 0.1
    index = getIndexFromCov(coverages, 0.01)
    print(f,totalRate[index])
    x.append(f)
    y.append(totalRate[index])
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
fig.savefig("totalRate.svg")#, bbox_inches='tight')
plt.close(fig)

fig, ax = plt.subplots(1, 1, sharey=True, figsize=(8.8,12))
fig.subplots_adjust(top=0.85, bottom=0.15, left=0.15, right=0.95, hspace=0.25,
                    wspace=0.35)
for i,f in enumerate(mIter):
    y = allRates[i,:,index]
    for j,value in enumerate(y):
        plotSome(f,value,p,ax,j)

#ax.set_xlim(37,100)
#ax.set_ylim(1e2,2e5)
mp.setY2TemperatureLabels(ax,kb)
fig.savefig("allTotalRate.svg")#, bbox_inches='tight')
plt.close(fig)
    
fig, ax = plt.subplots(1, 1, sharey=True, figsize=(6,4))
fig.subplots_adjust(top=0.85, bottom=0.15, left=0.15, right=0.95, hspace=0.25,
                    wspace=0.35)

y = allRates[::-1,:,index] / (p.sizI * p.sizJ)
y2 = []
for i in range(0,len(y)): # filters NaN values
    y2.append(y[i][~np.isnan(y[i])])

ax.boxplot(np.transpose(y2), showmeans=True, meanline=True, showfliers=False)
ax.set_xticklabels(mIter[::-1],rotation=45, fontsize=8)
ax.set_yscale("log")
ax.set_xlabel("Temperature (K)")
fig.savefig("allTotalRateErrorBars.svg")#, bbox_inches='tight')
plt.close(fig)
    
