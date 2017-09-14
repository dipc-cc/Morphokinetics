import glob
import os
import sys
import numpy as np
import matplotlib.pyplot as plt
import info as inf
import math

def getTotalRate():
    files = glob.glob("dataCatalysis0*.txt")
    rate = 0
    rates = np.zeros(4)
    for t in files:
        data = np.loadtxt(t)
        events = 0
        eventsA = np.zeros(4)
        for i in range(5,9):
            events += data[-1,i] - data[0,i]
            eventsA[i-5] += data[-1,i] - data[0,i]
        rate += events / data[-1,0] # last time
        rates += eventsA / data[-1,0]
    rate = rate / len(files)
    rates = rates / len(files)
    return rate, rates
        

def plot(x,y,p,ax,label=""):
    kb = 8.617332e-5
    # Labels
    ax.set_title("TOF "+str(p.sizI)+"x"+str(p.sizJ))
    ax.set_ylabel(r"$\log(\frac{TOF}{molecules/sec})$")
    ax.set_xlabel(r"$1000/T(1/K)$")
    ref = False
    if label=="":
        label = p.rLib.title()
        ref = True
    ucArea = 3.12*6.43/4
    toCm = 1e-8
    area = p.sizI * p.sizJ * ucArea * toCm * toCm
    x = np.array(x)
    y = np.array(y)
    x = 1000 / x
    #y = np.log(y / area) # for different pressures
    y = np.log(y / (p.sizI * p.sizJ))

    ax.plot(x,y,label=label, ls="-")
    # reference
    if ref:
        scriptDir = os.path.dirname(os.path.realpath(__file__))
        data = np.loadtxt(scriptDir+"/tof"+p.rLib.title()+".txt")
        ax.plot(data[:,0],data[:,1],label="ref TOF", ls="-", marker="^")
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
        rate, rates = getTotalRate()
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

fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,4))
plot(x,y,p,ax)
labels=["Adsorption", "Desorption", "Reaction", "Diffusion"]
for i in range(0,4):
    plot(x, y2[:,i], p, ax, labels[i])
#plot(x,np.sum(y2[:,:],axis=1),p, ax, "sum")
fig.savefig("totalRate.svg", bbox_inches='tight')
