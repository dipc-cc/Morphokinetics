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
label = r'r_tt'
plt.ylabel(label)
label = r'Number of islands (N)'
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
temperatures = list(range(120,321,5))
useNaN = False
results = results.Results(temperatures, useNaN)
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

    n = results.islands()
    r = np.array(mk.getRtt(temperatures))/flux
    plt.loglog(r, n, ".", label="inverse island"+folder)
    if (i > -3):
        popt = curve_fit(mk.powerFunc, r, n)
        a = popt[0][0]
        b = popt[0][1]
        label = "{}x^{}".format(a, b)
        x = r
        y = mk.powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
    plt.legend(loc='lower left', prop={'size':6})
    plt.savefig("rttVsN.png")
    
plt.close()


print("Good bye!")
          
