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

label = r'total rate and gyradius'
plt.ylabel(label)
label = r'1/kbT'
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
    totalRatio = (np.array(results[-1][1])/(flux**0.81))**(1)
    x = totalRatio
    y = mk.fractalDFunc(x)
    gyradius = 4e7*(np.array(results[-1][2])/(flux**0.88))**(3/4*(np.array(y)-1))
    gyradius = 1.3e5*(np.array(results[-1][2])/(flux**0.88))**(3*(np.array(y)-1))
    r = np.array(Rtt)/flux**0.36
    inverseTemperature = 1/(kb*np.array(temperatures))
    plt.semilogy(inverseTemperature, gyradius, "-", label="gyradius "+folder)
    plt.semilogy(inverseTemperature, totalRatio, "--", label="total ratio "+folder)
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
    plt.legend(loc='lower left', prop={'size':6})
    plt.savefig("gyradiusVsTemperature.png")
    
plt.close()

print("Good bye!")
          
