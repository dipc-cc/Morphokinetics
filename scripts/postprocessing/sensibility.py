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
total = False
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


tempOmegaCo2, tempEaCo2, tempEaMCo2, tempEaRCo2 = mi.getEaMandEaR(p,temperatures,labelAlfa,sp,tempMavg,tempOavg,tempRavg)
for alfa in range(0,maxAlfa):
    tempEaRCo2[:,alfa,:] = energies[alfa]
maxAlfa = 20
p.maxA = 20
energies = e.catalysisEnergiesTotal(p)
tempMavgT, tempOavgT, tempRavgT = mi.getMavgAndOmega(temperatures,workingPath,total=True)
tempOmegaCo2T, tempEaCo2T, tempEaMCo2T, tempEaRCo2T = mi.getEaMandEaR(p,temperatures,labelAlfa,sp,tempMavgT,tempOavgT,tempRavgT)

tempOmegaCo2T[:,4:,:] = 0
tempOmegaCo2T[:,0:4,:] = tempOmegaCo2
tempEaMCo2T[:,4:,:] = 0
tempEaMCo2T[:,0:4,:] = tempEaMCo2

fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,4))
fig.subplots_adjust(wspace=0.1)
#B1 = np.sum(tempOmegaCo2T*(tempEaMCo2T),axis=1)
#A1 = 0
B = np.sum(tempOmegaCo2T*(tempEaRCo2T),axis=1)
A = tempEaCo2
C = energies# + kb * temperatures#corrections
correction = np.zeros(shape=(maxAlfa,len(temperatures)))
correction[0:4,:] = kb*temperatures
if True:
    correction[4:6,:] = -kb*temperatures/2.0 #Adsorption
    correction[6:8,:] = 3.0*kb*temperatures+e.getDesorptionCorrection(temperatures,0)
    correction[8:12,:] = 3.0*kb*temperatures+e.getDesorptionCorrection(temperatures,1)
    correction[12:20,:] = kb*temperatures
sensibilityCo2 = []
print("i")
for i in range(0,maxAlfa):
    sensibilityCo2.append(tempOmegaCo2T[:,i,:] + (A-B)/(C[i]+correction[i,:]))
sensibilityCo2 = np.array(sensibilityCo2)
print("o")

cm = plt.get_cmap('tab20')
markers=["o", "s","D","^","d","h","p","o"]
for i in range(0,maxAlfa):
    axarr.plot(1000/temperatures, sensibilityCo2[i,-1,:], label=labelAlfa[i],color=cm(abs(i/20)), marker=markers[i%8] )

#axarr.set_ylim(-2,6)
axarr.legend(loc=(1.10,0.0), prop={'size':6})
os.chdir(workingPath)
plt.savefig("sensibility.svg", bbox_inches='tight')

