import sys
import re
import info as inf
import energies as e
import matplotlib.pyplot as plt
from matplotlib.ticker import FixedFormatter
import os
import glob
import numpy as np
import multiplicitiesPlot as mp
import multiplicitiesInfo as mi


##########################################################
##########           Main function   #####################
##########################################################

temperatures = inf.getTemperatures("float")
maxRanges = len(temperatures)
kb = 8.6173324e-5
p = inf.getInputParameters(glob.glob("*/output*")[0])
maxCo2 = int(p.nCo2/10)
total = False
sp = False
rAndM = False
omegas = False
sensibility = False
tofSensibility = False
ext = ""
if len(sys.argv) > 1:
    total = "t" in sys.argv[1]
    sp = "p" in sys.argv[1]
    rAndM = "r" in sys.argv[1]
    omegas = "o" in sys.argv[1]
    sensibility = "s" in sys.argv[1]
    tofSensibility = "f" in sys.argv[1]
if total:
    minAlfa = 0
    maxAlfa = 20
    p.maxA = 20
    energies = e.catalysisEnergiesTotal(p)
    ext = "T"
else:
    minAlfa = 0
    maxAlfa = 4
    p.minA = 0
    p.maxA = 4
    energies = e.catalysisEnergies(p)
labelAlfa = [r"$CO^B+O^B\rightarrow CO_2$",r"$CO^B+O^C\rightarrow CO_2$",r"$CO^C+O^B\rightarrow CO_2$",r"$CO^C+O^C\rightarrow CO_2$", #Reaction
             r"$V\rightarrow CO$",r"$V\rightarrow O$", # Adsorption
             r"$CO^B\rightarrow V$",r"$CO^C\rightarrow V$", # Desorption CO
             r"$O^B+O^B\rightarrow V^B+V^B$",r"$O^B+O^C\rightarrow V^B+V^C$",r"$O^C+O^B\rightarrow V^C+V^B$",r"$O^C+O^C\rightarrow V^C+V^C$", # Desorption O
             r"$CO^B\rightarrow CO^B$",r"$CO^B\rightarrow CO^C$",r"$CO^C\rightarrow CO^B$",r"$CO^C\rightarrow CO^C$",  # Diffusion CO
             r"$O^B\rightarrow O^B$",r"$O^B\rightarrow O^C$",r"$O^C\rightarrow O^B$",r"$O^C\rightarrow O^C$"] # Diffusion O

workingPath = os.getcwd()
tempMavg, tempOavg, tempRavg = mi.getMavgAndOmega(p,temperatures,workingPath,total)
os.chdir(workingPath)

print(np.shape(tempMavg))

tempOmegaCo2, tempEaCo2, tempEaMCo2, tempEaRCo2 = mi.getEaMandEaR(p,temperatures,labelAlfa,sp,tempMavg,tempOavg,tempRavg)
for alfa in range(minAlfa,maxAlfa):
    tempEaRCo2[:,alfa,:] = energies[alfa]
tempEaRCo2 += e.getEaCorrections(temperatures)[minAlfa:maxAlfa]

if tofSensibility:
    sensibilityCo2 = []
    sumBeta = 0
    sumOmegaBeta = 0
    for beta in range(0,4):
        sumBeta += tempOmegaCo2[:,beta,:]*(tempEaRCo2[:,beta,:]-tempEaMCo2[:,beta,:])
        sumOmegaBeta += tempOmegaCo2[:,beta,:]
    for alfa in range(minAlfa,maxAlfa):
        sensibilityCo2.append(tempOmegaCo2[:,alfa,:]/sumOmegaBeta*(sumBeta/tempEaRCo2[:,alfa,:]))
    sensibilityCo2 = np.array(sensibilityCo2)

    mp.plotSensibility(sensibilityCo2,temperatures,labelAlfa,total=False)
    os.chdir(workingPath)

if sensibility:
    sensibilityCo2 = []  
    for i in range(minAlfa,maxAlfa):
        sensibilityCo2.append(tempOmegaCo2[:,i,:]*(1-tempEaMCo2[:,i,:]/tempEaRCo2[:,i,:]))
    sensibilityCo2 = np.array(sensibilityCo2)
    
    mp.plotSensibility(sensibilityCo2,temperatures,labelAlfa,total=True)
    os.chdir(workingPath)


fig, axarr = plt.subplots(1, maxRanges, sharey=True, figsize=(maxRanges,4))
fig.subplots_adjust(wspace=0.1)
tempEaCov2 = np.sum(tempOmegaCo2*(tempEaRCo2-tempEaMCo2), axis=1)

    
axarr[0].set_ylabel("eV")
minCo2 = 0
co2 = list(range(minCo2,maxCo2-1))
tgt = []
rct = []
x = []
err = []
for i in range(0,maxRanges): # different temperature ranges (low, medium, high)
    rcmpt = tempEaCov2[minCo2:-1,maxRanges-1-i]
    targt = tempEaCo2[minCo2:-1,maxRanges-1-i]
    error = abs(1-tempEaCov2[minCo2:-1,maxRanges-1-i]/tempEaCo2[minCo2:-1,maxRanges-1-i])
    handles = mp.plotSimple(co2, targt, rcmpt, error, axarr[i],
                             maxRanges, i, not rAndM and not omegas)
    x.append(1000/temperatures[maxRanges-1-i])
    tgt.append(targt[-1])
    rct.append(rcmpt[-1])
    err.append(error[-1])

plt.savefig("multiplicities"+ext+".svg", bbox_inches='tight')


if (rAndM): # plot total activation energy as the sum of ratios and multiplicities
    label = ["multiplicity", "sum", "ratio"]
    cm = plt.get_cmap('Accent')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        partialSum1 = np.sum(tempOmegaCo2[:,:,j]*(-tempEaMCo2[:,:,j]), axis=1)
        partialSum2 = np.sum(tempOmegaCo2[:,:,j]*(tempEaRCo2[:,:,j]), axis=1)
        mp.plotRandM(co2, partialSum1, partialSum2, axarr[maxRanges-1-j],
                     handles, j == maxRanges-1)

    plt.savefig("multiplicitiesRandM"+ext+".png", bbox_inches='tight')

lastOmegas = np.zeros(shape=(maxRanges,maxAlfa-minAlfa))
if (omegas):
    co2.append(maxCo2)
    labels = ["0", "20", "40", "60", "80", "100"]
    cm = plt.get_cmap('tab20c')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        axarr[maxRanges-1-j].get_xaxis().set_major_formatter(FixedFormatter(labels))
        partialSum = np.sum(tempOmegaCo2[:,:,j]*(tempEaRCo2[:,:,j]-tempEaMCo2[:,:,j]), axis=1)
        lgs = []
        for i in range(minAlfa,maxAlfa): #alfa
            lgs.append(axarr[maxRanges-1-j].fill_between(co2, partialSum, color=cm(i/(maxAlfa-1)), label=labelAlfa[i]))
            lastOmegas[maxRanges-1-j][i] = partialSum[-1]
            partialSum -= tempOmegaCo2[:,i,j]*(tempEaRCo2[:,i,j]-tempEaMCo2[:,i,j])
    
    myLegends = []
    myLabels = []#[r"$E_a$", r"$E^f + \sum_\alpha \;\epsilon_\alpha$"]
    myLegends += lgs
        
    for i in range(maxAlfa-1,minAlfa-1,-1): #alfa
        myLabels.append(labelAlfa[i])
    myLabels.append("Rel. err.")
    plt.figlegend(myLegends, myLabels, loc=(0.68,0.15), prop={'size':11})
    plt.savefig("multiplicitiesOmegas"+ext+".svg", bbox_inches='tight')

figR, ax = plt.subplots(1, figsize=(5,4))
ax.plot(x, tgt, label="target", color="red")
ax.plot(x, rct, "--", label="recomputed")
cm = plt.get_cmap('tab20c')
for i in range(minAlfa,maxAlfa):
    #ax.plot(x, lastOmegas[:,i], "--", label=i)
    ax.fill_between(x, lastOmegas[:,i], label=labelAlfa[i], color=cm(i/(maxAlfa-1)))
# ax2 = ax.twinx()
# ax2.plot(x, err, label="Relative error")
# ax2.set_ylim(0,1)
ax.plot(x, abs(np.array(tgt)-np.array(rct)), label="Absolute error")
ax.legend(loc=(1.10,0.0), prop={'size':6})
#ax.set_yscale("log")
plt.savefig("multiplicitiesResume"+ext+".svg", bbox_inches='tight')
