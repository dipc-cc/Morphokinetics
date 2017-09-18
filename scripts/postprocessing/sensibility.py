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
maxCo2 = 100
total = True
if total:
    maxAlfa = 20
    p.maxA = 20
    energies = e.catalysisEnergiesTotal(p)
else:
    maxAlfa = 4
    energies = e.catalysisEnergies(p)
labelAlfa = [r"$CO^B+O^B\rightarrow CO_2$",r"$CO^B+O^C\rightarrow CO_2$",r"$CO^C+O^B\rightarrow CO_2$",r"$CO^C+O^C\rightarrow CO_2$", #Reaction
             r"$V\rightarrow CO$",r"$V\rightarrow O$", # Adsorption
             r"$CO^B\rightarrow V$",r"$CO^C\rightarrow V$", # Desorption CO
             r"$O^B+O^B\rightarrow V^B+V^B$",r"$O^B+O^C\rightarrow V^B+V^C$",r"$O^C+O^B\rightarrow V^C+V^B$",r"$O^C+O^C\rightarrow V^C+V^C$", # Desorption O
             r"$CO^B\rightarrow CO^B$",r"$CO^B\rightarrow CO^C$",r"$CO^C\rightarrow CO^B$",r"$CO^C\rightarrow CO^C$",  # Diffusion CO
             r"$O^B\rightarrow O^B$",r"$O^B\rightarrow O^C$",r"$O^C\rightarrow O^B$",r"$O^C\rightarrow O^C$"] # Diffusion O

workingPath = os.getcwd()
tempMavg, tempOavg, tempRavg = mi.getMavgAndOmega(temperatures,workingPath,total)
os.chdir(workingPath)

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
print(maxAlfa)
for alfa in range(0,maxAlfa):
    tempEaRCo2[:,alfa,:] = energies[alfa]

fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,4))
fig.subplots_adjust(wspace=0.1)
tempEaCov2 = np.sum(tempOmegaCo2*(tempEaRCo2-tempEaMCo2), axis=1)
B = np.sum(tempOmegaCo2*(tempEaRCo2),axis=1)
A = tempEaCo2
C = energies# + kb * temperatures#corrections
D = np.sum(tempOmegaCo2)
correction = np.zeros(shape=(maxAlfa,len(temperatures)))
correction[0:4,:] = kb*temperatures
if total:
    correction[4:6,:] = -kb*temperatures/2.0
    correction[6:8,:] = 3.0*kb*temperatures+e.getDesorptionCorrection(temperatures,0)
    correction[8:12,:] = 3.0*kb*temperatures+e.getDesorptionCorrection(temperatures,1)
    correction[12:20,:] = kb*temperatures
    correction = np.zeros(shape=(20,len(temperatures)))
correctionReaction = kb * temperatures
sensibilityCo2 = []
for i in range(0,maxAlfa):
    sensibilityCo2.append(tempOmegaCo2[:,i,:] + (A-B)/(C[i]+correction[i]))
sensibilityCo2 = np.array(sensibilityCo2)

cm = plt.get_cmap('tab20')
markers=["o", "s","D","^","d","h","p","o"]
for i in range(0,maxAlfa):
    axarr.plot(1000/temperatures, sensibilityCo2[i,-1,:], label=labelAlfa[i],color=cm(abs(i/20)), marker=markers[i%8] )

#axarr.set_ylim(-2,6)
axarr.legend(loc=(1.10,0.0), prop={'size':6})
fig.savefig("sensibility.svg", bbox_inches='tight')

