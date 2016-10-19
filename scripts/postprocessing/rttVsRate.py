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
Rtt = []
Rstep = ([85941.5666677768, 185709.051983031, 378203.30334162, 730700.474287996, 1346871.73342121, 2380119.6620485, 4049377.66119581, 6657170.49755918, 10609590.0115993, 16437717.6779536, 24819933.7510902, 36604495.7633867, 52831748.04825, 74755336.5257832, 103861844.747233, 141888332.294831, 190837338.622147, 252989007.881088, 330910087.306819, 427459648.223626, 545791470.564676])
for index,i in enumerate(temperatures):
    Rtt.append(1e13*np.exp(-0.2/(kb*i)))

#temp2 = list(range(205,221,5))
#temperatures = temperatures + temp2 # concatenation
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
    r = np.array(Rtt)/flux**0.39
    #plt.loglog(totalRatio, vSlope, "-", label="slopes"+folder)
    plt.loglog(r, totalRatio, "-", label="inverse island"+folder)
    #plt.loglog(np.array(results[-1][1]), np.array(results[-1][0]),  "x", label=folder)
    #plt.semilogy(1/(kb*np.array(temperatures)), 1e5*np.array(results[-1][0]),  "x-", label=folder)
    #plt.semilogy(1/(kb*np.array(temperatures)), np.array(results[-1][1]),  "s-", label=folder)
    #plt.semilogy(1/(kb*np.array(temperatures)), 1e-5*np.array(Rtt),  "1", label=folder)
    #plt.semilogy(1/(kb*np.array(temperatures)), 2e-2*np.array(Rstep),  "1", label=folder)
    if (i > -7):
        popt = curve_fit(powerFunc, r, totalRatio)
        a = popt[0][0]
        b = popt[0][1]
        a = 500
        b = 0.6666
        label = "{}x^{}".format(a, b)
        x = r
        y = powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
    #plt.loglog(np.array(results[-1][1]), np.array(results[-1][2]), '--', label="gyradius "+str(flux))
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("rttVsRate.png")
    
plt.close()

print("Good bye!")
          
