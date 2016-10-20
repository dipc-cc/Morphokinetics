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
label = r''
plt.ylabel(label)
label = r''
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
results = []
temperatures = list(range(120,221,5))
kb = 8.6173324e-5
Rtt = []
Rstep = ([85941.5666677768, 185709.051983031, 378203.30334162, 730700.474287996, 1346871.73342121, 2380119.6620485, 4049377.66119581, 6657170.49755918, 10609590.0115993, 16437717.6779536, 24819933.7510902, 36604495.7633867, 52831748.04825, 74755336.5257832, 103861844.747233, 141888332.294831, 190837338.622147, 252989007.881088, 330910087.306819, 427459648.223626, 545791470.564676])
for index,i in enumerate(temperatures):
    Rtt.append(1e13*np.exp(-0.2/(kb*i)))

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
    r = np.array(Rtt)#/flux**0.5
    plt.loglog(mtt, r, "-", label="N "+folder)
    if (i < -7):
        popt = curve_fit(powerFunc, r, mtt)
        a = popt[0][0]
        b = popt[0][1]
        x = r
        a = 2e2
        b = 2/3
        label = "{}x^{}".format(a, b)
        y = powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
    plt.legend(loc='lower right', prop={'size':6})
    plt.savefig("aeRatioVsRate.png")
    
plt.close()

print("Good bye!")
          
