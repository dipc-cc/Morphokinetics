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


def powerFunc(x, a, b):
    """ a*x^b function """
    return a*x**b

plt.title("Average island growth")
label = r'Average island radius growth rate $\sqrt{{ \dot{{r}} }} $'
plt.ylabel(label)
label = r'Time-averaged total rate $ < R >_t $'
plt.xlabel(label)
plt.legend(loc='upper left', prop={'size':6})
plt.grid(True)

workingPath = os.getcwd()
results = []
temperatures = list(range(120,221,5))
kb = 8.6173324e-5

rAll = []
nAll = []
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
    axes.set_ylim([1e6,1e10])
    #axes.set_xlim([1e6,1e10])
    #adapt y to x size
    while(len(temperatures) > len(results[-1][0])):
        results[-1][0].append(0)
        results[-1][1].append(0)
        results[-1][2].append(0)
        results[-1][3].append(0)

    v = 0.82*400*400/(np.array(results[-1][3]))*(flux**0.21)
    #v = flux*0.7*400*400/(np.array(results[-1][3]))*(flux**0)
    n = np.array(results[-1][3])
    nAll.append(n)
    vSlope = np.array(results[-1][0])/(flux**0.79)
    totalRatio = np.array(results[-1][1])/(flux**0.82)
    r = np.array(mk.getRtt(temperatures))/flux**0.39
    plt.loglog(r, totalRatio, "-", label="inverse island"+folder)
    if (i > -7):
        popt = curve_fit(powerFunc, r, totalRatio)
        a = popt[0][0]
        b = popt[0][1]
        a = 500
        b = 0.6666 # The fit is either this or 1/3
        label = "{}x^{}".format(a, b)
        x = r
        y = powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
    #plt.loglog(np.array(results[-1][1]), np.array(results[-1][2]), '--', label="gyradius "+str(flux))
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("rttVsRate.png")
    
plt.close()

print("Good bye!")
          
