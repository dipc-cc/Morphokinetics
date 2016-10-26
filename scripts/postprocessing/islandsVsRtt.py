# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
from scipy.optimize import curve_fit

label = r'Number of islands (N)/F^0.23'
plt.ylabel(label)
label = r'r_tt/F^0.33'
plt.xlabel(label)
plt.legend(loc='upper left', prop={'size':6})
plt.grid(True)

workingPath = os.getcwd()
results = []
temperatures = list(range(120,321,5))

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
    while(len(temperatures) > len(results[-1][0])):
        results[-1][0].append(0)
        results[-1][1].append(0)
        results[-1][2].append(0)
        results[-1][3].append(0)

    v = 0.82*400*400/(np.array(results[-1][3]))*(flux**0.21)
    n = np.array(results[-1][3])/(flux**0.23)
    r = np.array(mk.getRtt(temperatures))/(flux**0.33)
    plt.loglog(r, n, ".", label="N "+folder)
    if (i == -1):
        popt = curve_fit(mk.powerFunc, r, n)
        a = popt[0][0]
        b = popt[0][1]
        label = "{}x^{}".format(a, b)
        x = r
        y = mk.powerFunc(x, a, b)
        plt.loglog(x, y, label=label)

    if (i == -2):
        a = 2e7
        b = -(5/7)
        x = r
        y = mk.powerFunc(x, a, b)
        label = "{}x^{}".format(a, b)
        plt.loglog(x, y, label=label)
        a = 4e7
        b = -(2/3)
        y = mk.powerFunc(x, a, b)
        label = "{}x^{}".format(a, b)
        plt.loglog(x, y, label=label)
        a = 1e7
        b = -0.5
        y = mk.powerFunc(x, a, b)
        label = "{}x^{}".format(a, b)
        plt.loglog(x, y, label=label)
        
        
    plt.legend(loc='lower left', prop={'size':6})
    plt.savefig("islandsVsRtt.png")
    
plt.close()

print("Good bye!")
          
