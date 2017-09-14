import sys
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
kb = 8.6173324e-5
p = inf.getInputParameters(glob.glob("*/output*")[0])
maxCo2 = int(p.nCo2/10)
maxAlfa = 20 # I think is correct
labelAlfa = [r"$CO^B+O^B\rightarrow CO_2$",r"$CO^B+O^C\rightarrow CO_2$",r"$CO^C+O^B\rightarrow CO_2$",r"$CO^C+O^C\rightarrow CO_2$", #Reaction
             r"$V\rightarrow CO$",r"$V\rightarrow O$", # Adsorption
             r"$CO^B\rightarrow V$",r"$CO^C\rightarrow V$", # Desorption CO
             r"$O^B+O^B\rightarrow V^B+V^B$",r"$O^B+O^C\rightarrow V^B+V^C$",r"$O^C+O^B\rightarrow V^C+V^B$",r"$O^C+O^C\rightarrow V^C+V^C$", # Desorption O
             r"$CO^B\rightarrow CO^B$",r"$CO^B\rightarrow CO^C$",r"$CO^C\rightarrow CO^B$",r"$CO^C\rightarrow CO^C$",  # Diffusion CO
             r"$O^B\rightarrow O^B$",r"$O^B\rightarrow O^C$",r"$O^C\rightarrow O^B$",r"$O^C\rightarrow O^C$"] # Diffusion O
energies = e.catalysisEnergiesTotal(p)
tempMavg = []
tempOavg = []
tempRavg = []

workingPath = os.getcwd()
for t in temperatures:
    print(t)
    os.chdir(workingPath)
    try:
        os.chdir(str(t)+"/results")
        runFolder = glob.glob("*/");
        runFolder.sort()
        os.chdir(runFolder[-1])
    except FileNotFoundError:
        continue
    tmp1, tmp2, tmp3 = mi.computeMavgAndOmegaOverRuns(total=True)
    tempMavg.append(tmp1)
    tempOavg.append(tmp2)
    tempRavg.append(tmp3)
   
os.chdir(workingPath) 
tempMavg = np.array(tempMavg)
tempOavg = np.array(tempOavg)
tempRavg = np.array(tempRavg)

print(np.shape(tempMavg))
sp=False
if len(sys.argv) > 1:
    sp = sys.argv[1] == "p"

tempOmegaCo2 = []
tempEaMCo2 = []
tempEaCo2 = []
tempEafCo2 = []
rngt = e.defineRangesCatalysis(p.calc, p.rLib, temperatures) #list([0, 3])

maxRanges = len(temperatures)
for co2 in range(0,maxCo2): # created co2: 10,20,30...1000
    showPlot = sp and float(co2+(maxCo2/10)+1) % float(maxCo2/10) == 0
    if float(co2+(maxCo2/10)+1) % float(maxCo2/10) == 0:
        print(co2)
    x = 1/kb/temperatures
    y = tempRavg
    if showPlot:
        fig, axarr = plt.subplots(3, sharex=True, figsize=(5,6))
        fig.subplots_adjust(right=0.7, hspace=0.1)
    else:
        axarr = np.zeros(3)
    # N_h
    tempEaCo2.append(mp.localAvgAndPlotLinear(x, y[:,co2], axarr[0], -1, showPlot, co2))
    tempOmega = np.zeros((maxAlfa,maxRanges))
    tempEaM = []
    
    for i in range(0,maxAlfa): # alfa
        y = np.sum(tempMavg[:,co2,i:i+1], axis=1)
        tempEaM.append(mp.localAvgAndPlotLinear(x, y, axarr[1], i, showPlot, co2))
        if showPlot:
            y = np.sum(tempOavg[:,co2,i:i+1], axis=1)
            mp.plotOmegas(x, y, axarr[-1], i, tempOmega[i], rngt, labelAlfa)
        tempOmega[i] = list(tempOavg[:, co2, i:i+1])
    
    tempOmegaCo2.append(tempOmega)
    tempEaMCo2.append(tempEaM)
    if showPlot:
        plt.savefig("plot"+str(co2)+".svg", bbox_inches='tight')
        plt.close()

tempOmegaCo2 = np.array(tempOmegaCo2) # [co2, type (alfa), temperature range]
tempEaCo2 = -np.array(tempEaCo2) # [co2, temperature range]
tempEaMCo2 = np.array(tempEaMCo2) # [co2, type (alfa), temperature range]
tempEaRCo2 = np.zeros(np.shape(tempEaMCo2))
for alfa in range(0,maxAlfa):
    tempEaRCo2[:,alfa,:] = energies[alfa]

fig, axarr = plt.subplots(1, maxRanges, sharey=True, figsize=(maxRanges,4))
fig.subplots_adjust(wspace=0.1)
tempEaCov2 = np.sum(tempOmegaCo2*(tempEaRCo2-tempEaMCo2), axis=1)

rAndM = False
omegas = False
if len(sys.argv) > 1:
    rAndM = sys.argv[1] == "r"
    omegas = sys.argv[1] == "o"
    
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

plt.savefig("multiplicities.png", bbox_inches='tight')


if (rAndM): # plot total activation energy as the sum of ratios and multiplicities
    label = ["multiplicity", "sum", "ratio"]
    cm = plt.get_cmap('Accent')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        partialSum1 = np.sum(tempOmegaCo2[:,:,j]*(-tempEaMCo2[:,:,j]), axis=1)
        partialSum2 = np.sum(tempOmegaCo2[:,:,j]*(tempEaRCo2[:,:,j]), axis=1)
        mp.plotRandM(co2, partialSum1, partialSum2, axarr[maxRanges-1-j],
                     handles, j == maxRanges-1)

    plt.savefig("multiplicitiesRandM.png", bbox_inches='tight')

lastOmegas = np.zeros(shape=(maxRanges,maxAlfa))
if (omegas):
    co2.append(maxCo2)
    labels = ["0", "20", "40", "60", "80", "100"]
    cm = plt.get_cmap('tab20c')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        axarr[maxRanges-1-j].get_xaxis().set_major_formatter(FixedFormatter(labels))
        partialSum = np.sum(tempOmegaCo2[:,:,j]*(tempEaRCo2[:,:,j]-tempEaMCo2[:,:,j]), axis=1)
        lgs = []
        for i in range(0,maxAlfa): #alfa
            lgs.append(axarr[maxRanges-1-j].fill_between(co2, partialSum, color=cm(i/(maxAlfa-1)), label=labelAlfa[i]))
            lastOmegas[maxRanges-1-j][i] = partialSum[-1]
            partialSum -= tempOmegaCo2[:,i,j]*(tempEaRCo2[:,i,j]-tempEaMCo2[:,i,j])
    
    myLegends = []
    myLabels = []#[r"$E_a$", r"$E^f + \sum_\alpha \;\epsilon_\alpha$"]
    myLegends += lgs
        
    for i in range(maxAlfa-1,-1,-1): #alfa
        myLabels.append(labelAlfa[i])
    myLabels.append("Rel. err.")
    plt.figlegend(myLegends, myLabels, loc=(0.68,0.15), prop={'size':11})
    plt.savefig("multiplicitiesOmegasP.png", bbox_inches='tight')

figR, ax = plt.subplots(1, figsize=(5,4))
ax.plot(x, tgt, label="target", color="red")
ax.plot(x, rct, "--", label="recomputed")
cm = plt.get_cmap('tab20c')
for i in range(0,maxAlfa):
    #ax.plot(x, lastOmegas[:,i], "--", label=i)
    ax.fill_between(x, lastOmegas[:,i], label=labelAlfa[i], color=cm(i/(maxAlfa-1)))
# ax2 = ax.twinx()
# ax2.plot(x, err, label="Relative error")
# ax2.set_ylim(0,1)
ax.plot(x, abs(np.array(tgt)-np.array(rct)), label="Absolute error")
ax.legend(loc=(1.10,0.0), prop={'size':6})
#ax.set_yscale("log")
plt.savefig("multiplicitiesResume.svg", bbox_inches='tight')
