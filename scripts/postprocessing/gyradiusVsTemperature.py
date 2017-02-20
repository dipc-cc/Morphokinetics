import functions as f
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

label = r'total rate and gyradius'
plt.ylabel(label)
label = r'1/kbT'
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
temperatures = list(range(120,221,5))
kb = 8.6173324e-5

for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, False, False)
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)

    totalRatio = (meanValues.getTotalRatio()/(flux**0.81))**(1)
    x = totalRatio
    y = f.fractalD(x)
    gyradius = 4e7*(meanValues.getGyradiusSlope()/(flux**0.88))**(3/4*(np.array(y)-1))
    gyradius = 1.3e5*(meanValues.getGyradiusSlope()/(flux**0.88))**(3*(np.array(y)-1))
    r = mk.getRtt(temperatures)/flux**0.36
    inverseTemperature = 1/(kb*np.array(temperatures))
    plt.semilogy(inverseTemperature, gyradius, "-", label="gyradius "+folder)
    plt.semilogy(inverseTemperature, totalRatio, "--", label="total ratio "+folder)
    if (i < -7):
        popt = curve_fit(f.power, r, totalRatio)
        a = popt[0][0]
        b = popt[0][1]
        a = 8e-11
        b = 1.33
        label = "{}x^{}".format(a, b)
        x = r
        y = f.power(x, a, b)
        plt.loglog(x, y, label=label)
    plt.legend(loc='lower left', prop={'size':6})
    plt.savefig("gyradiusVsTemperature.png")
    
plt.close()

print("Good bye!")
          
