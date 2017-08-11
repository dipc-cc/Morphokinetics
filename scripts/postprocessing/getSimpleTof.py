import glob
import os
import numpy as np
import info as inf
import math

def getSimpleTof():
    tofFiles = glob.glob("dataTof0*.txt")
    tofs = 0
    for t in tofFiles:
        data = np.loadtxt(t)
        if len(data) > 0:
            try:
                tofs += data[-1,0] # last simulated time, assuming 1000 atoms are created
            except IndexError:
                continue
    tof = 1000/ (tofs / len(tofFiles))
    return tof
        

    

workingPath = os.getcwd()
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
    os.chdir(workingPath)
