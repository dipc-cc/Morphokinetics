#
# Compares in a graph the average island size (or its square root)
# growth with the total rate.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
from scipy.optimize import curve_fit
import results

plt.title("Average gyradius growth")
label = r'Average island radius growth rate $\sqrt{{ \dot{{r}} }} $'
plt.ylabel(label)
label = r'Time-averaged total rate $ < R >_t $'
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
results = results.Results()
temperatures = list(range(120,221,5))
kb = 8.6173324e-5

for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(temperatures, False, False))
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)
    axes = plt.gca()
    axes.set_ylim([1e0,1e2])
    axes.set_xlim([3e6,1e9])

    v = 0.82*400*400/(results.islands())*(flux**0.21)
    n = results.islands()
    vSlope = results.growthSlope()/(flux**0.79)
    totalRatio = results.totalRatio()/(flux**0.81)
    x = totalRatio
    y = mk.fractalDFunc(x)
    gyradius = (results.gyradius()/(flux**0.88))**((np.array(y)-1))
    r = np.array(mk.getRtt(temperatures))#/flux**0.36
    plt.loglog(totalRatio, gyradius, "-", label="inverse island"+folder)
    if (i < -7):
        popt = curve_fit(mk.powerFunc, r, totalRatio)
        a = popt[0][0]
        b = popt[0][1]
        a = 8e-11
        b = 1.33
        label = "{}x^{}".format(a, b)
        x = r
        y = mk.powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("gyradiusVsRate.png")
    
plt.close()

print("Good bye!")
          
