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

plt.title("Average island growth")
label = r'Average island radius growth rate $\sqrt{{ \dot{{r}} }} $'
plt.ylabel(label)
label = r'Time-averaged total rate $ < R >_t $'
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

    totalRatio = results.totalRatio()/(flux**0.82)
    gyradius = results.gyradius()/flux**0.88
    r = np.array(mk.getRtt(temperatures))/flux**0.4
    plt.loglog(r, gyradius, "-", label="inverse island"+folder)
    if (i > -7):
        popt = curve_fit(f.power, r, totalRatio)
        a = popt[0][0]
        b = popt[0][1]
        a = 6e-3
        b = 0.4
        label = "{}x^{}".format(a, b)
        x = r
        y = f.power(x, a, b)
        plt.loglog(x, y, label=label)
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("rttVsGyradius.png")
    
plt.close()

print("Good bye!")
          
