import glob
import os
import numpy as np
import matplotlib.pyplot as plt
import info as inf
import math

def getSimpleTof():
    tofFiles = glob.glob("dataTof0*.txt")
    tofs = 0
    for t in tofFiles:
        data = np.loadtxt(t)
        if len(data) > 0:
            try:
                tofs += data[-1,0] - data[0,0]# last simulated time, assuming 1000 atoms are created
            except IndexError:
                continue
    tof = 1000/ (tofs / len(tofFiles))
    return tof
        

def plot(x,y,p):
    fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,4))
    # Labels
    ax.set_title("TOF "+str(p.sizI)+"x"+str(p.sizJ))
    ax.set_ylabel(r"$\log(\frac{TOF}{molecules/sec})$")
    ax.set_xlabel(r"$1000/T(1/K)$")
    
    ucArea = 3.12*6.43/4
    toCm = 1e-8
    area = p.sizI * p.sizJ * ucArea * toCm * toCm
    x = np.array(x)
    x = 1000 / x
    y = np.array(y)
    #y = np.log(y / area) # for different pressures
    y = np.log(y / (p.sizI * p.sizJ))
    ax.plot(x,y,label=p.rLib.title())

    # reference
    scriptDir = os.path.dirname(os.path.realpath(__file__))
    data = np.loadtxt(scriptDir+"/tof"+p.rLib.title()+".txt")
    ax.plot(data[:,0],data[:,1],label="ref")
    ax.legend(loc="best", prop={'size':6})

    fig.savefig("tof.png")

workingPath = os.getcwd()
x = []
y = []
try:
    iter = inf.getTemperatures()
except ValueError:
    iter = inf.getPressures()
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
plot(x,y,p)
