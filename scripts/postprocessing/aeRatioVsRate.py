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
label = r''
plt.ylabel(label)
label = r''
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
results = results.Results()
temperatures = list(range(120,221,5))

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

    mtt = results.aeRatioTimesPossible()#/flux**.063
    r = np.array(mk.getRtt(temperatures))#/flux**0.5
    plt.loglog(mtt, r, "-", label="N "+folder)
    if (i < -7):
        popt = curve_fit(mk.powerFunc, r, mtt)
        a = popt[0][0]
        b = popt[0][1]
        x = r
        a = 2e2
        b = 2/3
        label = "{}x^{}".format(a, b)
        y = mk.powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
    plt.legend(loc='lower right', prop={'size':6})
    plt.savefig("aeRatioVsRate.png")
    
plt.close()

print("Good bye!")
          
