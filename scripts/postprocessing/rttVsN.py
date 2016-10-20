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
label = r'r_tt'
plt.ylabel(label)
label = r'Number of islands (N)'
plt.xlabel(label)
plt.legend(loc='upper left', prop={'size':6})
plt.grid(True)

workingPath = os.getcwd()
results = []
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
    while(len(temperatures) > len(results[-1][0])):
        results[-1][0].append(0)
        results[-1][1].append(0)
        results[-1][2].append(0)
        results[-1][3].append(0)

    v = 0.82*400*400/(np.array(results[-1][3]))*(flux**0.21)
    n = np.array(results[-1][3])
    vSlope = np.array(results[-1][0])/(flux**0.79)
    totalRatio = np.array(results[-1][1])/(flux**0.82)
    r = np.array(mk.getRtt(temperatures))/flux
    plt.loglog(r, n, ".", label="inverse island"+folder)
    if (i > -3):
        popt = curve_fit(powerFunc, r, n)
        a = popt[0][0]
        b = popt[0][1]
        label = "{}x^{}".format(a, b)
        x = r
        y = powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("rttVsN.png")
    
plt.close()


print("Good bye!")
          
