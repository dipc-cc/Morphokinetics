
import glob
import os
import sys
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties
import matplotlib.ticker as ticker
import info as inf
import math
import multiplicitiesInfo as mi
        
kb = 8.6173324e-5

def plot(x,y,p,ax,meskinePlot,label="",marker="o"):
    # Labels
    ax.set_ylabel(r"events/site/s")
    ax.set_xlabel(r"$1/k_BT$")
    ref = False
    color = "w"
    if label=="":
        color = "C5"
        label = p.rLib.title()
        label = "Total "
        ref = True
        cm = plt.get_cmap('Set1')
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
        font = FontProperties()
        font.set_size(6)
        ax.annotate("Library: "+p.rLib.title()+"\nSize: "+str(p.sizI)+"x"+str(p.sizJ), xy=(0.97, 0.96), xycoords="axes fraction",
                    bbox=bbox_props, fontproperties=font, horizontalalignment='right', verticalalignment='top',)
    ucArea = 3.12*6.43/4
    toCm = 1e-8
    area = p.sizI * p.sizJ * ucArea * toCm * toCm
    x = np.array(x)
    y = np.array(y)
    x = 1 / kb / x
    #if meskinePlot:
    y = y / (p.sizI * p.sizJ)
    #else:
    #    #y = np.log(y / area) # for different pressures
    #    y = np.log(y / (p.sizI * p.sizJ))
    
    # reference
    if ref:
        scriptDir = os.path.dirname(os.path.realpath(__file__))
        data = np.loadtxt(scriptDir+"/tof"+p.rLib.title()+".txt")
        ax.set_yscale("log")
        if meskinePlot:
            data = np.loadtxt(scriptDir+"/tofMeskine.txt")
        data[:,0] = 1/(1000 / data[:,0])/kb
        if not meskinePlot:
            data[:,1] = np.exp(data[:,1])
        ax.plot(data[:,0],data[:,1],label="Reference (TOF)", ls="-", lw=2)#, marker="^")
    ax.plot(x,y,label=label, ls="-", marker=marker, mfc=color)
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

fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,3.5))
fig.subplots_adjust(top=0.85, bottom=0.15, left=0.15, right=0.95, hspace=0.25,
                    wspace=0.35)
labels=["Adsorption", "Desorption", "Reaction (TOF)", "Diffusion"]
markers=["o", "+","x","1","s","d","h","o"]
for i in range(0,4):
    plot(x, y2[:,i], p, ax, meskinePlot, labels[i], markers[i+1])
#plot(x,np.sum(y2[:,:],axis=1),p, ax, "sum")
plot(x,y,p,ax,meskinePlot)
ax2 = ax.twiny()
ax2.set_xlim(1/kb/ax.get_xlim()[0],1/kb/ax.get_xlim()[1])
ax2.set_xlim(ax.get_xlim()[0],ax.get_xlim()[1])
#ax2.set_xscale("log")
ticks_x = ticker.FuncFormatter(lambda x, pos: '${0:d}$'.format(int(1/kb/x)))
ax2.xaxis.set_major_formatter(ticks_x)
majors = np.array(list(np.arange(200,400,20))+list(np.arange(400,760,50)))
majors = 1/kb/majors
ax2.xaxis.set_major_locator(ticker.FixedLocator(majors))
ax2.set_xlabel("temperature (T)")
fig.savefig("totalRate.svg")#, bbox_inches='tight')
