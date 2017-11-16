#!/usr/bin/env python3
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import sys
import re
import info as inf
import energies as e
from matplotlib.ticker import FixedFormatter
import os
import glob
import numpy as np
import multiplicitiesPlot as mp
import multiplicitiesInfo as mi
import rds

##########################################################
##########           Main function   #####################
##########################################################

temperatures = inf.getTemperatures("float")
maxRanges = len(temperatures)
kb = 8.6173324e-5
p = inf.getInputParameters(inf.getLastOutputFile("*"))
print("Reference file is ",inf.getLastOutputFile("*"))
maxCo2 = int(p.nCo2/10)
total = False
sp = False
rAndM = False
omegas = False
sensibility = False
tofSensibility = False
kindOfSensibility = False
lmbdas = False
ext = ""
if len(sys.argv) > 1:
    total = "t" in sys.argv[1]
    sp = "p" in sys.argv[1]
    rAndM = "r" in sys.argv[1]
    omegas = "o" in sys.argv[1]
    sensibility = "s" in sys.argv[1]
    tofSensibility = "f" in sys.argv[1]
    kindOfSensibility = "k" in sys.argv[1]
    lmbdas = "l" in sys.argv[1]
if total:
    minAlfa = 0
    maxAlfa = 20
    p.maxA = 20
    if len(sys.argv) > 3:
        minAlfa = int(sys.argv[2])
        maxAlfa = int(sys.argv[3])
    p.minA = minAlfa
    p.maxA = maxAlfa
    ext = "T"
else:
    minAlfa = 0
    maxAlfa = 4
    if len(sys.argv) > 3:
        minAlfa = int(sys.argv[2])
        maxAlfa = int(sys.argv[3])
    p.minA = minAlfa
    p.maxA = maxAlfa
    ratesI = 2 # reaction

energies = e.catalysisEnergiesTotal(p)
labelAlfa = [r"$CO_B+O_B\rightarrow CO_2$",r"$CO_B+O_C\rightarrow CO_2$",r"$CO_C+O_B\rightarrow CO_2$",r"$CO_C+O_C\rightarrow CO_2$", #Reaction
             r"$V\rightarrow CO$",r"$V_2 \rightarrow O_2 $", # Adsorption
             r"$CO_B\rightarrow V$",r"$CO_C\rightarrow V$", # Desorption CO
             r"$O_B+O_B\rightarrow V_B+V_B$",r"$O_B+O_C\rightarrow V_B+V_C$",r"$O_C+O_B\rightarrow V_C+V_B$",r"$O_C+O_C\rightarrow V_C+V_C$", # Desorption O
             r"$CO_B\rightarrow CO_B$",r"$CO_B\rightarrow CO_C$",r"$CO_C\rightarrow CO_B$",r"$CO_C\rightarrow CO_C$",  # Diffusion CO
             r"$O_B\rightarrow O_B$",r"$O_B\rightarrow O_C$",r"$O_C\rightarrow O_B$",r"$O_C\rightarrow O_C$", # Diffusion O
             r"$CO_C\rightarrow V$ (1 NN)",r"$CO_C\rightarrow V$ (2 NN)", # Desorption CO
             r"$CO_C+O_B\rightarrow CO_2$ (1 NN)",r"$CO_C+O_B\rightarrow CO_2$ (2 NN)",r"$CO_C+O_C\rightarrow CO_2$ (1 NN)", #Reaction
             r"$CO_C\rightarrow CO_B$ (1 NN)",r"$CO_C\rightarrow CO_B$ (2 NN)",r"$CO_C\rightarrow CO_C$ (1 NN)", # Diffusion CO
             r"$V\rightarrow O$"] # adsorption of one unique O atom, which is not counted in the program
if maxAlfa == 7:  # for farkas TOF
    labelAlfa[4] = labelAlfa[22]
    labelAlfa[5] = labelAlfa[23]
    labelAlfa[6] = labelAlfa[24]
workingPath = os.getcwd()
tempMavg, omega, totalRate, totalRateEvents, rates, ratios = mi.getMavgAndOmega(p,temperatures,workingPath)
print(np.shape(ratios))
if not total:
    totalRateEvents = np.copy(rates[:,:,ratesI]) # it is a inner rate
os.chdir(workingPath)
fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,4))
fig.subplots_adjust(wspace=0.1)
axarr.plot(1/kb/temperatures, totalRateEvents[-1], label="Total rate from events")
axarr.plot(1/kb/temperatures, totalRate[-1], label="Total rate from M")
axarr.plot(1/kb/temperatures, abs(totalRateEvents[-1]-totalRate[-1]), label="Error abs")
axarr.plot(1/kb/temperatures, abs(totalRateEvents[-1]-totalRate[-1])/totalRateEvents[-1], label="Error rel")
axarr.set_yscale("log")
axarr.legend(loc="best", prop={'size':6})
fig.savefig("totalRates.svg",  bbox_inches='tight')

print(np.shape(tempMavg))

activationEnergy, multiplicityEa = mi.getMultiplicityEa(p,temperatures,labelAlfa,sp,tempMavg,omega,totalRateEvents,ext)
ratioEa = np.zeros(shape=(maxCo2,maxRanges,p.maxA-p.minA))
for i,a in enumerate(range(minAlfa,maxAlfa)):
    ratioEa[:,:,i] = energies[a]

ratioEa[:,:,0:maxAlfa-minAlfa] += e.getEaCorrections(p,temperatures)[:,minAlfa:maxAlfa]

if kindOfSensibility:
    localAe = np.zeros(shape=(maxRanges,maxAlfa))
    for i in range(0,maxAlfa):
        localAe[:,i] = activationEnergy[-1,:]
    mp.plotKindOfSensibility(1/kb/temperatures,(-multiplicityEa[-1,:,:]),labelAlfa,"M")
    mp.plotKindOfSensibility(1/kb/temperatures,(localAe-multiplicityEa[-1,:,:]-ratioEa[-1,:,:]),labelAlfa,"omega")
    
if tofSensibility:
    sensibilityCo2 = mi.getTofSensibility(p,omega,ratioEa,multiplicityEa)
    mp.plotSensibility(sensibilityCo2,temperatures,labelAlfa,total=False)
    os.chdir(workingPath)

if sensibility:
    sensibilityCo2 = mi.getTotalSensibility(p,omega,ratioEa,multiplicityEa)
    mp.plotSensibility(sensibilityCo2,temperatures,labelAlfa,total=True)
    os.chdir(workingPath)


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
                             maxRanges, i, not rAndM and not omegas)
    x.append(1/kb/temperatures[maxRanges-1-i])
    tgt.append(targt[-1])
    rct.append(rcmpt[-1])
    err.append(error[-1])

plt.savefig("multiplicities"+ext+".svg", bbox_inches='tight')


if rAndM: # plot total activation energy as the sum of ratios and multiplicities
    label = ["multiplicity", "sum", "ratio"]
    cm = plt.get_cmap('Accent')
    for j in range(0,maxRanges): # different temperature ranges (low, medium, high)
        partialSum1 = np.sum(omega[:,j,:]*(-multiplicityEa[:,j,:]), axis=1)
        partialSum2 = np.sum(omega[:,j,:]*(ratioEa[:,j,:]), axis=1)
        mp.plotRandM(co2, partialSum1, partialSum2, axarr[maxRanges-1-j], handles, j == maxRanges-1)

    plt.savefig("multiplicitiesRandM"+ext+".png", bbox_inches='tight')

lastOmegas = np.zeros(shape=(maxRanges,maxAlfa-minAlfa))
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
        for i,a in enumerate(range(minAlfa,maxAlfa)): #alfa
            lgs.append(axarr[maxRanges-1-j].fill_between(co2, partialSum, color=cm(a/(maxAlfa-1)), label=labelAlfa[a]))
            lastOmegas[maxRanges-1-j,i] = partialSum[-1]
            partialSum -= omega[:,j,i]*(ratioEa[:,j,i]-multiplicityEa[:,j,i])
            epsilon[:,j,i] = omega[:,j,i]*(ratioEa[:,j,i]-multiplicityEa[:,j,i])

    myLegends = []
    myLabels = []#[r"$E_a$", r"$E^f + \sum_\alpha \;\epsilon_\alpha$"]
    myLegends += lgs
        
    for i in range(maxAlfa-1,minAlfa-1,-1): #alfa
        myLabels.append(labelAlfa[i])
    myLabels.append("Rel. err.")
    plt.figlegend(myLegends, myLabels, loc=(0.68,0.15), prop={'size':11})
    #plt.savefig("multiplicitiesOmegas"+ext+".svg", bbox_inches='tight')

if lmbdas:
    figS, axar = plt.subplots(2, sharex=True, figsize=(5,4))
    figS.subplots_adjust(top=0.95,left=0.15, right=0.95)
    inf.smallerFont(axar[0], 8)
    inf.smallerFont(axar[1], 8)

    lmbda, maxI = rds.plotRds(temperatures,tempMavg,rates,ratios,omega,minAlfa,maxAlfa,labelAlfa,axar[0])
    p.minA = 0; p.maxA = 4; p.maxA = 4
    if p.rLib == "farkas":
        p.minA = 0; p.maxA = 7; p.maxA = 7
    tempMavgS, omegaS, totalRateS, totalRateEventsS, ratesS, ratiosS = mi.getMavgAndOmega(p,temperatures,workingPath)
    totalRateEventsS = np.copy(rates[:,:,2]) # it is a inner rate
    os.chdir(workingPath)
    activationEnergyT, multiplicityEaS = mi.getMultiplicityEa(p,temperatures,labelAlfa,sp,tempMavgS,omegaS,totalRateEventsS,ext="")
    os.chdir(workingPath)
    ratioEaTmp = np.zeros(len(temperatures))
    multiplicityEaTmp = np.zeros(len(temperatures))
    for u,t in enumerate(temperatures):
        ratioEaTmp[u] = ratioEa[-1,u,maxI[u]]
        multiplicityEaTmp[u] = multiplicityEa[-1,u,maxI[u]]
    activationEnergyS = ratioEaTmp - multiplicityEaTmp
    
    fig, ax = plt.subplots(1, figsize=(5,3))
    fig.subplots_adjust(top=0.85,left=0.15,right=0.95,bottom=0.05)
    cm = plt.get_cmap('tab20')
    ax.plot(1/kb/temperatures, activationEnergyT[-1,:], label=r"$E^{TOF}_{app}$", color="red")
    ax.plot(1/kb/temperatures, activationEnergyS[:], "--", label=r"$\sum \xi^{TOF}_\alpha$")
    ax.plot(1/kb/temperatures, abs(activationEnergyT[-1,:]-activationEnergyS[:]), label="Absolute error", color="black")
    maxI = np.array(maxI)
    first = 0
    for i in np.unique(maxI)[::-1]:
        last = np.where(maxI == i)[0][-1]
        ax.fill_between(1/kb/temperatures[first:last+1],activationEnergyS[first:last+1],label=labelAlfa[i], color=cm(i%20/(19)))
        first = last
                        
    ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"Energy $(eV)$")
    labels = [item for item in ax.get_xticklabels()]
    ax.set_xticklabels(labels)
    mp.setY2TemperatureLabels(ax,kb)
    fig.savefig(p.rLib+"Lambdas.pdf")
    plt.close(fig)

cm = plt.get_cmap('tab20')
markers=["o", "s","D","^","d","h","p"]
for i,a in enumerate(range(minAlfa,maxAlfa)):
    if any(abs(omega[-1,:,i]) >= 1e-8):
        #ax.fill_between(x, lastOmegas[:,i], label=labelAlfa[a], color=cm(a%20/(19)))
        axar[1].plot(1/kb/temperatures, -multiplicityEa[-1,:,i],label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
axar[1].legend(loc="best", prop={'size':6})
axar[1].set_ylabel(r"$E^M_\alpha$")
axar[1].set_ylabel(r"Energy $(eV)$")
axar[1].set_xlabel(r"$1/k_BT$")

figS.savefig("multiplicitiesSlope"+ext+".pdf")#, bbox_inches='tight')
plt.close(figS)

figR, ax = plt.subplots(1, figsize=(5,3))
figR.subplots_adjust(top=0.85,left=0.15,right=0.95,bottom=0.05)
ax.plot(x, tgt, label=r"$E^{TOF}_{app}$", color="red")
ax.plot(x, rct, "--", label=r"$\sum \epsilon^{TOF}_\alpha$")
cm = plt.get_cmap('tab20')
markers=["o", "s","D","^","d","h","p"]
for i,a in enumerate(range(minAlfa,maxAlfa)):
    if any(abs(epsilon[-1,::-1,i]) > 0.005):
        #ax.plot(x, epsilon[-1,::-1,i], label=labelAlfa[a], color=cm(abs(i/20)), marker=markers[i%8])
        ax.fill_between(x, lastOmegas[:,i], label=labelAlfa[a], color=cm(a%20/(19)))
# ax2 = ax.twinx()
# ax2.plot(x, err, label="Relative error")
#ax.set_ylim(0,3.2)
#ax.set_xlim(20,30)
labels = [item for item in ax.get_xticklabels()]
#labels[1] = 'Testing'
ax.plot(x, abs(np.array(tgt)-np.array(rct)), label="Absolute error", color="black")
ax.legend(loc="best", prop={'size':6})
ax.set_xticklabels(labels)
#ax.set_xlabel(r"$1/k_BT$")
ax.set_ylabel(r"Energy $(eV)$")
#ax.set_yscale("log")
mp.setY2TemperatureLabels(ax,kb)
if total:
    rl = "R"
else:
    rl = "TOF"
ax.annotate(r"$\epsilon^{"+rl+r"}_\alpha=\omega^{"+rl+r"}_\alpha(E^k_\alpha+E^{k0}_\alpha+E^M_\alpha)$", xy=(0.45,0.2), xycoords="axes fraction")
plt.savefig("multiplicitiesResume"+ext+".pdf")#, bbox_inches='tight')
