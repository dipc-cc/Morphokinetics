import glob
import os
import sys
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
import info as inf
import math

def getSimpleTof():
    i = -1
    j = 0
    i_j = (i-j)*10
    if i_j < 0:
        i_j = 1000
    tofFiles = glob.glob("dataTof0*.txt")
    tofs = 0
    for t in tofFiles:
        data = np.loadtxt(t)
        if len(data) > 0:
            try:
                tofs += data[i,0] - data[j,0]# last simulated time, assuming 1000 atoms are created
                #print(data[i,0] - data[j,0])
            except IndexError:
                continue
    tof = i_j / (tofs / len(tofFiles))
    return tof
        

def plot(x,y,p,meskinePlot):
    kb = 8.617332e-5
    fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,4))
    # Labels
    ax.set_ylabel(r"$\log(\frac{TOF}{molecules/sec})$")
    ax.set_xlabel(r"$1000/T(1/K)$")
    
    ucArea = 3.12*6.43/4
    toCm = 1e-8
    area = p.sizI * p.sizJ * ucArea * toCm * toCm
    x = np.array(x)
    y = np.array(y)
    x = 1000 / x
    if meskinePlot:
        y = y / (p.sizI * p.sizJ)
    else:
        #y = np.log(y / area) # for different pressures
        y = np.log(y / (p.sizI * p.sizJ))

    # reference
    scriptDir = os.path.dirname(os.path.realpath(__file__))
    data = np.loadtxt(scriptDir+"/tof"+p.rLib.title()+".txt")
    if meskinePlot:
        ax.set_yscale("log")
        data = np.loadtxt(scriptDir+"/tofMeskine.txt")
    ax.plot(x,y,label=p.rLib.title(), ls="-", marker="+")
    ax.plot(data[:,0],data[:,1],label="ref", ls="-", marker="^")
    ax.legend(loc="best", prop={'size':6})

    fig.savefig("tof.svg", bbox_inches='tight')
    
def plotPressures(x,y,p,meskinePlot=False):
    fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,3.5))
    fig.subplots_adjust(top=0.85, bottom=0.15, left=0.15, right=0.95, hspace=0.25,
                        wspace=0.35)
    # Labels
    #ax.set_title("TOF "+str(p.sizI)+"x"+str(p.sizJ))
    ax.set_ylabel(r"events/site/s")
    if meskinePlot:
        ax.set_xlabel(r"$P_{CO}$")
    else:
        ax.set_xlabel(r"$P_{CO}/P_{O}$")
    
    ucArea = 3.12*6.43/4
    toCm = 1e-8
    area = p.sizI * p.sizJ * ucArea * toCm * toCm
    x = np.array(x)
    y = np.array(y)
    y = y / (p.sizI * p.sizJ)
    # reference
    scriptDir = os.path.dirname(os.path.realpath(__file__))
    data = np.loadtxt(scriptDir+"/tofP"+p.rLib.title()+".txt")
    dataE = np.loadtxt(scriptDir+"/tofPExperiment.txt")
    data[:,1] = data[:,1] * area/(p.sizI * p.sizJ)
    dataE[:,1] = dataE[:,1] * area/(p.sizI * p.sizJ)
    print(data)
    ax.set_yscale("log")
    if meskinePlot:
        #y = y / 1e15
        ax.set_xscale("log")
        data = np.loadtxt(scriptDir+"/tofPMeskine.txt")
    else:
        ax.set_xlim(0,10)
        ax.plot(dataE[:,0],dataE[:,1],label="Experiment (TOF)", ls="-", lw=2)# marker="^")

    ax.set_yscale("log")
    #ax.set_ylim(1e11,1e14)
    ax.plot(data[:,0],data[:,1],label="Reference (TOF)", ls="-", lw=2, marker="x")
    ax.plot(x,y,label=r"$R_r$ (TOF)", marker="+")

    ax.legend(loc="best", prop={'size':6})
    bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
    font = FontProperties()
    font.set_size(6)
    ax.annotate("10 runs\nSize: "+str(p.sizI)+"x"+str(p.sizJ)+"\n"+r"$T=350 K$", xy=(0.97, 0.16), xycoords="axes fraction",
                bbox=bbox_props, fontproperties=font, horizontalalignment='right', verticalalignment='top',)
    ax.annotate("TOF",xy=(0.4,0.7), xycoords="axes fraction")

    fig.savefig("tof.pdf")#, bbox_inches='tight')

workingPath = os.getcwd()
x = []
y = []
temperatures = True
try:
    iter = inf.getTemperatures()
except ValueError:
    iter = inf.getPressures()

if iter[0] < 15:
    temperatures = False
for f in iter:
    try:
        os.chdir(str(f)+"/results")
        runFolder = glob.glob("*/");
        runFolder.sort()
        os.chdir(runFolder[-1])
    except FileNotFoundError:
        pass
    os.getcwd()
    tof = 0
    try:
        tof = getSimpleTof()
    except ZeroDivisionError:
        tof = 0
    except IndexError:
        pass
    print(f,tof)
    x.append(f)
    y.append(tof)
    os.chdir(workingPath)

p = inf.getInputParameters(glob.glob("*/output*")[0])
meskinePlot = False
if len(sys.argv) > 1:
    meskinePlot = True
if temperatures:
    plot(x,y,p,meskinePlot)
else:
    plotPressures(x,y,p,meskinePlot)
