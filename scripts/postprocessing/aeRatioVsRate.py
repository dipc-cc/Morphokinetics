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


plt.title("Average island growth")
label = r''
plt.ylabel(label)
label = r''
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
results = []
temperatures = list(range(120,221,5))

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
    #axes.set_ylim([1e3,1e10])
    #axes.set_xlim([1e6,1e10])
    #adapt y to x size
    while(len(temperatures) > len(results[-1][0])):
        results[-1][0].append(0)
        results[-1][1].append(0)
        results[-1][2].append(0)
        results[-1][3].append(0)
        results[-1][6].append(0)

    mtt = np.array(results[-1][6])#/flux**.063
    print(mtt)
    #r = np.array(results[-1][1])/flux**0.88
    #r = np.array(Rtt)/flux**(1/3)
    r = np.array( mk.getRtt(temperatures))#/flux**0.5
    plt.loglog(mtt, r, "-", label="N "+folder)
    if (i < -7):
        popt = curve_fit(mk.powerFunc, r, mtt)
        a = popt[0][0]
        b = popt[0][1]
        x = r
        a = 2e2
        b = 2/3
        label = "{}x^{}".format(a, b)
        y = mk.powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
    plt.legend(loc='lower right', prop={'size':6})
    plt.savefig("aeRatioVsRate.png")
    
plt.close()

print("Good bye!")
          
