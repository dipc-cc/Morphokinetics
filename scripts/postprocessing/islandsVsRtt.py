# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
from scipy.optimize import curve_fit
import results

label = r'Number of islands (N)/F^0.23'
plt.ylabel(label)
plt.figure(num=None, figsize=(5,5), dpi=80, facecolor='w', edgecolor='k')
label = r'r_tt/F^0.33'
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
temperatures = list(range(120,321,5))

for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, False, False, False)
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)

    v = 0.82*400*400/meanValues.getIslandsAmount()*(flux**0.21)
    n = meanValues.getIslandsAmount()/(flux**0.23)
    r = np.array(mk.getRtt(temperatures))/(flux**0.33)
    plt.loglog(r, n, "-", label="N "+folder)
    if (i == 0):
        x = r
        c = []
        exponent = []
        for i in temperatures:
            if (i > 250):
                c.append(3.5e7)
                exponent.append(-0.666)
            else:
                c.append(40023)
                exponent.append(-0.333)
        y = c * r ** exponent
        plt.loglog(x, y, "*", label="fit")
    if (i == -1000):
        print(r)
        print(n)
        popt = curve_fit(mk.powerFunc, r, n)
        a = popt[0][0]
        b = popt[0][1]
        label = "{}x^{}".format(a, b)
        x = r
        y = mk.powerFunc(x, a, b)
        plt.loglog(x, y, label=label)

    if (i == -2000):
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
          
