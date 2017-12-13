#!/usr/bin/env python3
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


temperatures = inf.getTemperatures("float")
maxRanges = len(temperatures)
kb = 8.6173324e-5
p = inf.getInputParameters(inf.getLastOutputFile("*"))
print("Reference file is ",inf.getLastOutputFile("*"))
p.maxA = 49
p.minA = 0
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
    maxCo2 = len(matrix)
    p.nCo2 = maxCo2 * 10
labelAlfa = [r"$D_0\rightarrow D_0$", r"$D_0\rightarrow D_1$", r"$D_0\rightarrow D_2$", r"$D_0\rightarrow D_3$", r"$D_0\rightarrow D_4$", r"$D_0\rightarrow D_5$", r"$D_0\rightarrow D_6$", 
r"$D_1\rightarrow D_0$", r"$D_1\rightarrow D_1$", r"$D_1\rightarrow D_2$", r"$D_1\rightarrow D_3$", r"$D_1\rightarrow D_4$", r"$D_1\rightarrow D_5$", r"$D_1\rightarrow D_6$", 
r"$D_2\rightarrow D_0$", r"$D_2\rightarrow D_1$", r"$D_2\rightarrow D_2$", r"$D_2\rightarrow D_3$", r"$D_2\rightarrow D_4$", r"$D_2\rightarrow D_5$", r"$D_2\rightarrow D_6$", 
r"$D_3\rightarrow D_0$", r"$D_3\rightarrow D_1$", r"$D_3\rightarrow D_2$", r"$D_3\rightarrow D_3$", r"$D_3\rightarrow D_4$", r"$D_3\rightarrow D_5$", r"$D_3\rightarrow D_6$", 
r"$D_4\rightarrow D_0$", r"$D_4\rightarrow D_1$", r"$D_4\rightarrow D_2$", r"$D_4\rightarrow D_3$", r"$D_4\rightarrow D_4$", r"$D_4\rightarrow D_5$", r"$D_4\rightarrow D_6$", 
r"$D_5\rightarrow D_0$", r"$D_5\rightarrow D_1$", r"$D_5\rightarrow D_2$", r"$D_5\rightarrow D_3$", r"$D_5\rightarrow D_4$", r"$D_5\rightarrow D_5$", r"$D_5\rightarrow D_6$", 
r"$D_6\rightarrow D_0$", r"$D_6\rightarrow D_1$", r"$D_6\rightarrow D_2$", r"$D_6\rightarrow D_3$", r"$D_6\rightarrow D_4$", r"$D_6\rightarrow D_5$", r"$D_6\rightarrow D_6$"]

energies = e.concertedEnergies(p)
print(energies)
workingPath = os.getcwd()
tempMavg, omega, totalRate, totalRateEvents, rates, ratios = mi.getMavgAndOmega(p,temperatures,workingPath)
os.chdir(workingPath)
activationEnergy, multiplicityEa = mi.getMultiplicityEa(p,temperatures,labelAlfa,sp,tempMavg,omega,totalRateEvents,ext)
os.chdir(workingPath)

fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,4))
fig.subplots_adjust(wspace=0.1)
axarr.plot(1/kb/temperatures, totalRateEvents[-1], "x",label="Total rate from events")
axarr.plot(1/kb/temperatures, totalRate[-2], "o",label="Total rate from M")
#axarr.plot(1/kb/temperatures, abs(totalRateEvents[-1]-totalRate[-1]), label="Error abs", ls=":")
#axarr.plot(1/kb/temperatures, abs(totalRateEvents[-1]-totalRate[-1])/totalRateEvents[-1], label="Error rel",ls=":")
axarr.set_yscale("log")
#axarr.set_ylim(1e6,1e7)
axarr.legend(loc="best", prop={'size':6})
fig.savefig("totalRates.svg",  bbox_inches='tight')

print(np.shape(tempMavg))

activationEnergy, multiplicityEa = mi.getMultiplicityEa(p,temperatures,labelAlfa,sp,tempMavg,omega,totalRateEvents,ext)
ratioEa = np.zeros(shape=(maxCo2,maxRanges,p.maxA-p.minA))
for i,a in enumerate(range(p.minA,p.maxA)):
    ratioEa[:,:,i] = energies[a]

#ratioEa[:,:,0:maxAlfa-minAlfa] += e.getEaCorrections(p,temperatures)[:,minAlfa:maxAlfa]

# fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,4))
# fig.subplots_adjust(wspace=0.1)
# axarr.plot(1/kb/temperatures, totalRate[-1], label="Total rate from M")
# for i in range(p.minA,p.maxA):
#     axarr.plot(1/kb/temperatures, tempMavg[-1,:,i], label=labelAlfa[i])
# axarr.set_yscale("log")
# fig.savefig("Mk.svg",  bbox_inches='tight')

fig, axarr = plt.subplots(1, maxRanges, sharey=True, figsize=(maxRanges,4))
fig.subplots_adjust(wspace=0.1)
activationEnergyC = np.sum(omega*(ratioEa-multiplicityEa), axis=2)
    
axarr[0].set_ylabel("eV")
minCo2 = 0

co2 = list(range(minCo2,maxCo2-1))
tgt = []
rct = []
x = []
err = []
for i in range(0,maxRanges): # different temperature ranges (low, medium, high)
    rcmpt = activationEnergyC[minCo2:-1,maxRanges-1-i]
    targt = activationEnergy[minCo2:-1,maxRanges-1-i]
    error = abs(1-activationEnergyC[minCo2:-1,maxRanges-1-i]/activationEnergy[minCo2:-1,maxRanges-1-i])
    handles = mp.plotSimple(co2, targt, rcmpt, error, axarr[i],
                             maxRanges, i, not omegas)
    x.append(1/kb/temperatures[maxRanges-1-i])
    tgt.append(targt[-1])
    rct.append(rcmpt[-1])
    err.append(error[-1])

plt.savefig("multiplicities"+ext+".svg", bbox_inches='tight')

minCo2 = 0
#co2 = list(range(minCo2,p.nCo2-1))
co2 = list(range(minCo2,maxCo2-1))

lastOmegas = np.zeros(shape=(maxRanges,p.maxA-p.minA))
#lastM = np.zeros(shape=(maxRanges
epsilon = np.zeros(shape=(maxCo2,maxRanges,p.maxA-p.minA))
if omegas:
    co2.append(maxCo2)
    labels = ["0", "20", "40", "60", "80", "100"]
    cm = plt.get_cmap('tab20')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        axarr[maxRanges-1-j].get_xaxis().set_major_formatter(FixedFormatter(labels))
        partialSum = np.sum(omega[:,j,:]*(ratioEa[:,j,:]-multiplicityEa[:,j,:]), axis=1)
        lgs = []
        for i,a in enumerate(range(p.minA,p.maxA)): #alfa
            lgs.append(axarr[maxRanges-1-j].fill_between(co2, partialSum, color=cm(a/(p.maxA-1)), label=labelAlfa[a]))
            lastOmegas[maxRanges-1-j,i] = partialSum[-1]
            partialSum -= omega[:,j,i]*(ratioEa[:,j,i]-multiplicityEa[:,j,i])
            epsilon[:,j,i] = omega[:,j,i]*(ratioEa[:,j,i]-multiplicityEa[:,j,i])

    myLegends = []
    myLabels = []#[r"$E_a$", r"$E^f + \sum_\alpha \;\epsilon_\alpha$"]
    myLegends += lgs
        
    for i in range(p.maxA-1,p.minA-1,-1): #alfa
        myLabels.append(labelAlfa[i])
    myLabels.append("Rel. err.")
    plt.figlegend(myLegends, myLabels, loc=(0.68,0.15), prop={'size':11})
    plt.savefig("multiplicitiesOmegas"+ext+".svg", bbox_inches='tight')


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
plt.savefig("multiplicitiesResume"+ext+".pdf")#, bbox_inches='tight')
