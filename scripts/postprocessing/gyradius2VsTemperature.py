#
# Compares in a graph the average island size (or its square root)
# growth with the total rate.
#
# Author: J. Alberdi-Rodriguez

import os
import math
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
from scipy.optimize import curve_fit
import results
         
label = r'total rate and gyradius'
plt.ylabel(label)
label = r'1/kbT'
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

    v = 0.82*400*400/(results.islands())*(flux**0.21)
    #v = flux*0.7*400*400/(np.array(results[-1][3]))*(flux**0)
    n = results.islands()
    vSlope = results.growthSlope()/(flux**0.79)
    totalRatio = (results.totalRatio()/(flux**0.81))**(1)
    x = totalRatio
    y = mk.fractalDFunc(x)
    gyradius = results.gyradius()
    f = math.pi
    c = 4e-1
    d = np.array(y)
    Rtt = mk.getRtt(temperatures)
    gyradiusCalc = (flux*0.7/(d*(f*c)**(1/d)*0.3**(1-1/d)))*(np.array(Rtt)/flux)**(0.33/d)
    r = np.array(Rtt)/flux**0.36
    inverseTemperature = 1/(kb*np.array(temperatures))
    plt.semilogy(inverseTemperature, gyradius, "-", label="gyradius "+folder)
    plt.semilogy(inverseTemperature, gyradiusCalc, "--", label="gyradius calc "+folder)
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
          
