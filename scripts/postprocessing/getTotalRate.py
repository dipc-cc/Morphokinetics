import glob
import os
import sys
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
import info as inf
import math
import multiplicitiesInfo as mi
        

def plot(x,y,p,ax,meskinePlot,label="",marker="o"):
    kb = 8.6173324e-5
    # Labels
    ax.set_ylabel(r"TOF ($\times 10^{-15}$ cm$^{-2} \cdot s^{-1}$)")
    ax.set_xlabel(r"$1/k_BT$")
    ref = False
    if label=="":
        label = p.rLib.title()
        label = "Total "
        ref = True
        cm = plt.get_cmap('Set1')
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
        font = FontProperties()
        font.set_size(6)
        ax.text(20,1e-5,"Library: "+p.rLib.title()+"\nSize: "+str(p.sizI)+"x"+str(p.sizJ),
                bbox=bbox_props, fontproperties=font)
    ucArea = 3.12*6.43/4
    toCm = 1e-8
    area = p.sizI * p.sizJ * ucArea * toCm * toCm
    x = np.array(x)
    y = np.array(y)
    x = 1 / kb / x
    if meskinePlot:
        y = y / (p.sizI * p.sizJ)
    else:
        #y = np.log(y / area) # for different pressures
        y = np.log(y / (p.sizI * p.sizJ))
    
    # reference
    if ref:
        scriptDir = os.path.dirname(os.path.realpath(__file__))
        data = np.loadtxt(scriptDir+"/tof"+p.rLib.title()+".txt")
        if meskinePlot:
            ax.set_yscale("log")
            data = np.loadtxt(scriptDir+"/tofMeskine.txt")
        data[:,0] = 1/(1000 / data[:,0])/kb
        ax.plot(data[:,0],data[:,1],label="Reference", ls="-")#, marker="^")
    ax.plot(x,y,label=label+" rate", ls="-", marker=marker)
    ax.legend(loc="best", prop={'size':6})

    

workingPath = os.getcwd()
x = []
y = []
temperatures = True
try:
    iter = inf.getTemperatures()
except ValueError:
    iter = inf.getPressures()

y2 = np.zeros(shape=(len(iter),4))
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
        rate, rates = mi.getTotalRate()
    except ZeroDivisionError:
        rate = 0
    except IndexError:
        rates = np.zeros(4)
        pass
    print(f,rate)
    x.append(f)
    y.append(rate)
    y2[i] = rates
    os.chdir(workingPath)
    i += 1

p = inf.getInputParameters(glob.glob("*/output*")[0])
meskinePlot = False
if len(sys.argv) > 1:
    meskinePlot = True

fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,3))
plot(x,y,p,ax,meskinePlot)
labels=["Adsorption", "Desorption", "Reaction", "Diffusion"]
markers=["o", "s","^","D","p","d","h","o"]
for i in range(0,4):
    plot(x, y2[:,i], p, ax, meskinePlot, labels[i], markers[i+1])
#plot(x,np.sum(y2[:,:],axis=1),p, ax, "sum")
fig.savefig("totalRate.pdf", bbox_inches='tight')
