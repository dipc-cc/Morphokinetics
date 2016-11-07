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

label = r'total rate and numbe of islands'
plt.ylabel(label)
label = r'1/kbT'
plt.xlabel(label)
plt.grid(True)
fig, ax1 = plt.subplots()
ax2 = ax1.twinx()

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
        results.append(mk.getIslandDistribution(temperatures, False, False, False))
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)

    totalRatio = (results.totalRatio()/(flux**0.81))**(1)
    x = totalRatio
    inverseTemperature = 1/(kb*np.array(temperatures))
    d = mk.fractDFuncTemperature(inverseTemperature)
    numberOfIslands = 9e9/results.islands()**(2/np.array(d))
    ax1.semilogy(inverseTemperature, numberOfIslands , "-", label="numberOfIslands "+folder)
    ax1.semilogy(inverseTemperature, totalRatio, "--", label="total ratio "+folder)
    ax2.plot(inverseTemperature, d, ".", label="total ratio "+folder)
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
    plt.savefig("islandsVsTemperature.png")
    
plt.close()

print("Good bye!")
          
