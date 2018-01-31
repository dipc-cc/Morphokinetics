#!/usr/bin/env python3

# Copyright (C) 2018 J. Alberdi-Rodriguez
#
# This file is part of Morphokinetics.
#
# Morphokinetics is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Morphokinetics is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.


import matplotlib
matplotlib.use("Agg")
import os
import glob
import sys
import numpy as np
import info as inf
import energies as e
import matplotlib.pyplot as plt
from matplotlib.ticker import FixedFormatter
import multiplicitiesPlot as mp
import multiplicitiesInfo as mi


def defineTypesLabels():
    code = ["0",
             "1",
             "2,0",
             "2,1",
             "2,2",
             "3,0",
             "3,1",
             "3,2",
             "4,0",
             "4,1",
             "4,2",
             "5"]
    types = []
    for i in range(0,12):
        types.append(r"D_{"+code[i]+r"}")
    # detachs
    types.append(r"D_{1}'")
    types.append(r"D_{2,0}'")
    types.append(r"D_{2,1}'")
    types.append(r"D_{3,0}'")
    return types

def getLabel(i,j):
    types = defineTypesLabels()
    label = r"$" + types[i] + r"\rightarrow " + types[j] + r"$"
    return label

temperatures = inf.getTemperatures()
maxRanges = len(temperatures)
kb = 8.6173324e-5
p = inf.getInputParameters(inf.getLastOutputFile("*"))
print("Reference file is ",inf.getLastOutputFile("*"))
ext = ""
sp = False
omegas = False
if len(sys.argv) > 1:
    sp = "p" in sys.argv[1]
    omegas = "o" in sys.argv[1]

if p.nCo2 == -1: # dirty way to use same script for growth and catalysis
    files = glob.glob("*/results/run*/dataAePossibleFromList000.txt")
    files.sort()
    matrix = np.loadtxt(fname=files[0])
    p.mCov = len(matrix)
    p.mCov = 10
    #p.nCo2 = maxCo2 * 10

labelAlfa = []
for i in range(0,12):
    for j in range(0,16):
        labelAlfa.append(getLabel(i,j))
for i in range(0,9):
    labelAlfa.append(r"$I_{"+str(i)+"}$")
for i in range(0,4):
    labelAlfa.append(r"$M_{"+str(i)+"}$")

energies = e.concertedEnergies(p)
print(energies)
workingPath = os.getcwd()
tempMavg, omega, totalRate, totalRateEvents, rates, ratios = mi.getMavgAndOmega(p,temperatures,workingPath)
os.chdir(workingPath)
activationEnergy, multiplicityEa = mi.getMultiplicityEa(p,temperatures,labelAlfa,sp,tempMavg,omega,totalRateEvents,ext)
os.chdir(workingPath)

for i in range(0,p.mMsr):
    mp.plotTotalRates(1/kb/temperatures, totalRateEvents[i], totalRate[i], i)

print(np.shape(tempMavg))

ratioEa = np.zeros(shape=(p.mCov,maxRanges,p.maxA-p.minA))
for i,a in enumerate(range(p.minA,p.maxA)):
    ratioEa[:,:,i] = energies[a]

fig, axarr = plt.subplots(1, maxRanges, sharey=True, figsize=(maxRanges,4))
fig.subplots_adjust(wspace=0.1)
activationEnergyC = np.sum(omega*(ratioEa-multiplicityEa), axis=2)
    
axarr[0].set_ylabel("eV")
minCov = 0

cov = list(range(minCov,p.mCov-1))
tgt = []
rct = []
x = []
err = []
for i in range(0,maxRanges): # different temperature ranges (low, medium, high)
    rcmpt = activationEnergyC[minCov:-1,maxRanges-1-i]
    targt = activationEnergy[minCov:-1,maxRanges-1-i]
    error = abs(1-activationEnergyC[minCov:-1,maxRanges-1-i]/activationEnergy[minCov:-1,maxRanges-1-i])
    handles = mp.plotSimple(cov, targt, rcmpt, error, axarr[i],
                             maxRanges, i, not omegas)
    x.append(1/kb/temperatures[maxRanges-1-i])
    tgt.append(targt[-1])
    rct.append(rcmpt[-1])
    err.append(error[-1])

plt.savefig("multiplicities"+ext+".svg", bbox_inches='tight')

minCov = 0
cov = list(range(minCov,p.mCov-1))

lastOmegas = np.zeros(shape=(maxRanges,p.maxA-p.minA))
epsilon = np.zeros(shape=(p.mCov,maxRanges,p.maxA-p.minA))
if omegas:
    cov.append(p.mCov)
    # cm = plt.get_cmap('tab20')
    # for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
    #     partialSum = np.sum(omega[:,j,:]*(ratioEa[:,j,:]-multiplicityEa[:,j,:]), axis=1)
    #     lgs = []
    #     for i,a in enumerate(range(p.minA,p.maxA)): #alfa
    #         if any(omega[:,j,a] > 1e-2):
    #             #print(j,a,omega[:,j,a])
    #             lgs.append(axarr[maxRanges-1-j].fill_between(cov, partialSum, color=cm(a/(p.maxA-1)), label=labelAlfa[a]))
    #             lastOmegas[maxRanges-1-j,i] = partialSum[-1]
    #             partialSum -= omega[:,j,i]*(ratioEa[:,j,i]-multiplicityEa[:,j,i])
    #             epsilon[:,j,i] = omega[:,j,i]*(ratioEa[:,j,i]-multiplicityEa[:,j,i])

    # myLegends = []
    # myLabels = []#[r"$E_a$", r"$E^f + \sum_\alpha \;\epsilon_\alpha$"]
    # myLegends += lgs
        
    # for i in range(p.maxA-1,p.minA-1,-1): #alfa
    #     myLabels.append(labelAlfa[i])
    # myLabels.append("Rel. err.")
    # plt.figlegend(myLegends, myLabels, loc=(0.68,0.15), prop={'size':11})
    # plt.savefig("multiplicitiesOmegas"+ext+".svg", bbox_inches='tight')

    cm = plt.get_cmap('tab20')
    markers=["o", "s","D","^","d","h","p"]
    for t in range(0,maxRanges): # different temperature ranges (low, medium, high)
        mk = np.sum(omega[:,t,:]*(ratioEa[:,t,:]-multiplicityEa[:,t,:]), axis=1)
        for i,a in enumerate(range(p.minA,p.maxA)): #alfa
            mk = omega[:,t,a]*(ratioEa[:,t,a]-multiplicityEa[:,t,a])
            if any(omega[:,t,a] > 1e-2):
                axarr[maxRanges-1-t].plot(cov, mk, ls="", label=labelAlfa[a],color=cm(abs((a%20)/20)), alpha=0.75, marker=markers[a%7], markersize=0.2)
        axarr[maxRanges-1-t].legend(loc="best",  prop={'size':4})
    #plt.figlegend(myLegends, myLabels, loc=(0.68,0.15), prop={'size':11})
    plt.savefig("multiplicitiesOmegas"+ext+"2.svg", bbox_inches='tight')


figR, ax = plt.subplots(1, figsize=(5,3))
figR.subplots_adjust(top=0.85,left=0.15,right=0.95,bottom=0.15)
ax.plot(x, tgt, label=r"$E_{app}$", color="red")
ax.plot(x, rct, "--", label=r"$\sum \epsilon_\alpha$")
cm = plt.get_cmap('tab20')
markers=["o", "s","D","^","d","h","p"]
for i,a in enumerate(range(p.minA,p.maxA)):
    if any(abs(epsilon[-1,::-1,i]) > 0.005):
        #ax.plot(x, epsilon[-1,::-1,i], label=labelAlfa[a], color=cm(abs(i/20)), marker=markers[i%8])
        ax.fill_between(x, lastOmegas[:,i], label=labelAlfa[a], color=cm(a%20/(19)))
# ax2 = ax.twinx()
# ax2.plot(x, err, label="Relative error")
#ax.set_ylim(0,0.1)
#ax.set_xlim(20,30)
#labels = [item for item in ax.get_xticklabels()]
#labels[1] = 'Testing'
ax.plot(x, abs(np.array(tgt)-np.array(rct)), label="Absolute error", color="black")
ax.legend(loc="best", prop={'size':6})
#ax.set_xticklabels(labels)
ax.set_xlabel(r"$1/k_BT$")
ax.set_ylabel(r"Energy $(eV)$")
#ax.set_yscale("log")
mp.setY2TemperatureLabels(ax,kb)
ax.annotate(r"$\epsilon_\alpha=\omega_\alpha(E^k_\alpha+E^{k0}_\alpha+E^M_\alpha)$", xy=(0.45,0.2), xycoords="axes fraction")
plt.savefig("multiplicitiesResume"+ext+".svg")#, bbox_inches='tight')

