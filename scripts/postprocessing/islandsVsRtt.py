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
label = r'Number of islands (N)'
plt.ylabel(label)
label = r'r_tt/F'
plt.xlabel(label)
plt.legend(loc='upper left', prop={'size':6})
plt.grid(True)

workingPath = os.getcwd()
results = []
temperatures = list(range(120,221,5))

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
    #axes.set_ylim([1e3,1e10])
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
    r = np.array(Rtt)/flux
    #plt.loglog(totalRatio, vSlope, "-", label="slopes"+folder)
    plt.loglog(r, n, ".", label="N "+folder)
    #plt.loglog(np.array(results[-1][1]), np.array(results[-1][0]),  "x", label=folder)
    #plt.semilogy(1/(kb*np.array(temperatures)), 1e5*np.array(results[-1][0]),  "x-", label=folder)
    #plt.semilogy(1/(kb*np.array(temperatures)), np.array(results[-1][1]),  "s-", label=folder)
    #plt.semilogy(1/(kb*np.array(temperatures)), 1e-5*np.array(Rtt),  "1", label=folder)
    #plt.semilogy(1/(kb*np.array(temperatures)), 2e-2*np.array(Rstep),  "1", label=folder)
    if (i > -3):
        popt = curve_fit(powerFunc, r, n)
        a = popt[0][0]
        b = popt[0][1]
        label = "{}x^{}".format(a, b)
        x = r
        y = powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
    #plt.loglog(np.array(results[-1][1]), np.array(results[-1][2]), '--', label="gyradius "+str(flux))
    plt.legend(loc='lower left', prop={'size':6})
    plt.savefig("islandsVsRtt.png")
    
plt.close()

print("Good bye!")
          
